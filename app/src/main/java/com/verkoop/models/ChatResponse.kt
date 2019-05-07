package com.verkoop.models


data class ChatData(
        val id: Int,
        val senderId: Int,
        val receiverId: Int,
        val message: String,
        val timeStamp: String,
        val type: Int,
        val item_id: Int,
        val chat_user_id: Int
)