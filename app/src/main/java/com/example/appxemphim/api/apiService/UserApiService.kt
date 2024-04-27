package com.example.appxemphim.api.apiService

import com.example.appxemphim.request.ChangePassRequest
import com.example.appxemphim.request.SignInRequest
import com.example.appxemphim.model.Token
import com.example.appxemphim.model.User
import com.example.appxemphim.request.SignUpRequest
import com.example.appxemphim.request.Status
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {



    @POST("/api/login/signin")
    suspend fun userLogin(@Body signInRequest: SignInRequest): Response<Token>
    @POST("/api/login/signup")
    suspend fun userSignup(@Body signUpRequest: SignUpRequest): Response<Status>
    @GET("/api/movie-user/{username}")
    suspend fun getUser(@Path(value = "username") username: String): Response<User>

    @POST("/api/movie-user/change-pass")
    suspend fun userChangePass(@Body changePassRequest: ChangePassRequest): Response<Status>
}