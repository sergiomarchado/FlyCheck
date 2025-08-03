package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import com.sergiom.flycheck.data.model.FlatBlock
import com.sergiom.flycheck.data.model.RenameTargetType
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.item.ItemCardEntry
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.titlesection.CheckListSectionTitleCard
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.subsection.SubsectionCardEntry
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils.SectionAddControls
import com.sergiom.flycheck.util.toFlatBlockListWithIndices

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

    // LazyColumn para renderizar la lista de bloques planos de forma eficiente
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        // espacio inferior para evitar solapamiento con posibles FABs o barras
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {

        // Renderizado de cada tipo de bloque según su tipo concreto (clave única por ID)
        itemsIndexed(flatBlocksWithIndices, key = { _, meta ->
            when (val block = meta.block) {
                is FlatBlock.SectionHeader -> block.section.id
                is FlatBlock.Subsection -> block.subsectionBlock.subsection.id
                is FlatBlock.Item -> block.itemBlock.item.id

                // Clave pseudo-única para controles de adición
                is FlatBlock.AddControls -> "add-${block.sectionId}"
            }
        }) { _, meta ->
            when (val block = meta.block) {
                is FlatBlock.SectionHeader -> {

                    // CABECERA de la sección con opciones para renombrar o eliminar
                    CheckListSectionTitleCard(
                        title = block.section.title,
                        onRenameClick = {
                            onRename(block.section.id, block.section.title, RenameTargetType.SECTION)
                        },
                        onDeleteClick = {
                            onDelete(block.section.id)
                        }
                    )
                }

                is FlatBlock.Subsection -> {
                    // SUBSECCIÓN: renderiza subsecciones y delega comportamiento
                    SubsectionCardEntry(
                        meta = meta,
                        viewModel = viewModel,
                        onRename = onRename
                    )
                }

                is FlatBlock.Item -> {
                    // ITEMS: renderiza items con acciones propias
                    ItemCardEntry(
                        meta = meta,
                        viewModel = viewModel
                    )
                }

                is FlatBlock.AddControls -> {
                    // BOTONES para añadir ítems o subsecciones
                    SectionAddControls(
                        sectionId = block.sectionId,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
