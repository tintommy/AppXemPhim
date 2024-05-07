package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Category(
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("name")
    val name: String
):Serializable