package com.verkoop.utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.andrognito.flashbar.Flashbar
import com.verkoop.R

import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils{
    private const val GIF_PATTERN = "(.+?)\\.gif$"
    fun savePreferencesString(context: Context, key: String, value: String): String {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()

        return key
    }

    fun getPreferences(context: Context, key: String): String {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, "")

    }

    fun removePreferences(context: Activity, key: String) {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove(key)

    }

    fun getPreferencesBoolean(context: Activity, key: String): Boolean {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, false)

    }

    fun getPreferencesString(context: Context, key: String): String {
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, "")
    }

    fun saveIntPreferences(context: Context, key: String, value: Int) {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun emailValidator(mailAddress: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(mailAddress)
        return matcher.matches()
    }

    fun isValidPassword(str: String): Boolean {
        return if (str.contains(" ")) {
            false
        } else !str.isEmpty()
    }

    fun showSimpleMessage(context:Context,text:String) :Flashbar{
        return Flashbar.Builder(context as AppCompatActivity)
                .gravity(Flashbar.Gravity.TOP)
                .messageSizeInSp(16f)
                //.titleTypeface(Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Regular.ttf"))
                .message(text)
                .backgroundColorRes(R.color.colorPrimary)
                .dismissOnTapOutside()
                .duration(5000)
                .build()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isOnline(context: Context): Boolean {
        val conMgr = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo

        return !(netInfo == null || !netInfo.isConnected || !netInfo.isAvailable)
    }

    fun clearPreferences(context: Context) {
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
    }

    fun checkGif(path: String): Boolean {
        return path.matches(GIF_PATTERN.toRegex())
    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        val result: String
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}