package com.example.appxemphim.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appxemphim.R
import com.example.appxemphim.adapters.MovieSearchAdapter
import com.example.appxemphim.databinding.ActivityMainBinding
import com.example.appxemphim.databinding.DialogBuyMovieBinding
import com.example.appxemphim.fragments.DanhMucFragment
import com.example.appxemphim.fragments.HomeFragment
import com.example.appxemphim.fragments.MovieBoughtFragment
import com.example.appxemphim.fragments.PageFragment
import com.example.appxemphim.fragments.PhimFragment
import com.example.appxemphim.fragments.SavedMovieFragment
import com.example.appxemphim.fragments.UserFragment
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.MovieViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.zalopay.sdk.ZaloPaySDK
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    @Inject
    lateinit var sharedPref: SharedPreferences
    public lateinit var binding: ActivityMainBinding
    private lateinit var movieSearchAdapter: MovieSearchAdapter
    private val movieViewModel by viewModels<MovieViewModel>()
    private lateinit var selectedMovie: Movie

    object muaPhim {
        var thanhToan = false
        lateinit var phim: Movie
    }

    var querySearch: String = ""
    val delayTime: Long = 700

    val handler = Handler()

    val search = Runnable {
        Log.e("Query", "delay " + querySearch)
        if (querySearch != "") {
            movieViewModel.loadPhimSearch(querySearch)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        daDangNhap()




        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener(this)



        binding.svPhim.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
//
//                if (p0.equals("")) {
//                    binding.rvSearch.visibility = View.GONE
//                }
//                if (p0 != null) {
//                 //   movieViewModel.loadPhimSearch(p0)
//                    handler.removeCallbacks(search)
//                    handler.postDelayed(search, delayTime)
//                    Log.e("Query",p0)
//                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                if (p0.equals("") || p0 == null) {
                    movieSearchAdapter.differ.submitList(ArrayList<Movie>())
                    binding.rvSearch.visibility = View.GONE
                    querySearch = ""
                }
                if (p0 != null && !p0.equals("")) {
                    //   movieViewModel.loadPhimSearch(p0)
                    querySearch = p0
                    handler.removeCallbacks(search)
                    handler.postDelayed(search, delayTime)
                    Log.e("Query", p0)
                }

                return false
            }

        })

        binding.svPhim.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                binding.rvSearch.visibility = View.GONE
                movieSearchAdapter.differ.submitList(ArrayList<Movie>())
                return false
            }

        }


        )



        lifecycleScope.launchWhenStarted {
            movieViewModel.phimSearch.collectLatest {
                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        movieSearchAdapter.differ.submitList(it.data)
                        binding.rvSearch.visibility = View.VISIBLE


                    }

                    is Resource.Error -> {
                        //   Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                        binding.rvSearch.visibility = View.GONE

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
                            replaceFragment(phimFragment,"MOVIE")
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }


    override fun onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.closeDrawer(
            GravityCompat.START
        )
        else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId) {
            R.id.nav_dangNhap -> {
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_caNhan -> {
                val userFragment = UserFragment()
                replaceFragment(userFragment,"USER")

            }

            R.id.nav_phimDaLuu -> {
                val savedMovieFragment = SavedMovieFragment()
                replaceFragment(savedMovieFragment,"SAVED")


            }

            R.id.nav_phimDaMua -> {
                val movieBoughtFragment = MovieBoughtFragment()
                replaceFragment(movieBoughtFragment,"BOUGHT")

            }

            R.id.nav_trangChu -> {

                replaceFragment(HomeFragment(),"HOME")
                supportFragmentManager.popBackStackImmediate(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }

            R.id.nav_phimLe -> {
                var b: Bundle = Bundle()
                b.putInt("categoryId", 1)
                b.putString("categoryName", "Phim lẻ")
                val pageFragment = PageFragment()
                pageFragment.arguments = b
                replaceFragment(pageFragment,"SHORT")

            }

            R.id.nav_phimBo -> {
                var b: Bundle = Bundle()
                b.putInt("categoryId", 2)
                b.putString("categoryName", "Phim bộ")
                val pageFragment = PageFragment()
                pageFragment.arguments = b
                replaceFragment(pageFragment,"LONG")

            }

            R.id.nav_theLoaivaQuocGia -> {
                supportFragmentManager.popBackStackImmediate(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                replaceFragment(DanhMucFragment(),"CATEGORY")


            }

            R.id.nav_dangXuat -> {

                val editor = sharedPref.edit()
                editor.remove("token")
                editor.remove("username")
                editor.apply()

                Toast.makeText(this@MainActivity, "Đã đăng xuất ", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    this@MainActivity,
                    LoginSignUpActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            else -> return false
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun daDangNhap() {
//        val menuItem = navigationV.menu.findItem(R.id.nav_dangNhap)
//         menuItem.setVisible(false)

        binding.navView.apply {
            menu.findItem(R.id.nav_dangNhap).setVisible(false);
            menu.findItem(R.id.nav_caNhan).setVisible(true);
            menu.findItem(R.id.nav_phimDaMua).setVisible(true);
            menu.findItem(R.id.nav_dangXuat).setVisible(true);
        }
    }

    private fun khongDangNhap() {
        binding.navView.apply {
            menu.findItem(R.id.nav_dangNhap).setVisible(true);
            menu.findItem(R.id.nav_caNhan).setVisible(false);
            menu.findItem(R.id.nav_phimDaMua).setVisible(false);
            menu.findItem(R.id.nav_dangXuat).setVisible(false);

        }
    }


    fun replaceFragment(fragment: Fragment, tag: String) {

        val fragmentTag = tag

        // Kiểm tra xem fragment đã tồn tại chưa
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment == null) {
            transaction.replace(R.id.fragmentContainerView, fragment, fragmentTag)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    private fun initAdapter() {
        movieSearchAdapter = MovieSearchAdapter()
        binding.rvSearch.adapter = movieSearchAdapter
        binding.rvSearch.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        movieSearchAdapter.setOnItemClickListener(object : MovieSearchAdapter.OnItemClickListener {
            override fun onItemClick(movie: Movie) {
                binding.rvSearch.visibility = View.GONE
//                var b: Bundle = Bundle()
//                b.putInt("movieId", mo)
//                val phimFragment = PhimFragment()
//                phimFragment.arguments = b
//                replaceFragment(phimFragment)

                checkMovie(movie)

            }
        })
    }

    fun checkMovie(movie: Movie) {
        selectedMovie = movie


        if (movie.price > 0) {
            movieViewModel.checkExistMovieBuy(selectedMovie)

        } else {
            var b: Bundle = Bundle()
            b.putInt("movieId", selectedMovie.movieId)
            val phimFragment = PhimFragment()
            phimFragment.arguments = b
            replaceFragment(phimFragment,"MOVIE")
        }

    }

    fun openBuyMovieDialog(movie: Movie) {
        val dialogBuyMovieBinding: DialogBuyMovieBinding =
            DialogBuyMovieBinding.inflate(layoutInflater)

        val mDialog =
            AlertDialog.Builder(this).setView(dialogBuyMovieBinding.root)
                .create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuyMovieBinding.tvMoneyPrice.text = movie.price.toString()
        mDialog.show()
        // mDialog.dismiss()


        dialogBuyMovieBinding.btnConfirm.setOnClickListener {
            val intent = Intent(this, ThanhToanActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
            mDialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("MyTag", "on resume")
        if (muaPhim.thanhToan == true) {
            muaPhim.thanhToan = false
            var b: Bundle = Bundle()
            b.putInt("movieId", muaPhim.phim.movieId)
            val phimFragment = PhimFragment()
            phimFragment.arguments = b
            replaceFragment(phimFragment,"MOVIE")
        }
    }

    override fun onPause() {
        super.onPause()
        // Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show()
    }
}