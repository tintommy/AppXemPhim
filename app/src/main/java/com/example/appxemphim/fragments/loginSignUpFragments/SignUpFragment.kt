package com.example.appxemphim.fragments.loginSignUpFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.databinding.FragmentSignUpBinding
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val userViewModel by viewModels<UserViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonEvent()
        lifecycleScope.launch {
            userViewModel.signup.collectLatest {
                when (it) {

                    is Resource.Loading -> {
                        binding.btnSignUp.startAnimation()
                        binding.tvThongBao.text = ""
                    }

                    is Resource.Success -> {
                        if (it.data!!.status == true) {
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Trùng username hoặc email",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        binding.btnSignUp.revertAnimation()

                    }

                    is Resource.Error -> {
                        binding.btnSignUp.revertAnimation()
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }

            }
        }
    }

    private fun setButtonEvent() {
        binding.btnSignUp.setOnClickListener {
            if (checkInput()) {
                userViewModel.userSignUp(
                    binding.etUsername.text.toString(),
                    binding.etPass.text.toString(),
                    binding.etEmail.text.toString()
                )
            }
        }

        binding.btnSignIn.setOnClickListener {
            it.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun checkInput(): Boolean {
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        val repass = binding.etRePass.text.toString()

        var check = 0
        if (username.equals("") || email.equals("") || pass.equals("") || repass.equals("")) {
            Toast.makeText(requireContext(), "Hãy nhập đủ các thông tin", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isEmailValid(email)) {
            binding.tvEmail.setError("Hãy nhập email đúng định dạng")
            check += 1
        } else
            binding.tvEmail.isErrorEnabled = false

        if (pass.length < 8) {
            binding.tvPass.setError("Hãy nhập mật khẩu dài hơn 8 kí tự")
            check += 1
        } else
            binding.tvPass.isErrorEnabled = false

        if (!pass.equals(repass)) {
            binding.tvRePass.setError("Xác nhận mật khẩu không đúng")
            check += 1
        } else
            binding.tvRePass.isErrorEnabled = false

        if (check == 0) {
            return true
        } else
            return false

    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }
}