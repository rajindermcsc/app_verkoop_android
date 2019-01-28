package com.verkoop.models

import com.google.gson.annotations.SerializedName


data class SignUpRequest(
    val email: String,
    val Username: String,
    val password: String,
    val requestType: String,
    val Country: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    @SerializedName("requestType")
    val request_type: String
)
data class LoginSocialRequest(
    val FirstName: String,
    val email: String,
    val socialId: String,
    @SerializedName("requestType")
    val request_type: String
)