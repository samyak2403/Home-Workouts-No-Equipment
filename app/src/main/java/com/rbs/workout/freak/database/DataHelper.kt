package com.rbs.workout.freak.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.rbs.workout.freak.history.HistoryDetailsClass
import com.rbs.workout.freak.history.HistoryWeekDataClass
import com.rbs.workout.freak.pojo.PWorkOutDetails
import com.rbs.workout.freak.pojo.pWeekDayData
import com.rbs.workout.freak.pojo.pWeeklyDayData
import com.rbs.workout.freak.reminder.ReminderClass
import com.rbs.workout.freak.utils.CommonUtility
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DataHelper(val mContext: Context) {

    val DBName = "HomeWorkout.db"

    val workout_id = "Workout_id"
    val title = "Title"
    val videoLink = "videoLink"
    val descriptions = "Description"
    val time = "Time"
    val time_type = "time_type"
    val image = "Image"
    val tbl_youtube_link = "tbl_youtube_link"
    val youtube_link = "youtube_link"

    /* Todo History Table */
    val tbl_history = "tbl_history"
    val Id = "Id"
    val LevelName = "LevelName"
    val PlanName = "PlanName"
    val DateTime = "DateTime"
    val CompletionTime = "CompletionTime"
    val BurnKcal = "BurnKcal"
    val TotalWorkout = "TotalWorkout"
    val Kg = "Kg"
    val Feet = "Feet"
    val Inch = "Inch"
    val FeelRate = "FeelRate"

    /* Todo Weight Table */
    private val WeightTable = "tbl_weight"

    private val WeightKG = "WeightKG"
    private val WeightLB = "WeightLB"
    private val CurrentDate = "CurrentDate"
    private val Date = "Date"

    /* Todo Reminder Table */
    private val ReminderTable = "tbl_reminder"

    private val Time = "Time"
    private val Days = "Days"
    private val IsActive = "IsActive"

    // Todo Full body Table
    private val tbl_full_body_workouts_list = "tbl_full_body_workouts_list"

    private val Workout_id = "Workout_id"
    private val Title = "Title"
    private val Description = "Description"
    private val Time_beginner = "Time_beginner"
    private val Time_intermidiate = "Time_intermidiate"
    private val Time_advanced = "Time_advanced"
    private val Day_name = "Day_name"
    private val Week_name = "Week_name"
    private val Level = "Level"
    private val Is_completed = "Is_completed"
    private val Image = "Image"

    fun getReadWriteDB(): SQLiteDatabase {
        val dbFile = mContext.getDatabasePath(DBName)
        if (!dbFile.exists()) {
            try {
                val checkDB = mContext.openOrCreateDatabase(DBName, Context.MODE_PRIVATE, null)
                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: Exception) {
                throw RuntimeException("Error creating source database", e)
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Throws(IOException::class)
    private fun copyDatabase(dbFile: File) {
        val `is` = mContext.assets.open(DBName)
        val os = FileOutputStream(dbFile)

        val buffer = ByteArray(1024)
        while (`is`.read(buffer) > 0) {
            os.write(buffer)
        }
        os.flush()
        os.close()
        `is`.close()
    }

    fun getWorkOutDetails(workoutTable: String): ArrayList<PWorkOutDetails> {

        val workdetails: ArrayList<PWorkOutDetails> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From " + workoutTable
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val aClass = PWorkOutDetails()
                    aClass.workout_id = cursor.getInt(cursor.getColumnIndexOrThrow(workout_id))
                    aClass.title = cursor.getString(cursor.getColumnIndexOrThrow(title))
                    aClass.videoLink = cursor.getString(cursor.getColumnIndexOrThrow(videoLink))
                    aClass.descriptions = cursor.getString(cursor.getColumnIndexOrThrow(descriptions))
                    aClass.time = cursor.getString(cursor.getColumnIndexOrThrow(time))
                    aClass.time_type = cursor.getString(cursor.getColumnIndexOrThrow(time_type))
                    aClass.image = cursor.getString(cursor.getColumnIndexOrThrow(image))
                    workdetails.add(aClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && !db.isOpen()) {
                db.close()
            }
        }
        return workdetails
    }

    fun getVideoLink(strWorkoutTitle: String): String {
        var strVideoLink = ""
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From " + tbl_youtube_link + " WHERE " + title + "='" + strWorkoutTitle + "'"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    strVideoLink = cursor.getString(cursor.getColumnIndexOrThrow(youtube_link))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && !db.isOpen()) {
                db.close()
            }
        }
        return strVideoLink
    }

    /* Todo History Methods  */
    fun addHistory(strLevelName: String,
                   strPlanName: String,
                   strDateTime: String,
                   strCompletionTime: String,
                   strBurnKcal: String,
                   strTotalWorkout: String,
                   strKg: String,
                   strFeet: String,
                   strInch: String,
                   strFeelRate: String,
                   strDayName: String,
                   strWeekName: String
    ): Int {

        var db: SQLiteDatabase? = null
        var count = 0

        val cv = ContentValues()
        cv.put(LevelName, strLevelName)
        cv.put(PlanName, strPlanName)
        cv.put(DateTime, strDateTime)
        cv.put(CompletionTime, strCompletionTime)
        cv.put(BurnKcal, strBurnKcal)
        cv.put(TotalWorkout, strTotalWorkout)
        cv.put(Kg, strKg)
        cv.put(Feet, strFeet)
        cv.put(Inch, strInch)
        cv.put(FeelRate, strFeelRate)
        cv.put(Day_name, strDayName)
        cv.put(Week_name, strWeekName)

        try {
            db = getReadWriteDB()
            count = db.insert(tbl_history, null, cv).toInt()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
            }
        }

        return count
    }

    // Todo Check User history available or not
    fun isHistoryAvailable(strDate: String): Boolean {
        var dtIsAvailable = false
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()

//            val query = "Select * from $CompleteWorkoutTable"
            val query = "Select * From $tbl_history " +
                    "Where " +
                    "DateTime(strftime('%Y-%m-%d', DateTime($DateTime)))" +
                    "= " +
                    "DateTime(strftime('%Y-%m-%d', DateTime('$strDate')));"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                dtIsAvailable = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return dtIsAvailable
    }

    fun getHistoryTotalWorkout(): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val totWorkout = "totCompletionTime"
        var totWorkoutSum = 0

        try {
            db = getReadWriteDB()
            val query = "SELECT SUM(CAST($TotalWorkout as INTEGER)) as $totWorkout FROM $tbl_history"

            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                totWorkoutSum = cursor.getInt(cursor.getColumnIndex(totWorkout))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return totWorkoutSum
    }

    fun getHistorytotalKcal(): Float {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var totKcalSum = 0f

        try {
            db = getReadWriteDB()
            val query = "SELECT SUM(CAST($BurnKcal as Float)) as $BurnKcal FROM $tbl_history"

            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                totKcalSum = cursor.getFloat(cursor.getColumnIndex(BurnKcal))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return totKcalSum
    }

    fun getHistorytotalMinutes(): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var totMinutesSum = 0

        try {
            db = getReadWriteDB()
            val query = "SELECT SUM(CAST($CompletionTime as INTEGER)) as $CompletionTime FROM $tbl_history"

            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                totMinutesSum = cursor.getInt(cursor.getColumnIndex(CompletionTime))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return totMinutesSum
    }

    fun getCompleteExerciseDate(): ArrayList<String> {

        val arrDt = ArrayList<String>()
        val arrDtTemp = ArrayList<String>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()

            val query = "Select * from $tbl_history"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (!arrDtTemp.contains
                            (CommonUtility.convertFullDateToDate(cursor.getString(cursor.getColumnIndexOrThrow(DateTime))))) {
                        arrDtTemp.add(CommonUtility.convertFullDateToDate(cursor.getString(cursor.getColumnIndexOrThrow(DateTime))))
                        arrDt.add(cursor.getString(cursor.getColumnIndexOrThrow(DateTime)))
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrDt
    }

    fun getUserWeightData(): ArrayList<HashMap<String, String>> {

        //val arrKg = ArrayList<String>()
        val arrDateChange = ArrayList<HashMap<String, String>>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()

            val query = "Select * from $WeightTable where $WeightKG != '0' group by $Date order by $Date"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
//                    if (!arrKg.contains(cursor.getString(cursor.getColumnIndexOrThrow(WeightKG)))) {
                    //arrKg.add(cursor.getString(cursor.getColumnIndexOrThrow(WeightKG)))
                    val hashMap = HashMap<String, String>()
                    hashMap.put("KG", cursor.getString(cursor.getColumnIndexOrThrow(WeightKG)))
                    hashMap.put(
                            "DT",
                            cursor.getString(cursor.getColumnIndexOrThrow(Date))
                    )
                    arrDateChange.add(hashMap)
//                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrDateChange
    }

    fun restartProgress(): Int {

        val mCount: Int = 1
        val db = getReadWriteDB()
        val contentValues = ContentValues()
        contentValues.put(Is_completed, "0")

        // Todo clear  full body rbs progress
        db.update(ConstantString.tbl_full_body_workouts_list, contentValues, null, null)

        // Todo clear  lower body rbs progress
        db.update(ConstantString.tbl_lower_body_list, contentValues, null, null)

        db.delete(tbl_history, null, null)
        db.delete(WeightTable, null, null)

        return mCount
    }

    /* Todo Check weight record is exist or not */
    fun weightExistOrNot(strDate: String): Boolean {

        var boolResult = false
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()

            val query = "Select * From $WeightTable Where $Date = '$strDate'"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                boolResult = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }

            if (db != null && db.isOpen) {
                db.close()
            }
        }

        return boolResult
    }

    /* Todo add user Weight query */
    fun addUserWeight(strWeightKG: String, strDate: String, strweightLB: String): Int {
        var count = 0
        var db: SQLiteDatabase? = null

        val row = ContentValues()
//        row.put(Id,"")
        row.put(WeightKG, strWeightKG)
        row.put(Date, strDate)
        row.put(WeightLB, strweightLB)
        row.put(CurrentDate, CommonUtility.getCurrentTimeStamp())

        try {
            db = getReadWriteDB()
            count = db.insert(WeightTable, null, row).toInt()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && db.isOpen) {
                db.close()
            }
        }

        return count
    }

    /* Todo Update weight if weight exist */
    fun updateWeight(strDate: String, strWeightKG: String, strWeightLB: String): Boolean {
        var count = 0
        var db: SQLiteDatabase? = null

        val cv = ContentValues()
        cv.put(WeightKG, strWeightKG)
        cv.put(WeightLB, strWeightLB)

        try {
            db = getReadWriteDB()

            count = db.update(WeightTable, cv, "$Date = ?", arrayOf(strDate))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return count > 0
    }

    // Todo get Max Weight
    fun getMaxWeight(): String {

        var strMaxWeight = "0"
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val maxkg = "maxkg"
        try {
            db = getReadWriteDB()

            val query = "SELECT MAX(CAST($WeightKG as INTEGER)) as $maxkg from $WeightTable"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    strMaxWeight = cursor.getString(cursor.getColumnIndex(maxkg))

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return strMaxWeight
    }

    // Todo get Min Weight
    fun getMinWeight(): String {

        var strMinWeight = "0"
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val minkg = "minkg"
        try {
            db = getReadWriteDB()

            val query = "SELECT MIN(CAST($WeightKG as INTEGER)) as $minkg from $WeightTable"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    strMinWeight = cursor.getString(cursor.getColumnIndex(minkg))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return strMinWeight
    }

    // Todo Get Weekly data
    fun getWeekDayOfHistory(): ArrayList<HistoryWeekDataClass> {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val weekStart = "WeekStart"
        val WeekEnd = "WeekEnd"
        val WeekNumber = "WeekNumber"
        val arrHistoryData = ArrayList<HistoryWeekDataClass>()

        try {
            db = getReadWriteDB()

            val query = "select strftime('%W', $DateTime) $WeekNumber," +
                    "    max(date($DateTime, 'weekday 0' ,'-6 day')) $weekStart," +
                    "    max(date($DateTime, 'weekday 0', '-0 day')) $WeekEnd " +
                    "from $tbl_history " +
                    "group by $WeekNumber"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    val historyWeekDataClass = HistoryWeekDataClass()

                    historyWeekDataClass.weekNumber = cursor.getString(cursor.getColumnIndexOrThrow(WeekNumber))
                    historyWeekDataClass.weekStart = cursor.getString(cursor.getColumnIndexOrThrow(weekStart))
                    historyWeekDataClass.weekEnd = cursor.getString(cursor.getColumnIndexOrThrow(WeekEnd))
                    historyWeekDataClass.totKcal = getTotBurnWeekKcal(historyWeekDataClass.weekStart, historyWeekDataClass.weekEnd)
                    historyWeekDataClass.totTime = getTotWeekWorkoutTime(historyWeekDataClass.weekStart, historyWeekDataClass.weekEnd)

                    historyWeekDataClass.arrhistoryDetail = getWeekHistoryData(historyWeekDataClass.weekStart, historyWeekDataClass.weekEnd)

                    historyWeekDataClass.totWorkout = historyWeekDataClass.arrhistoryDetail.size
                    arrHistoryData.add(historyWeekDataClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrHistoryData
    }

    fun getTotBurnWeekKcal(strWeekStart: String, strWeekEnd: String): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val burnKcal = "BurnKcal"
        var totKcal = 0
        try {
            db = getReadWriteDB()

            val query = "SELECT sum($BurnKcal) as $burnKcal from $tbl_history WHERE date('$strWeekStart') <= date(datetime) AND date('$strWeekEnd') >= date(datetime)"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst()
                totKcal = cursor.getInt(cursor.getColumnIndexOrThrow(burnKcal))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return totKcal
    }

    fun getTotWeekWorkoutTime(strWeekStart: String, strWeekEnd: String): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var totCompletionTime = 0
        try {
            db = getReadWriteDB()

            val query = "SELECT sum($CompletionTime) as $CompletionTime from $tbl_history WHERE date('$strWeekStart') <= date(datetime) AND date('$strWeekEnd') >= date(datetime)"
            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst()
                totCompletionTime = cursor.getInt(cursor.getColumnIndexOrThrow(CompletionTime))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return totCompletionTime
    }

    fun getWeekHistoryData(strWeekStart: String, strWeekEnd: String): ArrayList<HistoryDetailsClass> {

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        val arrHistoryWeekDetails = ArrayList<HistoryDetailsClass>()
        try {
            db = getReadWriteDB()
//            val query = "SELECT * FROM tbl_history WHERE date('$strWeekStart') <= date(datetime) AND date('$strWeekEnd') >= date(datetime)"
            val query = "SELECT * FROM tbl_history WHERE date('$strWeekStart') <= date(datetime) AND date('$strWeekEnd') >= date(datetime) Order by $Id Desc "

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val historyDetailsClass = HistoryDetailsClass()
                    historyDetailsClass.LevelName = cursor.getString(cursor.getColumnIndexOrThrow(LevelName))
                    historyDetailsClass.PlanName = cursor.getString(cursor.getColumnIndexOrThrow(PlanName))
                    historyDetailsClass.DateTime = cursor.getString(cursor.getColumnIndexOrThrow(DateTime))
                    historyDetailsClass.CompletionTime = cursor.getString(cursor.getColumnIndexOrThrow(CompletionTime))
                    historyDetailsClass.BurnKcal = cursor.getString(cursor.getColumnIndexOrThrow(BurnKcal))
                    historyDetailsClass.TotalWorkout = cursor.getString(cursor.getColumnIndexOrThrow(TotalWorkout))
                    historyDetailsClass.Kg = cursor.getString(cursor.getColumnIndexOrThrow(Kg))
                    historyDetailsClass.Feet = cursor.getString(cursor.getColumnIndexOrThrow(Feet))
                    historyDetailsClass.Inch = cursor.getString(cursor.getColumnIndexOrThrow(Inch))
                    historyDetailsClass.FeelRate = cursor.getString(cursor.getColumnIndexOrThrow(FeelRate))
                    historyDetailsClass.DayName = cursor.getString(cursor.getColumnIndexOrThrow(Day_name))
                    historyDetailsClass.WeekName = cursor.getString(cursor.getColumnIndexOrThrow(Week_name))

                    arrHistoryWeekDetails.add(historyDetailsClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrHistoryWeekDetails
    }

    // Todo Reminder
    fun addReminder(reminderClass: ReminderClass): Int {
        val mCount: Int
        val db = getReadWriteDB()
        val contentValues = ContentValues()
        contentValues.put(Time, reminderClass.Time)
        contentValues.put(Days, reminderClass.Days)
        contentValues.put(IsActive, reminderClass.IsActive)

        mCount = db.insert(ReminderTable, null, contentValues).toInt()

        return mCount
    }

    fun getReminderById(mid: String): ReminderClass {
        val reminderClass = ReminderClass()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From $ReminderTable where $Id=$mid"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    reminderClass.Id = cursor.getString(cursor.getColumnIndexOrThrow(Id))
                    reminderClass.Time = cursor.getString(cursor.getColumnIndexOrThrow(Time))
                    reminderClass.Days = cursor.getString(cursor.getColumnIndexOrThrow(Days))
                    reminderClass.IsActive = cursor.getString(cursor.getColumnIndexOrThrow(IsActive))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return reminderClass
    }

    fun getRemindersList(): ArrayList<ReminderClass> {
        val arrReminder = ArrayList<ReminderClass>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From $ReminderTable order by Id DESC"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val reminderClass = ReminderClass()
                    reminderClass.Id = cursor.getString(cursor.getColumnIndexOrThrow(Id))
                    reminderClass.Time = cursor.getString(cursor.getColumnIndexOrThrow(Time))
                    reminderClass.Days = cursor.getString(cursor.getColumnIndexOrThrow(Days))
                    reminderClass.IsActive = cursor.getString(cursor.getColumnIndexOrThrow(IsActive))
                    arrReminder.add(reminderClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrReminder
    }

    fun deleteReminder(id: String): Boolean {
        var isSuccess = false
        var db: SQLiteDatabase? = null

        try {
            db = getReadWriteDB()
            val mCount = db.delete(ReminderTable, "$Id=?", arrayOf(id))
            if (mCount > 0) {
                isSuccess = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return isSuccess
    }

    fun updateReminder(strReminderId: String, strIsActive: String): Int {
        val mCount: Int
        val db = getReadWriteDB()
        val contentValues = ContentValues()
        contentValues.put(IsActive, strIsActive)

        mCount = db.update(ReminderTable, contentValues, "$Id = $strReminderId", null)

        return mCount
    }

    fun updateReminderDays(strReminderId: String, strDays: String): Int {
        val mCount: Int
        val db = getReadWriteDB()
        val contentValues = ContentValues()
        contentValues.put(Days, strDays)

        mCount = db.update(ReminderTable, contentValues, "$Id = $strReminderId", null)

        return mCount
    }

    fun updateReminderTimes(strReminderId: String, strTime: String): Int {
        val mCount: Int
        val db = getReadWriteDB()
        val contentValues = ContentValues()
        contentValues.put(Time, strTime)

        mCount = db.update(ReminderTable, contentValues, "$Id = $strReminderId", null)

        return mCount
    }

    // Todo Get Full body and Lower body rbs Data
    fun getWorkoutWeeklyData(strCategoryName: String): ArrayList<pWeeklyDayData> {

        val arrPWeeklyDayData = ArrayList<pWeeklyDayData>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            var query = ""
            if (strCategoryName == ConstantString.Full_Body) {
//                query = "SELECT $Workout_id, group_concat(DISTINCT(CAST($Day_name as INTEGER))) as $Day_name, $Week_name, $Is_completed from ${ConstantString.tbl_full_body_workouts_list} GROUP BY CAST($Week_name as INTEGER)"
                query = "SELECT  max($Workout_id) as Workout_id, $Workout_id, group_concat(DISTINCT(CAST($Day_name as INTEGER))) as $Day_name, $Week_name, $Is_completed from ${ConstantString.tbl_full_body_workouts_list} GROUP BY CAST($Week_name as INTEGER)"
            } else if (strCategoryName == ConstantString.Lower_Body) {
//                query = "SELECT $Workout_id, group_concat(DISTINCT(CAST($Day_name as INTEGER))) as $Day_name, $Week_name, $Is_completed from ${ConstantString.tbl_lower_body_list} GROUP BY CAST($Week_name as INTEGER)"
                query=  "SELECT  max($Workout_id) as Workout_id, $Workout_id, group_concat(DISTINCT(CAST($Day_name as INTEGER))) as $Day_name, $Week_name, $Is_completed from ${ConstantString.tbl_lower_body_list} GROUP BY CAST($Week_name as INTEGER)"
            }

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val aClass = pWeeklyDayData()
                    aClass.Workout_id = cursor.getString(cursor.getColumnIndex(Workout_id))
                    aClass.Day_name = cursor.getString(cursor.getColumnIndex(Day_name))
                    aClass.Week_name = cursor.getString(cursor.getColumnIndex(Week_name))
                    aClass.Is_completed = cursor.getString(cursor.getColumnIndex(Is_completed))
                    aClass.categoryName = strCategoryName

                    aClass.arrWeekDayData = getWeekDaysData(aClass.Week_name, strCategoryName)

                    val aClass1 = pWeekDayData()
                    aClass1.Day_name = "Cup"

                    if (aClass.Is_completed == "1") {
                        aClass1.Is_completed = "1"
                    } else {
                        aClass1.Is_completed = "0"
                    }

                    aClass.arrWeekDayData.add(aClass1)
                    arrPWeeklyDayData.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }
        return arrPWeeklyDayData
    }

    fun getWeekDaysData(strWeekName: String, strCategoryName: String): ArrayList<pWeekDayData> {

        val arrWeekDayData = ArrayList<pWeekDayData>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {

            db = getReadWriteDB()
            var query = ""
            if (strCategoryName == ConstantString.Full_Body) {
                query = "select $Day_name,$Is_completed FROM ${ConstantString.tbl_full_body_workouts_list} " +
                        "WHERE $Day_name IN ('01','02','03','04','05','06','07') " +
                        "AND $Week_name = '$strWeekName' GROUP by $Day_name"
            } else if (strCategoryName == ConstantString.Lower_Body) {
                query = "select $Day_name,$Is_completed FROM ${ConstantString.tbl_lower_body_list} " +
                        "WHERE $Day_name IN ('01','02','03','04','05','06','07') " +
                        "AND $Week_name = '$strWeekName' GROUP by $Day_name"
            }

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val aClass = pWeekDayData()
                    aClass.Day_name = cursor.getString(cursor.getColumnIndex(Day_name))
                    aClass.Is_completed = cursor.getString(cursor.getColumnIndex(Is_completed))
                    arrWeekDayData.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrWeekDayData
    }

    fun getWeekDayExerciseData(strDayName: String, strWeekName: String, strTableName: String): ArrayList<PWorkOutDetails> {

        val arrDayWorkoutList = ArrayList<PWorkOutDetails>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = getReadWriteDB()

//            val query = "SELECT * from $tbl_full_body_workouts_list WHERE $Day_name = '$strDayName' AND $Week_name = '$strWeekName'"
            val query = "SELECT * from $strTableName WHERE $Day_name = '$strDayName' AND $Week_name = '$strWeekName'"

            cursor = db.rawQuery(query, null)

            val time: String
            if (strTableName == ConstantString.tbl_full_body_workouts_list) {
                time = LocalDB.getFullBodyLevel(mContext)!!
            } else {
                time = ConstantString.DEF_FULL_BODY_BEGINNER
            }

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getString(cursor.getColumnIndexOrThrow(time)) != "-") {
                        val aClass = PWorkOutDetails()
                        aClass.workout_id = cursor.getInt(cursor.getColumnIndexOrThrow(workout_id))
                        aClass.title = cursor.getString(cursor.getColumnIndexOrThrow(title))
                        aClass.videoLink = cursor.getString(cursor.getColumnIndexOrThrow(videoLink))
                        aClass.descriptions = cursor.getString(cursor.getColumnIndexOrThrow(descriptions))
                        aClass.time = cursor.getString(cursor.getColumnIndexOrThrow(time))
                        aClass.time_type = cursor.getString(cursor.getColumnIndexOrThrow(time_type))
                        aClass.image = cursor.getString(cursor.getColumnIndexOrThrow(image))

                        arrDayWorkoutList.add(aClass)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrDayWorkoutList
    }

    fun getWorkoutWeekCompltedDayCount(strCategoryName: String): Int {

        var completedDay = 0
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            var query = ""
            if (strCategoryName == ConstantString.Full_Body) {
                query = "SELECT $Workout_id, group_concat(DISTINCT(CAST($Day_name as INTEGER))) as $Day_name, $Week_name, $Is_completed from ${ConstantString.tbl_full_body_workouts_list} GROUP BY CAST($Week_name as INTEGER)"
            } else if (strCategoryName == ConstantString.Lower_Body) {
                query = "SELECT $Workout_id, group_concat(DISTINCT(CAST($Day_name as INTEGER))) as $Day_name, $Week_name, $Is_completed from ${ConstantString.tbl_lower_body_list}  GROUP BY CAST($Week_name as INTEGER)"
            }

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {

                    completedDay += getWeekCompletedDays(cursor.getString(cursor.getColumnIndex(Week_name)), strCategoryName).size

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return completedDay
    }

    fun getWeekCompletedDays(strWeekName: String, strCategoryName: String): ArrayList<pWeekDayData> {

        val arrWeekDayData = ArrayList<pWeekDayData>()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {

            db = getReadWriteDB()
            var query = ""
            if (strCategoryName == ConstantString.Full_Body) {
                query = "select $Day_name,$Is_completed FROM ${ConstantString.tbl_full_body_workouts_list} " +
                        "WHERE $Day_name IN ('01','02','03','04','05','06','07') " +
                        "AND $Week_name = '$strWeekName' AND $Is_completed = '1' GROUP by $Day_name"
            } else if (strCategoryName == ConstantString.Lower_Body) {
                query = "select $Day_name,$Is_completed FROM ${ConstantString.tbl_lower_body_list} " +
                        "WHERE $Day_name IN ('01','02','03','04','05','06','07') " +
                        "AND $Week_name = '$strWeekName' AND $Is_completed = '1' GROUP by $Day_name"
            }

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val aClass = pWeekDayData()
                    aClass.Day_name = cursor.getString(cursor.getColumnIndex(Day_name))
                    aClass.Is_completed = cursor.getString(cursor.getColumnIndex(Is_completed))
                    arrWeekDayData.add(aClass)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            if (db != null && !db.isOpen) {
                db.close()
            }
        }

        return arrWeekDayData
    }

    fun updateFullWorkoutDay(strDayName: String, strWeekName: String, strTableName: String): Int {

        val mCount: Int
        val db = getReadWriteDB()
        val contentValues = ContentValues()
        contentValues.put(Is_completed, "1")

        mCount = db.update(strTableName, contentValues, "$Day_name = '$strDayName' AND $Week_name ='$strWeekName'", null)

        return mCount

//        val query = "UPDATE tbl_full_body_workouts_list SET Is_completed = '1' WHERE day_name = '01' AND Week_name = '02'"

    }

    // Todo Build Wider Excercise
    fun getBuildWiderWorkoutDetails(workoutTable: String, difficultyLvl: String): ArrayList<PWorkOutDetails> {

        val workdetails: ArrayList<PWorkOutDetails> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()

            val query = "Select * From $workoutTable Where $Level = '$difficultyLvl'"

            cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val aClass = PWorkOutDetails()
                    aClass.workout_id = cursor.getInt(cursor.getColumnIndexOrThrow(workout_id))
                    aClass.title = cursor.getString(cursor.getColumnIndexOrThrow(title))
                    aClass.videoLink = cursor.getString(cursor.getColumnIndexOrThrow(videoLink))
                    aClass.descriptions = cursor.getString(cursor.getColumnIndexOrThrow(descriptions))
                    aClass.time = cursor.getString(cursor.getColumnIndexOrThrow(time))
                    aClass.time_type = cursor.getString(cursor.getColumnIndexOrThrow(time_type))
                    aClass.image = cursor.getString(cursor.getColumnIndexOrThrow(image))
                    workdetails.add(aClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && !db.isOpen()) {
                db.close()
            }
        }

        return workdetails
    }

}
