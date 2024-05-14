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
import androidx.navigation.fragment.findNavController
import com.example.appxemphim.R
import com.example.appxemphim.activities.LoginSignUpActivity
import com.example.appxemphim.databinding.FragmentOtpVerifyBinding
import com.example.appxemphim.request.SignUpRequest
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpVerifyFragment : Fragment() {
    private lateinit var binding: FragmentOtpVerifyBinding
    private lateinit var signUpRequest: SignUpRequest
    private val userViewModel by viewModels<UserViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpVerifyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        if (b != null) {
            signUpRequest = b.getSerializable("signUpRequest") as SignUpRequest
        }



        lifecycleScope.launch {
            userViewModel.verify.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnConfirm.startAnimation()
                        binding.tvSai.visibility = View.INVISIBLE
                    }

                    is Resource.Success -> {
                        binding.btnConfirm.revertAnimation()
                        if (it.data == true) {
                            Toast.makeText(
                                requireContext(),
                                "Tạo tài khoản thành công",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(
                                requireActivity(),
                                LoginSignUpActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            binding.tvSai.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Error -> {
                        binding.btnConfirm.revertAnimation()
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }




        binding.tvEmail.text = signUpRequest.email
        binding.btnConfirm.setOnClickListener {
            userViewModel.userVerify(signUpRequest, binding.etOTP.text.toString())
        }
    }

}