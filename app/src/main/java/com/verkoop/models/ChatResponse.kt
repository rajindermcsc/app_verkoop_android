package com.verkoop.models


data class ChatData(
        val id: Int,
        val senderId: Int,
        val receiverId: Int,
        val message: String,
        val timeStamp: String,
        val type: Int,
        val item_id: Int,
        val chat_user_id: Int,
        val is_read: Int
)

data class ChatInboxResponse(
    val is_archive: Int,
    val is_delete: Int,
    val chat_user_id: String,
    val timestamp: String,
    val types: Int,
    val unread_count: Int,
    val sender_id: Int,
    val receiver_id: Int,
    val item_id: Int,
    val message_id: Int,
    val username: String,
    val profile_pic: String,
    val message: String,
    val user_id: Int,
    val is_sold: Int,
    val item_name: String,
    val offer_status: Int,
    val url: String
)