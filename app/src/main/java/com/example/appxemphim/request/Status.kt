package com.example.appxemphim.request


import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("status")
    val status: Boolean
)