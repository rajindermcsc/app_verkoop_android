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

    @POST("user/changePassword")
    fun updatePasswordApi(@Body request: UpdatePasswordRequest): Call<DisLikeResponse>

    @GET("categories")
    fun getCategoriesService(): Call<CategoriesResponse>

    @GET("user/profile/{userId}")
    fun getMyProfileService(@Path(value = "userId", encoded = true) fullUrl: String): Call<MyProfileResponse>

    @GET("items/{itemId}")
    fun getItemDetailsApi(@Path(value = "itemId", encoded = true) fullUrl: Int): Call<ItemDetailsResponse>

    @GET("getUserFavouriteData/{userId}")
    fun getFavouritesApi(@Path(value = "userId", encoded = true) fullUrl: String): Call<FavouritesResponse>

    @GET("items")
    fun getItemsService(): Call<HomeDataResponse>

    @POST("likes")
    fun likedApi(@Body request: LickedRequest): Call<LikedResponse>

    @DELETE("likes/{licked_id}")
    fun disLikedApi(@Path(value = "licked_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>


    @PUT("categoryFilterData/{user_id}")
    fun categoryPostApi(@Body request: FilterRequest,@Path(value = "user_id", encoded = true) userId: String): Call<CategoryPostResponse>

    @POST("user/selectedUserCategroy")
    fun updateServiceApi(@Body request: UpdateCategoryRequest): Call<LikedResponse>

    @PUT("dashboard/{userId}")
    fun getHomeDataApi(@Path(value = "userId", encoded = true) fullUrl: String, @Query("page") pageCount: Int): Call<HomeDataResponse>

    @GET("nearbysearch/json")
    fun getDetails(@Query("location") loc: String,
                   @Query("radius") radius: String,
                   @Query("key") key: String): Call<PlaceApiResponse>

    @GET("textsearch/json")
    fun getSearchPlaceApi(@Query("query") keyWord: String,
                   @Query("key") key: String): Call<PlaceApiResponse>

    @GET("user/profileData/{userId}")
    fun getMyProfileApi(@Path(value = "userId", encoded = true) fullUrl: String): Call<MyProfileIngoResponse>

    @POST("comments")
    fun postCommentApi(@Body request: PostCommentRequest): Call<CommentResponse>

    @DELETE("comments/{comment_id}")
    fun deleteCommentApi(@Path(value = "comment_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>

    @POST("reports")
    fun reportUserApi(@Body request: ReportUserRequest): Call<DisLikeResponse>

    @Multipart
    @POST("items")
    fun addClothApi(@Part files: List<MultipartBody.Part>,
                    @Part("category_id") categoryId: RequestBody,
                    @Part("name") name: RequestBody,
                    @Part("price") price: RequestBody,
                    @Part("item_type") itemType: RequestBody,
                    @Part("description") description: RequestBody,
                    @Part("user_id") userId: RequestBody,
                    @Part("address") address: RequestBody,
                    @Part("latitude") lat: RequestBody,
                    @Part("longitude") lng: RequestBody,
                    @Part("meet_up") meetUp: RequestBody): Call<AddItemResponse>


    @Multipart
    @POST("user/profileUpdate")
     fun editProfileWthOutImgApi(@Part("user_id") userId: RequestBody,
                                      @Part("username") userName: RequestBody,
                                      @Part("first_name") firstName: RequestBody,
                                      @Part("last_name") lastName: RequestBody,
                                      @Part("city") city: RequestBody,
                                      @Part("state") state: RequestBody,
                                      @Part("country") country: RequestBody,
                                      @Part("city_id") cityId: RequestBody,
                                      @Part("state_id") stateId: RequestBody,
                                      @Part("country_id") countryId: RequestBody,
                                      @Part("website") webSite: RequestBody,
                                      @Part("bio") bio: RequestBody,
                                      @Part("mobile_no") mobileNo: RequestBody,
                                      @Part("gender") gender: RequestBody,
                                      @Part("DOB") dob: RequestBody): Call<ProfileUpdateResponse>


    @Multipart
    @POST("user/profileUpdate")
     fun editProfileWthImageApi(@Part files: MultipartBody.Part,
                                      @Part("user_id") userId: RequestBody,
                                      @Part("username") userName: RequestBody,
                                      @Part("first_name") firstName: RequestBody,
                                      @Part("last_name") lastName: RequestBody,
                                      @Part("city") city: RequestBody,
                                      @Part("state") state: RequestBody,
                                      @Part("country") country: RequestBody,
                                      @Part("city_id") cityId: RequestBody,
                                      @Part("state_id") stateId: RequestBody,
                                      @Part("country_id") countryId: RequestBody,
                                      @Part("website") webSite: RequestBody,
                                      @Part("bio") bio: RequestBody,
                                      @Part("mobile_no") mobileNo: RequestBody,
                                      @Part("gender") gender: RequestBody,
                                      @Part("DOB") dob: RequestBody): Call<ProfileUpdateResponse>

}