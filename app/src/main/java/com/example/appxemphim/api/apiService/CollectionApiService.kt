package com.example.appxemphim.api.apiService

import com.example.appxemphim.model.Collection
import com.example.appxemphim.request.Status
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CollectionApiService {
    @POST("/api/movie-collection/create")
    suspend fun saveMovieCollection(@Body collection: Collection): Response<Collection>
    @GET("/api/movie-collection/check-exists-collection")
    suspend fun checkExistCollection(@Query("movieId") movieID: Int,@Query("username") username: String): Response<Status>
    @DELETE("/api/movie-collection/delete-by-movie-and-user")
    suspend fun deleteCollection(@Query("movieId") movieID: Int,@Query("username") username: String): Response<Status>
    @GET("/api/movie-collection/get-all-by-user")
    suspend fun getAllCollection(@Query("username") username: String): Response<List<Collection>>
}