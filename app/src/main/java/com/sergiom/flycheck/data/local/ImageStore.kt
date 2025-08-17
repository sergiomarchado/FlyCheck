package com.sergiom.flycheck.data.local

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri

object ImageStore {

    /**
     * Carpeta de imágenes por checklist: .../Android/data/<pkg>/files/Pictures/checklists/<templateId>/images
     */
    fun imagesDir(context: Context, templateId: String): File {
        val base = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return File(base, "checklists/$templateId/images").apply { mkdirs() }
    }

    /**
     * Si [src] es content://, lo copiamos a carpeta propia y devolvemos un Uri file:// estable.
     * Si ya es file:// o una ruta absoluta, la devolvemos tal cual.
     */
    fun ensureLocalCopy(context: Context, templateId: String, src: String): String {
        return try {
            val uri = src.toUri()
            if (uri.scheme?.equals("content", ignoreCase = true) == true) {
                val dstFile = copyContentUri(context, templateId, uri)
                dstFile.toURI().toString() // => "file:/…"
            } else {
                // file:// o ruta absoluta -> la dejamos
                src
            }
        } catch (_: Throwable) {
            src // ante error, no rompemos
        }
    }

    /**
     * Variante para logoUri (Uri?).
     */
    fun ensureLocalCopy(context: Context, templateId: String, src: Uri?): Uri? {
        if (src == null) return null
        return try {
            if (src.scheme?.equals("content", ignoreCase = true) == true) {
                val dstFile = copyContentUri(context, templateId, src)
                Uri.fromFile(dstFile)
            } else src
        } catch (_: Throwable) {
            src
        }
    }

    private fun copyContentUri(context: Context, templateId: String, uri: Uri): File {
        val dir = imagesDir(context, templateId)
        val ext = guessExtension(context.contentResolver, uri) ?: "jpg"
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
}
