package com.verkoop.network

import com.verkoop.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MyService{

   @POST("user/register")
   fun userSignUpApi(@Body request: SignUpRequest): Call<SocialResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginRequest): Call<LoginResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginSocialRequest): Call<SocialResponse>
}