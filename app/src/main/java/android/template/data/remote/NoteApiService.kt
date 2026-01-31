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

package android.template.data.remote

import retrofit2.http.GET

/**
 * Remote data source: talks only to the network.
 * Repository is the single entry point; UI and ViewModel never use this directly.
 *
 * Demo: uses JSONPlaceholder (https://jsonplaceholder.typicode.com).
 * Replace base URL and endpoints with your real API.
 */
interface NoteApiService {

    @GET("posts")
    suspend fun getNotes(): List<NoteDto>
}
