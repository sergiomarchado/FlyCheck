// domain/usecase/ListLocalExportedTemplatesUseCase.kt
package com.sergiom.flycheck.domain.usecase

import android.content.Context
import android.os.Environment
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class ListLocalExportedTemplatesUseCase @Inject constructor(
    private val json: Json
) {
    operator fun invoke(context: Context): Result<List<CheckListTemplateModel>> = runCatching {
        val dir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        dir?.listFiles { f -> f.isFile && f.extension.equals("json", true) }
            ?.sortedByDescending { it.lastModified() }
            ?.mapNotNull { runCatching {
                json.decodeFromString(CheckListTemplateModel.serializer(), it.readText())
            }.getOrNull() }
            .orEmpty()
    }
}

class LoadLastLocalExportedTemplateUseCase @Inject constructor(
    private val lister: ListLocalExportedTemplatesUseCase
) {
    operator fun invoke(context: Context): Result<CheckListTemplateModel> =
        lister(context).mapCatching { list -> list.firstOrNull() ?: error("No hay JSON locales") }
}
