package com.example.appxemphim.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.UserApiService
import com.example.appxemphim.request.ChangePassRequest
import com.example.appxemphim.request.SignInRequest
import com.example.appxemphim.model.Token
import com.example.appxemphim.model.User
import com.example.appxemphim.request.SignUpRequest
import com.example.appxemphim.request.Status
import com.example.appxemphim.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {

    private lateinit var token: String
    private lateinit var username: String
    private lateinit var userService: UserApiService

    private val _login = MutableStateFlow<Resource<Token>>(Resource.Unspecified())
    val login = _login.asStateFlow()

    private val _signup = MutableStateFlow<Resource<Status>>(Resource.Unspecified())
    val signup = _signup.asStateFlow()

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _changePass = MutableStateFlow<Resource<Status>>(Resource.Unspecified())
    val changePass = _changePass.asStateFlow()


    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()
        var retrofit = API_Instance.getClient(token)
        userService = retrofit.create(UserApiService::class.java)

    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
            val response = userService.getUser(username)
            if (response.isSuccessful) {
                _user.emit(Resource.Success(response.body()!!))

            } else {
                _user.emit(Resource.Error("Lỗi khi lấy user"))

            }
        }

    }

    fun userLogin(username: String, email: String, password: String) {
        val signInRequest = SignInRequest(username, email, password)

        viewModelScope.launch {
            _login.emit(Resource.Loading())

            val response = userService.userLogin(signInRequest)
            if (response.isSuccessful) {
                _login.emit(Resource.Success(response.body()!!))
                val editor = sharedPref.edit()
                editor.putString("token", response.body()!!.token)
                editor.putString("username", username)
                editor.apply()

            } else if (response.code() == 404) {
                Log.e("User", "false")
                _login.emit(Resource.Error("Lỗi khi login"))
            }


        }


    }

    fun userChangePass(oldPass: String, newPass: String) {
        viewModelScope.launch {
            _changePass.emit(Resource.Loading())

            val changePassRequest = ChangePassRequest(username, oldPass, newPass, "", "")


            val changePassResponse = changePassRequest?.let { userService.userChangePass(it) }
            if (changePassResponse!!.isSuccessful) {

                if (changePassResponse.body()!!.status)
                    _changePass.emit(Resource.Success(changePassResponse.body()!!))
                else if (!changePassResponse.body()!!.status) {
                    _changePass.emit(Resource.Error("false"))
                }
            } else {
                _changePass.emit(Resource.Error("error"))
            }

        }
    }

    fun userSignUp(username: String, password: String, email: String) {
        viewModelScope.launch {
            val signUpRequest = SignUpRequest(username, password, email, 1)

            _signup.emit(Resource.Loading())
            val response = userService.userSignup(signUpRequest)
            if (response.isSuccessful)
                _signup.emit(Resource.Success(response.body()!!))
            else {
                _signup.emit(Resource.Error("Lỗi khi đăng kí"))

            }

        }

    }

}

