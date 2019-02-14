package com.verkoop.models



data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val login_type: String,
    val country: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    val login_type: String
)
data class LoginSocialRequest(
    val first_name: String,
    val email: String,
    val social_id: String,
    val login_type: String
)
data class AddItemRequest(
        val imageList: List<String>,
        val categoriesId: String,
        val name: String,
        val price: String,
        val item_type: String,
        val description: String,
        val user_id: String
)