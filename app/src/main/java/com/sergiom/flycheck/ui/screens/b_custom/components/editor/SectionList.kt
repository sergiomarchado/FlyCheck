package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.item.CheckListItemEditor
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.section.CheckListSectionTitleCard
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.subsection.SubsectionEditor

@Composable
fun SectionList(
    template: CheckListTemplateModel,
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    onRename: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    viewModel: TemplateEditorViewModel
) {
    // ✅ Manejamos el estado por sección (clave = section.id)
    val subsectionForms = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        header?.let {
            item { it() }
        }

        items(template.blocks) { block ->
            if (block is CheckListBlock.SectionBlock) {
                val section = block.section
                val showSubsectionForm = subsectionForms[section.id] ?: false

                Column {
                    CheckListSectionTitleCard(
                        title = section.title,
                        onRenameClick = { onRename(section.id, section.title) },
                        onDeleteClick = { onDelete(section.id) }
                    )

                    CheckListItemEditor(
                        sectionId = section.id,
                        title = section.title,
                        blocks = section.blocks,
                        onTitleChange = { viewModel.updateSectionTitle(section.id, it) },
                        onAddItem = { title, action -> viewModel.addItem(section.id, title, action) },
                        onToggleItemChecked = { itemId -> viewModel.toggleItemCompleted(section.id, itemId) },
                        onItemTitleChange = { itemId, newTitle -> viewModel.updateItem(section.id, itemId, newTitle, "") },
                        onItemActionChange = { itemId, newAction -> viewModel.updateItem(section.id, itemId, "", newAction) },
                        onDeleteItem = { itemId -> viewModel.deleteItem(section.id, itemId) },
                        onAddSubsection = { subsectionForms[section.id] = true } // ✅ Aquí activamos el formulario
                    )

                    SubsectionEditor(
                        parentSectionId = section.id,
                        blocks = section.blocks,
                        showFormExternally = showSubsectionForm,
                        onDismissForm = { subsectionForms[section.id] = false },
                        onAddSubsection = { title ->
                            val added = viewModel.addSubsection(section.id, title)
                            if (added) subsectionForms[section.id] = false
                            added
                        },
                        onRenameSubsection = { subId, newTitle -> viewModel.updateSectionTitle(subId, newTitle) },
                        onDeleteSubsection = { subId -> viewModel.deleteSubsection(section.id, subId) }
                    )
                }
            }
        }
    }
}
