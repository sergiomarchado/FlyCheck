package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import com.sergiom.flycheck.viewmodel.TemplateEditorViewModel

@Composable
fun SectionList(
    template: CheckListTemplateModel,
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    onRename: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    viewModel: TemplateEditorViewModel
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {

        header?.let {
            item { it() }
        }

        template.sections.forEachIndexed { index, section ->

            stickyHeader(key = "header_$index") {
                CheckListSectionHeader(
                    title = section.title,
                    onRenameClick = { onRename(section.id, section.title) },
                    onDeleteClick = { onDelete(section.id) }
                )
            }

            item(key = "editor_$index") {
                CheckListSectionEditor(
                    sectionId = section.id,
                    title = section.title,
                    items = section.items,
                    onTitleChange = { viewModel.updateSectionTitle(section.id, it) },
                    onAddItem = { title, action ->
                        viewModel.addItemToSection(section.id, title, action)
                    },
                    onToggleItemChecked = { itemId ->
                        viewModel.toggleItemCompleted(section.id, itemId)
                    },
                    onItemTitleChange = { itemId, newTitle ->
                        viewModel.updateItemTitle(section.id, itemId, newTitle)
                    },
                    onItemActionChange = { itemId, newAction ->
                        viewModel.updateItemAction(section.id, itemId, newAction)
                    },
                    onDeleteItem = { itemId ->
                        viewModel.deleteItemFromSection(section.id, itemId)
                    }
                )
            }
        }
    }
}
