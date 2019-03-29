package com.verkoop.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.claudiodegio.msv.OnSearchViewListener
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R
import com.verkoop.activity.RegionActivity
import kotlinx.android.synthetic.main.region_list_fragment.*


class RegionListFragment:BaseFragment(), OnSearchViewListener {

    val TAG=RegionListFragment::class.java.simpleName.toString()
    private lateinit var regionActivity: RegionActivity

    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        regionActivity=activity as RegionActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =inflater.inflate(R.layout.region_list_fragment,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        toolbarRegion.title = "Region"
        toolbarRegion.setLogo(R.mipmap.back)
        toolbarRegion.titleMarginStart = 70
        sv.setOnSearchViewListener(this)
        //toolbarRegion.setDisplayHomeAsUpEnabled(true)
        regionActivity.setSupportActionBar(toolbarRegion)
    }
    override fun onSearchViewClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChange(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchViewShown() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    companion object {
        fun newInstance():RegionListFragment{
            val arg=Bundle()
            val fragment=RegionListFragment()
            fragment.arguments=arg
             return fragment
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        // Display menu item's title by using a Toast.
        if (id == R.id.action_search) {
            Toast.makeText(regionActivity, "Search Menu", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        // Inflate the menu; this adds items to the action bar if it is present.
       regionActivity.menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu!!.findItem(R.id.action_search)
        // searchView.setMenuItem(item)

    }
}