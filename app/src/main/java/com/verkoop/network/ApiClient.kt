package com.verkoop.network

import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/*This class used to hit service*/
object ApiClient {
    private const val BASE_URL = "http://ec2-18-222-175-74.us-east-2.compute.amazonaws.com/trivia/index.php/"
    private var retrofit: Retrofit? = null

    val client: MyService
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                                .setLenient()
                                .create()))
                        .client(buildClient())
                        .build()
            }
            return retrofit!!.create(MyService::class.java)
        }

    private fun buildClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build()
    }


    /* public static class MyOkHttpInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            String token ="";// get token logic
            Request newRequest = originalRequest.newBuilder()
                    .header("X-Authorization", token)
                    .build();
            return chain.proceed(newRequest);
        }
    }*/

}