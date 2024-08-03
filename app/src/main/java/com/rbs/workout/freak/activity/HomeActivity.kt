package com.rbs.workout.freak.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.messaging.FirebaseMessaging
import com.rbs.workout.freak.All_ads
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.ExitDialog
import com.rbs.workout.freak.R
import com.rbs.workout.freak.adapter.WorkoutCategoryAdapter
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.databinding.ActivityHomeBinding
import com.rbs.workout.freak.history.HistoryActivity
import com.rbs.workout.freak.interfaces.CallbackListener
import com.rbs.workout.freak.pojo.PWorkOutCategory
import com.rbs.workout.freak.report.WeekDayReportAdapter
import com.rbs.workout.freak.utils.CommonUtility
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_home.*
import thehexcoders.google.android.ads.nativetemplates.TemplateView
import kotlin.math.roundToInt

class HomeActivity : BaseActivity(), View.OnClickListener, CallbackListener {

    private lateinit var binding: ActivityHomeBinding
    var adsHelper: All_ads? = null
    private var exitDialog: ExitDialog? = null
    var templateView: TemplateView? = null

    var nativeAd: NativeAd? = null

    override fun onBackPressed() {
//        confirmationDialog(this, object : ConfirmDialogCallBack {
//            override fun Okay() {
//                val homeIntent = Intent(Intent.ACTION_MAIN)
//                homeIntent.addCategory(Intent.CATEGORY_HOME)
//                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                startActivity(homeIntent)
//                finishAffinity()
//            }
//
//            override fun cancel() {
//            }
//
//        }, "", getString(R.string.exit_confirmation))
//        super.onBackPressed()


        exitDialog!!.show()


    }





    override fun onResume() {
        super.onResume()


        setupWeekTopData()
        workoutAdapter.notifyDataSetChanged()

    }

    /* Todo Objects*/
    private lateinit var mContext: Context
    lateinit var dbHelper: DataHelper
    lateinit var workoutAdapter: WorkoutCategoryAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        //setBannerAdd(binding.adMobView)

        adsHelper = All_ads(this)
        exitDialog = ExitDialog(this)


        templateView = findViewById(R.id.my_template)


        setBannerAdd(adMobView)

        mContext = this
        DataHelper(mContext).getReadWriteDB()

        dbHelper = DataHelper(mContext)

        setupHomeData()
        successCall()



        MobileAds.initialize(
            this@HomeActivity
        ) { initializationStatus: InitializationStatus? -> }


        //Fragment use


        //Fragment use
        val builder = AdLoader.Builder(this@HomeActivity, "ca-app-pub-4660381108030921/9216228350")
        builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
               // Toast.makeText(this@HomeActivity, loadAdError.message, Toast.LENGTH_SHORT).show()
            }
        })

        builder.forNativeAd { NativeAd: NativeAd ->
            nativeAd = NativeAd
        }
        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())


        subScribeToFirebaseTopic()

    }

    private fun subScribeToFirebaseTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("workout_at_home_topic")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("subScribeFirebaseTopic", ": Fail")
                } else {
                    Log.e("subScribeFirebaseTopic", ": Success")
                }
            }
    }

    // Todo setup weekly data
    private fun setupWeekTopData() {

        txtTotalWorkouts.text = dbHelper.getHistoryTotalWorkout().toString()
        txtTotalKcal.text = dbHelper.getHistorytotalKcal().toInt().toString()
        txtTotalMinutes.text = ((dbHelper.getHistorytotalMinutes() / 60).toDouble()).roundToInt().toString()

        val arrCurrentWeek = CommonUtility.getCurrentWeek()
        var completedWeekDay = 0
        for (pos in 0 until arrCurrentWeek.size) {
            if (dbHelper.isHistoryAvailable(CommonUtility.convertFullDateToDate(arrCurrentWeek[pos]))) {
                completedWeekDay++
            }
        }

        val weekDayGoal = LocalDB.getWeekGoalDay(this)

        txtWeekStatus.text = HtmlCompat.fromHtml("<font color='${ContextCompat.getColor(mContext, R.color.colorWhite)}'>$completedWeekDay</font>/$weekDayGoal", HtmlCompat.FROM_HTML_MODE_LEGACY)
//      txtWeekStatus.text = Html.fromHtml("<font color='${ContextCompat.getColor(mContext,R.color.colorTheme)}'>$completedWeekDay</font>/7")

        rcyHistoryWeek.adapter = WeekDayReportAdapter(this, true)

    }

    private fun setupHomeData() {



        val arrWorkoutCategoryData: ArrayList<PWorkOutCategory> = ArrayList()

        var workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = "7 X 4 Challenge"
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.full_body
        workoutCategoryDetails.catName = ConstantString.Full_Body
        workoutCategoryDetails.catSubCategory = "7 X 4 Challenge"
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.full_body
        workoutCategoryDetails.catTableName = ConstantString.tbl_full_body_workouts_list
        arrWorkoutCategoryData.add(workoutCategoryDetails)

/*        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.Build_wider
        workoutCategoryDetails.catName = ConstantString.Build_wider
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.build_wider
        workoutCategoryDetails.catTableName = ConstantString.tbl_bw_exercise
        arrWorkoutCategoryData.add(workoutCategoryDetails)*/

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.full_body
        workoutCategoryDetails.catName = ConstantString.Lower_Body
        workoutCategoryDetails.catSubCategory = "7 X 4 Challenge"
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.lower_body
        workoutCategoryDetails.catTableName = ConstantString.tbl_lower_body_list
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = ConstantString.Chest
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = ConstantString.Chest
        workoutCategoryDetails.catSubCategory = "Beginners"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.chest_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_chest_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = ConstantString.Chest
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.chest_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_chest_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = ConstantString.Chest
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.chest_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_chest_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = ConstantString.Abs
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = ConstantString.Abs
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_abs_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = ConstantString.Abs
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_abs_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = ConstantString.Abs
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_abs_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = ConstantString.Arm
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = ConstantString.Arm
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.arm_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_arm_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = ConstantString.Arm
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_arm_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = ConstantString.Arm
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_arm_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = ConstantString.Shoulder_and_Back
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = ConstantString.Shoulder_and_Back
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.shoulder_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_shoulder_back_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = ConstantString.Shoulder_and_Back
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.shoulder_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_shoulder_back_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = ConstantString.Shoulder_and_Back
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.shoulder_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_shoulder_back_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = ConstantString.Leg
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = ConstantString.Leg
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.leg_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_leg_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = ConstantString.Leg
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.leg_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_leg_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = ConstantString.Leg
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.leg_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_leg_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        rcyWorkout.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        //rcyWorkout.addItemDecoration(Utils.SimpleDividerItemDecoration(this))

        workoutAdapter = WorkoutCategoryAdapter(mContext, arrWorkoutCategoryData)
        rcyWorkout.adapter = workoutAdapter




    }

    /* Todo Override methods */
    override fun onClick(v: View) {

        when (v.id) {



            R.id.imgbtnDrawer -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            R.id.rltLibrary, R.id.tvLibrary -> {
                startActivity(Intent(this, LibraryActivity::class.java))
            }

            R.id.txtWeekStatus -> {
                val intent = Intent(mContext, WeeklyGoalSetActivity::class.java)
                mContext.startActivity(intent)
                overridePendingTransition(R.anim.slide_up, R.anim.none)
            }

            R.id.rlReport, R.id.llReport, R.id.llHistory -> {
                val intent = Intent(mContext, HistoryActivity::class.java)
                intent.putExtra(ConstantString.HISTORY_FROM,false)
                mContext.startActivity(intent)
            }

        }
    }



    private fun successCall() {

        if (isNetworkConnected()) {
            if (ConstantString.ENABLE_DISABLE == ConstantString.ENABLE) {

                LocalDB.setString(this@HomeActivity, ConstantString.AD_TYPE_FB_GOOGLE, ConstantString.AD_TYPE_FACEBOOK_GOOGLE)
                LocalDB.setString(this@HomeActivity, ConstantString.FB_BANNER, ConstantString.FB_BANNER_ID)
                LocalDB.setString(this@HomeActivity, ConstantString.FB_INTERSTITIAL, ConstantString.FB_INTERSTITIAL_ID)
                LocalDB.setString(this@HomeActivity, ConstantString.STATUS_ENABLE_DISABLE, ConstantString.ENABLE_DISABLE)


                setAppAdId(ConstantString.GOOGLE_ADMOB_APP_ID)


            } else {
                LocalDB.setString(this@HomeActivity, ConstantString.STATUS_ENABLE_DISABLE, ConstantString.ENABLE_DISABLE)
            }
        } else {
            openInternetDialog(this, true)
        }

    }


    fun setAppAdId(id: String?) {
        try {
            val applicationInfo =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            val beforeChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:BeforeChange:::::  $beforeChangeId")
            applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", id)
            val AfterChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:AfterChange::::  $AfterChangeId")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {
        successCall()
    }


    override fun setBannerAdd(adContainer: View) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = sessionManager.getStringValue(Const.bannerAdId)
            (adContainer as RelativeLayout).addView(adView)
            adView.loadAd(adRequest)
        }
    }



}
