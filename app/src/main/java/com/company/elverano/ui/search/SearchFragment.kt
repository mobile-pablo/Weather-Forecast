package com.company.elverano.ui.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.company.elverano.R
import com.company.elverano.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    private var _binding : FragmentSearchBinding?=null
    private val binding get() = _binding
    private val viewModel by viewModels<SearchViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)


        binding?.apply {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}