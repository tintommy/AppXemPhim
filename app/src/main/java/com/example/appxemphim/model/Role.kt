package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("name")
    val name: String,
    @SerializedName("roleId")
    val roleId: Int
)