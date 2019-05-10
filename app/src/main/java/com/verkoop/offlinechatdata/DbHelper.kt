package com.verkoop.offlinechatdata

import com.verkoop.models.ChatInboxResponse
import io.realm.Realm
import io.realm.RealmResults
import java.util.ArrayList



class DbHelper{
    var realm = Realm.getDefaultInstance()

    fun insertAllChatData(AllChatList: ArrayList<ChatInboxResponse>) {
        realm.executeTransaction { realm1 ->
            for (i in AllChatList.indices) {
                val allChatRealModel = realm1.createObject(ChatInboxRealmResponse::class.java)
                allChatRealModel.is_archive=AllChatList[i].is_archive
                allChatRealModel.is_delete=AllChatList[i].is_delete
                allChatRealModel.chat_user_id=AllChatList[i].chat_user_id
                allChatRealModel.timestamp=AllChatList[i].timestamp
                allChatRealModel.types=AllChatList[i].types
                allChatRealModel.unread_count=AllChatList[i].unread_count
                allChatRealModel.sender_id=AllChatList[i].sender_id
                allChatRealModel.receiver_id=AllChatList[i].receiver_id
                allChatRealModel.item_id=AllChatList[i].item_id
                allChatRealModel.message_id=AllChatList[i].message_id
                allChatRealModel.username=AllChatList[i].username
                allChatRealModel.profile_pic=AllChatList[i].profile_pic
                allChatRealModel.message=AllChatList[i].message
                allChatRealModel.user_id=AllChatList[i].user_id
                allChatRealModel.is_sold=AllChatList[i].is_sold
                allChatRealModel.item_name=AllChatList[i].item_name
                allChatRealModel.offer_status=AllChatList[i].offer_status
                allChatRealModel.url=AllChatList[i].url
            }
        }
    }

    fun getAddChatList(): RealmResults<ChatInboxRealmResponse> {
        val value=0
        return  realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).findAll()

    }

    fun deleteAllChatData() {
        val results = realm.where(ChatInboxRealmResponse::class.java).findAll()
        realm.executeTransaction { realm1 -> results.deleteAllFromRealm() }
    }

    fun getSellerList(): RealmResults<ChatInboxRealmResponse> {
        val value=0
        return  realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).and().equalTo("userId", value).findAll()

    }
    fun getBuyerList(userId:Int): RealmResults<ChatInboxRealmResponse> {
        val value=0
        return  realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).notEqualTo("user_id", userId).findAll()
    }

    fun getArchiveList(valueArc:Int): RealmResults<ChatInboxRealmResponse> {
        val value=0
        return  realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).equalTo("is_archive",valueArc).findAll()

    }
}