package com.example.appxemphim.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.appxemphim.R
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.MovieViewModel
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginSignUpActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_sign_up)


    }

//    private fun getUser(){
//        val sharedPref = baseContext.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        editor.putString("key_string", "value")
//        editor.putInt("key_int", 123)
//        editor.putBoolean("key_boolean", true)
//        editor.apply()
//        val token = sharedPref.getString("token", "")
//        val username = sharedPref.getString("username", "")
//    }
}