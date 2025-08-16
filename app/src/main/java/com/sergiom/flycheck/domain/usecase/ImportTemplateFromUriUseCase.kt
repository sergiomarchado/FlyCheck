package com.sergiom.flycheck.domain.usecase

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.File
import java.util.UUID
import java.util.zip.ZipInputStream
import javax.inject.Inject

class ImportTemplateFromUriUseCase @Inject constructor(
    private val json: Json
) {
    operator fun invoke(context: Context, uri: Uri): Result<CheckListTemplateModel> = runCatching {
        val name = context.contentResolver.query(uri, null, null, null, null)?.use { c ->
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (c.moveToFirst() && i >= 0) c.getString(i) else ""
        } ?: ""

        context.contentResolver.openInputStream(uri)?.use { inStream ->
            val isZip = name.endsWith(".zip", true) ||
                    (context.contentResolver.getType(uri)?.contains("zip") == true)

            if (!isZip) {
                val text = inStream.bufferedReader().readText()
                json.decodeFromString(CheckListTemplateModel.serializer(), text)
            } else {
                // 1) Extraer el ZIP a un directorio temporal
                val tempDir = File(context.cacheDir, "import_${UUID.randomUUID()}").apply { mkdirs() }
                val zis = ZipInputStream(BufferedInputStream(inStream))
                zis.use { zip ->
                    var entry = zip.nextEntry
                    var jsonFile: File? = null
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            val outFile = File(tempDir, entry.name)
                            outFile.parentFile?.mkdirs()
                            outFile.outputStream().use { zip.copyTo(it) }
                            if (entry.name.endsWith(".json", true)) jsonFile = outFile
                        }
                        entry = zip.nextEntry
                    }
                    val raw = jsonFile?.readText()
                        ?: error("ZIP sin JSON interno")
                    val templateRaw = json.decodeFromString(CheckListTemplateModel.serializer(), raw)

                    // 2) Remapear URIs relativas "images/..." a file://... en cache
                    fun mapRelToFileUri(rel: String?): String? {
                        if (rel.isNullOrBlank()) return rel
                        val f = File(tempDir, rel)
                        return if (f.exists()) f.toURI().toString() else rel
                    }

                    val mappedBlocks = templateRaw.blocks.map { block ->
                        if (block is CheckListBlock.SectionBlock) {
                            val mappedSection = block.section.copy(
                                blocks = block.section.blocks.map { inner ->
                                    if (inner is CheckListBlock.ItemBlock) {
                                        val it = inner.item
                                        inner.copy(item = it.copy(
                                            imageUri = mapRelToFileUri(it.imageUri),
                                        ))
                                    } else inner
                                }
                            )
                            CheckListBlock.SectionBlock(mappedSection)
                        } else block
                    }

                    return@use templateRaw.copy(
                        // logoUri puede llegar como Uri contextual; guardamos como string file:// si existe
                        logoUri = mapRelToFileUri(templateRaw.logoUri?.toString())?.toUri() ?: templateRaw.logoUri,
                        blocks = mappedBlocks
                    )
                }
            }
        } ?: error("No se pudo abrir el input stream")
    }
}
