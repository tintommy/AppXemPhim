package com.example.appxemphim.api.apiService

import android.provider.ContactsContract.CommonDataKinds.Email
import com.example.appxemphim.request.ChangePassRequest
import com.example.appxemphim.request.SignInRequest
import com.example.appxemphim.model.Token
import com.example.appxemphim.model.User
import com.example.appxemphim.request.SignUpRequest
import com.example.appxemphim.request.Status
import com.example.appxemphim.util.ResponseData
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {


    @POST("/api/login/signin")
    suspend fun userLogin(@Body signInRequest: SignInRequest): Response<ResponseData<String>>

    @POST("/api/login/signup")
    suspend fun userSignup(@Body signUpRequest: SignUpRequest): Response<ResponseData<Boolean>>

    @GET("/api/login/verify-account")
    suspend fun userVerify(
        @Query("email") email: String,
        @Query("otp") otp: String,
        @Query("newPass") newPass: String,
        @Query("roleId") roleId: String
    ): Response<ResponseData<Boolean>>

    @GET("/api/movie-user/{username}")
    suspend fun getUser(@Path(value = "username") username: String): Response<ResponseData<User>>

    @GET("/api/movie-user/get-by-email")
    suspend fun getUserByEmail(@Query("email") email: String): Response<ResponseData<User>>

    @POST("/api/movie-user/change-pass")
    suspend fun userChangePass(@Body changePassRequest: ChangePassRequest): Response<ResponseData<Boolean>>

    @PUT("/api/login/change-pass-otp")
    suspend fun userChangePassOTP(@Body changePassRequest: ChangePassRequest): Response<ResponseData<Boolean>>

    @POST("/api/movie-user/upload")
    @Multipart
    suspend fun userChangeAvatar(
        @Part fileUpload: MultipartBody.Part,
        @Query("username") username: String
    ): Response<ResponseData<Boolean>>
}