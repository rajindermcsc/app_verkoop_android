package com.verkoop.models


data class LoginResponse(
    val status: String,
    val status_code: Int,
    val message: String,
    val data: Data
)

data class Data(
    val id: Int,
    val username: Any,
    val FirstName: String,
    val LastName: Any,
    val Website: Any,
    val Bio: Any,
    val ProfilePhoto: Any,
    val Mobile: Any,
    val Gender: Any,
    val Birthdate: Any,
    val email: Any,
    val loginType: String,
    val socialId: String,
    val email_verified_at: Any,
    val City: Any,
    val Country: Any,
    val is_active: String,
    val api_token: String,
    val created_at: String,
    val updated_at: String
)