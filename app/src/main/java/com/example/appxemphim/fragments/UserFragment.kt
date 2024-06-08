package com.example.appxemphim.fragments

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appxemphim.R
import com.example.appxemphim.databinding.DialogLayoutChangePassBinding
import com.example.appxemphim.databinding.FragmentUserBinding
import com.example.appxemphim.model.User
import com.example.appxemphim.util.CONFIG
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var selectImageActivityResult: ActivityResultLauncher<Intent>

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

        lifecycleScope.launch {
            userViewModel.changeAvatar.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBarChangeAvatar.visibility= View.VISIBLE

                    }
                    is Resource.Success -> {
                        binding.progressBarChangeAvatar.visibility= View.GONE
                        userViewModel.getUser()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

         selectImageActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val imageUri = intent?.data
                    if (imageUri != null) {
                        uploadImage(imageUri)
                    }
                }

            }
    }



    private fun initUser(user: User) {


        Glide.with(requireContext()).load(CONFIG.CLOUD_URL+user.avatar).apply(RequestOptions().error(R.drawable.no_avatar)).into(binding.ivAnhUser)
        binding.tvUsername.text = user.username

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



        binding.ivAnhUser.setOnClickListener {

          openBottomDialog()
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

    fun openBottomDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_dialog_layout)
        val btnDoiAvatar :TextView= dialog.findViewById(R.id.btnDoiAvatar)


        btnDoiAvatar.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImageActivityResult.launch(intent)
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }


    private fun uploadImage(uri: Uri) {
        val file = createFileFromUri(uri)
        val requestFile = RequestBody.create(requireContext().contentResolver.getType(uri)?.toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("fileUpload", file.name, requestFile)

        userViewModel.userChangeAvatar(body)


    }

    private fun createFileFromUri(uri: Uri): File {
        val originalFileName = getFileName(uri)
        val cleanFileName = removeTempExtension(originalFileName)
        Log.e("NAME", cleanFileName)
        val tempFile = File.createTempFile(cleanFileName, null, requireContext().cacheDir)
        tempFile.outputStream().use { outputStream ->
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun removeTempExtension(fileName: String): String {
        val regex = Regex("(.*?\\.jpg).*")
        val matchResult = regex.matchEntire(fileName)



        return matchResult?.groups?.get(1)?.value ?: fileName
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "unknown_file"
    }
}









