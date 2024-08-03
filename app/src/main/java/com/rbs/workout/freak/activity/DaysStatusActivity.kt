package com.rbs.workout.freak.activity

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.PopupMenu
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.MyInterstitialAds_abc
import com.rbs.workout.freak.R
import com.rbs.workout.freak.adapter.WeeklyDayStatusAdapter
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.databinding.ActivityDaysStatusBinding
import com.rbs.workout.freak.pojo.pWeeklyDayData
import com.rbs.workout.freak.utils.CommonUtility
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_days_status.*

//import kotlinx.android.synthetic.main.activity_days_status.llAdView
//import kotlinx.android.synthetic.main.activity_days_status.llAdViewFacebook

class DaysStatusActivity : BaseActivity()  {

    private lateinit var binding: ActivityDaysStatusBinding





    override fun onResume() {
        super.onResume()
        init()
        initAction()
    }

    // Todo Object declaration
    var arrWeeklyDayStatus = ArrayList<pWeeklyDayData>()
    lateinit var dbHelper: DataHelper
    var strCategoryName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days_status)

        binding = ActivityDaysStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setSmallNativeAdd()


        dbHelper = DataHelper(this)

        try {
            strCategoryName = intent.getStringExtra(ConstantString.key_category_name)!!
            Log.e("TAG", "onCreate:::strCategoryName $strCategoryName")
            txtTitle.text = strCategoryName
        } catch (e: Exception) {
            e.printStackTrace()
        }


//        init()
//        initAction()

    }

    // Todo Common methods
    private fun init() {


        if (strCategoryName == ConstantString.Full_Body) {
            imgMore.visibility = View.VISIBLE
            imgHeader.setImageResource(R.drawable.full_body)
        } else {
            imgMore.visibility = View.GONE
            imgHeader.setImageResource(R.drawable.lower_body)
        }

        // Set week data
        arrWeeklyDayStatus = dbHelper.getWorkoutWeeklyData(strCategoryName)
        rcyDaysName.adapter = WeeklyDayStatusAdapter(this, arrWeeklyDayStatus)

        // Week day progress data
        CommonUtility.setDayProgressData(this,strCategoryName,txtDayLeft,txtDayPer,pbDay)

    }

    private fun initAction() {

        imgBack.setOnClickListener { finish() }

        imgMore.setOnClickListener {
            showPushUpLvlPopup(imgMore)
        }

    }

    /* Todo Show push up level popup */
    private fun showPushUpLvlPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.m_push_up_level, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            selectPushUpLevelDialog()
            true
        }

        popup.show()
    }

    // Todo Select push up level dialog
    private fun selectPushUpLevelDialog() {
        val handler = Handler()
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_full_body_lvl)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        val rltBeginner = dialog.findViewById(R.id.rltBeginner) as RelativeLayout
        val rltIntermediate = dialog.findViewById(R.id.rltIntermediate) as RelativeLayout
        val rltAdvanced = dialog.findViewById(R.id.rltAdvanced) as RelativeLayout
        val imgBeginner = dialog.findViewById(R.id.imgBeginner) as ImageView
        val imgIntermediate = dialog.findViewById(R.id.imgIntermediate) as ImageView
        val imgAdvanced = dialog.findViewById(R.id.imgAdvanced) as ImageView

        imgClose.setOnClickListener {
            dialog.cancel()
        }

        rltBeginner.setOnClickListener {

            handler.postDelayed({
                // Do something after 5s = 5000ms
                dialog.cancel()
            }, 1000)

            rltBeginner.setBackgroundResource(R.drawable.dl_full_body_btn_bg_sel)
            imgBeginner.setImageResource(R.drawable.ic_dialog_done_red)

            rltBeginner.invalidate()
            LocalDB.setFullBodyLevel(this, ConstantString.DEF_FULL_BODY_BEGINNER)
        }

        rltIntermediate.setOnClickListener {
            handler.postDelayed({
                // Do something after 5s = 5000ms
                dialog.cancel()
            }, 1000)
            rltIntermediate.setBackgroundResource(R.drawable.dl_full_body_btn_bg_sel)
            imgIntermediate.setImageResource(R.drawable.ic_dialog_done_red)
            LocalDB.setFullBodyLevel(this, ConstantString.DEF_FULL_BODY_INTERMEDIATE)
        }

        rltAdvanced.setOnClickListener {
            handler.postDelayed({
                // Do something after 5s = 5000ms
                dialog.cancel()
            }, 1000)
            rltAdvanced.setBackgroundResource(R.drawable.dl_full_body_btn_bg_sel)
            imgAdvanced.setImageResource(R.drawable.ic_dialog_done_red)
            LocalDB.setFullBodyLevel(this, ConstantString.DEF_FULL_BODY_ADVANCE)
        }

        dialog.show()
    }



}
