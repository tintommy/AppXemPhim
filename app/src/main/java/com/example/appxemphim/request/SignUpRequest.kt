package com.example.appxemphim.request

import java.io.Serializable

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
    val roleId: Int):Serializable