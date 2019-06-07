package com.verkoopapp.network

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import com.verkoopapp.models.*

import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.LOG_TAG
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*


class ServiceHelper {
    interface OnResponse {
        fun onSuccess(response: Response<*>)
        fun onFailure(msg: String?)
    }

    fun userSignUPService(request: SignUpRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userSignUpApi(request)
        responseCall.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun userLoginService(request: LoginRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userLoginApi(request)
        responseCall.enqueue(object : Callback<LogInResponse> {
            override fun onResponse(call: Call<LogInResponse>, response: Response<LogInResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<LogInResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }


    fun socialLoginService(request: LoginSocialRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userLoginApi(request)
        responseCall.enqueue(object : Callback<SocialGoogleResponse> {
            override fun onResponse(call: Call<SocialGoogleResponse>, response: Response<SocialGoogleResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SocialGoogleResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun categoriesService(onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getCategoriesService()
        responseCall.enqueue(object : Callback<CategoriesResponse> {
            override fun onResponse(call: Call<CategoriesResponse>, response: Response<CategoriesResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun addItemsApi(request: AddItemRequest, onResponse: OnResponse) {
        //  val service = ServiceGenerator().createService(MyService::class.java, "idress", "idress")
        val myService = ApiClient.getClient().create(MyService::class.java)
        val parts = ArrayList<MultipartBody.Part>()
        for (i in 0 until request.imageList.size) {
            if (!TextUtils.isEmpty(request.imageList[i])) {
                val file = File(request.imageList[i])
                val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
                val body = MultipartBody.Part.createFormData("image[]", file.name, reqFile)
                parts.add(body)
            }
        }
        val call: Call<AddItemResponse>
        val categoryId = RequestBody.create(MediaType.parse("text/plain"), request.categoriesId)
        val name = RequestBody.create(MediaType.parse("text/plain"), request.name)
        val price = RequestBody.create(MediaType.parse("text/plain"), request.price)
        val itemType = RequestBody.create(MediaType.parse("text/plain"), request.item_type)
        val description = RequestBody.create(MediaType.parse("text/plain"), request.description)
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id)
        val address = RequestBody.create(MediaType.parse("text/plain"), request.Address)
        val lat = RequestBody.create(MediaType.parse("text/plain"), request.Latitude)
        val lng = RequestBody.create(MediaType.parse("text/plain"), request.Longitude)
        val meetUp = RequestBody.create(MediaType.parse("text/plain"), request.meet_up)
        val type = RequestBody.create(MediaType.parse("text/plain"), request.type.toString())
        val carBrandId = RequestBody.create(MediaType.parse("text/plain"), request.brand_id.toString())
        val carType = RequestBody.create(MediaType.parse("text/plain"), request.car_type_id.toString())
        val zoneId = RequestBody.create(MediaType.parse("text/plain"), request.zone_id.toString())
        val prettyGson = GsonBuilder().setPrettyPrinting().create()
        val prettyJson = prettyGson.toJson(request.additional_info)

        Log.e("<<stringRequest>>", prettyJson)
        val additionalInfo = RequestBody.create(okhttp3.MultipartBody.FORM, prettyJson)
        call = myService.addClothApi(parts, categoryId, name, price, itemType, description, userId, address, lat, lng, meetUp, type, carBrandId, carType, additionalInfo, zoneId)
        call.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.body() != null) {
                        onResponse.onFailure(response.body()!!.message)
                    } else {
                        onResponse.onFailure("Something went wrong!")
                    }

                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun myProfileService(userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getMyProfileService(userId)
        responseCall.enqueue(object : Callback<MyProfileResponse> {
            override fun onResponse(call: Call<MyProfileResponse>, response: Response<MyProfileResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getPlacesService(placeSearchRequest: PlaceSearchRequest, onResponse: OnResponse) {
        val service = ApiClientLocation.client
        val responseCall = service.getDetails(placeSearchRequest.loc, placeSearchRequest.radius, placeSearchRequest.key)
        responseCall.enqueue(object : Callback<PlaceApiResponse> {
            override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getSearchPlaceService(placeSearchRequest: PlaceSearchRequest, onResponse: OnResponse) {
        val service = ApiClientLocation.client
        val responseCall = service.getSearchPlaceApi(placeSearchRequest.keyword, placeSearchRequest.key)
        responseCall.enqueue(object : Callback<PlaceApiResponse> {
            override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getItemDetailService(itemId: Int,userId:Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getItemDetailsApi(itemId,userId)
        responseCall.enqueue(object : Callback<ItemDetailsResponse> {
            override fun onResponse(call: Call<ItemDetailsResponse>, response: Response<ItemDetailsResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<ItemDetailsResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getHomeDataService(userId: String, pageCount: Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getItemsService()
        responseCall.enqueue(object : Callback<HomeDataResponse> {
            override fun onResponse(call: Call<HomeDataResponse>, response: Response<HomeDataResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<HomeDataResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getItemsService(homeRequest:HomeRequest,pageCount: Int, userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getHomeDataApi(homeRequest,userId, pageCount)
        responseCall.enqueue(object : Callback<HomeDataResponse> {
            override fun onResponse(call: Call<HomeDataResponse>, response: Response<HomeDataResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<HomeDataResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getBuyCarService(homeRequest:HomeRequest,pageCount: Int, userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getBuyCarDataApi(homeRequest,userId, pageCount)
        responseCall.enqueue(object : Callback<BuyCarResponse> {
            override fun onResponse(call: Call<BuyCarResponse>, response: Response<BuyCarResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<BuyCarResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun likeService(lickedRequest: LickedRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.likedApi(lickedRequest)
        responseCall.enqueue(object : Callback<LikedResponse> {
            override fun onResponse(call: Call<LikedResponse>, response: Response<LikedResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 201 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<LikedResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun disLikeService(lickedId: Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.disLikedApi(lickedId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun categoryPostService(categoryPostRequest: FilterRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.categoryPostApi(categoryPostRequest, categoryPostRequest.userId)
        responseCall.enqueue(object : Callback<CategoryPostResponse> {
            override fun onResponse(call: Call<CategoryPostResponse>, response: Response<CategoryPostResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CategoryPostResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun updateCategoryService(updateCategoryRequest: UpdateCategoryRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.updateServiceApi(updateCategoryRequest)
        responseCall.enqueue(object : Callback<LikedResponse> {
            override fun onResponse(call: Call<LikedResponse>, response: Response<LikedResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<LikedResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getFavouritesService(userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getFavouritesApi(userId)
        responseCall.enqueue(object : Callback<FavouritesResponse> {
            override fun onResponse(call: Call<FavouritesResponse>, response: Response<FavouritesResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }


    fun updateProfileService(request: ProfileUpdateRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        var body: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(request.profile_pic)) {
            val file = File(request.profile_pic)
            val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            body = MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)
        }

        var call: Call<ProfileUpdateResponse>? = null
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id)
        val userName = RequestBody.create(MediaType.parse("text/plain"), request.username)
        val firstName = RequestBody.create(MediaType.parse("text/plain"), request.first_name)
        val lastName = RequestBody.create(MediaType.parse("text/plain"), request.last_name)
        val city = RequestBody.create(MediaType.parse("text/plain"), request.city)
        val state = RequestBody.create(MediaType.parse("text/plain"), request.state)
        val country = RequestBody.create(MediaType.parse("text/plain"), request.country)
        val cityId = RequestBody.create(MediaType.parse("text/plain"), request.City_id)
        val stateId = RequestBody.create(MediaType.parse("text/plain"), request.State_id)
        val countryId = RequestBody.create(MediaType.parse("text/plain"), request.country_id)
        val webSite = RequestBody.create(MediaType.parse("text/plain"), request.website)
        val bio = RequestBody.create(MediaType.parse("text/plain"), request.bio)
        val mobileNo = RequestBody.create(MediaType.parse("text/plain"), request.mobile_no)
        val gender = RequestBody.create(MediaType.parse("text/plain"), request.gender)
        val dob = RequestBody.create(MediaType.parse("text/plain"), request.DOB)

        call = if (body != null) {
            myService.editProfileWthImageApi(body, userId, userName, firstName, lastName, city, state, country, cityId, stateId, countryId, webSite, bio, mobileNo, gender, dob)
        } else {
            myService.editProfileWthOutImgApi(userId, userName, firstName, lastName, city, state, country, cityId, stateId, countryId, webSite, bio, mobileNo, gender, dob)
        }

        call.enqueue(object : Callback<ProfileUpdateResponse> {
            override fun onResponse(call: Call<ProfileUpdateResponse>, response: Response<ProfileUpdateResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    onResponse.onFailure(response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<ProfileUpdateResponse>, t: Throwable) {
                Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun getMyProfileInfoService(userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getMyProfileApi(userId)
        responseCall.enqueue(object : Callback<MyProfileIngoResponse> {
            override fun onResponse(call: Call<MyProfileIngoResponse>, response: Response<MyProfileIngoResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<MyProfileIngoResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun updatePasswordService(request: UpdatePasswordRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.updatePasswordApi(request)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun postCommentService(request: PostCommentRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.postCommentApi(request)
        responseCall.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {

                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun deleteCommentService(commentId: Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.deleteCommentApi(commentId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun reportAddedService(request: ReportUserRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.reportUserApi(request)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun followService(request: FollowRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.followApi(request)
        responseCall.enqueue(object : Callback<FollowResponse> {
            override fun onResponse(call: Call<FollowResponse>, response: Response<FollowResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun unFollowService(followId: Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.unFollowApi(followId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun blockUserService(request: BlockUserRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.blockUserApi(request)
        responseCall.enqueue(object : Callback<BlockUserResponse> {
            override fun onResponse(call: Call<BlockUserResponse>, response: Response<BlockUserResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<BlockUserResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun unBlockUserService(followId: Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.unBlockUserApi(followId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun userProfileService(request: FollowRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userProfileApi(request)
        responseCall.enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun markAsSoldService(item_id: Int, markAsSoldRequest: MarkAsSoldRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.markAsSoldApi(item_id, markAsSoldRequest)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun deleteListingService(itemId: Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.deleteListingApi(itemId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun searchItemService(request: SearchItemRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.searchItemApi(request)
        responseCall.enqueue(object : Callback<SearchItemResponse> {
            override fun onResponse(call: Call<SearchItemResponse>, response: Response<SearchItemResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SearchItemResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun searchByUserService(UserId: Int, request: SearchUserRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.searchByUserApi(UserId, request)
        responseCall.enqueue(object : Callback<SearchByUserResponse> {
            override fun onResponse(call: Call<SearchByUserResponse>, response: Response<SearchByUserResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SearchByUserResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun forgotPasswordService(request: ForgotPasswordRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.forgotPasswordApi(request)
        responseCall.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun editItemsApi(request: EditItemRequest, onResponse: OnResponse) {
        //  val service = ServiceGenerator().createService(MyService::class.java, "idress", "idress")
        val myService = ApiClient.getClient().create(MyService::class.java)
        val parts = ArrayList<MultipartBody.Part>()
        for (i in 0 until request.imageList.size) {
            if (!TextUtils.isEmpty(request.imageList[i])) {
                val file = File(request.imageList[i])
                val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
                val body = MultipartBody.Part.createFormData("image[]", file.name, reqFile)
                parts.add(body)
            }
        }
        val call: Call<AddItemResponse>
        val categoryId = RequestBody.create(MediaType.parse("text/plain"), request.categoriesId)
        val name = RequestBody.create(MediaType.parse("text/plain"), request.name)
        val price = RequestBody.create(MediaType.parse("text/plain"), request.price)
        val itemType = RequestBody.create(MediaType.parse("text/plain"), request.item_type)
        val description = RequestBody.create(MediaType.parse("text/plain"), request.description)
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id)
        val address = RequestBody.create(MediaType.parse("text/plain"), request.Address)
        val lat = RequestBody.create(MediaType.parse("text/plain"), request.Latitude)
        val lng = RequestBody.create(MediaType.parse("text/plain"), request.Longitude)
        val meetUp = RequestBody.create(MediaType.parse("text/plain"), request.meet_up)
        val itemId = RequestBody.create(MediaType.parse("text/plain"), request.item_id.toString())
        val deleteImageId = RequestBody.create(MediaType.parse("text/plain"), request.deleteImageList)

        val type = RequestBody.create(MediaType.parse("text/plain"), request.type.toString())
        val carBrandId = RequestBody.create(MediaType.parse("text/plain"), request.brand_id.toString())
        val carType = RequestBody.create(MediaType.parse("text/plain"), request.car_type_id.toString())
        val zoneId = RequestBody.create(MediaType.parse("text/plain"), request.zone_id.toString())

        val prettyGson = GsonBuilder().setPrettyPrinting().create()
        val prettyJson = prettyGson.toJson(request.additional_info)
        val additionalInfo = RequestBody.create(okhttp3.MultipartBody.FORM, prettyJson)
        if (request.imageList.size > 0) {
            call = myService.updateProductApi(parts, deleteImageId, itemId, categoryId, name, price, itemType, description, userId, address, lat, lng, meetUp,type,carBrandId,carType,additionalInfo,zoneId)
        } else {
            call = myService.updateWithoutImageApi(deleteImageId, itemId, categoryId, name, price, itemType, description, userId, address, lat, lng, meetUp,type,carBrandId,carType,additionalInfo,zoneId)
        }
        call.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.body() != null) {
                        onResponse.onFailure(response.body()!!.message)
                    } else {
                        onResponse.onFailure("Something went wrong!")
                    }

                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun getCarBrandService(comingFrom: Int, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall: Call<CarBrandResponse>
        responseCall = if (comingFrom != 0) {
            myService.getCarTypeApi()

        } else {
            myService.getCarBrandApi()
        }
        responseCall.enqueue(object : Callback<CarBrandResponse> {
            override fun onResponse(call: Call<CarBrandResponse>, response: Response<CarBrandResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CarBrandResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }
    fun getCarFilterService(userId: Int,carFilterRequest: CarsFilterRequest ,onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.carsFilterApi(userId,carFilterRequest)
        responseCall.enqueue(object : Callback<FavouritesResponse> {
            override fun onResponse(call: Call<FavouritesResponse>, response: Response<FavouritesResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun followFollowingService(UserId: Int, request: HomeRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.followFollowingApi(UserId, request)
        responseCall.enqueue(object : Callback<SearchByUserResponse> {
            override fun onResponse(call: Call<SearchByUserResponse>, response: Response<SearchByUserResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SearchByUserResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun addMoneyService(request: AddMoneyRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.addMoneyApi(request)
        responseCall.enqueue(object : Callback<UpdateWalletResponse> {
            override fun onResponse(call: Call<UpdateWalletResponse>, response: Response<UpdateWalletResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateWalletResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getWalletHistoryService(userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getWalletHistoryApi(userId.toInt())
        responseCall.enqueue(object : Callback<WalletHistoryResponse> {
            override fun onResponse(call: Call<WalletHistoryResponse>, response: Response<WalletHistoryResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<WalletHistoryResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getCoinPlanService( userUd:Int,onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getCoinPlanApi(userUd)
        responseCall.enqueue(object : Callback<CoinPlanResponse> {
            override fun onResponse(call: Call<CoinPlanResponse>, response: Response<CoinPlanResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CoinPlanResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getAdvertisementPlanService( onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getAdvertPlanApi()
        responseCall.enqueue(object : Callback<AdvertPlanActivity> {
            override fun onResponse(call: Call<AdvertPlanActivity>, response: Response<AdvertPlanActivity>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<AdvertPlanActivity>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun purchaseCoinService(request: PurchaseCoinRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.coinPurchaseApi(request)
        responseCall.enqueue(object : Callback<UpdateWalletResponse> {
            override fun onResponse(call: Call<UpdateWalletResponse>, response: Response<UpdateWalletResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())

                            val messageE = JSONObject(messageError.getString("errors"))
                            val lessAmount=JSONArray(messageE.getString("less_amount"))
                            val value=lessAmount[0].toString()

                           // onResponse.onFailure(messageError.getString("message"))
                            onResponse.onFailure(value)
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateWalletResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getCoinHistoryService(userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getCoinHistoryApi(userId.toInt())
        responseCall.enqueue(object : Callback<WalletHistoryResponse> {
            override fun onResponse(call: Call<WalletHistoryResponse>, response: Response<WalletHistoryResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<WalletHistoryResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun updateBannerService(request: UploadBannerRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        var body: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(request.banner)) {
            val file = File(request.banner)
            val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            body = MultipartBody.Part.createFormData("banner", file.name, reqFile)
        }
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id.toString())
        val advertPlan = RequestBody.create(MediaType.parse("text/plain"), request.advertisement_plan_id.toString())

        val call = myService.uploadBannerApi(body!!, userId, advertPlan)
        call.enqueue(object : Callback<ProfileUpdateResponse> {
            override fun onResponse(call: Call<ProfileUpdateResponse>, response: Response<ProfileUpdateResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    try {
                        val messageError = JSONObject(response.errorBody()!!.string())
                        val messageE = JSONObject(messageError.getString("errors"))
                        val lessAmount=JSONArray(messageE.getString("less_coin"))
                        val value=lessAmount[0].toString()
                        onResponse.onFailure(value)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onFailure(call: Call<ProfileUpdateResponse>, t: Throwable) {
                Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun getAlBannerService(userId: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getAllBannerApi(userId)
        responseCall.enqueue(object : Callback<ViewAllBannerResponse> {
            override fun onResponse(call: Call<ViewAllBannerResponse>, response: Response<ViewAllBannerResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<ViewAllBannerResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun sendQrService(request: SendQrCodeRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.qrCodeApi(request)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            val messageE = JSONObject(messageError.getString("errors"))
                            val lessAmount=JSONArray(messageE.getString("less_coin"))
                            val value=lessAmount[0].toString()
                            onResponse.onFailure(value)
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getAllUserInfoService(token: String, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.getUserInfoApi(token)
        responseCall.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun rateUserService(request: RateUserRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.rateUserApi(request)
        responseCall.enqueue(object : Callback<UpdateWalletResponse> {
            override fun onResponse(call: Call<UpdateWalletResponse>, response: Response<UpdateWalletResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateWalletResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun uploadImageService(imageUrl:String ,onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        var body: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(imageUrl)) {
            val file = File(imageUrl)
            val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            body = MultipartBody.Part.createFormData("chat_image", file.name, reqFile)
        }
        val call = myService.uploadImageApi(body!!)
        call.enqueue(object : Callback<ChatImageResponse> {
            override fun onResponse(call: Call<ChatImageResponse>, response: Response<ChatImageResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<ChatImageResponse>, t: Throwable) {
                Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }
}