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

package android.template.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.template.data.local.database.Note
import android.template.data.local.database.NoteDao
import android.template.data.remote.NoteApiService
import android.util.Log
import javax.inject.Inject

/**
 * Repository: single entry point to the data layer (official Android pattern).
 *
 * - Exposes data from one place (Room = source of truth).
 * - Coordinates local (Room) and remote (API) data sources.
 * - UI and ViewModel never talk to NoteDao or NoteApiService directly.
 *
 * Flow: UI reads from Room via [notes]. Sync from API via [refreshFromApi] (writes to Room).
 */
interface NoteRepository {
    /** Notes from local DB (single source of truth). */
    val notes: Flow<List<String>>

    /** Save a note locally. */
    suspend fun add(name: String)

    /** Fetch from remote API and save into Room. Call on refresh or app start. */
    suspend fun refreshFromApi()
}

class DefaultNoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApiService: NoteApiService
) : NoteRepository {

    override val notes: Flow<List<String>> =
        noteDao.getNotes().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        noteDao.insertNote(Note(name = name))
    }

    override suspend fun refreshFromApi() {
        try {
            val dtos = noteApiService.getNotes()
            val entities = dtos.map { Note(name = it.title) }
            entities.forEach { noteDao.insertNote(it) }
        } catch (e: Exception) {
            Log.e(TAG, "refreshFromApi failed", e)
            // Don't crash; UI keeps showing local data
        }
    }

    companion object {
        private const val TAG = "NoteRepository"
    }
}
