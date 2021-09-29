package com.company.elverano.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.company.elverano.R
import com.company.elverano.databinding.FragmentSearchBinding
import com.company.elverano.utils.ResultEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    private var _binding : FragmentSearchBinding?=null
    private val binding get() = _binding
    private val viewModel by viewModels<SearchViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)


        binding?.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    if (query != null) {
                        searchProgressBar.visibility= View.VISIBLE
                        viewModel.searchLocation(query)
                        searchView.setQuery("", false)
                        searchView.clearFocus()
                        searchView.onActionViewCollapsed()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.resultEvent.collect { event ->
                when (event) {
                    is ResultEvent.Success -> {
                        Log.d("ResultEvent", "Success")
                        val action = SearchFragmentDirections.actionSearchFragmentToCurrentFragment()
                        findNavController().navigate(action)
                        binding?.searchProgressBar?.visibility= View.INVISIBLE
                    }
                    is ResultEvent.Error -> {
                        binding.apply {
                            binding?.searchProgressBar?.visibility= View.INVISIBLE
                        }
                        Log.d("ResultEvent", "Error")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}