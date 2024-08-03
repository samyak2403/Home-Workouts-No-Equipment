package com.rbs.workout.freak.utils

import android.content.Context
import android.content.SharedPreferences

object LocalDB {

    @Synchronized
    private fun getPrefrense(context: Context): SharedPreferences {
        val sharedPreferences: SharedPreferences
        synchronized(LocalDB::class.java) {
            sharedPreferences = context.getSharedPreferences("home_workout", 0)
        }
        return sharedPreferences
    }

    fun clearAllData(context: Context){
        context.getSharedPreferences("home_workout",0).edit().clear().apply()
        setIsFirstTime(context,false)


    }

    fun getWeightUnit(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_WEIGHT_UNIT, ConstantString.DEF_KG)
    }

    fun setWeightUnit(context: Context, value: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_WEIGHT_UNIT, value).apply()
    }

    fun getHeightUnit(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_HEIGHT_UNIT, ConstantString.DEF_IN)
    }

    fun setHeightUnit(context: Context, value: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_HEIGHT_UNIT, value).apply()
    }

    fun setLastInputWeight(context: Context, weight: Float) {
        getPrefrense(context).edit().putFloat(ConstantString.PREF_LAST_INPUT_WEIGHT, weight).apply()
    }

    fun getLastInputWeight(context: Context): Float {
        return getPrefrense(context).getFloat(ConstantString.PREF_LAST_INPUT_WEIGHT, 0f)
    }

    fun setLastInputFoot(context: Context, weight: Int) {
        getPrefrense(context).edit().putInt(ConstantString.PREF_LAST_INPUT_FOOT, weight).apply()
    }

    fun getLastInputFoot(context: Context): Int {
        return getPrefrense(context).getInt(ConstantString.PREF_LAST_INPUT_FOOT, 0)
    }

    fun setLastInputInch(context: Context, weight: Float) {
        getPrefrense(context).edit().putFloat(ConstantString.PREF_LAST_INPUT_INCH, weight).apply()
    }

    fun getLastInputInch(context: Context): Float {
        return getPrefrense(context).getFloat(ConstantString.PREF_LAST_INPUT_INCH, 0f)
    }

    // Todo Store days Id
    fun setLastWorkoutDayId(context: Context, strWorkoutID: String, DaysId: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_LAST_COMPLETE_DAY_ID + strWorkoutID, DaysId).apply()
    }

    fun getLastWorkoutDayId(context: Context, strWorkoutID: String): String? {
        return getPrefrense(context).getString(ConstantString.PREF_LAST_COMPLETE_DAY_ID + strWorkoutID, "")
    }

    // Todo Store is First Time
    fun setIsFirstTime(context: Context, isFirstTime: Boolean) {
        getPrefrense(context).edit().putBoolean(ConstantString.PREF_LAST_COMPLETE_DAY_ID , isFirstTime).apply()
    }

    fun getIsFirstTime(context: Context): Boolean {
        return getPrefrense(context).getBoolean(ConstantString.PREF_LAST_COMPLETE_DAY_ID, true)
    }

    // Todo sound options
    fun setSoundMute(context: Context, isFirstTime: Boolean) {
        getPrefrense(context).edit().putBoolean(ConstantString.PREF_MUTE , isFirstTime).apply()
    }

    fun getSoundMute(context: Context): Boolean {
        return getPrefrense(context).getBoolean(ConstantString.PREF_MUTE, false)
    }

    fun setVoiceGuide(context: Context, isFirstTime: Boolean) {
        getPrefrense(context).edit().putBoolean(ConstantString.PREF_VOICE_GUIDE , isFirstTime).apply()
    }

    fun getVoiceGuide(context: Context): Boolean {
        return getPrefrense(context).getBoolean(ConstantString.PREF_VOICE_GUIDE, true)
    }

    fun setCoachTips(context: Context, isFirstTime: Boolean) {
        getPrefrense(context).edit().putBoolean(ConstantString.PREF_COACH_TIPS , isFirstTime).apply()
    }

    fun getCoachTips(context: Context): Boolean {
        return getPrefrense(context).getBoolean(ConstantString.PREF_COACH_TIPS, true)
    }

    // Todo Set countdown and rest set time
    fun setCountDownTime(context: Context, weight: Int) {
        getPrefrense(context).edit().putInt(ConstantString.PREF_COUNTDOWN_TIME, weight).apply()
    }

    fun getCountDownTime(context: Context): Int {
        return getPrefrense(context).getInt(ConstantString.PREF_COUNTDOWN_TIME, 15)
    }

    fun setRestTime(context: Context, weight: Int) {
        getPrefrense(context).edit().putInt(ConstantString.PREF_REST_TIME, weight).apply()
    }

    fun getRestTime(context: Context): Int {
        return getPrefrense(context).getInt(ConstantString.PREF_REST_TIME, 30)
    }

    // Keep Screen On or off
    fun setKeepScreen(context: Context, isFirstTime: Boolean) {
        getPrefrense(context).edit().putBoolean(ConstantString.PREF_COACH_TIPS , isFirstTime).apply()
    }

    fun getKeepScreen(context: Context): Boolean {
        return getPrefrense(context).getBoolean(ConstantString.PREF_COACH_TIPS, true)
    }

    // BirthDate
    fun setBirthDate(context: Context, dateOfYear: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_BIRTH_DATE , dateOfYear).apply()
    }

    fun getBirthDate(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_BIRTH_DATE, "1990")
    }

    // Gender
    fun setGender(context: Context, isFirstTime: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_GENDER , isFirstTime).apply()
    }

    fun getGender(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_GENDER, "Male")
    }

    // Full body Selected Level
    fun setFullBodyLevel(context: Context, Lvlname: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_FULL_BODY_LEVEL , Lvlname).apply()
    }

    fun getFullBodyLevel(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_FULL_BODY_LEVEL, ConstantString.DEF_FULL_BODY_BEGINNER)
    }

    // Check Reward Ad is Show or not
    fun setBWBeginnerAd(context: Context, adTime: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_BW_BEGINNER_AD_TIME , adTime).apply()
    }

    fun getBWBeginnerAd(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_BW_BEGINNER_AD_TIME, "")
    }

    fun setBWIntermediateAd(context: Context, adTime: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_BW_INTERMEDIATE_AD_TIME , adTime).apply()
    }

    fun getBWIntermediateAd(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_BW_INTERMEDIATE_AD_TIME, "")
    }

    fun setBWAdvancedAd(context: Context, adTime: String) {
        getPrefrense(context).edit().putString(ConstantString.PREF_BW_ADVANCED_AD_TIME , adTime).apply()
    }

    fun getBWAdvancedAd(context: Context): String? {
        return getPrefrense(context).getString(ConstantString.PREF_BW_ADVANCED_AD_TIME, "")
    }

    // set Week Goal Day
    fun setWeekGoalDay(context: Context, dayName: Int) {
        getPrefrense(context).edit().putInt(ConstantString.PREF_WEEK_GOAL_DAYS , dayName).apply()
    }

    fun getWeekGoalDay(context: Context): Int {
        return getPrefrense(context).getInt(ConstantString.PREF_WEEK_GOAL_DAYS, 7)
    }

    // Set first day of week
    fun setFirstDayOfWeek(context: Context, dayName: Int) {
        getPrefrense(context).edit().putInt(ConstantString.PREF_FIRST_DAY_OF_WEEK , dayName).apply()
    }

    fun getFirstDayOfWeek(context: Context): Int {
        return getPrefrense(context).getInt(ConstantString.PREF_FIRST_DAY_OF_WEEK, 1)
    }

    fun setInteger(context: Context,key:String,value: Int){
        getPrefrense(context).edit().putInt(key, value).apply()
    }

    fun getInteger(context: Context,key: String,value: Int):Int{
        return getPrefrense(context).getInt(key,value)
    }


    fun setString(context: Context,key:String,value: String){
        getPrefrense(context).edit().putString(key, value).apply()
    }

    fun getString(context: Context,key: String,value: String):String{
        return getPrefrense(context).getString(key,value)!!
    }


    fun setBoolean(context: Context,key:String,value: Boolean){
        getPrefrense(context).edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context,key: String,value: Boolean):Boolean{
        return getPrefrense(context).getBoolean(key,value)!!
    }


  /*  fun setLastUnCompletedExPos(context: Context, strPlanId: Int, strLastDayName: String, NumLastPosition: Int) {

        when (strPlanId) {
            1 -> getPrefrense(context).edit().putInt(ConstantString.PREF_LAST_UN_COMPLETE_BEGINNER_DAY + strLastDayName, NumLastPosition).apply()
            2 -> getPrefrense(context).edit().putInt(ConstantString.PREF_LAST_UN_COMPLETE_INTERMEDIATE_DAY + strLastDayName, NumLastPosition).apply()
            3 -> getPrefrense(context).edit().putInt(ConstantString.PREF_LAST_UN_COMPLETE_ADVANCED_DAY + strLastDayName, NumLastPosition).apply()
        }

    }

    fun getLastUnCompletedExPos(context: Context, strPlanId: Int, strLastDayName: String): Int {
        when (strPlanId) {
            1 -> return getPrefrense(context).getInt(ConstantString.PREF_LAST_UN_COMPLETE_BEGINNER_DAY + strLastDayName, 0)
            2 -> return getPrefrense(context).getInt(ConstantString.PREF_LAST_UN_COMPLETE_INTERMEDIATE_DAY + strLastDayName, 0)
            3 -> return getPrefrense(context).getInt(ConstantString.PREF_LAST_UN_COMPLETE_ADVANCED_DAY + strLastDayName, 0)
        }
        return 0
    }*/

    fun setLastUnCompletedExPos(context: Context,tableName:String,workoutId: String,NumLastPosition: Int){
        getPrefrense(context).edit().putInt(ConstantString.PREF_LAST_UN_COMPLETE_BEGINNER_DAY + "_"+tableName+"_"+workoutId, NumLastPosition).apply()
    }

    fun getLastUnCompletedExPos(context: Context,tableName:String,workoutId: String):Int{
        return getPrefrense(context).getInt(ConstantString.PREF_LAST_UN_COMPLETE_BEGINNER_DAY + "_"+tableName+"_"+workoutId, 0)
    }
}
