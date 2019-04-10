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
) : Parcelable

data class PlaceSearchRequest(
        val loc: String,
        val radius: String,
        val keyword: String,
        val key: String
)

@Parcelize
data class SelectedImage(
        val imageUrl: String,
        val adapterPosition: Int
) : Parcelable

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

@Parcelize
data class FilterRequest(
        val category_id: String,
        val type: Int,
        val userId: String,
        val sort_no: String,
        val latitude: String,
        val longitude: String,
        val item_type: String,
        val meet_up: String,
        val min_price: String,
        val max_price: String
) : Parcelable

@Parcelize
data class FilterModal(
    val FilterType: String,
    val FilterName: String,
    val isClicked: Boolean,
    val type: Int
): Parcelable



data class ProfileUpdateRequest(
    val user_id: String,
    val username: String,
    val first_name: String,
    val last_name: String,
    val city: String,
    val state: String,
    val country: String,
    val City_id: String,
    val State_id: String,
    val country_id: String,
    val website: String,
    val bio: String,
    val profile_pic: String,
    val mobile_no: String,
    val gender: String,
    val DOB: String
)

data class UpdatePasswordRequest(
    val user_id: String,
    val current_password: String,
    val new_password: String
)


data class PostCommentRequest(
    val user_id: String,
    val item_id: Int,
    val comment: String
)

data class ReportUserRequest(
    val user_id: String,
    val item_id: Int,
    val report_id: Int,
    val type: Int


)

data class FollowRequest(
    val user_id: String,
    val follower_id: Int
)

data class BlockUserRequest(
    val user_id: String,
    val user_block_id: Int
)

data class MarkAsSoldRequest(
    val user_id: String
)

class MessageEvent(
        val updateUi:String
)

data class SearchItemRequest(
    val name: String
)