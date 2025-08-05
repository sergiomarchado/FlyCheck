package com.sergiom.flycheck.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import com.sergiom.flycheck.util.JsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import androidx.core.net.toUri
import javax.inject.Inject

class ExportChecklistZipUseCase @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend operator fun invoke(context: Context, template: CheckListTemplateModel): Result<Pair<String, Uri>> {
        return withContext(Dispatchers.IO) {
            try {
                // Crear carpeta temporal
                val tempDir = File(context.cacheDir, "export_${UUID.randomUUID()}")
                tempDir.mkdirs()

                val imagesDir = File(tempDir, "images").apply { mkdirs() }

                // Copiar imágenes y actualizar URIs
                val updatedTemplate = template.copy(
                    logoUri = template.logoUri?.let { uri ->
                        val dest = File(imagesDir, "logo.jpg")
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            dest.outputStream().use { output -> input.copyTo(output) }
                        }
                        "images/logo.jpg".toUri()
                    },
                    blocks = template.blocks.map { block ->
                        if (block is CheckListBlock.SectionBlock) {
                            block.copy(
                                section = block.section.copy(
                                    blocks = block.section.blocks.map { inner ->
                                        if (inner is CheckListBlock.ItemBlock) {
                                            val item = inner.item
                                            val imageUri = item.imageUri
                                            if (!imageUri.isNullOrBlank()) {
                                                val destFile = File(imagesDir, "item_${item.id}.jpg")
                                                val parsedUri = imageUri.toUri()
                                                context.contentResolver.openInputStream(parsedUri)?.use { input ->
                                                    destFile.outputStream().use { output -> input.copyTo(output) }
                                                }
                                                inner.copy(
                                                    item = item.copy(imageUri = "images/item_${item.id}.jpg")
                                                )
                                            } else inner
                                        } else inner
                                    }
                                )
                            )
                        } else block
                    }
                )

                // Serializar JSON
                val jsonFile = File(tempDir, "checklist.json")
                jsonFile.writeText(JsonUtils.json.encodeToString(CheckListTemplateModel.serializer(), updatedTemplate))

                // Crear ZIP
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
                val zipFileName = "checklist_${template.name.ifBlank { "sin_nombre" }}_$timeStamp.zip"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, zipFileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/zip")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val downloadsUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val resolver = context.contentResolver
                val fileUri = resolver.insert(downloadsUri, contentValues)
                    ?: return@withContext Result.failure(Exception("No se pudo crear el archivo ZIP"))

                resolver.openOutputStream(fileUri)?.use { outputStream ->
                    ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                        tempDir.walkTopDown().forEach { file ->
                            if (file.isFile) {
                                val entryName = file.relativeTo(tempDir).path.replace("\\", "/")
                                zipOut.putNextEntry(ZipEntry(entryName))
                                file.inputStream().use { it.copyTo(zipOut) }
                                zipOut.closeEntry()
                            }
                        }
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(fileUri, contentValues, null, null)

                tempDir.deleteRecursively()
                Result.success(zipFileName to fileUri)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
