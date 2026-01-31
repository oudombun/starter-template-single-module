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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import android.template.databinding.FragmentNoteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()

    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NoteAdapter()
        binding.recyclerNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotes.adapter = adapter

        binding.buttonSave.setOnClickListener {
            val name = binding.inputName.text?.toString()?.trim()
            if (!name.isNullOrEmpty()) {
                viewModel.addNote(name)
                binding.inputName.text?.clear()
            }
        }

        binding.buttonSync.setOnClickListener { viewModel.refresh() }

        // Optional: sync from API once when screen loads
        viewModel.refresh()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is NoteUiState.Loading -> { /* show loading if needed */ }
                        is NoteUiState.Error -> { /* show error if needed */ }
                        is NoteUiState.Success -> adapter.submitList(state.data)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
