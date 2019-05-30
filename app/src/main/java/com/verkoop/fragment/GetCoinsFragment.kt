package com.verkoop.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R
import com.verkoop.activity.CoinsActivity
import com.verkoop.adapter.CoinListAdapter
import com.verkoop.models.CoinPlanResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.get_coin_fragment.*
import retrofit2.Response


class GetCoinsFragment : BaseFragment() {
    private lateinit var coinsActivity: CoinsActivity
    private val TAG = GetCoinsFragment::class.java.simpleName
    private lateinit var getCoinAdapter: CoinListAdapter


    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        coinsActivity = context as CoinsActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.get_coin_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        if (Utils.isOnline(coinsActivity)) {
            getWalletHistoryApi()
        } else {
            Utils.showSimpleMessage(coinsActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter() {
        val mLayoutManager = GridLayoutManager(coinsActivity, 3)
        rvCoinsList.layoutManager = mLayoutManager
        getCoinAdapter = CoinListAdapter(coinsActivity, rvCoinsList)
        rvCoinsList.adapter = getCoinAdapter
    }

    companion object {
        fun newInstance(): GetCoinsFragment {
            val arg = Bundle()
            val fragment = GetCoinsFragment()
            fragment.arguments = arg
            return fragment
        }
    }

    private fun getWalletHistoryApi() {
        pbProgressCoin.visibility = View.VISIBLE
        ServiceHelper().getCoinPlanService(object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressCoin.visibility = View.GONE
                val responseWallet = response.body() as CoinPlanResponse
                if (responseWallet.data.isNotEmpty()) {
                    getCoinAdapter.setData(responseWallet.data)
                    getCoinAdapter.notifyDataSetChanged()

                } else {
                    Utils.showSimpleMessage(coinsActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressCoin.visibility = View.GONE
                Utils.showSimpleMessage(coinsActivity, msg!!).show()
            }
        })

    }
}