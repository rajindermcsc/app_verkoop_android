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
    val likes_count: Int,
    val items_images: List<ItemsImage>,
    val likes: List<Likes>

)

data class Likes(
    val id: String,
    val item_id: String,
    val user_id: String,
    val created_at: String,
    val updated_at: String
)
data class ItemsImage(
    val id: Int,
    val item_id: Int,
    val url: String,
    val created_at: String,
    val updated_at: String
)


data class PlaceApiResponse(
    val html_attributions: List<Any>,
    val results: ArrayList<ResultLocation>,
    val status: String
)

data class ResultLocation(
    val geometry: Geometry,
    val icon: String,
    val id: String,
    val name: String,
    val opening_hours: OpeningHours,
    val photos: List<Photo>,
    val place_id: String,
    val plus_code: PlusCode,
    val rating: Double,
    val reference: String,
    val scope: String,
    val types: List<String>,
    val user_ratings_total: Int,
    val vicinity: String,
    val formatted_address: String
)

data class PlusCode(
    val compound_code: String,
    val global_code: String
)

data class OpeningHours(
    val open_now: Boolean
)

data class Geometry(
    val location: Location,
    val viewport: Viewport
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)

data class Southwest(
    val lat: Double,
    val lng: Double
)

data class Northeast(
    val lat: Double,
    val lng: Double
)

data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)


data class ItemDetailsResponse(
    val message: String,
    val data: DataItemDetails
)

data class DataItemDetails(
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val name: String,
    val price: Int,
    val item_type: Int,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val items_like_count: Int,
    val items_images: ArrayList<ItemsImageDetails>,
    val user: User,
    val items_like: List<Any>
)

data class ItemsImageDetails(
    val id: Int,
    val item_id: Int,
    val url: String,
    val created_at: String,
    val updated_at: String
)

data class User(
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
    val updated_at: String
)



data class HomeDataResponse(
    val data: DataHome,
    val message: String
)

data class DataHome(
    val advertisments: List<Advertisment>,
    val categories: ArrayList<Category>,
    val items: Items
)

data class Category(
    val id: Int,
    val name: String,
    val image: String,
    val parent_id: Int
)

data class Advertisment(
    val id: Int,
    val name: String,
    val image: String,
    val category_id: Int
)

data class Items(
    val current_page: Int,
    val data: List<DataImage>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class DataImage(
    val name: String,
    val price: Int,
    val item_type: Int,
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val items_images: List<ItemsImageHome>,
    val user: UserHome,
    val likes: LikesHome?=null
)

data class LikesHome(
    val item_id: Int,
    val id: Int,
    val user_id: Int
)
data class ItemsImageHome(
    val item_id: Int,
    val url: String
)

data class UserHome(
    val id: Int,
    val username: String
)