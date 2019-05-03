package com.verkoop.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R
import com.verkoop.activity.ChatInboxActivity
import java.util.zip.Inflater


class ChatInboxFragment:BaseFragment(){
    private var TAG=ChatInboxFragment::class.java.simpleName.toString()
    private lateinit var chatInboxActivity: ChatInboxActivity
    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
       return TAG
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        chatInboxActivity=context as ChatInboxActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val view=inflater.inflate(R.layout.chat_inbox_fragment,container,false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): ChatInboxFragment? {
            val bundle=Bundle()
            val fragment=ChatInboxFragment()
            fragment.arguments=bundle
            return fragment
        }

    }

}