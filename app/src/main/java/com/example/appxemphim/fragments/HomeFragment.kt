package com.example.appxemphim.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.adapters.MovieAdapter
import com.example.appxemphim.databinding.FragmentHomeBinding
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val movieViewModel by viewModels<MovieViewModel>()
    private lateinit var phimLeAdapter: MovieAdapter
    private lateinit var phimBoAdapter: MovieAdapter
    private lateinit var phimMoiAdapter: MovieAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        movieViewModel.loadPhimBo()
        movieViewModel.loadPhimLe()
        movieViewModel.loadPhimMoi()

        lifecycleScope.launchWhenStarted {
            movieViewModel.phimLe.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        phimLeAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }

            }


        }
        lifecycleScope.launchWhenStarted {
            movieViewModel.phimBo.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        phimBoAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }


        }
        lifecycleScope.launchWhenStarted {
            movieViewModel.phimMoi.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        phimMoiAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }

                    else -> {}
                }
            }


        }


        binding.tvPhimLe.setOnClickListener {
            var b: Bundle = Bundle()
            b.putInt("categoryId", 1)
            b.putString("categoryName", "Phim lẻ")
            val pageFragment = PageFragment()
            pageFragment.arguments = b
            (activity as MainActivity).replaceFragment(pageFragment)
        }
        binding.tvPhimBo.setOnClickListener {
            var b: Bundle = Bundle()
            b.putInt("categoryId", 2)
            b.putString("categoryName", "Phim bộ")
            val pageFragment = PageFragment()
            pageFragment.arguments = b
            (activity as MainActivity).replaceFragment(pageFragment)

        }
    }

    private fun initAdapter() {
        phimLeAdapter = MovieAdapter()
        binding.rvPhimLe.adapter = phimLeAdapter
        binding.rvPhimLe.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        phimLeAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movieId: Int) {
                var b: Bundle = Bundle()
                b.putInt("movieId", movieId)
                val phimFragment = PhimFragment()
                phimFragment.arguments = b
                (activity as MainActivity).replaceFragment(phimFragment)
            }

        })


        phimBoAdapter = MovieAdapter()
        binding.rvPhimBo.adapter = phimBoAdapter
        binding.rvPhimBo.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        phimBoAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movieId: Int) {
//                var b: Bundle= Bundle()
//                b.putInt("movieId",movieId)
//                findNavController().navigate(R.id.action_homeFragment_to_phimFragment,b)
                var b: Bundle = Bundle()
                b.putInt("movieId", movieId)
                val phimFragment = PhimFragment()
                phimFragment.arguments = b
                (activity as MainActivity).replaceFragment(phimFragment)

            }

        })
        phimMoiAdapter = MovieAdapter()
        binding.rvPhimMoi.adapter = phimMoiAdapter
        binding.rvPhimMoi.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        phimMoiAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
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