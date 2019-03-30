package com.verkoop.network

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.verkoop.models.*
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.LOG_TAG
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.ArrayList

 class ServiceHelper{
    interface OnResponse{
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
                            val messageError= JSONObject(response.errorBody()!!.string())
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
                            val messageError= JSONObject(response.errorBody()!!.string())
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
        val myService =  ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userLoginApi(request)
        responseCall.enqueue(object : Callback<SocialLoginResponse> {
            override fun onResponse(call: Call<SocialLoginResponse>, response: Response<SocialLoginResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError= JSONObject(response.errorBody()!!.string())
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

            override fun onFailure(call: Call<SocialLoginResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun  categoriesService(onResponse: OnResponse) {
        val myService =  ApiClient.getClient().create(MyService::class.java)
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
                }else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun addItemsApi(request: AddItemRequest,onResponse: OnResponse) {
      //  val service = ServiceGenerator().createService(MyService::class.java, "idress", "idress")
        val myService =  ApiClient.getClient().create(MyService::class.java)
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
        call = myService.addClothApi( parts, categoryId, name, price,itemType,description,userId,address,lat,lng,meetUp)
        call.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if(response.code()==201){
                    onResponse.onSuccess(response)
                }else{
                    onResponse.onFailure(response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

     fun  myProfileService(userId:String,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
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
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }
     fun  getPlacesService(placeSearchRequest: PlaceSearchRequest,onResponse: OnResponse) {
         val service = ApiClientLocation.client
         val responseCall = service.getDetails(placeSearchRequest.loc,placeSearchRequest.radius,placeSearchRequest.key)
         responseCall.enqueue(object : Callback<PlaceApiResponse> {
             override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
                 val res = response.body()
                 Log.e("<<<Response>>>", Gson().toJson(res))
                 if (res != null) {
                     when {
                         response.code() == 200 -> onResponse.onSuccess(response)
                         else -> onResponse.onFailure(response.message())
                     }
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  getSearchPlaceService(placeSearchRequest: PlaceSearchRequest,onResponse: OnResponse) {
         val service = ApiClientLocation.client
         val responseCall = service.getSearchPlaceApi(placeSearchRequest.keyword,placeSearchRequest.key)
         responseCall.enqueue(object : Callback<PlaceApiResponse> {
             override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
                 val res = response.body()
                 Log.e("<<<Response>>>", Gson().toJson(res))
                 if (res != null) {
                     when {
                         response.code() == 200 -> onResponse.onSuccess(response)
                         else -> onResponse.onFailure(response.message())
                     }
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  getItemDetailService(itemId:Int,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
         val responseCall = myService.getItemDetailsApi(itemId)
         responseCall.enqueue(object : Callback<ItemDetailsResponse> {
             override fun onResponse(call: Call<ItemDetailsResponse>, response: Response<ItemDetailsResponse>) {
                 val res = response.body()
                 Log.e("<<<Response>>>", Gson().toJson(res))
                 if (res != null) {
                     when {
                         response.code() == 200 -> onResponse.onSuccess(response)
                         else -> onResponse.onFailure(response.message())
                     }
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<ItemDetailsResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  getHomeDataService(userId:String,pageCount:Int,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
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
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<HomeDataResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  getItemsService(pageCount:Int,userId:String,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
         val responseCall = myService.getHomeDataApi(userId,pageCount)
         responseCall.enqueue(object : Callback<HomeDataResponse> {
             override fun onResponse(call: Call<HomeDataResponse>, response: Response<HomeDataResponse>) {
                 val res = response.body()
                 Log.e("<<<Response>>>", Gson().toJson(res))
                 if (res != null) {
                     when {
                         response.code() == 200 -> onResponse.onSuccess(response)
                         else -> onResponse.onFailure(response.message())
                     }
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<HomeDataResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  likeService(lickedRequest: LickedRequest,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
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
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<LikedResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  disLikeService(lickedId: Int,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
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
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  categoryPostService(categoryPostRequest: FilterRequest,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
         val responseCall = myService.categoryPostApi(categoryPostRequest,categoryPostRequest.userId)
         responseCall.enqueue(object : Callback<CategoryPostResponse> {
             override fun onResponse(call: Call<CategoryPostResponse>, response: Response<CategoryPostResponse>) {
                 val res = response.body()
                 Log.e("<<<Response>>>", Gson().toJson(res))
                 if (res != null) {
                     when {
                         response.code() == 200 -> onResponse.onSuccess(response)
                         else -> onResponse.onFailure(response.message())
                     }
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<CategoryPostResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

     fun  updateCategoryService(updateCategoryRequest: UpdateCategoryRequest,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
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
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<LikedResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }

 fun  getFavouritesService(userId:String,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
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
                 }else {
                     onResponse.onFailure("Something went wrong!")
                 }
             }

             override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {
                 onResponse.onFailure(t.message)
             }
         })
     }



     fun  updateProfileService(request:ProfileUpdateRequest, onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
          var body: MultipartBody.Part?=null
             if (!TextUtils.isEmpty(request.profile_pic)) {
                 val file = File(request.profile_pic)
                 val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
                 body = MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)
             }

         var call: Call<ProfileUpdateResponse>?=null
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

         call = if(body!=null){
             myService.editProfileWthImageApi( body,userId,userName,firstName,lastName,city,state,country,cityId,stateId,countryId,webSite,bio,mobileNo,gender,dob)
         }else{
             myService.editProfileWthOutImgApi( userId,userName,firstName,lastName,city,state,country,cityId,stateId,countryId,webSite,bio,mobileNo,gender,dob)
         }

         call.enqueue(object : Callback<ProfileUpdateResponse> {
             override fun onResponse(call: Call<ProfileUpdateResponse>, response: Response<ProfileUpdateResponse>) {
                 Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                 if(response.code()==200){
                     onResponse.onSuccess(response)
                 }else{
                     onResponse.onFailure(response.body()!!.message)
                 }
             }

             override fun onFailure(call: Call<ProfileUpdateResponse>, t: Throwable) {
                 Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                 onResponse.onFailure("Something went wrong!")

             }
         })
     }

     fun  getMyProfileInfoService(userId:String,onResponse: OnResponse) {
         val myService =  ApiClient.getClient().create(MyService::class.java)
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
                 }else {
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
                             val messageError= JSONObject(response.errorBody()!!.string())
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
                             val messageError= JSONObject(response.errorBody()!!.string())
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
}
