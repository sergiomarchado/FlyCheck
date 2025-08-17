// data/repository/FileChecklistRepository.kt
package com.sergiom.flycheck.data.repository

import android.content.Context
import android.os.Environment
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.data.models.ChecklistInfo
import com.sergiom.flycheck.data.storage.ImageStore
import com.sergiom.flycheck.di.IoDispatcher
import com.sergiom.flycheck.domain.repository.ChecklistRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class FileChecklistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    @IoDispatcher private val io: CoroutineDispatcher
) : ChecklistRepository {

    private val folder: File? =
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.apply { mkdirs() }

    override suspend fun listChecklists(): List<ChecklistInfo> = withContext(io) {
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
                        id = file.nameWithoutExtension,
                        name = template.name.ifBlank { "(sin nombre)" },
                        model = template.aircraftModel.ifBlank { "-" },
                        airline = template.airline.ifBlank { "-" },
                        lastModified = file.lastModified()
                    )
                }.getOrNull()
            }
    }

    /** Carga + normaliza a file:// usando el id (nombre de archivo) como carpeta. */
    override suspend fun loadChecklist(id: String): CheckListTemplateModel? = withContext(io) {
        val dir = folder ?: return@withContext null
        val file = File(dir, "$id.json")
        if (!file.exists()) return@withContext null

        runCatching {
            val original = json.decodeFromString(
                CheckListTemplateModel.serializer(),
                file.readText()
            )
            // Normaliza usando el id de fichero como “namespace” de imágenes
            val normalized = ImageStore.ensureLocalCopies(context, id, original)

            // Si ha cambiado algo (p.ej., URIs content:// -> file://), persistimos para evitar recopiados
            if (normalized != original) {
                val payload = json.encodeToString(CheckListTemplateModel.serializer(), normalized)
                file.atomicWrite(payload)
            }
            normalized
        }.getOrNull()
    }

    /** Guarda normalizado (evita dejar content:// en disco). */
    override suspend fun saveChecklist(id: String, template: CheckListTemplateModel) = withContext(io) {
        val dir = folder ?: return@withContext
        val file = File(dir, "$id.json")
        val normalized = ImageStore.ensureLocalCopies(context, id, template)
        val payload = json.encodeToString(CheckListTemplateModel.serializer(), normalized)
        file.atomicWrite(payload)
    }

    /** Renombra JSON y mueve la carpeta de imágenes asociada. */
    override suspend fun renameChecklist(oldId: String, newId: String): Boolean = withContext(io) {
        val dir = folder ?: return@withContext false
        val oldFile = File(dir, "$oldId.json")
        val safeNewId = newId.lowercase().replace(Regex("[^a-z0-9-_]"), "_")
        val newFile = File(dir, "$safeNewId.json")
        if (!oldFile.exists()) return@withContext false
        if (newFile.exists()) return@withContext false

        val ok = oldFile.renameTo(newFile)
        if (ok) ImageStore.moveTemplateImages(context, oldId, safeNewId)
        ok
    }

    /** Borra JSON y su carpeta de imágenes. */
    override suspend fun deleteChecklist(id: String): Boolean = withContext(io) {
        val dir = folder ?: return@withContext false
        val file = File(dir, "$id.json")
        val ok = file.exists() && file.delete()
        ImageStore.deleteTemplateImages(context, id) // idempotente
        ok
    }

    private fun File.atomicWrite(content: String) {
        val tmp = File(parentFile, "${name}.tmp")
        tmp.writeText(content)
        if (!tmp.renameTo(this)) {
            tmp.delete()
        }
    }
}
