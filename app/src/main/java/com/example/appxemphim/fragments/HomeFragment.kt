package com.example.appxemphim.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.activities.ThanhToanActivity
import com.example.appxemphim.adapters.MovieAdapter
import com.example.appxemphim.databinding.DialogBuyMovieBinding
import com.example.appxemphim.databinding.FragmentHomeBinding
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.MovieViewModel
import com.example.appxemphim.zalopay.CreateOrder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val movieViewModel by viewModels<MovieViewModel>()
    private lateinit var phimLeAdapter: MovieAdapter
    private lateinit var phimBoAdapter: MovieAdapter
    private lateinit var phimMoiAdapter: MovieAdapter
    private lateinit var selectedMovie: Movie
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // ZaloPay SDK Init
        ZaloPaySDK.init(2554, Environment.SANDBOX)
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


        lifecycleScope.launch {
            movieViewModel.existBuyMovie.collectLatest {
                when (it) {

                    is Resource.Loading -> {
                        binding.progressBar.visibility= View.VISIBLE

                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility= View.GONE
                        if(!it.data!!){
                           openBuyMovieDialog(selectedMovie)
                        }
                        else{
                            var b: Bundle = Bundle()
                            b.putInt("movieId", selectedMovie.movieId)
                            val phimFragment = PhimFragment()
                            phimFragment.arguments = b
                            (activity as MainActivity).replaceFragment(phimFragment)
                        }
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


            CoroutineScope(Dispatchers.IO).launch {
                if (movieViewModel.performTask()){
                    withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "abc", Toast.LENGTH_SHORT).show()
                        var b: Bundle = Bundle()
                        b.putInt("categoryId", 2)
                        b.putString("categoryName", "Phim bộ")
                        val pageFragment = PageFragment()
                        pageFragment.arguments = b
                        (activity as MainActivity).replaceFragment(pageFragment)
                }}
            }


        }

    }

    private fun initAdapter() {
        phimLeAdapter = MovieAdapter()
        binding.rvPhimLe.adapter = phimLeAdapter
        binding.rvPhimLe.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        phimLeAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movie: Movie, price: Int) {
                checkMovie(movie, price)
            }

        })


        phimBoAdapter = MovieAdapter()
        binding.rvPhimBo.adapter = phimBoAdapter
        binding.rvPhimBo.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        phimBoAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movie: Movie, price: Int) {
                checkMovie(movie, price)
            }

        })
        phimMoiAdapter = MovieAdapter()
        binding.rvPhimMoi.adapter = phimMoiAdapter
        binding.rvPhimMoi.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        phimMoiAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movie: Movie, price: Int) {

                checkMovie(movie, price)
            }

        })

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
    }


    fun checkMovie(movie: Movie, price: Int) {
        selectedMovie=movie


        if(price>0)
        {
        movieViewModel.checkExistMovieBuy(selectedMovie)

        }

        else{
            var b: Bundle = Bundle()
            b.putInt("movieId", selectedMovie.movieId)
            val phimFragment = PhimFragment()
            phimFragment.arguments = b
            (activity as MainActivity).replaceFragment(phimFragment)
        }

    }


}