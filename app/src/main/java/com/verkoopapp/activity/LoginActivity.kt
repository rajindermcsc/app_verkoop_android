package com.verkoopapp.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.iid.FirebaseInstanceId
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.LogInResponse
import com.verkoopapp.models.LoginRequest
import com.verkoopapp.models.LoginSocialRequest
import com.verkoopapp.models.SocialGoogleResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.login_activity.*
import org.json.JSONException
import retrofit2.Response
import java.util.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val TAG = MainActivity::class.java.simpleName
    private val RC_SIGN_IN = 7
    private var mGoogleApiClient: GoogleSignInClient? = null
    private var callbackManager: CallbackManager? = null
    private var fbAccessToken: AccessToken? = null
    private var url: String? = null
    private var loginType: String = ""
    private var deviceId: String = ""
    private var id = 0
    private var type = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        type = intent.getIntExtra(AppConstants.TYPE, 0)
        id = intent.getIntExtra(AppConstants.ID, 0)
        setData()
        googleLoginSetUp()
        firebaseDeviceToken()
    }

    private fun firebaseDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            deviceId = it.token
            Log.e("newToken", deviceId)
        }
    }

    private fun googleLoginSetUp() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

//        mGoogleApiClient = GoogleApiClient.Builder(this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build()
        mGoogleApiClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setData() {
        val typefaceFont = Typeface.createFromAsset(assets, "fonts/gothicb.ttf")
        etPassword.typeface = typefaceFont

        tvLogin.setOnClickListener {
            if (Utils.isOnline(this)) {
                if (isValidate()) {
                    if (Utils.isOnline(this)) {
                        callLoginApi()
                    } else {
                        Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                    }

                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        tvFacebook.setOnClickListener {
            loginType = getString(R.string.face_log)
            if (Utils.isOnline(this)) {
                loginWithFacebook()
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()

            }
        }
        tvGoogle.setOnClickListener {
            loginType = getString(R.string.google_plus)
            if (Utils.isOnline(this)) {
//                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                val signInIntent = mGoogleApiClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
        }
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivEmail.setImageResource(R.mipmap.email_enable)
                    vEmail.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.colorPrimary))
                } else {
                    ivEmail.setImageResource(R.mipmap.email_disable)
                    vEmail.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.light_gray))
                }
            }
        })
        etPassword.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivPassword.setImageResource(R.mipmap.password_enable)
                    vPassword.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.colorPrimary))
                } else {
                    ivPassword.setImageResource(R.mipmap.password_disable)
                    vPassword.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.light_gray))
                }
            }
        })
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etEmail.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_email)).show()
            false
        } else if (!Utils.emailValidator(etEmail.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_valid_email)).show()
            false
        } else if (!Utils.isValidPassword(etPassword.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password)).show()
            false
        } else if (etPassword.text.toString().trim().length < 7) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
            false
        } else {
            true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (loginType.equals(getString(R.string.face_log), ignoreCase = true)) {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        } else {
            if (requestCode == RC_SIGN_IN) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if(deviceId.equals("")){
                    firebaseDeviceToken()
                }
                handleSignInResult(result)
            }
        }
    }

    /*Login with Facebook*/
    private fun loginWithFacebook() {
        if(deviceId.equals("")){
            firebaseDeviceToken()
        }
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                fbAccessToken = loginResult.accessToken
                if (fbAccessToken != null) {
                    val request = GraphRequest.newMeRequest(fbAccessToken) { `object`, response ->
                        Log.e(TAG, ":::: facebook response::::" + response.toString())
                        val facebookId = response.jsonObject.optString("id")
                        val imageUrl = "https://graph.facebook.com/$facebookId/picture?type=large&redirect=true&width=800&height=800"
                        val email = response.jsonObject.optString("email")
                        val name = response.jsonObject.optString("name")
                        val first_name = response.jsonObject.optString("first_name")
                        val last_name = response.jsonObject.optString("last_name")
                        Log.e(TAG, "::Name::$name")
                        Log.e(TAG, "::Email::$email")

                        try {
                            url = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                            Log.e(TAG, ":::profile pic url:::$url")
                            LoginManager.getInstance().logOut()
//                            if (!TextUtils.isEmpty(facebookId) && !TextUtils.isEmpty(email)) {
                            if (!TextUtils.isEmpty(facebookId)) {
                                if (Utils.isOnline(this@LoginActivity)) {
//                                    callSocialLoginApi(email, name, facebookId)
                                    callSocialLoginApi(email, first_name, last_name, facebookId)
                                } else {
                                    Utils.showSimpleMessage(this@LoginActivity, getString(R.string.check_internet)).show()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,first_name,last_name,name,gender,email,picture,friends")
                    request.parameters = parameters
                    request.executeAsync()
                }
                /*  Intent intent= new Intent(context,SignUpActivity.class);
                startActivity(intent);*/
            }

            override fun onCancel() {
                //  Toast.makeText(getApplication(), "Cancelled", Toast.LENGTH_SHORT).show();
            }

            override fun onError(e: FacebookException) {
                Toast.makeText(application, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*Login with GooglePlus*/
    private fun handleSignInResult(result: GoogleSignInResult) {
        //If the login succeed
        if (result.isSuccess) {
            val acct = result.signInAccount
            var fullName = ""
            var email = ""
            if (!TextUtils.isEmpty(acct!!.displayName)) {
                fullName = acct.displayName!!
            }
            if (!TextUtils.isEmpty(acct.email)) {
                email = acct.email!!
            }
            if (!TextUtils.isEmpty(acct.id) && !TextUtils.isEmpty(acct.email)) {
                if (Utils.isOnline(this@LoginActivity)) {

                    callSocialLoginApi(email, fullName, "", acct.id.toString())
                } else {
                    Utils.showSimpleMessage(this@LoginActivity, getString(R.string.check_internet)).show()
                }

            }
            Handler().postDelayed(Runnable {
                //                mGoogleApiClient!!.maybeSignOut()
//                mGoogleApiClient!!.disconnect()
                mGoogleApiClient!!.signOut()
            }, 1000)

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    private fun callLoginApi() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().userLoginService(LoginRequest(etEmail.text.toString().trim(), etPassword.text.toString().trim(), "normal",deviceId,"1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        val loginResponse = response.body() as LogInResponse
                        if (loginResponse.data != null) {
                            setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.is_use, loginResponse.data.mobile_no, loginResponse.data.qrCode_image, loginResponse.data.coin, loginResponse.data.amount)
                        } else {
                            Utils.showSimpleMessage(this@LoginActivity, loginResponse.message).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
                    }
                })
    }

    private fun setResponseData(userId: String, api_token: String, firstName: String, email: String, loginType: String, firstTime: Int, mobileNo: String, qr_code: String, coin: Int, amount: Int) {
        Utils.savePreferencesString(this@LoginActivity, AppConstants.USER_ID, userId)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.API_TOKEN, api_token)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.USER_NAME, firstName)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.QR_CODE, qr_code)
        Utils.saveIntPreferences(this@LoginActivity, AppConstants.COIN, coin)
        Utils.saveIntPreferences(this@LoginActivity, AppConstants.AMOUNT, amount)

        if (!TextUtils.isEmpty(mobileNo)) {
            Utils.savePreferencesString(this@LoginActivity, AppConstants.MOBILE_NO, mobileNo)
        }
        if (!TextUtils.isEmpty(email)) {
            Utils.savePreferencesString(this@LoginActivity, AppConstants.USER_EMAIL_ID, email)
        }
        Utils.savePreferencesString(this@LoginActivity, AppConstants.LOGIN_TYPE, loginType)
        if (firstTime == 0) {
            val intent = Intent(this@LoginActivity, CategoriesActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
            finish()
        } else {
            Utils.savePreferencesString(this, AppConstants.COMING_FROM, "PickOptionActivity")
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
            finish()
        }
    }


//    private fun callSocialLoginApi(email: String, name: String, loginId: String) {
//        VerkoopApplication.instance.loader.show(this)
//        if (email == "") {
//            email.equals(" ")
//        }
//        ServiceHelper().socialLoginService(LoginSocialRequest(name, email, loginId, "social"),
//                object : ServiceHelper.OnResponse {
//                    override fun onSuccess(response: Response<*>) {
//                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
//                        val loginResponse = response.body() as SocialGoogleResponse
//                        Log.e("<<Log>>", "Login Successfully.")
//                        if (loginResponse.data != null) {
//                            setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.is_use, "", loginResponse.data.qrCode_image, loginResponse.data.coin, loginResponse.data.amount)
//                        }
//                    }
//
//                    override fun onFailure(msg: String?) {
//                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
//                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
//                    }
//                })
//    }


    private fun
            callSocialLoginApi(email: String, user_name: String, last_name: String, loginId: String) {
        VerkoopApplication.instance.loader.show(this)
//        if (email == "") {
//            email.equals(" ")
//        }
        ServiceHelper().socialLoginService(LoginSocialRequest(user_name, email, loginId, "social",deviceId,"1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        val loginResponse = response.body() as SocialGoogleResponse
                        Log.e("<<Log>>", "Login Successfully.")
                        if (loginResponse.data != null) {
                            setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.is_use, "", loginResponse.data.qrCode_image, loginResponse.data.coin, loginResponse.data.amount)
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
                    }
                })
    }

}