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
import com.example.appxemphim.adapters.MovieBoughtAdapter
import com.example.appxemphim.databinding.FragmentMovieBoughtBinding
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieBoughtFragment : Fragment() {
    private lateinit var binding : FragmentMovieBoughtBinding
    private val movieBoughtAdapter by lazy { MovieBoughtAdapter() }
    private val movieViewModel by viewModels<MovieViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMovieBoughtBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        movieViewModel.getAllMovieBought()
        lifecycleScope.launch {
            movieViewModel.tatCaPhimDaMua.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressBar.visibility=View.VISIBLE
                        binding.tvTrong.visibility=View.GONE
                    }
                    is Resource.Success ->{
                        movieBoughtAdapter.differ.submitList(it.data)
                        binding.progressBar.visibility=View.GONE
                        binding.tvTrong.visibility=View.GONE

                    }
                    is Resource.Error ->{
                        if(it.message.equals("404")){
                            binding.progressBar.visibility=View.GONE
                            binding.tvTrong.visibility=View.VISIBLE
                        }
                        else{
                            Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility=View.GONE
                            binding.tvTrong.visibility=View.GONE
                        }

                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun initAdapter() {
       binding.apply {
           rvPhim.layoutManager=GridLayoutManager(requireContext(),2)
           rvPhim.adapter= movieBoughtAdapter
           movieBoughtAdapter.setOnItemClickListener(object: MovieBoughtAdapter.OnItemClickListener{
               override fun onItemClick(movieId: Int) {
                   var b: Bundle = Bundle()
                   b.putInt("movieId", movieId)
                   val phimFragment = PhimFragment()
                   phimFragment.arguments = b
                   (activity as MainActivity).replaceFragment(phimFragment,"MOVIE")
               }
           })
       }
    }


}