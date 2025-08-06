package com.sergiom.flycheck.domain.usecase

import android.content.Context
import android.os.Environment
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.ui.utils.JsonUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExportTemplateUseCase @Inject constructor() {

    operator fun invoke (context: Context, template: CheckListTemplateModel): Result<File> {
        return try {
            val jsonString = JsonUtils.json.encodeToString(template)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "plantilla_${template.name.ifBlank { "sin_nombre" }}_$timestamp.json"

            // Carpeta segura externa
            val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (exportDir != null && !exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, fileName)
            file.writeText(jsonString)

            Result.success(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
