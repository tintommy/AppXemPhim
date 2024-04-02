package com.example.appxemphim.api.apiService

import com.example.appxemphim.model.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieApiService {
    @GET("/api/movie/get-phim-trang-chu/{loaiPhim}")
    suspend fun getMovieHome(@Path(value = "loaiPhim") loaiPhim: String): Response<List<Movie>>
    @GET("/api/movie/{id}")
    suspend fun getMovie(@Path(value = "id") id: Int): Response<Movie>
    @GET("/api/movie/get-all")
    suspend fun getMovieSearch(@Query("sortField") sortField: String = "",
                               @Query("typeSort") typeSort: String = "DESC",
                               @Query("searchContent") searchContent: String = ""
    ): Response<List<Movie>>
}