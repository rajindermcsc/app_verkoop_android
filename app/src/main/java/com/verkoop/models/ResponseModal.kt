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

data class ImageModal(
        var imageUrl: String,
        var isSelected: Boolean,
        var type: Boolean,
        var countSelect: Int
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


data class MyProfileResponse(
    val data: DataProfile,
    val message: String
)

data class DataProfile(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: Any,
    val last_name: Any,
    val mobile_no: Any,
    val website: Any,
    val bio: Any,
    val profile_pic: Any,
    val login_type: String,
    val social_id: Any,
    val city: Any,
    val country: String,
    val is_active: Int,
    val email_verified_at: Any,
    val created_at: String,
    val updated_at: String,
    val items: ArrayList<Item>
)

data class Item(
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val name: String,
    val price: Int,
    val item_type: Int,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val items_images: List<ProductImage>
)

data class ProductImage(
    val id: String,
    val item_id: String,
    val url: String,
    val created_at: String,
    val updated_at: String
)