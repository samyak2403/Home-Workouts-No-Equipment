package com.rbs.workout.freak.utils

import android.app.Dialog
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.rbs.workout.freak.R
import com.shawnlin.numberpicker.NumberPicker

// Created by Kuldeep
object ConstantDialog {

    fun soundOptionDialog(context: Context) {

        var boolOtherClick = false
        var boolMuteClick = false
        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_sound_option)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val swtMute = dialog.findViewById(R.id.swtMute) as Switch
        val swtVoiceGuide = dialog.findViewById(R.id.swtVoiceGuide) as Switch
        val swtCoachTips = dialog.findViewById(R.id.swtCoachTips) as Switch
        val btnOk = dialog.findViewById(R.id.btnOk) as TextView

        swtVoiceGuide.isChecked = LocalDB.getVoiceGuide(context)

        swtCoachTips.isChecked = LocalDB.getCoachTips(context)

        if (LocalDB.getSoundMute(context)) {
            swtMute.isChecked = true
            swtCoachTips.isChecked = false
            swtVoiceGuide.isChecked = false
        }

        swtMute.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!boolOtherClick) {
                    boolMuteClick = true
                    if (isChecked) {
                        LocalDB.setSoundMute(context, true)
                        swtVoiceGuide.isChecked = false
                        swtCoachTips.isChecked = false

                    } else {
                        LocalDB.setSoundMute(context, false)
                        swtVoiceGuide.isChecked = LocalDB.getVoiceGuide(context)
                        swtCoachTips.isChecked = LocalDB.getCoachTips(context)
                    }
                    boolMuteClick = false
                }
            }
        })

        swtVoiceGuide.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!boolMuteClick) {
                    boolOtherClick = true
                    if (isChecked) {
                        swtMute.isChecked = false
                        LocalDB.setSoundMute(context, false)
                        LocalDB.setVoiceGuide(context, true)
                    } else {
                        LocalDB.setVoiceGuide(context, false)
                    }
                    boolOtherClick = false
                }
            }
        })

        swtCoachTips.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!boolMuteClick) {
                    boolOtherClick = true
                    if (isChecked) {
                        swtMute.isChecked = false
                        LocalDB.setSoundMute(context, false)
                        LocalDB.setCoachTips(context, true)
                    } else {
                        LocalDB.setCoachTips(context, false)
                    }
                    boolOtherClick = false
                }
            }
        })

        btnOk.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

    fun setDurationDialog(context: Context, type: String, tvDuration: TextView) {

        var duration = 0
        var maxDuration = 0
        var minDuration = 0
        var startMillis: Long = 0

        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_set_duration)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val tvDlDurationTitle = dialog.findViewById(R.id.tvDlDurationTitle) as TextView
        val tvDurations = dialog.findViewById(R.id.tvDurations) as TextView
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvSet = dialog.findViewById(R.id.tvSet) as TextView

        val btnPrev = dialog.findViewById(R.id.btnPrev) as ImageButton
        val btnNext = dialog.findViewById(R.id.btnNext) as ImageButton

        if (type == ConstantString.DL_COUNT_DOWN_TIME) {
            tvDlDurationTitle.text = "Set duration (10 ~ 15 secs)"
            duration = LocalDB.getCountDownTime(context)

            maxDuration = 15
            minDuration = 10
        } else if (type == ConstantString.DL_REST_SET) {
            tvDlDurationTitle.text = "Set duration (5 ~ 180 secs)"
            duration = LocalDB.getRestTime(context)

            maxDuration = 180
            minDuration = 5
        }

        tvDurations.text = duration.toString()

        btnPrev.setOnTouchListener { v, event ->

            val time = System.currentTimeMillis()
            if (startMillis == (0).toLong() || (time - startMillis > 500)) {
                startMillis = time
//                count = 1
                if (duration > minDuration) {
                    duration--
                    tvDurations.text = duration.toString()
                }
            }
            if (MotionEvent.ACTION_UP == event.action) {
                startMillis = 0
            }

            false
        }

        btnNext.setOnTouchListener { v, event ->

            val time = System.currentTimeMillis()
            if (startMillis == (0).toLong() || (time - startMillis > 500)) {
                startMillis = time
                if (duration < maxDuration) {
                    duration++
                    tvDurations.text = duration.toString()
                }
            }
            if (MotionEvent.ACTION_UP == event.action) {
                startMillis = 0
            }

            false
        }

        tvCancel.setOnClickListener {
            dialog.cancel()
        }

        tvSet.setOnClickListener {
            if (type == ConstantString.DL_COUNT_DOWN_TIME) {
                LocalDB.setCountDownTime(context, duration)
            } else if (type == ConstantString.DL_REST_SET) {
                LocalDB.setRestTime(context, duration)
            }
            tvDuration.text = duration.toString().plus(" secs")
            dialog.cancel()
        }

        dialog.show()
    }

    fun setGenderDialog(context: Context, tvGender: TextView) {
        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_gender)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val rdgGender = dialog.findViewById(R.id.rdgGender) as RadioGroup

        rdgGender.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rdFemale) {
                tvGender.text = context.getString(R.string.female)
                LocalDB.setGender(context, context.getString(R.string.female))
            } else if (checkedId == R.id.rdMale) {
                tvGender.text = context.getString(R.string.male)
                LocalDB.setGender(context, context.getString(R.string.male))
            }
            dialog.cancel()
        }

        dialog.show()
    }

    // Todo Show before ad showing progress dialog
    fun adDimLightProgressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.anim_ad_progress)

//        dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        dialog.show()
        return dialog
    }

    // Todo Show before ad showing progress dialog
    fun adProgressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.anim_ad_progress)

        //dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        dialog.show()
        return dialog
    }

    // Todo Set weekly goal dialog
    fun setWeekDayGoalDialog(context: Context, edWeekDayGoal: EditText) {

        var curGoalDay = LocalDB.getWeekGoalDay(context)

        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_set_week_goal)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Todo Control declaration
        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnOkay = dialog.findViewById(R.id.btnOkay) as Button
        val npWeeklyDayGoal = dialog.findViewById(R.id.npWeeklyDayGoal) as NumberPicker

        val data = arrayOf("1", "2", "3", "4", "5", "6", "7")

        npWeeklyDayGoal.minValue = 1
        npWeeklyDayGoal.maxValue = data.size
        npWeeklyDayGoal.displayedValues = data
        npWeeklyDayGoal.wrapSelectorWheel = false
        npWeeklyDayGoal.value = curGoalDay

        npWeeklyDayGoal.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
//                edWeekDayGoal.setText(newVal.toString())
                curGoalDay = newVal
            }
        })

        btnCancel.setOnClickListener {

            dialog.cancel()
        }

        btnOkay.setOnClickListener {
            //LocalDB.setWeekGoalDay(context, curGoalDay)
            edWeekDayGoal.setText(curGoalDay.toString())
            dialog.cancel()
        }

        dialog.show()
    }

    // Todo set First Day of week dialog
    fun setFirstWeekDayDialog(context: Context, edFirstDayOfWeek: EditText) {

        var curWeekName = LocalDB.getFirstDayOfWeek(context)

        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_first_day_of_week)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Todo Control declaration
        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnOkay = dialog.findViewById(R.id.btnOkay) as Button
        val npWeeklyDayGoal = dialog.findViewById(R.id.npWeeklyDayGoal) as NumberPicker

        val data = arrayOf("Sunday", "Monday", "Saturday")

        npWeeklyDayGoal.minValue = 1
        npWeeklyDayGoal.maxValue = data.size
        npWeeklyDayGoal.displayedValues = data
        npWeeklyDayGoal.wrapSelectorWheel = false
        npWeeklyDayGoal.value = curWeekName

        npWeeklyDayGoal.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
//                edWeekDayGoal.setText(newVal.toString())
                curWeekName = newVal
            }
        })

        btnCancel.setOnClickListener {
            dialog.cancel()
        }

        btnOkay.setOnClickListener {
            edFirstDayOfWeek.setText(CommonUtility.getFirstWeekDayNameByDayNo(curWeekName))
            //LocalDB.setFirstDayOfWeek(context, curWeekName)
            //Toast.makeText(context, CommonUtility.getFirstWeekDayNameByDayNo(curWeekName), Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }

        dialog.show()
    }

    fun confirmDialog(content: Context, strTitle: String, strMsg: String): Boolean {

        val builder1 = AlertDialog.Builder(content)
        builder1.setTitle(strTitle)
        builder1.setMessage(strMsg)
        builder1.setCancelable(true)

        builder1.setPositiveButton("Yes") { dialog, id ->
            dialog.cancel()
            return@setPositiveButton
        }

        builder1.setNegativeButton("No") { dialog, id ->
            dialog.cancel()
            return@setNegativeButton
        }

        val alert11 = builder1.create()
        alert11.show()

        return false
    }

}
