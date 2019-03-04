package com.verkoop.network

import com.verkoop.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET




interface MyService {

    @POST("user/register")
    fun userSignUpApi(@Body request: SignUpRequest): Call<SignUpResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginRequest): Call<LogInResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginSocialRequest): Call<SocialLoginResponse>

    @GET("categories")
    fun getCategoriesService(): Call<CategoriesResponse>

    @GET("user/profile/{userId}")
    fun getMyProfileService(@Path(value = "userId", encoded = true) fullUrl: String): Call<MyProfileResponse>

    @GET("items/{itemId}")
    fun getItemDetailsApi(@Path(value = "itemId", encoded = true) fullUrl: Int): Call<ItemDetailsResponse>

    @PUT("dashboard/{userId}")
    fun getHomeDataApi(@Path(value = "userId", encoded = true) fullUrl: String, @Query("page") pageCount: Int): Call<HomeDataResponse>

    @GET("items")
    fun getItemsService(): Call<HomeDataResponse>

    @POST("likes")
    fun likedApi(@Body request: LickedRequest): Call<LikedResponse>

    @DELETE("likes/{licked_id}")
    fun disLikedApi(@Path(value = "licked_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>


    @PUT("categoryFilterData/{user_id}")
    fun categoryPostApi(@Body request: CategoryPostRequest,@Path(value = "user_id", encoded = true) userId: String): Call<CategoryPostResponse>

    @POST("user/selectedUserCategroy")
    fun updateServiceApi(@Body request: UpdateCategoryRequest): Call<LikedResponse>

    @GET("nearbysearch/json")
    fun getDetails(@Query("location") loc: String,
                   @Query("radius") radius: String,
                   @Query("key") key: String): Call<PlaceApiResponse>

    @GET("textsearch/json")
    fun getSearchPlaceApi(@Query("query") keyWord: String,
                   @Query("key") key: String): Call<PlaceApiResponse>

    @Multipart
    @POST("items")
    fun addClothApi(@Part files: List<MultipartBody.Part>,
                    @Part("category_id") categoryId: RequestBody,
                    @Part("name") name: RequestBody,
                    @Part("price") price: RequestBody,
                    @Part("item_type") itemType: RequestBody,
                    @Part("description") description: RequestBody,
                    @Part("user_id") userId: RequestBody,
                    @Part("Address") address: RequestBody,
                    @Part("Latitude") lat: RequestBody,
                    @Part("Longitude") lng: RequestBody,
                    @Part("meet_up") meetUp: RequestBody): Call<AddItemResponse>

}