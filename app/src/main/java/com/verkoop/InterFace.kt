package com.verkoop

/**
 * Created by intel on 26-02-2019.
 */
interface LikeDisLikeListener{
    fun getLikeDisLikeClick(type:Boolean,position:Int,lickedId:Int,itemId:Int)
    fun getItemDetailsClick(itemId:Int,userId:Int)
}

interface LoadingListener{
    fun getLoadingCallBack()
}