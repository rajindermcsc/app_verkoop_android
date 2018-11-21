package com.verkoop.network

import android.util.Log
import com.google.gson.Gson
import com.verkoop.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ServiceHelper{
    interface OnResponse{
        fun onSuccess(response: Response<*>)
        fun onFailure(msg: String?)
    }

    fun userSignUPService(request: SignUpRequest, onResponse: OnResponse) {
        val myService = ApiClient.client
        val responseCall = myService.userSignUpApi(request)
        responseCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        res.status_code == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(res.message)
                    }
                }else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun userLoginService(request: LoginRequest, onResponse: OnResponse) {
        val myService = ApiClient.client
        val responseCall = myService.userLoginApi(request)
        responseCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        res.status_code == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(res.message)
                    }
                }else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }


    fun socialLoginService(request: LoginSocialRequest, onResponse: OnResponse) {
        val myService = ApiClient.client
        val responseCall = myService.userLoginApi(request)
        responseCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        res.status_code == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(res.message)
                    }
                }else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }
}

