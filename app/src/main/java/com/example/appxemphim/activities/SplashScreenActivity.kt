package com.example.appxemphim.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appxemphim.R
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val userViewModel by viewModels<UserViewModel>()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPref: SharedPreferences

    //    private lateinit var token:String
//    private lateinit var username:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        sharedPref = application.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        handler.postDelayed({
            userViewModel.getUser()
            lifecycleScope.launchWhenStarted {
                userViewModel.user.collectLatest {
                    when (it) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Toast.makeText(
                                this@SplashScreenActivity,
                                "Đã đăng nhập vào " + it.data!!.username,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@SplashScreenActivity,
                                MainActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }

                        is Resource.Error -> {
                            val editor = sharedPref.edit()
                            editor.remove("token")
                            editor.remove("username")
                            editor.apply()
                            val intent = Intent(
                                this@SplashScreenActivity,
                                LoginSignUpActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }

                        else -> {}
                    }
                }

            }
            finish()
        }, 1500)
    }

}