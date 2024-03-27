package com.example.appxemphim.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.example.appxemphim.R
import com.example.appxemphim.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener(this)
daDangNhap()

    }


    private fun replaceFragment(fragment: Fragment) {
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

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
                Toast.makeText(this, "3", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_phimLe -> {
                Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_phimBo -> {
                Toast.makeText(this, "5", Toast.LENGTH_SHORT).show()

            }R.id.nav_theLoai-> {
                Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_quocGia -> {
                Toast.makeText(this, "5", Toast.LENGTH_SHORT).show()

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

}