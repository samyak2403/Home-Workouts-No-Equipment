package com.rbs.workout.freak.utils

import com.rbs.workout.freak.pojo.PWorkOutCategory

object ConstantString {

    val HISTORY_FROM: String?="HISTORY_FROM"
    val workout_id_from_workactivity: String?="workout_id_from_workactivity"
    val table_name_from_workactivity: String?="table_name_from_workactivity"
    val work_table_name: String?="work_table_name"
    val PREF_LANGUAGE= "pref_language"
    val PREF_LANGUAGE_NAME= "pref_language_name"
    /* Todo Calculation Values */
    const val secDurationCal = 0.2235
    const val CalForKj = 4.08
    const val ONE_DAY_COMPLETE_PER = 0.55 // And use Math.ceil

    const val CapDateFormatDisplay = "yyyy-MM-dd HH:mm:ss"

    /* Todo drawer static string */
    val Drawer_Home = "Training plans"
    val Drawer_Library = "Discover More Exercises"
    val Drawer_Report = "Report"
    val Drawer_Reminder = "Reminder"
    val Drawer_Settings = "Settings"
    val Drawer_RestartProgress = "Restart Progress"
    val Drawer_History = "History"

    const val PREF_IN_CM_UNIT = "PREF_IN_CM_UNIT"
    val CENTI_METER: String = "CENTI_METER"
    const val PREF_KG_LB_UNIT = "PREF_KG_LB_UNIT"

    /*KG*/
    const val MIN_KG = 20
    const val MAX_KG = 997

    /*LB*/
    const val MIN_LB = 44.09
    const val MAX_LB = 2198.01

    /*FT*/
    const val MIN_FT = 0
    const val MAX_FT = 13

    /*IN*/
    const val MIN_IN = 7.9
    const val MAX_IN = 1.5

    /*CM*/
    const val MIN_CM = 20
    const val MAX_CM = 400
    const val IsFrom: String = "IsFrom"
    const val FromNotification: String = "FromNotification"


    /* Todo database related string */
    const val tbl_full_body_workouts_list = "tbl_full_body_workouts_list"
    const val tbl_lower_body_list = "tbl_lower_body_list"
    const val tbl_bw_exercise = "tbl_bw_exercise"

    const val tbl_chest_advanced = "tbl_chest_advanced"
    const val tbl_chest_beginner = "tbl_chest_beginner"
    const val tbl_chest_intermediate = "tbl_chest_intermediate"

    const val tbl_abs_advanced = "tbl_abs_advanced"
    const val tbl_abs_beginner = "tbl_abs_beginner"
    const val tbl_abs_intermediate = "tbl_abs_intermediate"

    const val tbl_arm_advanced = "tbl_arm_advanced"
    const val tbl_arm_beginner = "tbl_arm_beginner"
    const val tbl_arm_intermediate = "tbl_arm_intermediate"

    const val tbl_leg_advanced = "tbl_leg_advanced"
    const val tbl_leg_beginner = "tbl_leg_beginner"
    const val tbl_leg_intermediate = "tbl_leg_intermediate"

    const val tbl_shoulder_back_advanced = "tbl_shoulder_back_advanced"
    const val tbl_shoulder_back_beginner = "tbl_shoulder_back_beginner"
    const val tbl_shoulder_back_intermediate = "tbl_shoulder_back_intermediate"

    /* Todo workout category static*/
    const val main = "main_title"
    const val Build_wider = "Build Wider"
    const val full_body = "full_data"
    const val biginner = "biginner"
    const val intermediate = "intermediate"
    const val advance = "advance"
    const val Full_Body = "Full Body"
    const val Lower_Body = "Lower Body"
    const val Chest = "Chest"
    const val Abs = "Abs"
    const val Arm = "Arm"
    const val Shoulder_and_Back = "Shoulder & Back"
    const val Leg = "Leg"
    const val Library = "Library"

    /* Todo passing value constant */
    const val workout_list = "workout_list"
    const val workout_type_time = "time"
    const val workout_type_step = "step"
    const val val_is_workout = "workout_activity"
    const val val_is_workout_list_activity = "workout_list_activity"

    /* Todo intent passing keys */
    const val key_workout_category_item = "key_workout_category_item"
    const val key_workout_list_array = "key_workout_list_array"
    const val extra_workout_list_pos = "extra_workout_list_pos"
    const val key_workout_list_pos = "key_workout_list_pos"
    const val key_workout_details_type = "key_workout_details_type"
    const val key_workout_sound = "key_workout_sound"
    const val key_day_name = "key_day_name"
    const val key_week_name = "key_week_name"
    const val key_category_name = "key_category_name"

    const val SHARE_IMAGE_PATH = "share_path"

    /* Todo preference static key*/
    const val pref_key_is_first_time = "is_first_time"
    const val pref_Key_purchase_status = "KeyPurchaseStatus"
    const val PREF_WEIGHT_UNIT = "PREF_WEIGHT_UNIT"
    const val PREF_HEIGHT_UNIT = "PREF_HEIGHT_UNIT"
    const val PREF_LAST_INPUT_WEIGHT = "PREF_LAST_INPUT_WEIGHT"
    const val PREF_LAST_INPUT_FOOT = "PREF_LAST_INPUT_FOOT"
    const val PREF_LAST_INPUT_INCH = "PREF_LAST_INPUT_INCH"
    const val PREF_LAST_COMPLETE_DAY_ID = "PREF_LAST_COMPLETE_DAY_ID"
    const val PREF_MUTE = "PREF_MUTE"
    const val PREF_VOICE_GUIDE = "PREF_VOICE_GUIDE"
    const val PREF_COACH_TIPS = "PREF_COACH_TIPS"
    const val PREF_COUNTDOWN_TIME = "PREF_COUNTDOWN_TIME"
    const val PREF_REST_TIME = "PREF_REST_TIME"
    const val PREF_BIRTH_DATE = "PREF_BIRTH_DATE"
    const val PREF_GENDER = "PREF_GENDER"
    const val PREF_FULL_BODY_LEVEL = "PREF_FULL_BODY_LEVEL"
    const val PREF_BW_BEGINNER_AD_TIME = "PREF_BW_BEGINNER_AD_TIME"
    const val PREF_BW_INTERMEDIATE_AD_TIME = "PREF_BW_INTERMEDIATE_AD_TIME"
    const val PREF_BW_ADVANCED_AD_TIME = "PREF_BW_ADVANCED_AD_TIME"
    const val PREF_WEEK_GOAL_DAYS = "PREF_WEEK_GOAL_DAYS"
    const val PREF_FIRST_DAY_OF_WEEK = "PREF_FIRST_DAY_OF_WEEK"


    /* Todo weight default value */
    const val DEF_KG = "KG"
    const val DEF_LB = "LB"
    const val DEF_IN = "IN"
    const val DEF_CM = "CM"
    const val DEF_FT = "FT"
    const val DEF_FULL_BODY_BEGINNER = "Time_beginner"
    const val DEF_FULL_BODY_INTERMEDIATE = "Time_intermidiate"
    const val DEF_FULL_BODY_ADVANCE = "Time_advanced"
    const val DEF_BUILD_WIDER_LVL_BEGINNER = "01"
    const val DEF_BUILD_WIDER_LVL_INTERMEDIATE = "02"
    const val DEF_BUILD_WIDER_LVL_ADVANCED = "03"

    /* Todo Static values */
    var pWorkOutCategory = PWorkOutCategory()

    /* Todo Constant value for confirmation */
    const val CONFIRM_VOICE_TEST = "CONFIRM_VOICE_TEST"

    /* Todo Constant value for set duration dialog */
    const val DL_COUNT_DOWN_TIME = "DL_COUNT_DOWN_TIME"
    const val DL_REST_SET = "DL_REST_SET"

    // Todo intent extra value key
    const val extraReminderId= "extraReminderId"

    const val beginnerColor= 2131034196
    const val intermediateColor= 2131034199
    const val advanceColor= 2131034195



    const val GOOGLE_BANNER_TYPE_AD = "GOOGLE_BANNER_TYPE_AD"




    var AD_TYPE_FB_GOOGLE = "AD_TYPE_FB_GOOGLE"


    var GOOGLE_BANNER = "GOOGLE_BANNER"
    var GOOGLE_INTERSTITIAL = "GOOGLE_INTERSTITIAL"

    var FB_BANNER = "FB_BANNER"
    var FB_INTERSTITIAL = "FB_INTERSTITIAL"
    var EXTRA_REMINDER_ID = "Reminder_ID"
    var WEIGHT_TABLE_DATE_FORMAT = "yyyy-MM-dd"
    var SPLASH_SCREEN_COUNT = "splash_screen_count"
    var START_BTN_COUNT = "start_btn_count"
    var EXIT_BTN_COUNT = "exit_btn_count"
    var STATUS_ENABLE_DISABLE = "STATUS_ENABLE_DISABLE"
    const val PREF_LAST_UN_COMPLETE_BEGINNER_DAY = "PREF_LAST_UN_COMPLETE_BEGINNER_DAY"

    /*Google ads*/
    var GOOGLE_ADMOB_APP_ID = "ca-app-pub-2383806638174989~7000529178"




    /*Facebook ads*/
    var FB_BANNER_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"
    var FB_INTERSTITIAL_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"


    /*Ad Type facebook or google*/
    const val AD_FACEBOOK = "facebook"
    const val AD_GOOGLE = "google"
    var AD_TYPE_FACEBOOK_GOOGLE = AD_GOOGLE


    /*Ad Enable ot Disable*/
    const val ENABLE = "Enable"
    const val DISABLE = "Disable"
    var ENABLE_DISABLE = ENABLE


}
