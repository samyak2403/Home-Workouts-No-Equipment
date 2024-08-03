package com.rbs.workout.freak.bw

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdSize
import com.google.android.material.appbar.AppBarLayout
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.MyInterstitialAds_abc
import com.rbs.workout.freak.R
import com.rbs.workout.freak.activity.BaseActivity
import com.rbs.workout.freak.activity.WorkoutActivity
import com.rbs.workout.freak.adapter.WorkoutListAdapter
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.interfaces.RewardedVideoCallBack
import com.rbs.workout.freak.pojo.PWorkOutCategory
import com.rbs.workout.freak.pojo.PWorkOutDetails
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_build_wider.flexible_example_appbar
import kotlinx.android.synthetic.main.activity_build_wider_workout_list.*


import kotlinx.android.synthetic.main.layout_ad_block_view.*
import java.util.*

class BuildWiderWorkoutListActivity : BaseActivity(), RewardedVideoCallBack , MyInterstitialAds_abc.OnInterstitialAdListnear{

    // Todo Implement methods for listener and overrides
    override fun videoCompleted() {
        rltAdblock.visibility = View.GONE
    }

    override fun videoFail() {
        Toast.makeText(this, "Video not completed, Please try Again!", Toast.LENGTH_SHORT).show()
    }
    private lateinit var CDInstrialAds: MyInterstitialAds_abc


    // Todo object declaration
    private lateinit var mContext: Context
    private lateinit var pWorkOutCategory: PWorkOutCategory
    private lateinit var workOutDetailData: ArrayList<PWorkOutDetails>
    var lvlBuildWider = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_wider_workout_list)
        CDInstrialAds = MyInterstitialAds_abc(this, this)

        /*val param = llMain.layoutParams as FrameLayout.LayoutParams
        param.setMargins(0, 0, 0, getNavigationSize(this))
        llMain.layoutParams = param



        if (Build.VERSION.SDK_INT >= 23) {
            val w: Window = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }*/


        mContext = this

        pWorkOutCategory = intent.getSerializableExtra(ConstantString.key_workout_category_item) as PWorkOutCategory
        ConstantString.pWorkOutCategory = pWorkOutCategory

        init()
        initAction()

        setSmallNativeAdd()
        


        var isShow = true
        var scrollRange = -1
        flexible_example_appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                /*status bar colortheme*/
                llText.visibility = View.GONE
                llOption.visibility = View.GONE
                isShow = true
            } else if (isShow) {
                /*status bar transparant*/
                llText.visibility = View.VISIBLE
                llOption.visibility = View.VISIBLE
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                isShow = false
            }
        })


    }

    // Todo Common methods
    private fun init() {

        /*if (Utils.getPref(this, ConstantString.pref_Key_purchase_status, false)) {
            rltAdblock.visibility = View.GONE
        } else{
            rltAdblock.visibility = View.VISIBLE
        }*/

        tvData1.typeface = Typeface.DEFAULT
        tvData2.typeface = Typeface.DEFAULT

        when {
            ConstantString.biginner == pWorkOutCategory.catDefficultyLevel -> {
                ivCategory.setImageResource(R.drawable.build_wider_beginner)
                lvlBuildWider = ConstantString.DEF_BUILD_WIDER_LVL_BEGINNER
                tvDifficulty.text = resources.getString(R.string.beginner)
                tvData1.text = resources.getString(R.string.build_wider_beginner_text_1)
                tvData2.text = resources.getString(R.string.build_wider_beginner_text_2)
            }
            ConstantString.intermediate == pWorkOutCategory.catDefficultyLevel -> {
                ivCategory.setImageResource(R.drawable.build_wider_intermidiate)
                lvlBuildWider = ConstantString.DEF_BUILD_WIDER_LVL_INTERMEDIATE
                tvDifficulty.text = resources.getString(R.string.intermediate)
                tvData1.text = resources.getString(R.string.build_wider_intermediate_text_1)
                tvData2.text = HtmlCompat.fromHtml(resources.getString(R.string.build_wider_intermediate_text_2), HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            ConstantString.advance == pWorkOutCategory.catDefficultyLevel -> {
                ivCategory.setImageResource(R.drawable.build_wider_advance)
                lvlBuildWider = ConstantString.DEF_BUILD_WIDER_LVL_ADVANCED
                tvDifficulty.text = resources.getString(R.string.advanced)
                tvData1.text = resources.getString(R.string.build_wider_advanced_text_1)
                tvData2.text = HtmlCompat.fromHtml(resources.getString(R.string.build_wider_advanced_text_2), HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

        }

        txtBWTime.text = pWorkOutCategory.BWTime
        txtBWWorkout.text = pWorkOutCategory.BWWorkout

        val data = DataHelper(mContext)
        workOutDetailData = data.getBuildWiderWorkoutDetails(pWorkOutCategory.catTableName, lvlBuildWider)

        val workoutListAdapter = WorkoutListAdapter(mContext, workOutDetailData)

        rcyWorkoutList.layoutManager = LinearLayoutManager(mContext)
        rcyWorkoutList.adapter = workoutListAdapter
    }

    private fun initAction() {

        btnStartWorkout.setOnClickListener {
            val intent = Intent(mContext, WorkoutActivity::class.java)
            intent.putExtra(ConstantString.workout_list, workOutDetailData)
            intent.putExtra(ConstantString.key_day_name, pWorkOutCategory.dayName)
            intent.putExtra(ConstantString.key_week_name, pWorkOutCategory.weekName)
            startActivity(intent)
            finish()
            interstitialAds()
            ///////////
        }

        ivMask.setOnClickListener {
            if (tvData2.visibility == View.VISIBLE) {
                ivMask.setImageResource(R.drawable.ic_build_arrow_down)
                tvData2.visibility = View.GONE
            } else {
                ivMask.setImageResource(R.drawable.ic_build_arrow_up)
                tvData2.visibility = View.VISIBLE
            }
        }

        toolbar.setNavigationOnClickListener { _ -> onBackPressed() }

        /*rltAdblock.setOnClickListener {
            CommonConstantAd.rewardedVideoAd(this, this)
        }*/

    }

    override fun setBannerAdd(adContainer: View) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = sessionManager.getStringValue(Const.bannerAdId)
            (adContainer as RelativeLayout).addView(adView)
            adView.loadAd(adRequest)
        }
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
