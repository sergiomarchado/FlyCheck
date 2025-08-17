// data/storage/ImageStore.kt
package com.sergiom.flycheck.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import java.io.File
import java.io.FileOutputStream

/**
 * Utilidad para gestionar imágenes de checklists en almacenamiento app-specific:
 *   .../Android/data/<pkg>/files/Pictures/checklists/<templateId>/images
 *
 * NOTA: Son operaciones de E/S. Lánzalas en Dispatchers.IO desde VM/UseCase.
 */
object ImageStore {

    /** Carpeta raíz de un template: .../Pictures/checklists/<templateId> */
    private fun templateRootDir(context: Context, templateId: String): File {
        val base = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(base, "checklists/$templateId").apply { mkdirs() }
    }

    /** Carpeta de imágenes: .../Pictures/checklists/<templateId>/images */
    fun imagesDir(context: Context, templateId: String): File {
        return File(templateRootDir(context, templateId), "images").apply { mkdirs() }
    }

    /**
     * Si [src] es content://, lo copiamos a carpeta propia y devolvemos un file:// estable.
     * Si ya es file:// (o una ruta) lo devolvemos tal cual.
     */
    fun ensureLocalCopy(context: Context, templateId: String, src: String): String {
        if (src.isBlank()) return src
        return try {
            val uri = src.toUri()
            if (uri.scheme.equals("content", ignoreCase = true)) {
                val dstFile = copyContentUri(context, templateId, uri)
                Uri.fromFile(dstFile).toString() // "file:///..."
            } else {
                src
            }
        } catch (_: Throwable) {
            src // ante error no rompemos el flujo
        }
    }

    /** Variante para logoUri (Uri?). */
    fun ensureLocalCopy(context: Context, templateId: String, src: Uri?): Uri? {
        if (src == null) return null
        return try {
            if (src.scheme.equals("content", ignoreCase = true)) {
                val dstFile = copyContentUri(context, templateId, src)
                Uri.fromFile(dstFile)
            } else src
        } catch (_: Throwable) {
            src
        }
    }

    /**
     * Asegura copias locales para todas las imágenes de un template usando el **id interno** del template.
     * Útil cuando el id (UUID) coincide con el “namespace” de imágenes.
     */
    fun ensureLocalCopies(context: Context, template: CheckListTemplateModel): CheckListTemplateModel {
        return ensureLocalCopies(context, template.id, template)
    }

    /**
     * Asegura copias locales para todas las imágenes de un template usando un **templateId** externo
     * (por ejemplo, el nombre de archivo sin extensión). Recomendado para repos basados en ficheros.
     */
    fun ensureLocalCopies(
        context: Context,
        templateId: String,
        template: CheckListTemplateModel
    ): CheckListTemplateModel {
        val newLogo = ensureLocalCopy(context, templateId, template.logoUri)
        val newBlocks = rewriteBlocks(context, templateId, template.blocks)
        return template.copy(
            logoUri = newLogo,
            blocks = newBlocks
        )
    }

    /** Elimina todas las imágenes locales (y subcarpetas) de una checklist. */
    fun deleteTemplateImages(context: Context, templateId: String): Boolean {
        return try {
            val dir = templateRootDir(context, templateId)
            if (dir.exists()) dir.deleteRecursively() else true
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Renombra/mueve la carpeta de imágenes de un template:
     * .../Pictures/checklists/<oldId> → .../Pictures/checklists/<newId>
     */
    fun moveTemplateImages(context: Context, oldId: String, newId: String) {
        if (oldId == newId) return
        val base = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val from = File(base, "checklists/$oldId")
        val to = File(base, "checklists/$newId")
        if (!from.exists()) return
        to.parentFile?.mkdirs()
        if (!from.renameTo(to)) {
            // Fallback si el rename falla (distintos FS, etc.)
            from.copyRecursively(target = to, overwrite = true)
            from.deleteRecursively()
        }
    }

    // ---------------- Internos ----------------

    private fun rewriteBlocks(
        context: Context,
        templateId: String,
        blocks: List<CheckListBlock>
    ): List<CheckListBlock> {
        return blocks.map { b ->
            when (b) {
                is CheckListBlock.ItemBlock -> {
                    val item = b.item
                    val newImageUri = item.imageUri?.let { ensureLocalCopy(context, templateId, it) }
                    b.copy(item = item.copy(imageUri = newImageUri))
                }
                is CheckListBlock.SubsectionBlock -> {
                    val sub = b.subsection
                    b.copy(subsection = sub.copy(blocks = rewriteBlocks(context, templateId, sub.blocks)))
                }
                is CheckListBlock.SectionBlock -> {
                    val sec = b.section
                    b.copy(section = sec.copy(blocks = rewriteBlocks(context, templateId, sec.blocks)))
                }
            }
        }
    }

    private fun copyContentUri(context: Context, templateId: String, uri: Uri): File {
        val dir = imagesDir(context, templateId)

        val ext = guessExtension(context.contentResolver, uri)
            ?: guessExtensionFromDisplayName(context, uri)
            ?: "jpg"

        val name = System.currentTimeMillis().toString() + "." + ext
        val dst = File(dir, name)

        context.contentResolver.openInputStream(uri).use { inStream ->
            FileOutputStream(dst).use { out ->
                inStream?.copyTo(out) ?: throw IllegalStateException("No InputStream for $uri")
            }
        }
        return dst
    }

    private fun guessExtension(cr: ContentResolver, uri: Uri): String? {
        val type = cr.getType(uri) ?: return null
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
    }

    private fun guessExtensionFromDisplayName(context: Context, uri: Uri): String? {
        return try {
            val doc = DocumentFile.fromSingleUri(context, uri)
            val name = doc?.name ?: return null
            val dot = name.lastIndexOf('.')
            if (dot in 0 until name.lastIndex) name.substring(dot + 1) else null
        } catch (_: Throwable) {
            null
        }
    }
}
