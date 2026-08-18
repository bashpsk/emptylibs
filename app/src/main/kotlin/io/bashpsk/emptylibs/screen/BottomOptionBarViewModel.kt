package io.bashpsk.emptylibs.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.bashpsk.emptylibs.jetpackui.optionbar.OptionBarData.Companion.copy
import io.bashpsk.emptylibs.component.jetpackui.FileOperation
import io.bashpsk.emptylibs.component.jetpackui.ListData
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class BottomOptionBarViewModel : ViewModel() {

    private val _selectedPaths = MutableStateFlow(value = persistentListOf<String>())
    val selectedPaths = _selectedPaths.asStateFlow()

    val optionList = selectedPaths.flatMapLatest { paths ->

        val newOptionList = when (paths.size) {

            2 -> ListData.FILE_OPERATION_LIST.map { option ->

                option.copy(
                    enabled = option.label.startsWith(FileOperation.SelectFolders.label).not()
                )
            }.toImmutableList()

            3 -> ListData.FILE_OPERATION_LIST.filter { option ->

                option.label.startsWith("Select")
            }.toImmutableList()

            else -> ListData.FILE_OPERATION_LIST
        }

        flowOf(value = newOptionList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = persistentListOf()
    )

    val isPathSelect = selectedPaths.flatMapLatest { paths ->

        flowOf(value = paths.isNotEmpty())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun addPathSelection(path: String) {

        _selectedPaths.update { oldList -> oldList.adding(element = path) }
    }

    fun clearPathSelection() {

        _selectedPaths.update { persistentListOf() }
    }
}