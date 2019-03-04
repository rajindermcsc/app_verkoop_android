package com.verkoop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


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

@Parcelize
data class AddItemRequest(
        val imageList: ArrayList<String>,
        val categoriesId: String,
        val categoryName: String,
        val name: String,
        val price: String,
        val item_type: String,
        val description: String,
        val user_id: String,
        val Latitude: String,
        val Longitude: String,
        val Address: String,
        val meet_up: String
): Parcelable

data class PlaceSearchRequest(
    val loc: String,
    val radius: String,
    val keyword: String,
    val key: String
)

data class LickedRequest(
    val user_id: String,
    val item_id: Int
)

data class CategoryPostRequest(
    val category_id: String,
    val type: Int,
    val userId: String
)

data class UpdateCategoryRequest(
    val user_id: String,
    val category_id: String
)