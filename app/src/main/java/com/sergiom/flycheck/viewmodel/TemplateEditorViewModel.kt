package com.sergiom.flycheck.viewmodel

import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.data.model.CheckListItemModel
import com.sergiom.flycheck.data.model.CheckListSection
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class TemplateEditorViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(CheckListTemplateModel())

    val uiState: StateFlow<CheckListTemplateModel> = _uiState

    fun setTemplateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun setAircraftModel(model: String) {
        _uiState.update { it.copy(aircraftModel = model) }
    }

    fun setAirline(name: String) {
        _uiState.update { it.copy(airline = name) }
    }

    fun toggleIncludeLogo() {
        _uiState.update { it.copy(includeLogo = !it.includeLogo) }
    }

    fun addSection(title: String): Boolean {
        val trimmedTitle = title.trim()
        val exists = _uiState.value.sections.any { it.title.equals(trimmedTitle, ignoreCase = true) }

        return if (trimmedTitle.isNotEmpty() && !exists) {
            val newSection = CheckListSection(title = trimmedTitle)
            _uiState.value = _uiState.value.copy(
                sections = _uiState.value.sections + newSection
            )
            true
        } else {
            false
        }
    }

    fun deleteSection(sectionId: String) {
        _uiState.update { it -> it.copy(sections = it.sections.filterNot { it.id == sectionId }) }
    }

    fun addItemToSection(sectionId: String, title: String, action: String = "", colorHex: String = "#ECECEC"): Boolean {
        val trimmedTitle = title.trim()
        var success = false

        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.map { section ->
                if (section.id == sectionId) {
                    val exists = section.items.any { it.title.equals(trimmedTitle, ignoreCase = true) }

                    if (trimmedTitle.isNotEmpty() && !exists) {
                        success = true
                        section.copy(
                            items = section.items + CheckListItemModel(
                                title = trimmedTitle,
                                action = action,
                                backgroundColorHex = colorHex
                            )
                        )
                    } else {
                        section
                    }
                } else section
            }
        )

        return success
    }

    fun deleteItemFromSection(sectionId: String, itemId: String) {
        _uiState.update { template ->
            template.copy(
                sections = template.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(items = section.items.filterNot { it.id == itemId })
                    } else section
                }
            )
        }
    }

    fun toggleItemCompleted(sectionId: String, itemId: String) {
        _uiState.update { template ->
            template.copy(
                sections = template.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(
                            items = section.items.map { item ->
                                if (item.id == itemId) item.copy(completed = !item.completed) else item
                            }
                        )
                    } else section
                }
            )
        }
    }

    fun updateItemTitle(sectionId: String, itemId: String, newTitle: String) {
        _uiState.update { template ->
            template.copy(
                sections = template.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(
                            items = section.items.map { item ->
                                if (item.id == itemId) item.copy(title = newTitle) else item
                            }
                        )
                    } else section
                }
            )
        }
    }

    fun updateItemAction(sectionId: String, itemId: String, newAction: String) {
        _uiState.update { template ->
            template.copy(
                sections = template.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(
                            items = section.items.map { item ->
                                if (item.id == itemId) item.copy(action = newAction) else item
                            }
                        )
                    } else section
                }
            )
        }
    }

    fun updateItemColor(sectionId: String, itemId: String, newColorHex: String) {
        _uiState.update { template ->
            template.copy(
                sections = template.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(
                            items = section.items.map { item ->
                                if (item.id == itemId) item.copy(backgroundColorHex = newColorHex) else item
                            }
                        )
                    } else section
                }
            )
        }
    }

    fun updateSectionTitle(sectionId: String, newTitle: String): Boolean {
        val trimmedTitle = newTitle.trim()
        val titleExists = _uiState.value.sections.any { it.id != sectionId && it.title.equals(trimmedTitle, ignoreCase = true) }

        return if (trimmedTitle.isNotEmpty() && !titleExists) {
            _uiState.update { template ->
                template.copy(
                    sections = template.sections.map { section ->
                        if (section.id == sectionId) section.copy(title = trimmedTitle) else section
                    }
                )
            }
            true
        } else {
            false
        }
    }

    fun initializeTemplate(
        name: String,
        model: String,
        airline: String,
        includeLogo: Boolean,
        sectionCount: Int
    ) {
        if (_uiState.value.sections.isNotEmpty()) return

        val sections = List(sectionCount) { index ->
            CheckListSection(title = "Section ${index + 1}")
        }

        _uiState.value = CheckListTemplateModel(
            name = name,
            aircraftModel = model,
            airline = airline,
            includeLogo = includeLogo,
            sections = sections
        )
    }
}