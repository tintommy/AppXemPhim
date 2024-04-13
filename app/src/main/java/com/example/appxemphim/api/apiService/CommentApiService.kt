package com.example.appxemphim.api.apiService

import com.example.appxemphim.model.Collection
import com.example.appxemphim.model.Comment
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CommentApiService {
    @POST("/api/comment/create")
    suspend fun saveMovieComment(@Body comment: Comment): Response<Comment>
}