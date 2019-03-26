package com.verkoop.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

import java.util.concurrent.TimeUnit


 class ServiceGenerator{
    private val API_BASE_URL = "http://18.197.155.81/idress/index.php/"

    private val httpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create())

    private var retrofit: Retrofit? = null

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null, null)
    }


    fun <S> createService(
            serviceClass: Class<S>, username: String?, password: String?): S {
        if (!checkEmptyString(username) && !checkEmptyString(password)) {
            val authToken = Credentials.basic(username, password)
            return createService(serviceClass, authToken)
        }

        return createService(serviceClass, null, null)
    }

    private fun <S> createService(
            serviceClass: Class<S>, authToken: String): S {
        if (!checkEmptyString(authToken)) {
            val interceptor = AuthenticationInterceptor(authToken)
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(MyOkHttpInterceptor(authToken))
                httpClient.addInterceptor(logInterceptor)
                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }

        return retrofit!!.create(serviceClass)
    }

    private fun buildClient(): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .connectTimeout(70000, TimeUnit.SECONDS)
                .readTimeout(70000, TimeUnit.SECONDS)
                .build()
    }

     class MyOkHttpInterceptor internal constructor(internal val tokenServer: String) : Interceptor {

         @Throws(IOException::class)
         override fun intercept(chain: Interceptor.Chain): Response {
             val originalRequest = chain.request()
             val token = tokenServer// get token logic
             val newRequest = originalRequest.newBuilder()
                     .header("Authorization", token)
                     .build()
             return chain.proceed(newRequest)
         }
     }

     fun checkEmptyString(string: String?): Boolean {
         return string == null || string.trim { it <= ' ' }.isEmpty()
     }
}