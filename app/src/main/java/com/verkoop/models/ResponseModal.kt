package com.verkoop.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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
        val data: Data?
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
    val created_at: String,
    val profile_pic: String,
    val items: ArrayList<Item>
)

data class Item(
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val name: String,
    val price: Int,
    val item_type: Int,
    val created_at: CreatedAtProfile,
    val likes_count: Int,
    val like_id: Int,
    val is_like: Boolean,
    val image_url: String
)

data class CreatedAtProfile(
    val date: String,
    val timezone_type: Int,
    val timezone: String
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
    val data: DataItems
)

data class DataItems(
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
    val username: String,
    val profile_pic: String,
    val category_name: String,
    val items_image: List<ItemsImage>
)

data class ItemsImage(
    val item_id: Int,
    val url: String
)


data class HomeDataResponse(
    val data: DataHome,
    val message: String
)

data class DataHome(
    val items: ArrayList<ItemHome>,
    val advertisments: ArrayList<Advertisment>,
    val categories: ArrayList<Category>
)

data class Advertisment(
    val id: Int,
    val name: String,
    val image: String,
    val category_id: Int
)

data class Category(
    val id: Int,
    val name: String,
    val parent_id: Int,
    val image: String
)

data class ItemHome(
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val name: String,
    val price: Int,
    val item_type: Int,
    val created_at: CreatedAtHome,
    val likes_count: Int,
    val like_id: Int,
    val is_like: Boolean,
    val image_url: String,
    val username: String,
    val profile_pic: String
)

data class CreatedAtHome(
    val date: String,
    val timezone_type: Int,
    val timezone: String
)

data class LikedResponse(
    val message: String,
    val like_id: Int
)

data class DisLikeResponse(
    val message: String
)