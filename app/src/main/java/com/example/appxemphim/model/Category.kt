package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("name")
    val name: String
)