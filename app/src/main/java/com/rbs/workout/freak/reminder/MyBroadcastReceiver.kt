package com.rbs.workout.freak.reminder

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.rbs.workout.freak.R
import com.rbs.workout.freak.activity.HomeActivity
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.utils.ConstantString

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ISKM on 26-Dec-18.
 */
class MyBroadcastReceiver : BroadcastReceiver() {

    lateinit var dataBaseHelper: DataHelper
    lateinit var reminderClass: ReminderClass
    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("<><><><>", "Broadcast Called")
        dataBaseHelper = DataHelper(context)
        val id = intent.getStringExtra(ConstantString.extraReminderId)
        reminderClass = dataBaseHelper.getReminderById(id!!)
        if (reminderClass.IsActive == "true") {

            var arrOfDays = ArrayList<String>()
            if (reminderClass.Days.contains(",")) {
                arrOfDays = (reminderClass.Days.split(",")) as ArrayList<String>
            } else {
                arrOfDays.add(reminderClass.Days)
            }

//            arrOfDays.removeAt(arrOfDays.size - 1)
            for (i in 0 until arrOfDays.size) {
                arrOfDays[i] = arrOfDays[i].replace("'", "")
            }

            val dayNumber = getDayNumber(getCurrentDayName().toUpperCase())
            if (arrOfDays.contains(dayNumber)) {
                fireNotification(context, reminderClass)
            }
        }
    }

    private fun fireNotification(context: Context, reminderClass: ReminderClass) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = reminderClass.Id
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelName = context.resources.getString(R.string.app_name)
        val channelDescription = "Application_name Alert"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.description = channelDescription
            mChannel.enableVibration(true)
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_alert_sound)
            builder.color = context.resources.getColor(R.color.colorTheme)
        } else {
            builder.setSmallIcon(R.drawable.ic_alert_sound)
        }

        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Your body needs energy! You haven't exercised in ${getCurrentFullDayName()}!"))
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        builder.setContentTitle(context.resources.getString(R.string.app_name))
//        builder.setContentText("Your body needs energy! You haven't exercised in ${getCurrentDayName().toUpperCase()}!")
        builder.setAutoCancel(true)
        builder.setOngoing(false)

        val notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(context, 0,notificationIntent, 0)
        builder.setContentIntent(intent)

        notificationManager.notify(reminderClass.Id.toInt(), builder.build())

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(date: String): Date {
        val simpleDateFormat = SimpleDateFormat("dd MMM, yyyy")
        val resultDate = simpleDateFormat.parse(date);
        return resultDate
    }

    private fun isDateBetweenStartEndDate(max: Date, date: Date): Boolean {
        var isDateBetweenToDate = false;
        var currentDate = getCurrentDate()
        var maxDate = getEndDate(max)

        if (currentDate == maxDate) {
            isDateBetweenToDate = true
        } else if (date <= max) {
            isDateBetweenToDate = true
        }
        return isDateBetweenToDate

    }

    private fun getCurrentFullDayName(): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getCurrentDayName(): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getEndDate(date: Date): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun getDayNumber(dayName: String): String {
        var dayNumber = ""
        when (dayName) {
            "MON" -> dayNumber = "1"
            "TUE" -> dayNumber = "2"
            "WED" -> dayNumber = "3"
            "THU" -> dayNumber = "4"
            "FRI" -> dayNumber = "5"
            "SAT" -> dayNumber = "6"
            "SUN" -> dayNumber = "7"
        }
        return dayNumber
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = Color.RED
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawOval(rectF, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        bitmap.recycle()

        return output
    }

}
