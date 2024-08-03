package com.rbs.workout.freak.history

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View

import com.rbs.workout.freak.R
import com.rbs.workout.freak.activity.BaseActivity
import com.rbs.workout.freak.compactcalender.CompactCalendarView
import com.rbs.workout.freak.compactcalender.Event
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.report.ReportActivity
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.CommonUtility
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB

import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.imgBack
import kotlinx.android.synthetic.main.activity_history.llAdView
import kotlinx.android.synthetic.main.activity_history.llAdViewFacebook
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : BaseActivity() {

    override fun onBackPressed() {
        if (isCheckFrom!!){
            startActivity(Intent(this,ReportActivity::class.java))
            finish()
        }else{
            super.onBackPressed()
        }

//        startActivity(Intent(this,ReportActivity::class.java))

    }

    // Todo Object declaration
    lateinit var context: Context
    lateinit var dbHelper: DataHelper
    var isCheckFrom :Boolean?=null

    private val dateFormatForMonth = SimpleDateFormat("MMM - yyyy", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        dbHelper = DataHelper(this)
        context = this

        init()

        initAction()

        if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
                LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            CommonConstantAd.loadBannerGoogleAd(this, llAdView, ConstantString.GOOGLE_BANNER_TYPE_AD)
            llAdViewFacebook.visibility = View.GONE
            llAdView.visibility = View.VISIBLE
        } else if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK
                &&
                LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            llAdViewFacebook.visibility = View.VISIBLE
            llAdView.visibility = View.GONE
            CommonConstantAd.loadFacebookBannerAd(this, llAdViewFacebook)
        } else {
            llAdView.visibility = View.GONE
            llAdViewFacebook.visibility = View.GONE
        }

        try {
            isCheckFrom = intent.getBooleanExtra(ConstantString.HISTORY_FROM,false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Todo Common Methods */
    private fun init() {

        compactCalendarSetup()

        val arrHistoryData = dbHelper.getWeekDayOfHistory()
        rcyHistoryList.adapter = HistoryTitleAdapter(this, arrHistoryData)

    }

    private fun initAction() {
        imgBack.setOnClickListener { onBackPressed() }

        imgbtnMonthPrev.setOnClickListener {
            CompatCalenderView.scrollLeft()
        }

        imgbtnMonthNext.setOnClickListener {
            CompatCalenderView.scrollRight()
        }

    }

    /* Todo Compact calender methods */
    private fun compactCalendarSetup() {

        CompatCalenderView.removeAllEvents()
        CompatCalenderView.shouldScrollMonth(false)

        tvMonthYear.text = dateFormatForMonth.format(Calendar.getInstance().time)

        val arrCompleteExerciseDt: ArrayList<String> = dbHelper.getCompleteExerciseDate()

        for (i in 0 until arrCompleteExerciseDt.size) {
            addEvents(CommonUtility.getFullDateStringToMilliSecond(arrCompleteExerciseDt[i]))
        }

//        addEvents(14, 6, 2019)
//        addEvents(15, 6, 2019)
//        addEvents(16, 6, 2019)
//        addEvents(17, 6, 2019)

        CompatCalenderView.setListener(object : CompactCalendarView.CompactCalendarViewListener {

            override fun onDayClick(dateClicked: Date?) {
                if (dateClicked != null) {
//                    setTotalWorkoutTime(dateClicked.time)
//                    setRoundViewAdapter(dateClicked.time)
                    //Toast.makeText(context, DateUtils.getDate(dateClicked.time, Locale.getDefault()), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                tvMonthYear.text = dateFormatForMonth.format(firstDayOfNewMonth)
            }
        })

    }

    /* Todo Add Events */
    private fun addEvents(timeInMillis: Long) {
//    private fun addEvents(date: Int, month: Int, year: Int) {
        val currentCalender = Calendar.getInstance()
        currentCalender.setTime(Date())
//        currentCalender.set(Calendar.DAY_OF_MONTH, 1)

//        currentCalender.set(Calendar.MONTH, month)
//        currentCalender.set(Calendar.YEAR, year)
//        currentCalender.set(Calendar.DATE, date)

//        val timeInMillis = currentCalender.timeInMillis

//        CompatCalenderView.setCurrentDate(currentCalender.time)

        CompatCalenderView.addEvent(
                Event(Color.argb(255, 255, 2, 2), timeInMillis,
                        "Event at " + Date(timeInMillis))
        )
    }

}
