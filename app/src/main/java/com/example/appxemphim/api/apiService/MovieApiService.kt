package com.example.appxemphim.api.apiService

import com.example.appxemphim.model.Collection
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.ResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieApiService {
    @GET("/api/movie/get-phim-trang-chu/{loaiPhim}")
    suspend fun getMovieHome(@Path(value = "loaiPhim") loaiPhim: String): Response<ResponseData<List<Movie>>>

    @GET("/api/movie/get-random")
    suspend fun getRandomMovie(@Query("top") top: Int): Response<ResponseData<List<Movie>>>

    @GET("/api/movie/{id}")
    suspend fun getMovie(@Path(value = "id") id: Int): Response<ResponseData<Movie>>

    @GET("/api/movie/get-all")
    suspend fun getMovieSearch(
        @Query("sortField") sortField: String = "",
        @Query("typeSort") typeSort: String = "DESC",
        @Query("searchContent") searchContent: String = ""
    ): Response<ResponseData<List<Movie>>>

    @GET("/api/movie/get-all-by-category")
    suspend fun getAllMovieByCategory(
        @Query("sortField") sortField: String,
        @Query("typeSort") typeSort: String,
        @Query("searchContent") searchContent: String,
        @Query("category_id") category_id: Int,
    ): Response<ResponseData<List<Movie>>>


    @GET("/api/movie/get-page-by-category")
    suspend fun getPageMovieByCategory(
        @Query("offset") offset: Int,
        @Query("pageSize") pageSize: Int,
        @Query("field") field: String,
        @Query("searchContent") searchContent: String,
        @Query("categoryId") categoryId: Int,
    ): Response<ResponseData<List<Movie>>>
}