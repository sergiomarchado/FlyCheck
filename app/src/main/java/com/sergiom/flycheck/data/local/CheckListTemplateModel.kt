// data/local/ChecklistManagerRepository.kt
package com.sergiom.flycheck.data.local

import android.content.Context
import android.os.Environment
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import kotlinx.serialization.json.Json
import java.io.File

data class ChecklistInfo(
    val id: String,          // filename sin extensión
    val name: String,
    val model: String,
    val airline: String,
    val lastModified: Long
)

class ChecklistManagerRepository(
    context: Context,
    private val json: Json
) {
    /**
     * Carpeta de export "Local" (la misma que usa ExportTemplateUseCase):
     * context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
     *
     * Nota: Es app-specific external storage. No requiere permisos de lectura/escritura
     * y es accesible sólo por tu app (se borra al desinstalar).
     */
    private val folder: File? =
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.apply { mkdirs() }

    /** Lista los .json que hay en la carpeta de export local */
    fun listChecklists(): List<ChecklistInfo> {
        val dir = folder ?: return emptyList()
        val files = dir.listFiles { f -> f.isFile && f.extension.equals("json", true) } ?: return emptyList()

        return files
            .sortedByDescending { it.lastModified() }
            .mapNotNull { file ->
                runCatching {
                    val template = json.decodeFromString(
                        CheckListTemplateModel.serializer(),
                        file.readText()
                    )
                    ChecklistInfo(
                        id = file.nameWithoutExtension, // usamos el nombre de archivo como id
                        name = template.name.ifBlank { "(sin nombre)" },
                        model = template.aircraftModel.ifBlank { "-" },
                        airline = template.airline.ifBlank { "-" },
                        lastModified = file.lastModified()
                    )
                }.getOrNull()
            }
    }

    /** Carga un template por id (nombre de archivo sin extensión) */
    fun loadChecklist(id: String): CheckListTemplateModel? {
        val dir = folder ?: return null
        val file = File(dir, "$id.json")
        if (!file.exists()) return null
        return runCatching {
            json.decodeFromString(CheckListTemplateModel.serializer(), file.readText())
        }.getOrNull()
    }

    /** Guarda/actualiza un template con un id (nombre de archivo sin extensión) */
    fun saveChecklist(id: String, template: CheckListTemplateModel) {
        val dir = folder ?: return
        val file = File(dir, "$id.json")
        file.writeText(json.encodeToString(CheckListTemplateModel.serializer(), template))
    }

    /**
     * Renombrado de archivo (cambia el id). Mantengo la firma por compatibilidad
     * pero más adelante te propondré renombrar SOLO el nombre visible del template.
     */
    fun renameChecklist(oldId: String, newId: String): Boolean {
        val dir = folder ?: return false
        val oldFile = File(dir, "$oldId.json")
        val newFile = File(dir, "$newId.json")
        if (!oldFile.exists()) return false
        if (newFile.exists()) return false // evita sobrescribir otra
        return oldFile.renameTo(newFile)
    }

    /** Elimina un template por id */
    fun deleteChecklist(id: String): Boolean {
        val dir = folder ?: return false
        val file = File(dir, "$id.json")
        return file.exists() && file.delete()
    }
}
