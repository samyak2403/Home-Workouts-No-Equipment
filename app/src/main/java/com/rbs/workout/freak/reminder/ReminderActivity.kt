package com.rbs.workout.freak.reminder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox

import android.widget.LinearLayout
import android.widget.Toast
import com.google.gson.Gson

import com.rbs.workout.freak.R
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.utils.ConstantString
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.rbs.workout.freak.activity.BaseActivity
import com.rbs.workout.freak.reminderNew.AlarmReceiver
import com.rbs.workout.freak.reminderNew.Reminder
import com.rbs.workout.freak.reminderNew.ReminderDatabase
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.android.synthetic.main.activity_reminder.imgBack
import kotlinx.android.synthetic.main.activity_reminder.llAdView
import kotlinx.android.synthetic.main.activity_reminder.llAdViewFacebook
import java.util.*
import kotlin.collections.ArrayList


class ReminderActivity : BaseActivity(), ReminderInterface {

    lateinit var context: Context
    lateinit var dbHelper: DataHelper
    var arrReminder = ArrayList<ReminderClass>()


    private var listReminder: java.util.ArrayList<Reminder>? = null
    private val mTitle: String? = ""
    private var mTime: String? = ""
    private var mDate: String? = ""
    private var mRepeatNo: String? = ""
    private val mRepeatType: String? = "Day"
    private val mActive: String? = "true"
    private val mRepeat: String? = "false"
    private var mDateSplit: Array<String>? = null
    private var mTimeSplit: Array<String>? = null
    private var mReceivedID = 0
    private var mYear = 0
    private var mMonth: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mDay: Int = 0
    private var mRepeatTime: Long = 0
    private var mCalendar: Calendar? = null
    private var mcalendarCurrent: Calendar? = null
    private val mReceivedReminder: Reminder? = null
    private var rb: ReminderDatabase? = null
    private var mAlarmReceiver: AlarmReceiver? = null

    // Constant values in milliseconds
    private val milMinute = 60000L
    private val milHour = 3600000L
    private val milDay = 86400000L
    private val milWeek = 604800000L
    private val milMonth = 2592000000L
    private lateinit var stringBuilderDay: StringBuilder
    private var repeatDialog: AlertDialog? = null
    private var arrOfDays = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        context = this

        dbHelper = DataHelper(context)

        init()
        initAction()
        initReminder()

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

    }

    private fun initReminder() {
        // Obtain Date and Time details

        // Obtain Date and Time details

        rb = ReminderDatabase(this)
        mRepeatNo = 1.toString()
        mCalendar = Calendar.getInstance()
        mcalendarCurrent = Calendar.getInstance()
        mAlarmReceiver = AlarmReceiver()


        mHour = mCalendar!!.get(Calendar.HOUR_OF_DAY)
        mMinute = mCalendar!!.get(Calendar.MINUTE)
        mYear = mCalendar!!.get(Calendar.YEAR)
        mMonth = mCalendar!!.get(Calendar.MONTH) + 1
        mDay = mCalendar!!.get(Calendar.DATE)

        mDate = "$mDay/$mMonth/$mYear"
        mTime = "$mHour:$mMinute"
        mDateSplit = mDate!!.split("/".toRegex()).toTypedArray()
        mTimeSplit = mTime!!.split(":".toRegex()).toTypedArray()

        mDay = mDateSplit!!.get(0).toInt()
        mMonth = mDateSplit!!.get(1).toInt()
        mYear = mDateSplit!!.get(2).toInt()
        mHour = mTimeSplit!!.get(0).toInt()
        mMinute = mTimeSplit!!.get(1).toInt()

    }


    /* Todo Common methods */
    private fun init() {

        /*arrReminder = dbHelper.getRemindersList()

        rcyReminderList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rcyReminderList.adapter = ReminderListAdapter(this, arrReminder)*/


        try {
            val rb = ReminderDatabase(this)
            listReminder = rb.allReminders
            rcyReminderList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            rcyReminderList.adapter = ReminderListAdapter(this, listReminder!!)
//            reminderAdapter!!.addAll(listReminder!!)
            Log.e("TAG", "init:All Reminder::::::  " + Gson().toJson(listReminder))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAction() {
        imgAddReminder.setOnClickListener {
            initReminder()
            openTimePicker(false, mHour, mMinute)
        }

        imgBack.setOnClickListener {
            finish()
        }

    }

    var hourOfDay = 0
    var minute = 0
    var seconds = 0
    var reminderId = ""

    /*private fun openTimePicker(isEdit: Boolean) {
        val mTimePicker = MyTimePickerDialog(
                this,
                MyTimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, seconds ->
                    this.hourOfDay = hourOfDay
                    this.minute = minute
                    this.seconds = seconds
                    if (isEdit) {
                        val mCount = dbHelper.updateReminderTimes(reminderId, hourOfDay.toString().plus(":").plus(minute))
                        init()
                        startAlarm(reminderId.toInt())
                    } else {
                        openDayPickerDialog(context, false, arrOfDays)
                    }
                },
                Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, true
        )
        mTimePicker.show()
    }*/


/*    override fun editDays(Id: String, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>) {
        reminderId = Id
        openDayPickerDialog(context, true, arrOfDaysArgs)
        repeatDialog = null
    }

    override fun editTime(Id: String, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>) {
        reminderId = Id
        openTimePicker(true)
    }

    */
    /* Todo Open day picker dialog *//*


    private fun openDayPickerDialog(context: Context, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>) {
        if (repeatDialog == null) {
            var stringArrayList = ArrayList<String>()
            stringArrayList.clear()
            var arrOfDays = arrOfDaysArgs
            stringArrayList = arrOfDays

            try {

                val margin = resources.getDimension(R.dimen.space_20dp).toInt()
                val llRepeat = layoutInflater.inflate(R.layout.dl_alarm_repeat_type, null) as LinearLayout

                val chkMon = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkMon)
                if (stringArrayList.contains("1")) {
                    chkMon.isChecked = true
                }

                val chkTue = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkTue)
                if (stringArrayList.contains("2")) {
                    chkTue.isChecked = true
                }

                val chkWed = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkWed)
                if (stringArrayList.contains("3")) {
                    chkWed.isChecked = true
                }

                val chkThu = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkThu)
                if (stringArrayList.contains("4")) {
                    chkThu.isChecked = true
                }

                val chkFri = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkFri)
                if (stringArrayList.contains("5")) {
                    chkFri.isChecked = true
                }

                val chkSat = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkSat)
                if (stringArrayList.contains("6")) {
                    chkSat.isChecked = true
                }

                val chkSun = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkSun)
                if (stringArrayList.contains("7")) {
                    chkSun.isChecked = true
                }

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Select Days")
                builder.setCancelable(true)
                val llMain = LinearLayout(context)
                val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(margin, 0, margin, 0)

                chkMon.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("1")
                    } else {
                        stringArrayList.remove("1")
                    }
                }

                chkTue.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("2")
                    } else {
                        stringArrayList.remove("2")
                    }
                }

                chkWed.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("3")
                    } else {
                        stringArrayList.remove("3")
                    }
                }

                chkThu.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("4")
                    } else {
                        stringArrayList.remove("4")
                    }
                }

                chkFri.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("5")
                    } else {
                        stringArrayList.remove("5")
                    }
                }

                chkSat.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("6")
                    } else {
                        stringArrayList.remove("6")
                    }
                }

                chkSun.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("7")
                    } else {
                        stringArrayList.remove("7")
                    }
                }

                if (stringArrayList.size == 7) {

                }

                llRepeat.layoutParams = layoutParams
                llMain.addView(llRepeat)
                builder.setView(llMain)
                builder.setPositiveButton("Done") { dialog, _ ->
                    try {

                        if (stringArrayList.size == 0 || stringArrayList.size == 7) {
                            arrOfDays = stringArrayList
                        } else {
                            arrOfDays = stringArrayList
                        }

                        if (isEdit) {
                            val mCount = dbHelper.updateReminderDays(reminderId, getDayInString(arrOfDays))
                        } else {
                            val reminderClass = ReminderClass()
                            reminderClass.Time = "$hourOfDay:$minute"
                            reminderClass.Days = getDayInString(arrOfDays)
                            reminderClass.IsActive = "true"

                            val mCount = dbHelper.addReminder(reminderClass)

                            startAlarm(mCount)
                        }
                        init()

                        dialog.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                repeatDialog = builder.create()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        try {
            if (!repeatDialog!!.isShowing) {
                repeatDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getDayInString(arrOfDays: ArrayList<String>): String {

        stringBuilderDay = StringBuilder()
        for (i in 0 until arrOfDays.size) {
            if (stringBuilderDay.isEmpty()) {
                stringBuilderDay.append(arrOfDays[i])
            } else {
                stringBuilderDay.append(',')
                stringBuilderDay.append(arrOfDays[i])
            }
        }

        return stringBuilderDay.toString()
    }

    fun startAlarm(mCount: Int) {
        val alarmStartTime = Calendar.getInstance()
        alarmStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        alarmStartTime.set(Calendar.MINUTE, minute)
        alarmStartTime.set(Calendar.SECOND, 0)

        val _12HourDt = CommonUtility.convertDate(alarmStartTime.timeInMillis, "yyyy-MM-dd hh:mm")

        val format = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val alarmDt = format.parse(_12HourDt)

        val intent = Intent(this, MyBroadcastReceiver::class.java)
        intent.putExtra(ConstantString.extraReminderId, "" + mCount)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, mCount, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmDt.time,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        )


    }*/





    private fun openTimePicker(isEdit: Boolean, hour: Int, minute: Int) {
        val mTimePicker = MyTimePickerDialog(
                this,
                MyTimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, seconds ->


                    if (hourOfDay > 12) {
                        Log.e("TAG", "openTimePicker:::: hourOfDay>12   " + (hourOfDay - 12) + ":" + (minute) + "pm")
                    } else if (hourOfDay == 12) {
                        Log.e("TAG", "openTimePicker:::: hourOfDay==12   " + "12" + ":" + (minute) + "pm")
                    } else if (hourOfDay < 12) {
                        if (hourOfDay != 0) {
                            Log.e("TAG", "openTimePicker:::: hourOfDay<12 IFFFFFFF  " + hourOfDay + ":" + minute + "am")
                        } else {
                            Log.e("TAG", "openTimePicker:::: hourOfDay<12  ELSEEEE " + "12:" + minute + "am")
                        }
                    }



                    mHour = hourOfDay
                    mMinute = minute
                    mTime = if (minute < 10) {
                        "$hourOfDay:0$minute"
                    } else {
                        "$hourOfDay:$minute"
                    }
                    Log.e("TAG", "openTimePicker:mTimemTime:::::  $mTime")

                    if (isEdit) {
                        val mcalender2 = Calendar.getInstance()

                        mcalender2[Calendar.MONTH] = --mMonth
                        mcalender2[Calendar.YEAR] = mYear
                        if (mHour <= mcalendarCurrent!!.get(Calendar.HOUR_OF_DAY) && mMinute < mcalendarCurrent!!.get(Calendar.MINUTE)) {
                            mDay += 1
                        }
                        mcalender2[Calendar.DAY_OF_MONTH] = mDay
                        mcalender2[Calendar.HOUR_OF_DAY] = hourOfDay
                        mcalender2[Calendar.MINUTE] = minute
                        mcalender2[Calendar.SECOND] = 0

//                    val c = rb!!.updateReminderTime(reminderId, "$hourOfDay:$minute")
                        val c = rb!!.updateReminderTime(reminderId, mTime!!)
                        init()

                        mAlarmReceiver!!.setAlarm(context, mcalender2, reminderId.toInt())

                        /*val currentTimeMillis = mCalendar!!.timeInMillis
                        val editTimeMillis = mcalender2.timeInMillis
                        if (currentTimeMillis < editTimeMillis){

                            Log.e("TAG", "openTimePicker:Val C:::::IFFFFF    ${mCalendar!!.timeInMillis}   ${mcalender2.timeInMillis}" )
                        }else{

                            Log.e("TAG", "openTimePicker:Val C:::::ELSEEE   ${mCalendar!!.timeInMillis}   ${mcalender2.timeInMillis}" )
                        }*/
                    } else {
                        Log.e("TAG", "openTimePicker:Anoptehr Wasaa  ")
                        openDayPickerDialog(context, false, arrOfDays)
                    }

                }, hour, minute, 0, true
        )
        mTimePicker.show()
    }

    override fun editDays(Id: String, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>) {
        reminderId = Id
        openDayPickerDialog(context, true, arrOfDaysArgs)
        repeatDialog = null
    }

    override fun editTime(Id: String, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>, hour: Int, minute: Int) {
        reminderId = Id
        initReminder()
        openTimePicker(true, hour, minute)
    }

    private fun openDayPickerDialog(context: Context, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>) {
//        if (repeatDialog == null) {
        Log.e("TAG", "openDayPickerDialog:String Array :::::  $isEdit ${arrOfDaysArgs.size}")
        var stringArrayList = ArrayList<String>()
        stringArrayList.clear()
        var arrOfDays = arrOfDaysArgs
        stringArrayList = arrOfDays

        try {

            val margin = resources.getDimension(R.dimen.space_20dp).toInt()

            val llRepeat = layoutInflater.inflate(R.layout.dl_alarm_repeat_type, null) as LinearLayout


            val chkMon = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkMon)
            val chkTue = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkTue)
            val chkWed = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkWed)
            val chkThu = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkThu)
            val chkFri = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkFri)
            val chkSat = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkSat)
            val chkSun = llRepeat.findViewById<AppCompatCheckBox>(R.id.chkSun)

            if (isEdit) {
                chkMon.isChecked = arrOfDaysArgs.contains("1")

                chkTue.isChecked = arrOfDaysArgs.contains("2")

                chkWed.isChecked = arrOfDaysArgs.contains("3")

                chkThu.isChecked = arrOfDaysArgs.contains("4")

                chkFri.isChecked = arrOfDaysArgs.contains("5")

                chkSat.isChecked = arrOfDaysArgs.contains("6")

                chkSun.isChecked = arrOfDaysArgs.contains("7")
            }
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Select Days")
            builder.setCancelable(true)
            val llMain = LinearLayout(context)
            val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(margin, 0, margin, 0)



            if (isEdit) {
                chkMon.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("1")
                    } else {
                        stringArrayList.remove("1")
                    }
                }

                chkTue.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("2")
                    } else {
                        stringArrayList.remove("2")
                    }
                }

                chkWed.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("3")
                    } else {
                        stringArrayList.remove("3")
                    }
                }

                chkThu.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("4")
                    } else {
                        stringArrayList.remove("4")
                    }
                }

                chkFri.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("5")
                    } else {
                        stringArrayList.remove("5")
                    }
                }

                chkSat.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("6")
                    } else {
                        stringArrayList.remove("6")
                    }
                }

                chkSun.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        stringArrayList.add("7")
                    } else {
                        stringArrayList.remove("7")
                    }
                }
            }

            if (stringArrayList.size == 7) {

            }

            llRepeat.layoutParams = layoutParams
            llMain.addView(llRepeat)
            builder.setView(llMain)
            builder.setPositiveButton("Done") { dialog, _ ->
                try {

                    if (stringArrayList.size == 0 || stringArrayList.size == 7) {
                        arrOfDays = stringArrayList
                    } else {
                        arrOfDays = stringArrayList
                    }

                    if (isEdit) {

                        val mCount = rb!!.updateReminderDays(reminderId, getDayInString(arrOfDays))
                        init()
                        Log.e("TAG", "openDayPickerDialog::::Edit Days ::::  $mCount")
                    } else {

                        /*val reminderClass = ReminderClass()
                        reminderClass.Time = "$hourOfDay:$minute"
                        reminderClass.Days = getDayInString(arrOfDays)
                        reminderClass.IsActive = "true"

                        val mCount = dbHelper.addReminder(reminderClass)

                        startAlarm(mCount)*/

                        stringArrayList.clear()

                        if (chkMon.isChecked) {
                            stringArrayList.add("1")
                        } else {
                            stringArrayList.remove("1")
                        }

                        if (chkTue.isChecked) {
                            stringArrayList.add("2")
                        } else {
                            stringArrayList.remove("2")
                        }

                        if (chkWed.isChecked) {
                            stringArrayList.add("3")
                        } else {
                            stringArrayList.remove("3")
                        }

                        if (chkThu.isChecked) {
                            stringArrayList.add("4")
                        } else {
                            stringArrayList.remove("4")
                        }

                        if (chkFri.isChecked) {
                            stringArrayList.add("5")
                        } else {
                            stringArrayList.remove("5")
                        }

                        if (chkSat.isChecked) {
                            stringArrayList.add("6")
                        } else {
                            stringArrayList.remove("6")
                        }

                        if (chkSun.isChecked) {
                            stringArrayList.add("7")
                        } else {
                            stringArrayList.remove("7")
                        }

                        if (stringArrayList.size == 0) {
                            Toast.makeText(context, "Please Select at least one day", Toast.LENGTH_LONG).show()
                        } else {
                            val currentTimeMillis = mcalendarCurrent!!.timeInMillis
                            val reminderTimeMillis = mCalendar!!.timeInMillis


                            val ID = rb!!.addReminder(
                                    Reminder(
                                            mTitle,
                                            mDate,
                                            mTime,
                                            mRepeat,
                                            mRepeatNo,
                                            mRepeatType,
                                            mActive,
                                            getDayInString(stringArrayList)
                                    )
                            )

                            mCalendar!![Calendar.MONTH] = --mMonth
                            mCalendar!![Calendar.YEAR] = mYear

                            if (mHour <= mcalendarCurrent!!.get(Calendar.HOUR_OF_DAY) && mMinute < mcalendarCurrent!!.get(Calendar.MINUTE)) {
                                mDay += 1

                                Log.e(
                                        "TAG",
                                        "openDayPickerDialog::::M calender:::IFFFFF  " + mHour + "  " + mcalendarCurrent!!.get(Calendar.HOUR_OF_DAY)
                                                + "  " + mMinute + "  " + mcalendarCurrent!!.get(Calendar.MINUTE) + "  " + mDay
                                )

                            }
                            mCalendar!![Calendar.DAY_OF_MONTH] = mDay


                            mCalendar!![Calendar.HOUR_OF_DAY] = mHour
                            mCalendar!![Calendar.MINUTE] = mMinute
                            mCalendar!![Calendar.SECOND] = 0




                            AlarmReceiver().setAlarm(applicationContext, mCalendar!!, ID)

                            init()

                            dialog.dismiss()
                        }
                        Log.e("TAG", "openDayPickerDialog::::Add Record:::  $isEdit")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            repeatDialog = builder.create()
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        }

        try {
            if (!repeatDialog!!.isShowing) {
                repeatDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getDayInString(arrOfDays: ArrayList<String>): String {

        stringBuilderDay = StringBuilder()

        try {
            for (i in 0 until arrOfDays.size) {
                if (stringBuilderDay.isEmpty()) {
                    stringBuilderDay.append(arrOfDays[i])
                } else {
                    stringBuilderDay.append(',')
                    stringBuilderDay.append(arrOfDays[i])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return stringBuilderDay.toString()
    }
}
