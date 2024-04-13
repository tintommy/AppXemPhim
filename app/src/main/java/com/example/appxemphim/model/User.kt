package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("money")
    val money: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: Role,
    @SerializedName("roleId")
    val roleId: Int,
    @SerializedName("username")
    val username: String
)