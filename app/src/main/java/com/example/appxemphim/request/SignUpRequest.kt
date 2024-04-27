package com.example.appxemphim.request

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
    val roleId: Int)