package com.example.appxemphim.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.appxemphim.R
import com.example.appxemphim.databinding.DialogLayoutChangePassBinding
import com.example.appxemphim.databinding.FragmentUserBinding
import com.example.appxemphim.model.User
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val userViewModel by viewModels<UserViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.getUser()
        lifecycleScope.launchWhenStarted {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        initUser(it.data!!)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }


            }
        }


    }

    private fun initUser(user: User) {


        Glide.with(requireContext()).load(user.avatar).into(binding.ivAnhUser)
        binding.tvTenUser.text = user.name
        binding.tvTien.text = "$ " + user.money.toString()
        binding.tvUsername.setText(user.username)
        binding.tvEmail.setText(user.email)

        binding.btnDoiMatKhau.setOnClickListener {
            val inflater = layoutInflater
            val mDialogView = inflater.inflate(R.layout.dialog_layout_change_pass, null)
            val dialogBinding: DialogLayoutChangePassBinding =
                DialogLayoutChangePassBinding.inflate(layoutInflater)
            val mDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
            mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mDialog.show()


            dialogChangePassEvent(dialogBinding)
        }

    }

    private fun dialogChangePassEvent(dialogBinding: DialogLayoutChangePassBinding) {

        dialogBinding.btnXacNhanDoiMatKhau.setOnClickListener {

            dialogBinding.apply {
                tvOldPass.text = ""
                tvNewPass.text = ""
                tvReNewPass.text = ""
                var check = 0;
                if (etOldPass.text!!.trim().toString().isBlank()) {
                    tvOldPass.text = "Hãy nhập mật khẩu cũ"
                    check += 1
                }
                if (etNewPass.text!!.trim().toString().isBlank()) {
                    tvNewPass.text = "Hãy nhập mật khẩu mới"

                    check += 1
                }
                if (etOldPass.text!!.trim().toString().equals(etNewPass.text!!.trim().toString())) {
                    tvNewPass.text = "Mật khẩu mới không được trùng mật khẩu cũ"
                    check += 1
                }

                if (etRePass.text!!.trim().toString().isBlank()) {
                    tvReNewPass.text = "Hãy nhập lại mật khẩu mới"

                    check += 1
                }

                if (etNewPass.text!!.length < 8) {
                    tvNewPass.text = "Hãy nhập mật khẩu mới có nhiều hơn 8 kí tự"

                    check += 1
                }
                if (!etNewPass.text!!.toString().equals(etRePass.text.toString())) {
                    tvReNewPass.text = "Xác nhận mật khẩu không đúng"

                    check += 1
                }

                if (check == 0) {
                    userViewModel.userChangePass(
                        etOldPass.text.toString(),
                        etNewPass.text.toString()
                    )
                }


            }

        }

        lifecycleScope.launch {
            userViewModel.changePass.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        dialogBinding.changePassProgressBar.visibility = View.VISIBLE
                        dialogBinding.tvSuccess.visibility = View.GONE

                    }

                    is Resource.Success -> {
                        dialogBinding.changePassProgressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Đổi mật khẩu thành công",
                            Toast.LENGTH_LONG
                        ).show()
                        dialogBinding.tvSuccess.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {
                        dialogBinding.changePassProgressBar.visibility = View.GONE
                        dialogBinding.tvSuccess.visibility = View.GONE
                        if (it.message.equals("false")) {
                            dialogBinding.tvOldPass.text = "Sai mật khẩu cũ"
                        } else if (it.message.equals("error")) {
                            Toast.makeText(
                                requireContext(),
                                "Xảy ra lỗi kết nối",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }

                    else -> {}
                }
            }
        }

    }
}








