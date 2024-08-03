package com.rbs.workout.freak.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.rbs.workout.freak.R
import com.rbs.workout.freak.utils.CommonUtility
import com.rbs.workout.freak.utils.ConstantDialog
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_weekly_goal_set.*

class WeeklyGoalSetActivity : BaseActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.slide_down)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_weekly_goal_set)

        init()
        initAction()

    }

    // Todo Common methods
    private fun init() {

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_setting_sec_arrow)
        drawable?.let { myDrawable ->
            DrawableCompat.setTint(myDrawable, ContextCompat.getColor(this, R.color.colorWhite))
            edWeekDays.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, myDrawable, null)
            edFirstDayOfWeek.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, myDrawable, null)
        }

        edWeekDays.setText(LocalDB.getWeekGoalDay(this).toString())
        edFirstDayOfWeek.setText(CommonUtility.getFirstWeekDayNameByDayNo(LocalDB.getFirstDayOfWeek(this)))

    }

    private fun initAction() {

        imgBack.setOnClickListener { onBackPressed() }

        btnSave.setOnClickListener {
            LocalDB.setWeekGoalDay(this, edWeekDays.text.toString().toInt())
            LocalDB.setFirstDayOfWeek(this, CommonUtility.getFirstWeekDayNoByDayName(edFirstDayOfWeek.text.toString()))

            onBackPressed()
        }

        edWeekDays.setOnClickListener { ConstantDialog.setWeekDayGoalDialog(this, edWeekDays) }

        edFirstDayOfWeek.setOnClickListener { ConstantDialog.setFirstWeekDayDialog(this, edFirstDayOfWeek) }

    }

}
