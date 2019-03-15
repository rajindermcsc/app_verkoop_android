package com.verkoop.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.andrognito.flashbar.Flashbar
import com.verkoop.R
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

    fun scaleDown(realImage: Bitmap, maxImageSize: Float,
                  filter: Boolean): Bitmap {
        val ratio = Math.min(
                maxImageSize / realImage.width,
                maxImageSize / realImage.height)
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    @SuppressLint("Recycle")
    fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDate(pre: String, dateString: String, post: String): String {
        val parseFormat = SimpleDateFormat(pre)
        parseFormat.timeZone = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        try {
            date = parseFormat.parse(dateString)
            parseFormat.timeZone = TimeZone.getDefault()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val format = SimpleDateFormat(post)
        format.timeZone = TimeZone.getDefault()
       // Log.e("dateTimeStamp",date)

        return format.format(date)

    }

    @SuppressLint("SimpleDateFormat")
    fun getDateDifference(FutureDate: String): String {
        var totalTime = 0
        var unit: String? = null
        try {
           // val netDate = java.sql.Date(java.lang.Long.parseLong(FutureDate) * 1000)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.ssssss")
            parseFormat.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            try {
                date = parseFormat.parse(FutureDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val oldDate = Date()

            var diff = oldDate.time - date!!.time

            val days = diff / (24 * 60 * 60 * 1000)
            diff -= days * (24 * 60 * 60 * 1000)

            val hours = diff / (60 * 60 * 1000)
            diff -= hours * (60 * 60 * 1000)

            val minutes = diff / (60 * 1000)
            diff -= minutes * (60 * 1000)

            val seconds = diff / 1000

            if (days != 0L) {
                totalTime = days.toInt()
                if (totalTime == 1) {
                    unit = "day"
                } else {
                    unit = "days"
                }
               // unit = "day"
            } else if (hours != 0L) {
                totalTime = hours.toInt()
                if (totalTime == 1) {
                    unit = "hour"
                } else {
                    unit = "hours"
                }
            } else if (minutes != 0L) {
                totalTime = minutes.toInt()
                unit = "min"
            } else if (seconds != 0L) {
                totalTime = seconds.toInt()
                unit = "sec"

            } else {
                totalTime = 0
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return totalTime.toString() + " " + unit
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateDifferenceDetails(FutureDate: String): String {
        var totalTime = 0
        var unit: String? = null
        try {
            // val netDate = java.sql.Date(java.lang.Long.parseLong(FutureDate) * 1000)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            parseFormat.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            try {
                date = parseFormat.parse(FutureDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val oldDate = Date()

            var diff = oldDate.time - date!!.time

            val days = diff / (24 * 60 * 60 * 1000)
            diff -= days * (24 * 60 * 60 * 1000)

            val hours = diff / (60 * 60 * 1000)
            diff -= hours * (60 * 60 * 1000)

            val minutes = diff / (60 * 1000)
            diff -= minutes * (60 * 1000)

            val seconds = diff / 1000

            if (days != 0L) {
                totalTime = days.toInt()
                if (totalTime == 1) {
                    unit = "day"
                } else {
                    unit = "days"
                }
                // unit = "day"
            } else if (hours != 0L) {
                totalTime = hours.toInt()
                if (totalTime == 1) {
                    unit = "hour"
                } else {
                    unit = "hours"
                }
            } else if (minutes != 0L) {
                totalTime = minutes.toInt()
                unit = "min"
            } else if (seconds != 0L) {
                totalTime = seconds.toInt()
                unit = "sec"

            } else {
                totalTime = 0
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return totalTime.toString() + " " + unit
    }

    fun saveTempBitmap(context: Context, mBitmap: Bitmap): String? {

        val outputDir = context.cacheDir

        var file: File? = null
        try {
            file = File.createTempFile("temp_post_img", ".jpg", outputDir)
            //outputFile.getAbsolutePath();
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val f3 = File(Environment.getExternalStorageDirectory().toString() + "/inpaint/")
        if (!f3.exists()) {
            f3.mkdirs()
        }
        val outStream: OutputStream?
        //File file = new File(Environment.getExternalStorageDirectory() + "/inpaint/"+"seconds"+".png");
        try {
            outStream = FileOutputStream(file!!)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()

            //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        //getPath( Uri.parse(file.getAbsolutePath()), context);

        return file.absolutePath//getPath( Uri.parse(file.getAbsolutePath()), context);
    }
}