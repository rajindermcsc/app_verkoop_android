package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.claudiodegio.msv.OnSearchViewListener
import com.verkoop.R
import com.verkoop.models.City
import com.verkoop.models.Country
import com.verkoop.models.State
import com.verkoop.utils.CommonUtils
import kotlinx.android.synthetic.main.regio_activity.*
import org.json.JSONException
import org.json.JSONObject


class RegionActivity : AppCompatActivity(), OnSearchViewListener {
    override fun onQueryTextSubmit(p0: String?): Boolean {
        return true
    }

    override fun onSearchViewClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChange(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchViewShown() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regio_activity)
        getStateList()
        setData()
    }


    private fun getStateList() {
        val countryList = ArrayList<Country>()
        val statesList = ArrayList<State>()
        try {
            val obj = JSONObject(CommonUtils.loadJSONFromAsset(this))
            val m_jArry = obj.getJSONArray("country")
            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)
                val states = jo_inside.getJSONArray("states")
                for (j in 0 until states.length()) {
                    val citiesList = ArrayList<City>()
                    val stateList = states.getJSONObject(j)
                    val citys = stateList.getJSONArray("cities")
                    for (k in 0 until citys.length()) {
                        val cityList = citys.getJSONObject(k)
                        val cities = City(cityList.getInt("id"), cityList.getString("name"))
                        citiesList.add(cities)
                    }
                    Log.e("cityList", citiesList.toString())
                    val state = State(stateList.getInt("id"), stateList.getString("name"), citiesList)
                    statesList.add(state)
                    Log.e("StateList", statesList.toString())
                }
                val country = Country(jo_inside.getInt("id"), jo_inside.getString("name"), statesList)
                countryList.add(country)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun setData() {
        toolbarRegion.title = "Region"
        toolbarRegion.setLogo(R.mipmap.back)
        toolbarRegion.titleMarginStart = 70
        sv.setOnSearchViewListener(this)
        //toolbarRegion.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbarRegion)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.action_search)
        // searchView.setMenuItem(item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        // Display menu item's title by using a Toast.
        if (id == R.id.action_search) {
            Toast.makeText(applicationContext, "Search Menu", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}