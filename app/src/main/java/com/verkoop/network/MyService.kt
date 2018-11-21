package com.verkoop.network

import com.verkoop.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by intel on 19-11-2018.
 */
interface MyService{

   @POST("user/register")
   fun userSignUpApi(@Body request: SignUpRequest): Call<LoginResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginRequest): Call<LoginResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginSocialRequest): Call<LoginResponse>
}