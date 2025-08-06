package com.sergiom.flycheck.domain.export

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.domain.usecase.ExportChecklistZipUseCase
import com.sergiom.flycheck.domain.usecase.ExportTemplateUseCase
import com.sergiom.flycheck.ui.utils.NotificationHelper
import java.io.File
import javax.inject.Inject

class TemplateExporter @Inject constructor(
    private val exportJsonUseCase: ExportTemplateUseCase,
    private val exportZipUseCase: ExportChecklistZipUseCase
) {

    /**
     * Exporta la plantilla como archivo JSON privado (uso interno).
     */
    fun exportToPrivateJson(
        context: Context,
        template: CheckListTemplateModel
    ): Result<File> {
        return exportJsonUseCase(context, template)
    }

    /**
     * Exporta como archivo ZIP en Descargas, incluyendo imágenes + JSON.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    suspend fun exportChecklistAsZip(
        context: Context,
        template: CheckListTemplateModel
    ): Result<Unit> {
        return exportZipUseCase(context, template).map { (fileName, fileUri) ->
            NotificationHelper.showExportWithActionsNotification(
                context = context,
                fileName = fileName,
                fileUri = fileUri
            )
        }
    }


}
