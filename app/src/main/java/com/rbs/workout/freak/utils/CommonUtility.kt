package com.rbs.workout.freak.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rbs.workout.freak.R
import com.rbs.workout.freak.database.DataHelper
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt


object CommonUtility {

    fun getStringFormat(str: Double): String {
        return String.format("%.2f", str)
    }

    fun getString2DFormat(str: Int): String {
        return String.format("%02d", str)
    }

    fun getBmiCalculation(kg: Float, foot: Int, inch: Int): Double {
        val bmi = kg / getMeter(ftInToInch(foot, inch.toDouble())).pow(2.0)
        return bmi
    }

    fun calculationForBmiGraph(point: Float): Float {

        var pos = 0f
        if (point < 15) {
            return 0f
        } else if (point > 15 && point < 16) {
            pos = (0.25f * point) / 15.5f

        } else if (point > 16 && point < 18.5) {

            pos = (0.75f * point) / 15.5f
            pos += 0.5f

        } else if (point > 18.5 && point < 25) {

            pos = (1.0f * point) / 15.5f
            pos += 0.5f + 1.5f

        } else if (point > 25 && point < 30) {

            pos = (0.50f * point) / 15.5f
            pos += 0.5f + 1.5f + 2f

        } else if (point > 30 && point < 35) {

            pos = (0.50f * point) / 15.5f
            pos += 0.5f + 1.5f + 2f + 1f

        } else if (point > 35 && point < 40) {

            pos = (0.50f * point) / 15.5f
            pos += 0.5f + 1.5f + 2f + 1f + 1f

        } else if (point > 40) {
            return 6.90f
        }

        return pos
    }

    fun bmiWeightString(point: Float): String {

        if (point < 15) {
            return "Severely underweight"
        } else if (point > 15 && point < 16) {
            return "Very underweight"
        } else if (point > 16 && point < 18.5) {
            return "Underweight"
        } else if (point > 18.5 && point < 25) {
            return "Healthy Weight"
        } else if (point > 25 && point < 30) {
            return "Overweight"
        } else if (point > 30 && point < 35) {
            return "Moderately obese"
        } else if (point > 35 && point < 40) {
            return "Very obese"
        } else if (point > 40) {
            return "Severely obese"
        }

        return ""
    }

    fun bmiWeightTextColor(content: Context, point: Float): Int {

        if (point < 15) {
            return content.resources.getColor(R.color.colorBlack)
        } else if (point > 15 && point < 16) {
            return content.resources.getColor(R.color.colorFirst)
        } else if (point > 16 && point < 18.5) {
            return content.resources.getColor(R.color.colorSecond)
        } else if (point > 18.5 && point < 25) {
            return content.resources.getColor(R.color.colorThird)
        } else if (point > 25 && point < 30) {
            return content.resources.getColor(R.color.colorFour)
        } else if (point > 30 && point < 35) {
            return content.resources.getColor(R.color.colorFive)
        } else if (point > 35 && point < 40) {
            return content.resources.getColor(R.color.colorSix)
        } else if (point > 40) {
            return content.resources.getColor(R.color.colorBlack)
        }

        return content.resources.getColor(R.color.colorBlack)
    }

    fun getMeter(inch: Double): Double {
        return inch * 0.0254
    }

    fun ftInToInch(ft: Int, `in`: Double): Double {
        return (ft * 12).toDouble() + `in`
    }

    fun calcInchToFeet(inch: Double): Int {
        return (inch / 12.0).toInt()
    }

    fun calcInFromInch(inch: Double): Double {
        return BigDecimal(inch % 12.0).setScale(1, 6).toDouble()
    }

    fun unitFormat(i: Int): String {
        return if (i < 0 || i >= 10) {
            "" + i
        } else "0$i"
    }

    fun LbToKg(weightValue: Double): Double {
        return weightValue / 2.2046226218488
    }

    fun KgToLb(weightValue: Double): Double {
        return weightValue * 2.2046226218488
    }

    fun cmToInch(heightValue: Double): Double {
        Log.d("<><><>Cm to Inch", (heightValue / 2.54).toString())
        return heightValue / 2.54
    }

    fun inchToCm(heightValue: Double): Double {
        Log.d("<><><>inch to cm", (heightValue * 2.54).toString())
        return heightValue * 2.54
    }

    fun timeToSecond(strTime: String): Int {

        val min = strTime.substring(0, strTime.indexOf(":")).toInt() * 60
        val sec = strTime.substring(strTime.indexOf(":") + 1).toInt()

        return (min + sec)
    }

    fun secToTime(time: Int): String {
        return if (time <= 0) {
            "00:00"
        } else unitFormat(time / 60) + ":" + unitFormat(time % 60)
    }

    /* Todo Communication methods */
    fun shareStringLink(content: Context, strSubject: String, strText: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, strText)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, strSubject)
        shareIntent.type = "text/plain"
        content.startActivity(Intent.createChooser(shareIntent, content.resources.getString(R.string.app_name)))
    }

    fun shareAppLink(content: Context) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND

        val link = "https://play.google.com/store/apps/details?id=${content.packageName}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, content.resources.getString(R.string.app_name))
        shareIntent.type = "text/plain"
        content.startActivity(Intent.createChooser(shareIntent, content.resources.getString(R.string.app_name)))
    }

    fun contactUs(content: Context) {
        try {
            val sendIntentGmail = Intent(Intent.ACTION_SEND)
            sendIntentGmail.type = "plain/text"
            sendIntentGmail.setPackage("com.google.android.gm")
            sendIntentGmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("studiorbs758@gmail.com"))
            sendIntentGmail.putExtra(Intent.EXTRA_SUBJECT, content.resources.getString(R.string.app_name) + " - Android")
            content.startActivity(sendIntentGmail)
        }
        catch (e: ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(content,"Gmail app not found on this device", Toast.LENGTH_LONG).show()
        }
    }

    fun rateUs(context: Context) {
        val appPackageName = context.getPackageName()
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.rbs.workout.freak")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.rbs.workout.freak")
                    )
            )
        }
    }

    fun openUrl(content: Context, strUrl: String) {
        val appPackageName = content.getPackageName() // getPackageName() from Context or Activity object
        try {
            content.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(strUrl)))
        } catch (e: android.content.ActivityNotFoundException) {
            content.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(strUrl)))
        }
    }

    fun DownloadTTSEngine(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=text to speech&c=apps")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/search?q=text to speech&c=apps")
                    )
            )
        }
    }

    /* Todo Date Time Methods */
    fun convertDate(dateInMilliseconds: Long, dateFormat: String): String {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString()
    }

    fun convertStringToHoursMinute(strDate: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = originalFormat.parse(strDate)
        return targetFormat.format(date)
    }

    fun convertDateStrToInputFormat(strDate: String, strDateFormat: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat(strDateFormat, Locale.getDefault())
        val date = originalFormat.parse(strDate)
        return targetFormat.format(date)
    }

    fun convertDateToDateMonthName(strDate: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val date = originalFormat.parse(strDate)
        return targetFormat.format(date)
    }

    fun convertLongToDay(strDate: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("dd", Locale.getDefault())
        val date = originalFormat.parse(strDate)
        return targetFormat.format(date)
    }

    fun getCurrentTimeStamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()).toString()
    }

    fun getStringToMilli(strDt: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(strDt) as Date
        val mills = date.time
        return mills
    }

    fun getStringToDate(strDt: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(strDt) as Date
        return date
    }

    fun convertFullDateToDate(strDate: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = originalFormat.parse(strDate)
        return targetFormat.format(date)
    }

    fun getFullDateStringToMilliSecond(strDt: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = formatter.parse(strDt) as Date
        val mills = date.time
        return mills
    }

    fun getCurrentWeekByFirstDay(context: Context): ArrayList<String> {
        val currentWeekArrayList = ArrayList<String>()
//        currentWeekArrayList.add("Temp Data")
        try {
            val format = SimpleDateFormat(ConstantString.CapDateFormatDisplay, Locale.getDefault())
            val calendar = Calendar.getInstance()

            when (getFirstWeekDayNameByDayNo(LocalDB.getFirstDayOfWeek(context))) {
                "Sunday" -> {
                    calendar.firstDayOfWeek = Calendar.SUNDAY
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
                "Monday" -> {
                    calendar.firstDayOfWeek = Calendar.MONDAY
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }
                "Saturday" -> {
                    calendar.firstDayOfWeek = Calendar.SATURDAY
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                }
            }

            //calendar.firstDayOfWeek = Calendar.SUNDAY

//            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            for (i in 1..7) {
                try {
                    val data = format.format(calendar.time)
                    currentWeekArrayList.add(data)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentWeekArrayList
    }

    fun getCurrentWeek(): ArrayList<String> {
        val currentWeekArrayList = ArrayList<String>()
//        currentWeekArrayList.add("Temp Data")
        try {
            val format = SimpleDateFormat(ConstantString.CapDateFormatDisplay, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = Calendar.SUNDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            for (i in 1..7) {
                try {
                    val data = format.format(calendar.time)
                    currentWeekArrayList.add(data)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentWeekArrayList
    }

    fun getWeekStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY
//        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
////            calendar.add(Calendar.DATE, -1)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
//        }
        return calendar.time
    }

    fun getWeekEndDate(): Date {
        val calendar = Calendar.getInstance()
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, 1)
        }
        calendar.add(Calendar.DATE, -1)
        return calendar.time
    }

    fun getDayName(dayNo: Int): String {
        var dayNumber = ""
        when (dayNo) {
            0 -> dayNumber = "S"
            1 -> dayNumber = "M"
            2 -> dayNumber = "T"
            3 -> dayNumber = "W"
            4 -> dayNumber = "T"
            5 -> dayNumber = "F"
            6 -> dayNumber = "S"
        }
        return dayNumber
    }

    fun getFirstWeekDayNameByDayNo(dayNo: Int): String {
        var dayNumber = ""
        when (dayNo) {
            1 -> dayNumber = "Sunday"
            2 -> dayNumber = "Monday"
            3 -> dayNumber = "Saturday"
        }
        return dayNumber
    }

    fun getFirstWeekDayNoByDayName(dayName: String): Int {
        var dayNumber = 1
        when (dayName) {
            "Sunday" -> dayNumber = 1
            "Monday" -> dayNumber = 2
            "Saturday" -> dayNumber = 3
        }
        return dayNumber
    }

    // Todo Keep screen on
    fun keepScreenOn(context: Context) {
        if (LocalDB.getKeepScreen(context)) {
            val params: WindowManager.LayoutParams = WindowManager.LayoutParams()
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            params.screenBrightness = 1f
            (context as AppCompatActivity).window.attributes = params
        }
    }

    // Set Day Progress in text and progress bar
    fun setDayProgressData(content: Context, catName: String, txtDayLeft: TextView, txtDayPer: TextView, pbDay: ProgressBar) {
        val dbHelper = DataHelper(content)
        // Week day progress data
        val compDay = dbHelper.getWorkoutWeekCompltedDayCount(catName)

        val proPercentage = String.format("%.1f", (((compDay).toFloat()) * 100) / 28)
        txtDayLeft.text = (28 - compDay).toString().plus(" Days left")
        txtDayPer.text = proPercentage.toDouble().roundToInt().toString().plus("%")

        pbDay.progress = proPercentage.toFloat().toInt()
    }

}
