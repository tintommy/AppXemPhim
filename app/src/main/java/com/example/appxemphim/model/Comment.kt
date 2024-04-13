package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("idComment")
    val idComment: Int,
    @SerializedName("movieId")
    val movieId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("value")
    val value: Int
)