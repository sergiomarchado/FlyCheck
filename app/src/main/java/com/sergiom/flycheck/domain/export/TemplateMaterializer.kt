package com.sergiom.flycheck.domain.export

import android.content.Context
import android.net.Uri
import com.sergiom.flycheck.data.storage.ImageStore
import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListItemModel
import com.sergiom.flycheck.data.models.CheckListSection
import com.sergiom.flycheck.data.models.CheckListTemplateModel

/**
 * Devuelve una copia del template donde cualquier content:// se ha copiado a almacenamiento propio
 * y las referencias pasan a ser file:///… (estables tras reiniciar).
 */
fun CheckListTemplateModel.materializeUris(context: Context): CheckListTemplateModel {
    val newLogo: Uri? = ImageStore.ensureLocalCopy(context, id, logoUri)
    val newBlocks = blocks.map { it.materialize(context, id) }
    return copy(logoUri = newLogo, blocks = newBlocks)
}

private fun CheckListBlock.materialize(context: Context, templateId: String): CheckListBlock = when (this) {
    is CheckListBlock.ItemBlock -> {
        val newItem: CheckListItemModel = item.materialize(context, templateId)
        copy(item = newItem)
    }
    is CheckListBlock.SubsectionBlock -> {
        val newSub = subsection.materialize(context, templateId)
        copy(subsection = newSub)
    }
    is CheckListBlock.SectionBlock -> {
        val newSec = section.materialize(context, templateId)
        copy(section = newSec)
    }
}

private fun CheckListSection.materialize(context: Context, templateId: String): CheckListSection {
    return copy(blocks = blocks.map { it.materialize(context, templateId) })
}

private fun CheckListItemModel.materialize(context: Context, templateId: String): CheckListItemModel {
    val newImageUri = imageUri?.let { ImageStore.ensureLocalCopy(context, templateId, it) }
    return copy(imageUri = newImageUri)
}
