package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.common.util.IOUtils
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.GenericData
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.HomePagerAdapter
import com.verkoopapp.fragment.ActivitiesFragment
import com.verkoopapp.fragment.HomeFragment
import com.verkoopapp.fragment.ProfileFragment
import com.verkoopapp.models.SocketCheckConnectionEvent
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.toolbar_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity() {
    private var homeFragment: HomeFragment? = null
    private var profileFragment: ProfileFragment? = null
    private var activitiesFragment: ActivitiesFragment? = null
    private var fragmentList = ArrayList<Fragment>()
    private var doubleBackToExitPressedOnce = false
    private var comingFrom: Int = 0
    private var visionData: String = ""
    private var socket: Socket? = VerkoopApplication.getAppSocket()
    private lateinit var inputStream: InputStream;
    private lateinit var photoData: ByteArray
    private var featureList: MutableList<Feature> = ArrayList()
    private lateinit var batchResponse: BatchAnnotateImagesResponse
    private lateinit var vision: Vision
    private var mCurrentPhotoPath: String? = null
    private val CAMERA_REQUEST = 1888
    private var uriTemp: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        homeFragment = HomeFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()
        activitiesFragment = ActivitiesFragment.newInstance()
        fragmentList.add(homeFragment!!)
        fragmentList.add(activitiesFragment!!)
        fragmentList.add(profileFragment!!)
        setData()
        callInit()
        val picOption = intent.getIntExtra(AppConstants.PICK_OPTION, 0)
        if (picOption == 1) {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivityForResult(intent, 2)
        }
        setBranchIdData()
        setIntentData()
//        setVisionData()
    }

//    private fun setVisionData() {
//        val visionBuilder = Vision.Builder(
//                NetHttpTransport(),
//                AndroidJsonFactory(),
//                null)
//
//        visionBuilder.setVisionRequestInitializer(
//                VisionRequestInitializer("AIzaSyDjTWXzAS6IvhCf7bscIyKYZXOUKsy4Tms"))
//
//        vision = visionBuilder.build()
//    }

    private fun setIntentData() {
        val result = intent!!.getIntExtra(AppConstants.TRANSACTION, 0)
        if (result == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                    profileFragment!!.refreshUI(0)

                }
                viewPager.currentItem == 1 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                }
                else -> {
                    profileFragment!!.refreshUI(1)
                }
            }

        }
    }

    private fun setBranchIdData() {
        val type = intent.getIntExtra(AppConstants.TYPE, 0)
        val id = intent.getIntExtra(AppConstants.ID, 0)
        if (type == 1) {
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra(AppConstants.ITEM_ID, id)
            startActivity(intent)
        } else if (type == 2) {
            if (id != Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                val reportIntent = Intent(this, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, id)
                startActivity(reportIntent)
            }
        }
    }

    private fun setTabLayout() {
        bottomTabLayout.setButtonTextStyle(R.style.TextGray12)
        // set buttons from menu resource
        bottomTabLayout.setItems(R.menu.menu_bottom_layout)
        //set on selected tab listener.
        bottomTabLayout.setListener { id ->
            switchFragment(id)
        }
        /* bottomTabLayout.setListener { position ->
             viewPager.currentItem = position
         }*/
        bottomTabLayout.setSelectedTab(R.id.menu_button1)
        //enable indicator
        bottomTabLayout.setIndicatorVisible(true)
        //indicator height
        bottomTabLayout.setIndicatorHeight(resources.getDimension(R.dimen.dp_2))
        //indicator color
        bottomTabLayout.setIndicatorColor(R.color.white)
        //indicator line color
        bottomTabLayout.setIndicatorLineColor(R.color.colorPrimary)
        //bottomTabLayout.setSelectedTab(R.id.menu_button5)
    }

    private fun switchFragment(id: Int) {
        when (id) {
            R.id.menu_button1 -> viewPager.currentItem = 0
            R.id.menu_button2 -> viewPager.currentItem = 1
            R.id.menu_button3 -> viewPager.currentItem = 2
        }
    }

    private fun setData() {
        val adapter = HomePagerAdapter(supportFragmentManager, 3, fragmentList)
        viewPager.adapter = adapter
        // viewPager.offscreenPageLimit = 2
        setTabLayout()
        ivChat.setOnClickListener {
            ivChat.isEnabled=false
            val intent = Intent(this, ChatInboxActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                ivChat.isEnabled = true
            }, 700)
        }
        ivFavourite.setOnClickListener {
            ivFavourite.isEnabled=false
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                ivFavourite.isEnabled = true
            }, 700)
        }
        if (comingFrom == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                    profileFragment!!.refreshUI(0)
                }
                viewPager.currentItem == 1 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                }
                else -> {
                    profileFragment!!.refreshUI(1)
                }
            }
        }
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    when {
                        viewPager.currentItem == 0 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                            profileFragment!!.refreshUI(0)

                        }
                        viewPager.currentItem == 1 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                        }
                        else -> {
                            profileFragment!!.refreshUI(1)
                        }
                    }

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
//        if (requestCode == 1888 ) {
//            if (resultCode == Activity.RESULT_OK ) {
////                VerkoopApplication.instance.loader.show(this)
////                val currentImage = data.extras!!.get("data") as Bitmap
////                val baos = ByteArrayOutputStream()
////                currentImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////                inputStream = ByteArrayInputStream(baos.toByteArray())
////                imageToVision()
//
//                VerkoopApplication.instance.loader.show(this)
//                val f = File(mCurrentPhotoPath!!)
//                uriTemp = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
//                val currentImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uriTemp) as Bitmap;
//
////                val currentImage = data.extras!!.get("data") as Bitmap
//                val baos = ByteArrayOutputStream()
//                currentImage.compress(Bitmap.CompressFormat.JPEG, 50, baos)
//                inputStream = ByteArrayInputStream(baos.toByteArray())
//                imageToVision()
//            }
//        }
    }

//    private fun imageToVision() {
//        visionData = ""
//        try {
//            photoData = IOUtils.toByteArray(inputStream)
//            inputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            VerkoopApplication.instance.loader.hide(this)
//        }
//
//        val inputImage = Image()
//        inputImage.encodeContent(photoData)
//
//        val labelDetection = Feature()
//        labelDetection.type = "LABEL_DETECTION"
//        labelDetection.setMaxResults(2)
//        featureList.add(labelDetection)
//
//        val webDetection = Feature()
//        webDetection.type = "WEB_DETECTION"
//        webDetection.setMaxResults(2)
//        featureList.add(webDetection)
//
//        val request = AnnotateImageRequest()
//        request.image = inputImage
//        request.features = featureList
//
//        val batchRequest = BatchAnnotateImagesRequest()
//
//        batchRequest.requests = Arrays.asList(request)
//
//        val thread = Thread(Runnable {
//            try {
//                batchResponse = vision.images().annotate(batchRequest).execute()
//                VerkoopApplication.instance.loader.hide(this@HomeActivity)
//
//                if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).size > 0) {
//                    if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(0).description != null) {
//                        visionData = ((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(0).description
//                    }
//                    if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).size > 1) {
//                        if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(1).description != null) {
//                            visionData = visionData + "," + ((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(1).description
//                        }
//                    }
//                }
//                if (batchResponse.responses.get(0).labelAnnotations.size > 0) {
//                    if (batchResponse.responses.get(0).labelAnnotations.get(0).description != null) {
//                        visionData = visionData + "," + batchResponse.responses.get(0).labelAnnotations.get(0).description
//                    }
//                    if (batchResponse.responses.get(0).labelAnnotations.size > 1) {
//                        if (batchResponse.responses.get(0).labelAnnotations.get(1).description != null) {
//                            visionData = visionData + "," + batchResponse.responses.get(0).labelAnnotations.get(1).description
//                        }
//                    }
//                }
//
//                if ((batchResponse.responses.get(0).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String> != null) {
//                    val listData = (batchResponse.responses.get(0).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String>
//                    if (listData.get(0) as com.google.api.client.util.ArrayMap<String, String> != null) {
//                        val arrayMap = listData.get(0) as com.google.api.client.util.ArrayMap<String, String>
//                        if (arrayMap.get("label") != null) {
//                            visionData = visionData + "," + visionData + arrayMap.get("label")
//                        }
//                    }
//                }
//                val intent = Intent(this@HomeActivity, SearchActivity::class.java)
//                intent.putExtra("visionData", visionData)
//                startActivity(intent)
//            } catch (e: Exception) {
//                VerkoopApplication.instance.loader.hide(this@HomeActivity)
//                e.printStackTrace()
//            }
//        })
//
//        thread.start()
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SocketCheckConnectionEvent) {
        /* Do something */
        callInit()

    }

    private fun callInit() {
        socket?.emit(AppConstants.INIT_USER_ID, getObj(), Ack {
            Log.e("<<<ACKRESPONSE--5>>>", Gson().toJson(it[0]))
        })

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun getObj(): Any {
        val jsonObject: JSONObject?
        jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", Utils.getPreferencesString(applicationContext, AppConstants.USER_ID).toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

//    fun startVison() {
//                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                if (takePictureIntent.resolveActivity(packageManager) != null) {
//                    var photoFile: File? = null
//                    try {
//                        photoFile = createImageFile()
//                    } catch (ex: IOException) {
//                        // Error occurred while creating the File
//                    }
//
//                    // Continue only if the File was successfully created
//                    if (photoFile != null) {
////                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                FileProvider.getUriForFile(this,
//                                        applicationContext.packageName + ".provider", photoFile))
//                        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
//                    }
//                }
////        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
//    }

//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
//        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//                imageFileName, /* prefix */
//                "." +
//                        "", /* suffix */
//                storageDir      /* directory */
//        )
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.absolutePath
//        return image
//    }
}