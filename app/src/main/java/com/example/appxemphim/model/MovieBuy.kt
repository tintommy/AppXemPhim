package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName

data class MovieBuy(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageMovie")
    val imageMovie: String,
    @SerializedName("movieId")
    val movieId: Int,
    @SerializedName("movieName")
    val movieName: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("username")
    val username: String
)