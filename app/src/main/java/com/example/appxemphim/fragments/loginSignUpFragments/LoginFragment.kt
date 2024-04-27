package com.example.appxemphim.fragments.loginSignUpFragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.databinding.FragmentLoginBinding
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val userViewModel by viewModels<UserViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var username:String=""
        var email:String=""
        var password=""





        binding.btnLogin.setOnClickListener {

            password=binding.etPass.text.toString()
            if(isFullyForm(binding.etUsernameEmail.getText().toString(),password)) {
                if(isEmailValid(binding.etUsernameEmail.getText().toString()))
                    email=binding.etUsernameEmail.getText().toString()
                else
                    username=binding.etUsernameEmail.getText().toString()

                userViewModel.userLogin(username,email,password)
            }
            else{
                binding.tvThongBao.text="Hãy nhập đủ username(email) và mật khẩu !!!"
            }



        }


        lifecycleScope.launchWhenStarted {
            userViewModel.login.collectLatest {
                when (it) {

                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                        binding.tvThongBao.text = ""
                    }

                    is Resource.Success -> {

                        binding.btnLogin.revertAnimation()
                        val intent = Intent(
                            requireContext(),
                            MainActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }

                    is Resource.Error -> {
                        binding.btnLogin.revertAnimation()
                        binding.tvThongBao.text = "Sai thông tin đăng nhập!!!"
                    }

                    else -> {}
                }

            }

        }

        binding.btnSignUp.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun isFullyForm(usernamEmail:String,password:String):Boolean{
        if(usernamEmail.equals("")||password.equals(""))
            return false
        return true
    }
}