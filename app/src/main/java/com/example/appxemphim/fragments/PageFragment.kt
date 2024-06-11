package com.example.appxemphim.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.activities.ThanhToanActivity
import com.example.appxemphim.adapters.MovieAdapter
import com.example.appxemphim.adapters.PageAdapter
import com.example.appxemphim.databinding.DialogBuyMovieBinding
import com.example.appxemphim.databinding.FragmentPageBinding
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PageFragment : Fragment() {
    private lateinit var binding: FragmentPageBinding
    private val movieViewModel by viewModels<MovieViewModel>()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var pageAdapter: PageAdapter
    private var page = 0
    private var categoryId = 0
    private var categoryName = ""
    private lateinit var selectedMovie: Movie
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        getBundle()
        binding.tvTheLoai.text=categoryName
        movieViewModel.loadTrangPhim(0, categoryId)
        movieViewModel.loadTatCaPhimTheoTheLoai(categoryId)
        lifecycleScope.launch {
            movieViewModel.trangPhim.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        movieAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }


            }


        }
        lifecycleScope.launch {
            movieViewModel.tatCaPhimtheoTheLoai.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        pageAdapter.differ.submitList(getListPage(it.data!!.size/9))
                        Log.e("SIZE", (it.data!!.size/9).toString())
                        Log.e("SIZE", (it.data!!.size).toString())
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }


            }


        }

        lifecycleScope.launch {
            movieViewModel.existBuyMovie.collectLatest {
                when (it) {

                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (!it.data!!) {
                            openBuyMovieDialog(selectedMovie)
                        } else {
                            var b: Bundle = Bundle()
                            b.putInt("movieId", selectedMovie.movieId)
                            val phimFragment = PhimFragment()
                            phimFragment.arguments = b
                            (activity as MainActivity).replaceFragment(phimFragment,"MOVIE")
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }


    }

    private fun initAdapter() {
        movieAdapter = MovieAdapter()
        binding.rvPhim.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)

        }
        movieAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movie: Movie,price: Int) {
                checkMovie(movie, price)
            }


        })


        pageAdapter= PageAdapter()
        binding.rvTrang.apply {
            adapter= pageAdapter
            layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        }
        pageAdapter.setOnItemClickListener(object : PageAdapter.OnItemClickListener{
            override fun onItemClick(page: Int) {
               movieViewModel.loadTrangPhim(page,categoryId)
            }
        })

    }

    private fun getBundle() {
        val bundle = arguments
        if (bundle != null) {
            categoryId = bundle.getInt("categoryId", 0)
            categoryName = bundle.getString("categoryName", "")
        }
    }


    fun openBuyMovieDialog(movie: Movie) {
        val dialogBuyMovieBinding: DialogBuyMovieBinding =
            DialogBuyMovieBinding.inflate(layoutInflater)

        val mDialog =
            AlertDialog.Builder(activity).setView(dialogBuyMovieBinding.root)
                .create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuyMovieBinding.tvMoneyPrice.text = movie.price.toString()
        mDialog.show()
        // mDialog.dismiss()


        dialogBuyMovieBinding.btnConfirm.setOnClickListener {
            val intent = Intent(requireActivity(), ThanhToanActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
            mDialog.dismiss()
        }
        dialogBuyMovieBinding.btnHuy.setOnClickListener {
            mDialog.dismiss()
        }
    }


    fun checkMovie(movie: Movie, price: Int) {
        selectedMovie = movie


        if (price > 0) {
            movieViewModel.checkExistMovieBuy(selectedMovie)

        } else {
            var b: Bundle = Bundle()
            b.putInt("movieId", selectedMovie.movieId)
            val phimFragment = PhimFragment()
            phimFragment.arguments = b
            (activity as MainActivity).replaceFragment(phimFragment,"MOVIE")
        }

    }



    private fun getListPage(number:Int): List<Int>{
        var pageList: MutableList<Int> = ArrayList<Int>()
        for (i in 0 .. number)
            pageList.add(i)

        return pageList
    }

}