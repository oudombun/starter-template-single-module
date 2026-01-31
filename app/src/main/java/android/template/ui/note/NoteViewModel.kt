/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.template.data.NoteRepository
import android.template.ui.note.NoteUiState.Error
import android.template.ui.note.NoteUiState.Loading
import android.template.ui.note.NoteUiState.Success
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    val uiState: StateFlow<NoteUiState> = noteRepository
        .notes.map<List<String>, NoteUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addNote(name: String) {
        viewModelScope.launch {
            noteRepository.add(name)
        }
    }

    /** Sync notes from remote API into local DB (official pattern: refresh from network). */
    fun refresh() {
        viewModelScope.launch {
            noteRepository.refreshFromApi()
        }
    }
}

sealed interface NoteUiState {
    object Loading : NoteUiState
    data class Error(val throwable: Throwable) : NoteUiState
    data class Success(val data: List<String>) : NoteUiState
}
