package com.example.appxemphim.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.adapters.EpisodeAdapter
import com.example.appxemphim.adapters.MovieAdapter
import com.example.appxemphim.adapters.PersonAdapter
import com.example.appxemphim.databinding.FragmentPhimBinding
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.CollectionViewModel
import com.example.appxemphim.viewModel.CommentViewModel
import com.example.appxemphim.viewModel.MovieViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhimFragment : Fragment() {

    private lateinit var binding: FragmentPhimBinding
    private val movieViewModel by viewModels<MovieViewModel>()
    private val collectionViewModel by viewModels<CollectionViewModel>()
    private val commentViewModel by viewModels<CommentViewModel>()
    private lateinit var player: ExoPlayer
    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var personAdapter: PersonAdapter
    private lateinit var randomMovieAdapter: MovieAdapter
    private var phimDaDuocLuu = false
    private var movieId: Int = 0
    private var ratingStar = 5
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        player = ExoPlayer.Builder(requireContext()).build()
        binding = FragmentPhimBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()


        var bundle = arguments
        if (bundle != null) {
            movieId = bundle.getInt("movieId")
            movieViewModel.loadPhim(movieId)
        }

        lifecycleScope.launchWhenStarted {
            movieViewModel.phim.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        setUpMovie(it.data!!)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }


        }

        lifecycleScope.launch {
            movieViewModel.phimNgauNhien.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        randomMovieAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            collectionViewModel.phimDaLuu.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        phimDaDuocLuu = it.data!!.status
                        if (phimDaDuocLuu) {
                            binding.btnLuuPhim.setImageResource(R.drawable.baseline_favorite_24)
                        } else {
                            binding.btnLuuPhim.setImageResource(R.drawable.baseline_favorite_border_24)
                        }

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            collectionViewModel.luuPhim.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        phimDaDuocLuu = true
                        binding.btnLuuPhim.setImageResource(R.drawable.baseline_favorite_24)
                        Toast.makeText(requireContext(), "Đã lưu phim ", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            collectionViewModel.xoaPhim.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data!!.status) {
                            phimDaDuocLuu = false
                            binding.btnLuuPhim.setImageResource(R.drawable.baseline_favorite_border_24)
                            Toast.makeText(
                                requireContext(),
                                "Đã xoá phim khỏi danh sách lưu ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            commentViewModel.luuBinhLuan.collectLatest { when (it) {

                is Resource.Loading -> {}
                is Resource.Success -> {

                        Toast.makeText(
                            requireContext(),
                            "Đã gửi bình luận",
                            Toast.LENGTH_SHORT
                        ).show()

                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            } }
        }

        binding.btnFullScreen.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


            }
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    230
                )


                binding.videoPlayer.layoutParams = layoutParams
            }

        }

        binding.btnLuuPhim.setOnClickListener {
            if (!phimDaDuocLuu)
                collectionViewModel.luuPhim(movieId)
            else {
                collectionViewModel.xoaPhim(movieId)

            }

        }

        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingStar = rating.toInt()
        }
        binding.btnGuiBinhLuan.setOnClickListener {
            commentViewModel.saveComment(
                binding.etBinhLuan.text.toString().trim(),
                movieId,
                ratingStar,
                "2024-01-01"
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpMovie(movie: Movie) {
        movieId = movie.movieId
        collectionViewModel.kiemTraPhimDaLuu(movieId)
        binding.apply {
            Glide.with(requireContext()).load(movie.image).into(ivHinh)
            tvTenPhim.text = movie.name
            tvLuotXem.text = "Lượt xem: " + movie.views.toString()
            tvSoSao.text = "Đánh giá: " + movie.star.toString() + "/5"
            tvQuocGia.text = "Quốc gia: " + movie.country.name
            tvThongTin.text = movie.movieContent

            if (movie.episodeList.size > 0) {
                setUpPlayerView(movie.episodeList[0].link)
                videoPlayer.visibility = View.VISIBLE
                ivError.visibility = View.GONE
            } else {
                videoPlayer.visibility = View.INVISIBLE
                ivError.visibility = View.VISIBLE
            }

            if (movie.episodes == 1) {
                linearChonTapPhim.visibility = View.GONE
            } else if (movie.episodes > 1) {
                linearChonTapPhim.visibility = View.VISIBLE

            }

        }
        initAdapter(movie)
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false

    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun onBackPressed() {
        view?.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    if (v != null) {
                        //Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigateUp()

                        //  (activity as MainActivity).replaceFragment(HomeFragment())
                        if ((activity as MainActivity).supportFragmentManager.backStackEntryCount > 0) {
                            (activity as MainActivity).supportFragmentManager.popBackStack()
                        }

                    }
                }
                return true
            }
        })

    }

    private fun initAdapter(movie: Movie) {
        //tập phim
        if (movie.episodes > 1) {
            episodeAdapter = EpisodeAdapter()
            binding.rvTapPhim.apply {
                adapter = episodeAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            episodeAdapter.differ.submitList(movie.episodeList)
            episodeAdapter.setOnItemClickListener(object : EpisodeAdapter.OnItemClickListener {
                override fun onItemClick(link: String) {
                    setUpPlayerView(link)
                }

            })
        }

        // diễn viên
        personAdapter = PersonAdapter()
        binding.rvDienVien.apply {
            adapter = personAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        personAdapter.differ.submitList(movie.persons)


        randomMovieAdapter = MovieAdapter()
        movieViewModel.loadPhimNgauNhien()
        binding.rvPhimNgauNhien.apply {
            adapter = randomMovieAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        }
        randomMovieAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movieId: Int) {
                movieViewModel.loadPhim(movieId)

            }
        })

    }

    private fun setUpPlayerView(link: String) {
        binding.videoPlayer.player = player
        val mediaItem = MediaItem.fromUri(link.trim())
        player.setMediaItem(mediaItem)



        player.prepare()
        player.play()
    }


}