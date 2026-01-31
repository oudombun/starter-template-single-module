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

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object) for the remote API.
 * Kept separate from the Room [android.template.data.local.database.Note] entity
 * so that API shape changes don't affect the local database schema.
 *
 * This shape matches JSONPlaceholder /posts (used for demo).
 * Replace with your own API's response shape when you have a real backend.
 */
data class NoteDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String? = null,
    @SerializedName("userId") val userId: Int? = null
)
