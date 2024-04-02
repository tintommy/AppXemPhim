package com.example.appxemphim.activities

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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appxemphim.R
import com.example.appxemphim.adapters.MovieSearchAdapter
import com.example.appxemphim.databinding.ActivityMainBinding
import com.example.appxemphim.fragments.DanhMucFragment
import com.example.appxemphim.fragments.HomeFragment
import com.example.appxemphim.fragments.PhimFragment
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.MovieViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieSearchAdapter: MovieSearchAdapter
    private val movieViewModel by viewModels<MovieViewModel>()

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
        khongDangNhap()




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
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_phimDaMua -> {
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_trangChu -> {

                replaceFragment(HomeFragment())
                supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

            R.id.nav_phimLe -> {
                Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_phimBo -> {
                Toast.makeText(this, "5", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_theLoaivaQuocGia -> {
                replaceFragment(DanhMucFragment())


            }

            R.id.nav_dangXuat -> {
                Toast.makeText(this, "6", Toast.LENGTH_SHORT).show()

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


    public fun replaceFragment(fragment: Fragment) {

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun initAdapter() {
        movieSearchAdapter = MovieSearchAdapter()
        binding.rvSearch.adapter = movieSearchAdapter
        binding.rvSearch.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        movieSearchAdapter.setOnItemClickListener(object : MovieSearchAdapter.OnItemClickListener {
            override fun onItemClick(movieId: Int) {
                binding.rvSearch.visibility = View.GONE
                var b: Bundle = Bundle()
                b.putInt("movieId", movieId)
                val phimFragment = PhimFragment()
                phimFragment.arguments = b
                replaceFragment(phimFragment)

            }
        })
    }

}