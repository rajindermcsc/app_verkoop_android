package com.verkoop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CategoryModal(
        var itamName: String,
        var unselectedImage: Int,
        var selectedImage: Int,
        var selectedPosition: Boolean
) : Parcelable

data class SignUpResponse(
    val status: Int,
    val message: String,
    val data: DataSignUp
)

data class DataSignUp(
    val userId: Int,
    val email: String,
    val username: String,
    val FirstName: Any,
    val LastName: Any,
    val requestType: String,
    val socialId: Any,
    val Country: String,
    val is_active: String,
    val created_at: CreatedAt,
    val token: String
)


data class LoginResponse(
    val status: String,
    val status_code: Int,
    val message: String,
    val data: Data
)

data class Data(
    val userId: Int,
    val email: String,
    val username: String,
    val FirstName: Any,
    val LastName: Any,
    val requestType: String,
    val socialId: Any,
    val Country: String,
    val is_active: String,
    val created_at: CreatedAt,
    val token: String
)

data class CreatedAt(
    val date: String,
    val timezone_type: Int,
    val timezone: String
)

data class SocialResponse(
    val status_code: Int,
    val message: String,
    val data: DataSocial
)

data class DataSocial(
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
    val email: String,
    val loginType: String,
    val socialId: String,
    val email_verified_at: Any,
    val City: Any,
    val Country: String,
    val is_active: String,
    val api_token: String,
    val created_at: String,
    val updated_at: String
)

data class QuestionnaireResponseModel(
        val `data`: List<QuestionsDataModel> = ArrayList(),
        val message: String,
        val status: Int
)

data class QuestionsDataModel(
        val answers: List<String>,
        val quesId: String
)


data class ImageModal(
        var imageUrl: String,
        var isSelected: Boolean,
        var type: Boolean,
        var countSelect:Int
)
