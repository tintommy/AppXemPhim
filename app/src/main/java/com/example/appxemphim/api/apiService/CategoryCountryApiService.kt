package com.example.appxemphim.api.apiService

import com.example.appxemphim.model.Category
import com.example.appxemphim.model.Country
import com.example.appxemphim.model.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryCountryApiService {
    @GET("/api/category/get-all")
    suspend fun getAllCategory(): Response<List<Category>>




    @GET("/api/country/get-all")
    suspend fun getAllCountry(): Response<List<Country>>
}