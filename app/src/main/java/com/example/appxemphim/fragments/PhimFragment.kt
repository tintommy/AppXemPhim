package com.example.appxemphim.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.adapters.CommentAdapter
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
import java.util.Calendar


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
    private lateinit var commentAdapter: CommentAdapter
    private var phimDaDuocLuu = false
    private var movieId: Int = 0
    private var ratingStar = 5
    private var pageCmt = 0

    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0

    private var ngay = calendar[Calendar.DAY_OF_MONTH]

    private lateinit var fullScreenDialog: Dialog
    private var fullscreen = false
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
        //onBackPressed()
        iniFullScreenDialog()
        binding.videoRatioLayout.setAspectRatio(16f/9f)
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
                        phimDaDuocLuu = it.data!!
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
                        if (it.data!!) {
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
            commentViewModel.luuBinhLuan.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        Toast.makeText(
                            requireContext(),
                            "Đã gửi bình luận",
                            Toast.LENGTH_SHORT
                        ).show()
                        pageCmt = 0
                        commentViewModel.loadCmt(0, movieId)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            commentViewModel.taiBinhLuan.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvKoCmt.visibility = View.GONE

                    }

                    is Resource.Success -> {

                        commentAdapter.differ.submitList(it.data)
                        if (pageCmt == 0) {
                            binding.btnCmtBefore.visibility = View.GONE
                        } else {
                            binding.btnCmtBefore.visibility = View.VISIBLE
                        }

                        if (it.data!!.size < 5) {
                            binding.btnCmtAfter.visibility = View.GONE
                        } else {
                            binding.btnCmtAfter.visibility = View.VISIBLE
                        }

                        if (pageCmt == 0 && it.data.size == 0) {
                            binding.tvKoCmt.visibility = View.VISIBLE
                        }
                        binding.progressBar.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvKoCmt.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }


        binding.btnFullScreen.setOnClickListener {

            openFullscreenDialog()

        }

        binding.btnLuuPhim.setOnClickListener {
            if (!phimDaDuocLuu)
                collectionViewModel.luuPhim(movieId,dinhDangNgayAPI(ngay, thang, nam))
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
                dinhDangNgayAPI(ngay, thang, nam)
            )

            binding.etBinhLuan.setText("")
        }

        binding.btnCmtBefore.setOnClickListener {
            pageCmt -= 1
            commentViewModel.loadCmt(pageCmt, movieId)

        }
        binding.btnCmtAfter.setOnClickListener {
            pageCmt += 1
            commentViewModel.loadCmt(pageCmt, movieId)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpMovie(movie: Movie) {
        pageCmt = 0
        movieId = movie.movieId
        collectionViewModel.kiemTraPhimDaLuu(movieId)
        commentViewModel.loadCmt(pageCmt, movieId)
        movieViewModel.loadPhimNgauNhien()
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
                player.stop()
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

        binding.rvPhimNgauNhien.apply {
            adapter = randomMovieAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        }
        randomMovieAdapter.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movie: Movie, price: Int) {
                movieViewModel.loadPhim(movie.movieId)

            }
        })

        commentAdapter = CommentAdapter()
        binding.rvBinhLuan.apply {
            adapter = commentAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun setUpPlayerView(link: String) {
        binding.videoPlayer.player = player
        val mediaItem = MediaItem.fromUri(link.trim())
        player.setMediaItem(mediaItem)



        player.prepare()
        player.play()
    }

    fun iniFullScreenDialog() {
        fullScreenDialog =
            object : Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (fullscreen) {
                        closeFullscreenDialog()
                    }
                    super.onBackPressed()
                }
            }

    }

    private fun closeFullscreenDialog() {


        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val parentView = binding.videoPlayer.parent as ViewGroup
        parentView.removeView(binding.videoPlayer)
        binding.videoRatioLayout.addView(binding.videoPlayer)
        fullscreen = false
        fullScreenDialog.dismiss()

            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }

    private fun openFullscreenDialog() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val parentView = binding.videoPlayer.parent as ViewGroup
        parentView.removeView(binding.videoPlayer)
        fullScreenDialog.addContentView(
            binding.videoPlayer,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        fullscreen = true
        fullScreenDialog.show()
        Handler().postDelayed({
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }, 3000)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openFullscreenDialog()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
          closeFullscreenDialog()
        }
    }


    private fun dinhDangNgayAPI(ngay: Int, thang: Int, nam: Int): String {
        var temp = ""
        temp += nam
        temp += "-"
        temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
        temp += "-"
        temp += if (ngay < 10) "0$ngay" else ngay.toString()
        return temp
    }
}