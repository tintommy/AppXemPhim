package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("token")
    val token: String
)