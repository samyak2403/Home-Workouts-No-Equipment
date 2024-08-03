package com.rbs.workout.freak.activity

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.rbs.workout.freak.*
import com.rbs.workout.freak.utils.*
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {


    private var appOpenManager: AppOpenManager? = null
    private var countDownTimer: CountDownTimer? = null
    private var adsLoaderPbar: ProgressBar? = null


    var isDialogShow = "0"
    var isUpdate = "0"
    var newPackageName: String = BuildConfig.APPLICATION_ID
    var SHARED_KEY = "ONBOARDING_CONDITION"
    var editor: SharedPreferences.Editor? = null

    override lateinit var sessionManager: SessionManager
    var f213i = 0
    override var TAG = "SPLASH_ACTIVITY"
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var f212gm: String
    private lateinit var f214sp: SharedPreferences


    private fun openPopupppp() {
        Network_popup(this,
            false,
            "No Internet Connection!",
            "Please check your internet connection and try again",
            "Close",
            object : Network_popup.OnPopupClickLisnter {
                override fun onClickCountinue() {
                    finishAffinity()
                }

                override fun onClickNo() {}
            })
    }

    @SuppressLint("MissingPermission")
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sessionManager = SessionManager(this)
        if (isNetworkConnected()) {
            initRemoteConfig()
        }
        else {
            openPopupppp()
        }




        setStoreToken(resources.getString(R.string.app_name))

        sharedPreferences = getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply()



        adsLoaderPbar = findViewById(R.id.adsloader)

        MobileAds.initialize(
            this@SplashActivity
        ) { }

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo


        if (isNetworkConnected()) {
            appOpenManager = AppOpenManager(this@SplashActivity)
            appOpenManager!!.fetchAd(resources.getString(R.string.app_open_ad_id))
            countDownTimer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (AppOpenManager.adsisLoaded() === true) {
                        //adsLoaderPbar!!.setVisibility(View.GONE)
                        appOpenManager!!.showAdIfAvailable()
                        countDownTimer!!.cancel()
                        Log.d("mmmm", "ads is show")
                    }
                }

                override fun onFinish() {
                    if (AppOpenManager.adsisLoaded() !== true) {
                        //intentToHomeScreen();
                        adsLoaderPbar!!.setVisibility(View.GONE)
                    }
                }
            }.start()
        }
        else {


            openPopupppp()

            Handler().postDelayed({
                intentToHomeScreen()
                adsLoaderPbar!!.setVisibility(View.GONE)
            }, 3000)



        }


    }



    private fun initRemoteConfig() {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    sessionManager!!.saveBooleanValue(
                        Const.Adshow,
                        mFirebaseRemoteConfig.getBoolean(Const.Adshow)
                    )
                    sessionManager!!.saveStringValue(
                        Const.interstitialAdId,
                        mFirebaseRemoteConfig.getString(Const.interstitialAdId)
                    )
                    sessionManager!!.saveStringValue(
                        Const.nativeAdId,
                        mFirebaseRemoteConfig.getString(Const.nativeAdId)
                    )
                    sessionManager!!.saveStringValue(
                        Const.bannerAdId,
                        mFirebaseRemoteConfig.getString(Const.bannerAdId)
                    )
                    Log.d(
                        TAG,
                        "onCreate: isAdShow " + sessionManager!!.getBooleanValue(Const.Adshow)
                    )
                    Log.d(
                        TAG,
                        "onCreate: interstitial_ad_key" + sessionManager!!.getStringValue(Const.interstitialAdId)
                    )
                    Log.d(
                        TAG,
                        "onCreate: native_ad_key " + sessionManager!!.getStringValue(Const.nativeAdId)
                    )
                    Log.d(
                        TAG,
                        "onCreate: banner_key " + sessionManager!!.getStringValue(Const.bannerAdId)
                    )
                    Log.d(
                        TAG,
                        "onCreate: appOpen_key " + sessionManager!!.getStringValue(Const.bannerAdId)
                    )
                    val handler = Handler()
                    handler.postDelayed({
                        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                        sessionManager!!.getBooleanValue(Const.isBoarding)
                    }, 200)
                } else {
                    Log.d(TAG, "onComplete: fetch failed")
                    Toast.makeText(
                        this@SplashActivity, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setStoreToken(str: String) {
        val sharedPreferences = getSharedPreferences(packageName, 0)
        f214sp = sharedPreferences
        val string = sharedPreferences.getString("gm", "")
        if (string != null) {
            f212gm = string
        }
        if (f213i == 0 && string == "") {
            val edit = f214sp.edit()
            edit.putString("gm", "0")
            edit.commit()
            f212gm = f214sp.getString("gm", "").toString()
        }
    }


    fun intentToHomeScreen() {
        Handler().postDelayed({
            val intent: Intent = Intent(
                this@SplashActivity,
                MetricActivity::class.java
            )
        }, 20)
    }

    fun stopCountdown() {
        countDownTimer!!.cancel()
        Log.d("mmmm", "stop countdown")
    }



}
