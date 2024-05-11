package com.example.appxemphim.api.apiService

import com.example.appxemphim.model.MovieBuy
import com.example.appxemphim.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BuyMovieApiService {
    @GET("/api/movie-buy/get-all-by-user")
    suspend fun getAllByUser(
        @Query(value = "username") username: String
    ): Response<ResponseData<List<MovieBuy>>>

    @GET("/api/movie-buy/check-exists-buy")
    suspend fun checkExistBuy(
        @Query(value = "movieId") movieId: Int,
        @Query(value = "username") username: String
    ): Response<ResponseData<Boolean>>

    @POST("/api/movie-buy/create")
    suspend fun buyMovie(
       @Body buyMovie: MovieBuy
    ): Response<ResponseData<MovieBuy>>
}