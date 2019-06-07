package com.verkoopapp.models

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

@Parcelize
data class ImageModal(
        var imageUrl: String,
        var isSelected: Boolean,
        var type: Boolean,
        var countSelect: Int,
        var imagePosition: Int,
        var iseditable: Boolean,
        var imageId: Int

) : Parcelable

data class SignUpResponse(
        val message: String,
        val data: DataSignUp?
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
        val is_use: Int,
        val created_at: CreatedAtSignUp,
        val token: String,
        val qrCode_image: String
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
        val is_use: Int,
        val created_at: CreatedAt,
        val token: String,
        val mobile_no: String,
        val qrCode_image: String
)

@Parcelize
data class CreatedAt(
        val date: String,
        val timezone_type: Int,
        val timezone: String
) : Parcelable

data class CategoriesResponse(
        val data: ArrayList<DataCategory>?,
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
) : Parcelable

@Parcelize
data class SubCategory(
        val id: Int,
        val name: String,
        val image: String,
        val parent_id: Int,
        val created_at: String,
        val updated_at: String
) : Parcelable


data class AddItemResponse(
        val message: String
)


data class MyProfileResponse(
        val data: DataProfile?,
        val message: String
)

data class DataProfile(
        val id: Int,
        val username: String,
        val created_at: String,
        val profile_pic: String,
        val email: String,
        val mobile_no: String,
        val website: String,
        val city: String,
        val state: String,
        val country: String,
        val city_id: String,
        val state_id: String,
        val country_id: String,
        val follow_id: Int,
        val follow_count: Int,
        val follower_count: Int,
        val items: ArrayList<Item>
)

data class Item(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        val price: Double,
        val item_type: Int,
        val created_at: CreatedAtProfile,
        @SerializedName("items_like_count")
        var likes_count: Int,
        var like_id: Int,
        var is_like: Boolean,
        var is_sold: Int,
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
        val data: DataItems?
)

@Parcelize
data class DataItems(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        val price: Double=0.00,
        val item_type: Int,
        val description: String,
        val created_at: String,
        val updated_at: String,
        val items_like_count: Int,
        val username: String,
        val profile_pic: String,
        val category_name: String,
        val address: String?,
        val latitude: Double,
        val longitude: Double,
        val meet_up: Int,
        val is_sold: Int,
        val items_image: List<ItemsImage>,
        var comments: ArrayList<CommentModal>?,
        var reports: ArrayList<ReportResponse>?,
        var type:Int=0,
        var brand_id :Int=0,
        var car_type_id : Int=0,
        var additional_info:AdditionalInfoResponse?=null,
        val zone_id:Int=0,
        var offer_price:Double= 0.0,
        val chat_count:Int=0,
        var make_offer:Boolean= false,
        var message_count:Int= 0

) : Parcelable

@Parcelize
data class AdditionalInfoResponse(
    val city: String?=null,
    val bathroom: Int,
    val bedroom: Int,
    val brand_name: String?=null,
    val car_brand_id: Int,
    val car_type: String?=null,
    val car_type_id: Int,
    val direct_owner: Int,
    val postal_code: String?=null,
    val registration_year: String?=null,
    val street_name: String?=null,
    val location: String?=null,
    val zoneId: Int,
    val min_price:Double=0.0,
    val max_price:Double=0.0,
    val property_type:String?=null,
    val parking_type:Int?=0,
    val from_year:Int?=0,
    val to_year:Int?=0,
    val furnished:Int?=0
): Parcelable

@Parcelize
data class ReportResponse(
        val id: Int,
        val name: String,
        val description: String,
        val notes: String,
        val type: Int,
        val created_at: String,
        val updated_at: String,
        var isSelected: Boolean
) : Parcelable


@Parcelize
data class CommentModal(
        val username: String,
        val profile_pic: String,
        val id: Int,
        val user_id: Int,
        val comment: String,
        val created_at: String
) : Parcelable


@Parcelize
data class ItemsImage(
        val item_id: Int,
        val image_id: Int,
        val url: String
) : Parcelable


data class HomeDataResponse(
        val data: DataHome?,
        val message: String
)

data class DataHome(
        val items: ArrayList<ItemHome>,
        val advertisments: ArrayList<Advertisment>,
        val categories: ArrayList<Category>,
        val daily_pic: ArrayList<ItemHome>,
        val totalPage: Int
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
        val price: Double,
        val item_type: Int,
        val created_at: CreatedAtHome,
        var items_like_count: Int,
        var like_id: Int,
        var is_like: Boolean,
        var is_sold: Int,
        val image_url: String,
        val username: String,
        val profile_pic: String,
        var isClicked: Boolean=false
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

data class SocialGoogleResponse(
    val data: DataGoogle?,
    val message: String
)

data class DataGoogle(
    val userId: Int,
    val email: String,
    val username: String,
    val first_name: String,
    val last_name: String,
    val login_type: String,
    val social_id: String,
    val mobile_no: String,
    val website: String,
    val city: String,
    val state: String,
    val country: String,
    val city_id: Int,
    val state_id: Int,
    val country_id: Int,
    val bio: String,
    val gender: String,
    val DOB: String,
    val profile_pic: String,
    val is_active: Int,
    val is_use: Int,
    val mobile_verified: Int,
    val created_at: CreatedAt,
    val token: String,
    val qrCode_image: String
)

data class SocialLoginResponse(
        val status_code: Int,
        val message: String,
        val data: DataSocial?
)

data class DataSocial(
        val id: Int,
        val username: String,
        val email: String,
        val first_name: String,
        val last_name: String,
        val mobile_no: String,
        val website: String,
        val bio: String,
        val profile_pic: String,
        val login_type: String,
        val social_id: String,
        val city: String,
        val country: String,
        val is_active: Int,
        val is_use: Int,
        val email_verified_at: Any,
        val created_at: String,
        val updated_at: String,
        val api_token: String
)

data class CategoryPostResponse(
        val data: DataPost,
        val message: String
)

data class DataPost(
        val subCategoryList: ArrayList<SubCategoryPost>,
        val items: ArrayList<ItemHome>
)

data class ItemData(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        val price: Int,
        val item_type: Int,
        val created_at: CreatedAt,
        val like_id: Int,
        val is_like: Boolean,
        val items_like_count: Int,
        val image_url: String,
        val username: String,
        val profile_pic: String
)

data class SubCategoryPost(
        val image: String,
        val name: String,
        val id: Int
)

data class FavouritesResponse(
        val data: ArrayList<ItemHome>,
        val message: String
)

/*data class Data(
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val name: String,
    val price: Int,
    val item_type: Int,
    val created_at: CreatedAt,
    val like_id: Int,
    val is_like: Boolean,
    val items_like_count: Int,
    val image_url: String,
    val username: String,
    val profile_pic: String
)*/

data class RegionResponse(
        val country: ArrayList<Country>
)

data class Country(
        val id: Int,
        val name: String,
        val states: ArrayList<State>
)

data class State(
        val id: Int,
        val name: String,
        var isSelected: Boolean,
        val cities: ArrayList<City>
)

@Parcelize
data class City(
        val id: Int,
        var isSelected: Boolean,
        val name: String
) : Parcelable


data class ProfileUpdateResponse(
        val data: DataProfileUpdate?,
        val message: String
)

data class DataProfileUpdate(
        val userId: Int,
        val email: String,
        val username: String,
        val first_name: String,
        val last_name: String,
        val login_type: String,
        val social_id: String,
        val country: String,
        val mobile_no: String,
        val website: String,
        val city: String,
        val bio: String,
        val gender: String,
        val DOB: String,
        val profile_pic: String,
        val is_active: Int,
        val is_use: Int,
        val mobile_verified: Int,
        val created_at: CreatedAt
)


data class MyProfileIngoResponse(
        val data: DataGetProfile?,
        val message: String
)

data class DataGetProfile(
        val id: Int,
        val username: String,
        val first_name: String,
        val last_name: String,
        val city: String,
        val state: String,
        val country: String,
        val city_id: Int,
        val state_id: Int,
        val country_id: Int,
        val website: String,
        val bio: String,
        val profile_pic: String,
        val email: String,
        val mobile_no: String,
        val gender: String,
        val DOB: String
)

data class CommentResponse(
        val message: String,
        val data: CommentModal?
)

@Parcelize
data class DataComment(
        val id: Int,
        val comment: String,
        val created_at: CreatedAt,
        val username: String,
        val profile_pic: String
) : Parcelable


data class FollowResponse(
        val message: String,
        val data: DataFollow
)

data class DataFollow(
        val follower_id: String,
        val user_id: String,
        val updated_at: String,
        val created_at: String,
        val id: Int
)


data class UserProfileResponse(
        val data: DataUserProfile?,
        val message: String
)

data class DataUserProfile(
        val id: Int,
        val username: String,
        val created_at: String,
        val profile_pic: String,
        val email: String,
        val mobile_no: String,
        val website: String,
        val city: String,
        val state: String,
        val country: String,
        val city_id: Int,
        val state_id: Int,
        val country_id: Int,
        val follow_count: Int,
        val follower_count: Int,
        val follower_id: Int,
        val block_id: Int,
        val items: ArrayList<ItemUserProfile>,
        val reports: ArrayList<ReportResponse>
)

data class ItemUserProfile(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        val price: Double,
        val item_type: Int,
        val created_at: CreatedAt,
        var like_id: Int,
        var is_like: Boolean,
        var items_like_count: Int,
        val image_url: String,
        val username: String,
        val profile_pic: String,
        val is_sold: Int
)


@Parcelize
data class Report(
        val id: Int,
        val name: String,
        val description: String,
        val notes: String,
        val type: Int,
        val created_at: String,
        val updated_at: String
) : Parcelable

data class BlockUserResponse(
        val message: String,
        val data: DataBlock?
)

data class DataBlock(
        val user_block_id: String,
        val user_id: String,
        val updated_at: String,
        val created_at: String,
        val id: Int
)

data class SearchItemResponse(
        val message: String,
        val data: ArrayList<DataSearch>
)

data class DataSearch(
        val id: Int,
        val category_id: Int,
        val name: String,
        val category_name: String,
        val category: CategorySearch
)

data class CategorySearch(
        val id: Int,
        val name: String,
        val parent_id: Int
)

data class SearchByUserResponse(
        val message: String,
        val data: ArrayList<DataUser>
)

data class DataUser(
        val id: Int,
        val username: String,
        val profile_pic: String,
        var follower_id: Int,
        var isClicked: Boolean
)

data class CarBrandResponse(
    val data: ArrayList<DataCarBrand>,
    val message: String
)

data class DataCarBrand(
    val id: Int,
    val name: String,
    val image: String?=null,
    val created_at: String?=null,
    val updated_at: String?=null,
    var isSelected: Boolean=false
)

data class BuyCarResponse(
    val data: DataCarResponse?,
    val message: String
)

data class DataCarResponse(
    val items: ArrayList<ItemHome>,
    val totalPage: Int,
    val brands: ArrayList<Brand>,
    val car_types: ArrayList<CarType>
)

data class CarType(
    val id: Int,
    val name: String,
    val image: String,
    val created_at: String?=null,
    val updated_at: String?=null
)

@Parcelize
data class Brand(
    val id: Int,
    val name: String,
    val image: String,
    val created_at: String?=null,
    val updated_at: String?=null
): Parcelable



data class CoinPlanResponse(
    var data: ArrayList<CoinPlan>,
    var message: String,
    var coins :  Int
)

data class CoinPlan(
    var id: Int,
    var amount: Int,
    var coin: Int,
    var is_active: Int,
    var created_at: String,
    var updated_at: String
)

data class AdvertPlanActivity(
    var data: ArrayList<DataAdvert>,
    var message: String
)

data class DataAdvert(
    var id: Int,
    var name: String,
    var day: Int,
    var coin: Int,
    var is_active: Int,
    var created_at: String,
    var updated_at: String
)

data class UpdateWalletResponse(
    var data: List<DataWalletAdd>,
    var amount: Int,
    var message: String
)

data class DataWalletAdd(
    var id: Int,
    var user_id: Int,
    var token: String,
    var amount: Int,
    var status: Int,
    var created_at: String,
    var updated_at: String
)
data class WalletHistoryResponse(
    var data: ArrayList<DataHistory>?,
    var amount: Int,
    var message: String
)

data class DataHistory(
    var id: Int,
    var type:Int,
    var user_id: Int,
    var token: String,
    var userName: String,
    var profilePic: String,
    var amount: Int,
    var status: Int,
    var coin_plan_id: Int,
    var is_active: Int,
    var coin: Int,
    var created_at: String,
    var updated_at: String
)
data class ViewAllBannerResponse(
    var data: ArrayList<DataBanner>
)

data class DataBanner(
    var id: Int,
    var image: String,
    var updated_at: String,
    var day: Int,
    var advertisement_plan_id: Int
)

data class UserInfoResponse(
    var data: DataInfoUser?,
    var message: String
)

data class DataInfoUser(
    var id: Int,
    var username: String,
    var first_name: String,
    var last_name: String,
    var profile_pic: String
)

data class ChatImageResponse(
    var data: Datamage,
    var message: String
)

data class Datamage(
    var image: String
)