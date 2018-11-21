package com.verkoop.models



data class SignUpRequest(
    val email: String,
    val Username: String,
    val password: String,
    val Country: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    val request_type: String
)
data class LoginSocialRequest(
    val FirstName: String,
    val email: String,
    val socialId: String,
    val request_type: String
)