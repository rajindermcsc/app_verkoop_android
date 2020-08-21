package com.verkoopapp.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.activity.PackageActivity
import kotlinx.android.synthetic.main.fragment_food_vervo.*
import kotlinx.android.synthetic.main.fragment_food_vervo.tv_verkoop
import kotlinx.android.synthetic.main.fragment_package_vervo.*


class PackageVervoFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_package_vervo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_verkoop.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
        }

        tv_food.setOnClickListener {
            val intent = Intent(context, PackageActivity::class.java)
            startActivity(intent)
        }

    }



    companion object {
        fun newInstance(): PackageVervoFragment {
            val args = Bundle()
            val fragment = PackageVervoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}