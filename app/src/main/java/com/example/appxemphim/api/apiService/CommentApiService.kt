package com.example.appxemphim.api.apiService

import com.example.appxemphim.model.Collection
import com.example.appxemphim.model.Comment
import com.example.appxemphim.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CommentApiService {
    @POST("/api/comment/create")
    suspend fun saveMovieComment(@Body comment: Comment): Response<ResponseData<Comment>>

    @GET("/api/comment/get-page-by-movie")
    suspend fun getPageComment(
        @Query("offset") offset: Int,
        @Query("pageSize") pageSize: Int,
        @Query("movieId") movieId: Int,
    ): Response<ResponseData<List<Comment>>>
}