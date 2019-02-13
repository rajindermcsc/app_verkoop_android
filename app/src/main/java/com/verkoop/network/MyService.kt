package com.verkoop.network

import com.verkoop.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface MyService{

   @POST("user/register")
   fun userSignUpApi(@Body request: SignUpRequest): Call<SignUpResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginRequest): Call<LogInResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginSocialRequest): Call<SignUpResponse>

    @GET("categories")
    fun getCategoriesService(): Call<CategoriesResponse>

    @Multipart
    @POST("items")
     fun addClothApi(@Part files: List<MultipartBody.Part>,
                             @Part("category_id") categoryId: RequestBody,
                             @Part("name") name: RequestBody,
                             @Part("price") price: RequestBody,
                             @Part("item_type") itemType: RequestBody,
                             @Part("description") description: RequestBody): Call<AddItemResponse>

}