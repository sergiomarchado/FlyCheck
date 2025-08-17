package com.sergiom.flycheck.domain.usecase

import android.content.Context
import android.os.Environment
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.domain.export.materializeUris
import com.sergiom.flycheck.ui.utils.JsonUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExportTemplateUseCase @Inject constructor() {

    operator fun invoke(context: Context, template: CheckListTemplateModel): Result<File> = runCatching {
        // 1) Materializar URIs (content:// -> file:/// en sandbox de la app)
        val materialized = template.materializeUris(context)

        // 2) Serializar
        val jsonString = JsonUtils.json.encodeToString(materialized)

        // 3) Guardar en app-specific external documents
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val safeName = template.name.ifBlank { "sin_nombre" }
        val fileName = "plantilla_${safeName}_$timestamp.json"

        val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?.apply { if (!exists()) mkdirs() }
            ?: throw IllegalStateException("No external documents dir")

        val file = File(exportDir, fileName)
        file.writeText(jsonString)
        file
    }
}
