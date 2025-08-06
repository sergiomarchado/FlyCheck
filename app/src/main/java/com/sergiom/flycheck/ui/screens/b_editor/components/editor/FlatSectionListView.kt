package com.sergiom.flycheck.ui.screens.b_editor.components.editor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.data.models.FlatBlock
import com.sergiom.flycheck.data.models.RenameTargetType
import com.sergiom.flycheck.presentation.viewmodel.editor.TemplateEditorViewModel
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.header.EditorHeaderMain
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.ItemCardEntry
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.subsection.SubsectionCardEntry
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.section.CheckListSectionTitleCard
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.utils.SectionAddControls
import com.sergiom.flycheck.ui.utils.toFlatBlockListWithIndices

/**
 * Composable principal que renderiza todos los elementos de un checklist (secciones, subsecciones, ítems, etc.)
 * en formato plano y ordenado, utilizando una LazyColumn para rendimiento eficiente.
 *
 * @param template Modelo completo del checklist, que contiene bloques jerárquicos.
 * @param viewModel ViewModel que gestiona la lógica del editor.
 * @param modifier Modificador de estilo externo.
 * @param onRename Callback para renombrar secciones o subsecciones.
 * @param onDelete Callback para eliminar secciones completas.
 */
@Composable
fun FlatSectionListView(
    template: CheckListTemplateModel,
    viewModel: TemplateEditorViewModel,
    modifier: Modifier = Modifier,
    onRename: (String, String, RenameTargetType) -> Unit,
    onDelete: (String) -> Unit
) {
    // Se obtiene una versión plana de todos los bloques jerárquicos (con sus índices locales)
    val flatBlocksWithIndices = remember(template) {
        template.blocks.toFlatBlockListWithIndices()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // CABECERA: información básica de la plantilla (no sticky)
        item {
            EditorHeaderMain(template)
        }

        // Itera manualmente los bloques planos
        flatBlocksWithIndices.forEachIndexed { _, meta ->
            when (val block = meta.block) {
                is FlatBlock.SectionHeader -> {
                    stickyHeader(key = block.section.id) {
                        CheckListSectionTitleCard(
                            title = block.section.title,
                            onRenameClick = {
                                onRename(block.section.id, block.section.title, RenameTargetType.SECTION)
                            },
                            onDeleteClick = {
                                onDelete(block.section.id)
                            },
                        )
                    }
                }

                is FlatBlock.Subsection -> {
                    item(key = block.subsectionBlock.subsection.id) {
                        SubsectionCardEntry(
                            meta = meta,
                            viewModel = viewModel,
                            onRename = onRename
                        )
                    }
                }

                is FlatBlock.Item -> {
                    item(key = block.itemBlock.item.id) {
                        ItemCardEntry(
                            meta = meta,
                            viewModel = viewModel
                        )
                    }
                }

                is FlatBlock.AddControls -> {
                    item(key = "add-${block.sectionId}") {
                        SectionAddControls(
                            sectionId = block.sectionId,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.addSection() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Añadir sección")
            }
        }
    }
}
