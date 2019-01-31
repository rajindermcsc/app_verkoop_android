package com.verkoop.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.verkoop.activity.GalleryActivity
import com.verkoop.models.ImageModal
import com.verkoop.utils.Utils.checkGif
import java.util.ArrayList


 class PickerController internal constructor(private val pickerActivity: GalleryActivity) {

     protected var addImagePaths = ArrayList<Uri>()
        set
    private val resolver: ContentResolver
   // private val cameraUtil = CameraUtil()
    private var pathDir = ""

    /*internal var savePath: String
        get() = cameraUtil.getSavePath()
        set(savePath) {
            cameraUtil.setSavePath(savePath)
        }*/


    init {

        resolver = pickerActivity.contentResolver
    }


    /*fun takePicture(activity: Activity, saveDir: String) {
        cameraUtil.takePicture(activity, saveDir)
    }*/



    fun setAddImagePath(imagePath: Uri) {
        this.addImagePaths.add(imagePath)
    }


    internal fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(pickerActivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.CheckStoragePermission())
                return true
        } else
            return true
        return false
    }


    internal fun displayImage(bucketId: Long?,
                              exceptGif: Boolean?) {
        DisplayImage(bucketId, exceptGif).execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DisplayImage internal constructor(private val bucketId: Long?,
                                                          internal var exceptGif: Boolean?) : AsyncTask<Void, Void, ArrayList<ImageModal>>() {

        override fun doInBackground(vararg params: Void): ArrayList<ImageModal> {
            return getAllMediaThumbnailsPath(bucketId!!, exceptGif)
        }

        override fun onPostExecute(result: ArrayList<ImageModal>) {
            super.onPostExecute(result)
           pickerActivity.setAdapterData(result)
        }
    }


    private fun getAllMediaThumbnailsPath(id: Long,
                                          exceptGif: Boolean?): ArrayList<ImageModal> {
        val selection = MediaStore.Images.Media.BUCKET_ID + " = ?"
        val bucketId = id.toString()
        val sort = MediaStore.Images.Media._ID + " DESC"
        val selectionArgs = arrayOf(bucketId)

        val images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var c: Cursor?
        if (bucketId != "0") {
            c = resolver.query(images, null, selection, selectionArgs, sort)
        } else {
            c = resolver.query(images, null, null, null, sort)
        }
      //  val imageUris = arrayOfNulls<ImageModal>(c?.count ?: 0)
      //  val imageUris = ArrayList<ImageModal>(c?.count ?: 0)
        val imageUris = ArrayList<ImageModal>()
        val imagemodal=ImageModal("",false,true)
        imageUris.add(imagemodal)
        if (c != null) {
            try {

                if (c.moveToFirst()) {
                    setPathDir(c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)),
                            c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)))
                    var position = -1
                    do {
                        if (exceptGif!! && checkGif(c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))))
                            continue
                        val imgId = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID))
                        val path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imgId)
                       // ++position
                        val imagemodal=ImageModal(path.toString(),false,false)
                        imageUris.add(imagemodal)
                      //  Log.e("imageURl",imgId.toString()+"  "+path.toString())

                    } while (c.moveToNext())
                }
                c.close()
            } catch (e: Exception) {
                if (!c.isClosed) c.close()
            }

        }
        return imageUris
    }

     private fun setPathDir(path: String, fileName: String): String {
         return   path.replace("/" + fileName, "")
     }

     fun getPathDir(bucketId: Long): String {
         if (pathDir == "" || bucketId == 0L)
             pathDir = Environment.getExternalStoragePublicDirectory(
                     Environment.DIRECTORY_DCIM + "/Camera").absolutePath
         return pathDir
     }

     fun finishActivity() {
         pickerActivity.finishAffinity()
     }
}