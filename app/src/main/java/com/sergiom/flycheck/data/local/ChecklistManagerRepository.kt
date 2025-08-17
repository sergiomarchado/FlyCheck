package com.sergiom.flycheck.data.local

import android.content.Context
import android.os.Environment
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

data class ChecklistInfo(
    val id: String,          // filename sin extensión
    val name: String,
    val model: String,
    val airline: String,
    val lastModified: Long
)

/**
 * Repositorio basado en ficheros JSON dentro de app-specific external storage.
 * Todos los métodos son suspend y se ejecutan en [io].
 */
class ChecklistManagerRepository(
    context: Context,
    private val json: Json,
    private val io: CoroutineDispatcher
) {
    /** Carpeta de export "Local" (la misma que usa ExportTemplateUseCase). */
    private val folder: File? =
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.apply { mkdirs() }

    /** Lista los .json que hay en la carpeta de export local (ordenados por última modificación desc). */
    suspend fun listChecklists(): List<ChecklistInfo> = withContext(io) {
        val dir = folder ?: return@withContext emptyList()
        val files = dir.listFiles { f -> f.isFile && f.extension.equals("json", true) } ?: return@withContext emptyList()

        files.sortedByDescending { it.lastModified() }
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

    /** Carga un template por id (nombre de archivo sin extensión). Devuelve null si no existe o falla el parseo. */
    suspend fun loadChecklist(id: String): CheckListTemplateModel? = withContext(io) {
        val dir = folder ?: return@withContext null
        val file = File(dir, "$id.json")
        if (!file.exists()) return@withContext null
        runCatching {
            json.decodeFromString(CheckListTemplateModel.serializer(), file.readText())
        }.getOrNull()
    }

    /** Guarda/actualiza un template con escritura atómica (.tmp → rename). */
    suspend fun saveChecklist(id: String, template: CheckListTemplateModel) = withContext(io) {
        val dir = folder ?: return@withContext
        val file = File(dir, "$id.json")
        val payload = json.encodeToString(CheckListTemplateModel.serializer(), template)
        file.atomicWrite(payload)
    }

    /**
     * Renombrado de archivo (cambia el id/filename). Mantiene compatibilidad.
     * Se sanitiza el nuevo id para evitar caracteres problemáticos.
     */
    suspend fun renameChecklist(oldId: String, newId: String): Boolean = withContext(io) {
        val dir = folder ?: return@withContext false
        val oldFile = File(dir, "$oldId.json")
        val safeNewId = newId.lowercase().replace(Regex("[^a-z0-9-_]"), "_")
        val newFile = File(dir, "$safeNewId.json")
        if (!oldFile.exists()) return@withContext false
        if (newFile.exists()) return@withContext false // evita sobrescribir otra
        oldFile.renameTo(newFile)
    }

    /** Elimina un template por id (filename sin extensión). */
    suspend fun deleteChecklist(id: String): Boolean = withContext(io) {
        val dir = folder ?: return@withContext false
        val file = File(dir, "$id.json")
        file.exists() && file.delete()
    }

    /** Escritura atómica: escribe en .tmp y luego renombra; intenta limpiar si falla. */
    private fun File.atomicWrite(content: String) {
        val tmp = File(parentFile, "${name}.tmp")
        tmp.writeText(content)
        if (!tmp.renameTo(this)) {
            tmp.delete()
        }
    }
}
