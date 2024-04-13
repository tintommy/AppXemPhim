package com.example.appxemphim.request

data class ChangePassRequest(
    val username: String,
    val password: String,
    val newPassword: String,
    val email: String?,
    val roleId: String?
)
