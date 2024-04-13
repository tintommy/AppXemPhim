package com.example.appxemphim.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.adapters.CollectionAdapter
import com.example.appxemphim.databinding.FragmentSavedMovieBinding
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.CollectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedMovieFragment : Fragment() {
    private lateinit var binding: FragmentSavedMovieBinding

    private lateinit var collectionAdapter: CollectionAdapter
    private val collectionViewModel by viewModels<CollectionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedMovieBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        collectionViewModel.layTatCaPhimDaLuu()
        lifecycleScope.launch {
            collectionViewModel.tatCaPhim.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        collectionAdapter.differ.submitList(it.data)

                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}

                }
            }
        }

    }

    private fun initAdapter() {
        collectionAdapter = CollectionAdapter()
        binding.rvPhim.adapter = collectionAdapter
        binding.rvPhim.layoutManager = GridLayoutManager(requireContext(), 2)
        collectionAdapter.setOnItemClickListener(object : CollectionAdapter.OnItemClickListener{
            override fun onItemClick(movieId: Int) {
                var b: Bundle = Bundle()
                b.putInt("movieId", movieId)
                val phimFragment = PhimFragment()
                phimFragment.arguments = b
                (activity as MainActivity).replaceFragment(phimFragment)
            }
        })

    }

}