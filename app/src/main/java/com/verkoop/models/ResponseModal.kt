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


data class QuestionnaireResponseModel(
        val `data`: List<QuestionsDataModel> = ArrayList(),
        val message: String,
        val status: Int
)

data class QuestionsDataModel(
        val answers: List<String>,
        val quesId: String
)

data class QuestionsDataDialogModel(
        val answers: List<SubModal>,
        val quesId: String,
        var isSelected: Boolean
)

data class ImageModal(
        var imageUrl: String,
        var isSelected: Boolean,
        var type: Boolean,
        var countSelect: Int
)

data class SubModal(
        var subCategoryId: String,
        var subCategoyName: Int
)

data class SignUpResponse(
        val status: Int,
        val message: String,
        val data: DataSignUp
)

data class DataSignUp(
        val userId: Int,
        val email: String,
        val username: String,
        val first_name: Any,
        val last_name: Any,
        val login_type: String,
        val social_id: Any,
        val country: String,
        val is_active: Int,
        val created_at: CreatedAtSignUp,
        val token: String
)

data class CreatedAtSignUp(
        val date: String,
        val timezone_type: Int,
        val timezone: String
)

data class LogInResponse(
        val status: String,
        val status_code: Int,
        val message: String,
        val data: Data
)

data class Data(
        val userId: Int,
        val email: String,
        val username: String,
        val first_name: Any,
        val last_name: Any,
        val login_type: String,
        val social_id: Any,
        val country: String,
        val is_active: Int,
        val created_at: CreatedAt,
        val token: String
)

data class CreatedAt(
        val date: String,
        val timezone_type: Int,
        val timezone: String
)

data class CategoriesResponse(
        val data: ArrayList<DataCategory>,
        val message: String
)
@Parcelize
data class DataCategory(
        val id: Int,
        val name: String,
        val image: String,
        val parent_id: Int,
        val created_at: String,
        val updated_at: String,
        var isSelected: Boolean,
        val sub_category: ArrayList<SubCategory>
): Parcelable
@Parcelize
data class SubCategory(
        val id: Int,
        val name: String,
        val image: String,
        val parent_id: Int,
        val created_at: String,
        val updated_at: String
): Parcelable


data class AddItemResponse(
    val message: String
)

