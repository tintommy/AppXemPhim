package com.example.appxemphim.fragments.loginSignUpFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.appxemphim.databinding.FragmentForgetPassBinding
import com.example.appxemphim.request.ChangePassRequest
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ForgetPassFragment : Fragment() {

    private lateinit var binding: FragmentForgetPassBinding
    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var changePassRequest: ChangePassRequest
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPassBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            userViewModel.sendOTP.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSendOTP.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnSendOTP.revertAnimation()
                        if (it.data == true) {
                            Toast.makeText(
                                requireContext(),
                                "OTP đã được gửi đến email",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            binding.tvUsername.error = "username và email không khớp"
                        }
                    }

                    is Resource.Error -> {
                        binding.btnConfirm.revertAnimation()
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            userViewModel.verify.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnConfirm.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnConfirm.revertAnimation()
                        if (it.data == true) {
                            Toast.makeText(
                                requireContext(),
                                "Đổi mật khẩu thành công",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        } else {
                            binding.tvOTP.error = "OTP không khớp"
                        }
                    }

                    is Resource.Error -> {
                        binding.btnSendOTP.revertAnimation()
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }


        binding.apply {

            btnSendOTP.setOnClickListener {
                binding.tvUsername.isErrorEnabled = false
                binding.tvEmail.isErrorEnabled = false
                if (etUsername.text.toString().equals("")) {
                    tvUsername.error = "Hãy nhập username"
                } else if (etEmail.text.toString().equals("")) {
                    tvEmail.error = "Hãy nhập email"
                } else {
                    changePassRequest = ChangePassRequest(
                        etUsername.text.toString(),
                        "",
                        etPass.text.toString(),
                        etEmail.text.toString(),
                        "1"
                    )
                    userViewModel.userChangePassOTP(changePassRequest)
                }

            }
            btnConfirm.setOnClickListener {
                if (checkInput()) {
                    userViewModel.userVerify(binding.etEmail.text.toString(),binding.etOTP.text.toString(),binding.etPass.text.toString())
                }
            }
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        // This callback will only be called when MyFragment is at least Started.
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                  findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    fun checkInput(): Boolean {
        var check = true
        val username = binding.etUsername.text.toString()
        val otp = binding.etOTP.text.toString()
        val pass = binding.etPass.text.toString()
        val rePass = binding.etRePass.text.toString()
        binding.tvUsername.isErrorEnabled = false
        binding.tvPass.isErrorEnabled = false
        binding.tvRePass.isErrorEnabled = false
        binding.tvOTP.isErrorEnabled = false
        if (username.equals("")) {
            binding.tvUsername.error = "Hãy nhập username"
            check = false
        }
        if (pass.length < 8) {
            binding.tvPass.error = "Hãy nhập mật khẩu có ít nhất 8 kí tự "
            check = false
        }
        if (!rePass.equals(pass)) {
            binding.tvRePass.error = "Xác nhận mật khẩu không đúng"
            check = false
        }
        if (otp.equals("")) {
            binding.tvOTP.error = "Hãy nhập otp"
            check = false
        }
        return check
    }
}