package com.rbs.workout.freak.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.gson.Gson
import com.rbs.workout.freak.AppSettings
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.MyInterstitialAds_abc
import com.rbs.workout.freak.R
import com.rbs.workout.freak.adapter.WorkoutListAdapter
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.databinding.ActivityLibraryBinding
import com.rbs.workout.freak.databinding.ActivityWorkoutListBinding
import com.rbs.workout.freak.databinding.AdUnifiedBinding
import com.rbs.workout.freak.interfaces.AdsCallback
import com.rbs.workout.freak.pojo.PWorkOutCategory
import com.rbs.workout.freak.pojo.PWorkOutDetails
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_workout_list.*
//import kotlinx.android.synthetic.main.activity_workout_list.llAdView
//import kotlinx.android.synthetic.main.activity_workout_list.llAdViewFacebook
import java.util.*

class WorkoutListActivity : BaseActivity(), AdsCallback ,MyInterstitialAds_abc.OnInterstitialAdListnear {

    private lateinit var binding: ActivityWorkoutListBinding
    private lateinit var CDInstrialAds: MyInterstitialAds_abc

    private lateinit var mContext: Context
    private lateinit var pWorkOutCategory: PWorkOutCategory
    private lateinit var workOutDetailData: ArrayList<PWorkOutDetails>
    private var lvlBuildWider = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_workout_list)

        CDInstrialAds = MyInterstitialAds_abc(this, this)
        binding = ActivityWorkoutListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSmallNativeAdd()

//        loadNativeAd()
        /*val param = llMain.layoutParams as FrameLayout.LayoutParams
        param.setMargins(0, 0, 0, getNavigationSize(this))
        llMain.layoutParams = param



        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("TAG", "onCreate::::223 "+ Build.VERSION.SDK_INT)
            val w: Window = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }*/


        mContext = this

        try {
            pWorkOutCategory = intent.getSerializableExtra(ConstantString.key_workout_category_item) as PWorkOutCategory
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ConstantString.pWorkOutCategory = pWorkOutCategory

        defaultSetup()

        initAction()

//        if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
//                LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
//            CommonConstantAd.loadBannerGoogleAd(this, llAdView, ConstantString.GOOGLE_BANNER_TYPE_AD)
//            llAdViewFacebook.visibility = View.GONE
//            llAdView.visibility = View.VISIBLE
//        } else if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK
//                &&
//                LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
//            llAdViewFacebook.visibility = View.VISIBLE
//            llAdView.visibility = View.GONE
//            CommonConstantAd.loadFacebookBannerAd(this, llAdViewFacebook)
//        } else {
//            llAdView.visibility = View.GONE
//            llAdViewFacebook.visibility = View.GONE
//        }

    }

    /* Todo common methods */
    private fun defaultSetup() {
        Log.e("TAG", "defaultSetup::::pWorkOutCategory  "+Gson().toJson(pWorkOutCategory) )
        txtWorkoutListCategoryName.text = pWorkOutCategory.catName
        if (pWorkOutCategory.dayName == "") {
            txtWorkoutListCategoryDetails.text = pWorkOutCategory.catSubCategory
        }else{
            txtWorkoutListCategoryDetails.text = "Day "+pWorkOutCategory.dayName.replace("0","")
        }
        imgToolbarBack.setImageResource(pWorkOutCategory.catImage)

        when {
            ConstantString.biginner == pWorkOutCategory.catDefficultyLevel -> {
                imgWorkoutDificultyImage.setImageResource(R.drawable.ic_beginner_level)
                lvlBuildWider = ConstantString.DEF_BUILD_WIDER_LVL_BEGINNER
            }
            ConstantString.intermediate == pWorkOutCategory.catDefficultyLevel -> {
                imgWorkoutDificultyImage.setImageResource(R.drawable.ic_intermediate_level)
                lvlBuildWider = ConstantString.DEF_BUILD_WIDER_LVL_INTERMEDIATE
            }
            ConstantString.advance == pWorkOutCategory.catDefficultyLevel -> {
                imgWorkoutDificultyImage.setImageResource(R.drawable.ic_advanced_level)
                lvlBuildWider = ConstantString.DEF_BUILD_WIDER_LVL_ADVANCED
            }
            else -> imgWorkoutDificultyImage.visibility = View.GONE
        }

        val data = DataHelper(mContext)
        if (pWorkOutCategory.catName == ConstantString.Full_Body || pWorkOutCategory.catName == ConstantString.Lower_Body) {
//            workOutDetailData = data.getWeekDayExerciseData(intent.getStringExtra(ConstantString.key_day_name), intent.getStringExtra(ConstantString.key_week_name))
            workOutDetailData = data.getWeekDayExerciseData(pWorkOutCategory.dayName, pWorkOutCategory.weekName, pWorkOutCategory.catTableName)
        } else if (pWorkOutCategory.catName == ConstantString.Build_wider) {
            workOutDetailData = data.getBuildWiderWorkoutDetails(pWorkOutCategory.catTableName,lvlBuildWider)
        } else {
            workOutDetailData = data.getWorkOutDetails(pWorkOutCategory.catTableName)
        }

        rcyWorkoutList.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        //rcyWorkoutList.addItemDecoration(Utils.SimpleDividerItemDecoration(this))

        tvWorkoutTotal.text = workOutDetailData.size.toString()

        val workoutListAdapter = WorkoutListAdapter(mContext, workOutDetailData)
        rcyWorkoutList.adapter = workoutListAdapter

    }
    var adClickCount: Int = 1
    private fun initAction() {
        imgWorkOutListBack.setOnClickListener {
            finish()
        }

        btnStartWorkout.setOnClickListener {

            if (LocalDB.getInteger(this, ConstantString.START_BTN_COUNT, 1) == 1) {
                if (LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                    when (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "")) {
                        else -> {
                            startExerciseActivity()
                            interstitialAds()
                        }
                    }
                    LocalDB.setInteger(this, ConstantString.START_BTN_COUNT, 0)
                } else {
                    startExerciseActivity()
                    interstitialAds()
                }
            } else {
                if (adClickCount == 1) {
                    LocalDB.setInteger(this, ConstantString.START_BTN_COUNT, 1)
                }
                startExerciseActivity()
                interstitialAds()
            }
        }



    }

    private fun startExerciseActivity() {
        try {
            val intent = Intent(mContext, WorkoutActivity::class.java)
            intent.putExtra(ConstantString.workout_list, workOutDetailData)
            intent.putExtra(ConstantString.key_day_name, pWorkOutCategory.dayName)
            intent.putExtra(ConstantString.key_week_name, pWorkOutCategory.weekName)
            intent.putExtra(ConstantString.work_table_name, pWorkOutCategory.catTableName)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun adLoadingFailed() {
        startExerciseActivity()
    }

    override fun adClose() {
        startExerciseActivity()
    }

    override fun startNextScreen() {
        startExerciseActivity()

    }

    override fun onAdClosed() {
        finish()
    }

    override fun onAdFail() {
        finish()
    }

    fun interstitialAds() {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            CDInstrialAds.showAds()
        } else {
            onAdFail()
        }
    }
}
