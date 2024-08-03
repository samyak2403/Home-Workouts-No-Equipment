package com.rbs.workout.freak.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.MyInterstitialAds_abc
import com.rbs.workout.freak.R
import com.rbs.workout.freak.adapter.CompletedDayStatusAdapter
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.pojo.PWorkOutDetails
import com.rbs.workout.freak.utils.*
import kotlinx.android.synthetic.main.activity_completed.*
import kotlinx.android.synthetic.main.activity_completed.imgBack
import kotlinx.android.synthetic.main.activity_completed.llAdView
import kotlinx.android.synthetic.main.activity_completed.llAdViewFacebook
import java.util.*
import kotlin.math.roundToInt

open class CompletedActivity : BaseActivity() , MyInterstitialAds_abc.OnInterstitialAdListnear {
    private lateinit var CDInstrialAds: MyInterstitialAds_abc

    override fun onBackPressed() {
        saveData()
        interstitialAds()
    }

    // Todo Controls
    lateinit var txtTotalNoOfLevel: TextView
    lateinit var txtDurationTime: TextView

    // Todo Object
    lateinit var context: Context
    lateinit var pWorkoutList: ArrayList<PWorkOutDetails>

    var calValue = 0.0
    var feelRate = "0"

    var strDayName: String = ""
    var strWeekName: String = ""
    var table_name: String = ""
    var workout_id: String = ""
    private lateinit var dbHelper: DataHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed)
        CDInstrialAds = MyInterstitialAds_abc(this, this)
        context = this
        dbHelper = DataHelper(context)
        defaultSetup()

        initAction()

    }

    // Todo common methods
    private fun defaultSetup() {
        /* Utils.initAdd(this, adView)

         Utils.initFullAdd(this)*/

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

        txtDurationTime = findViewById(R.id.txtDurationTime)
        txtTotalNoOfLevel = findViewById(R.id.txtTotalNoOfLevel)

        pWorkoutList = intent.getSerializableExtra(ConstantString.workout_list) as ArrayList<PWorkOutDetails>
        strDayName = intent.getStringExtra(ConstantString.key_day_name)!!
        strWeekName = intent.getStringExtra(ConstantString.key_week_name)!!


        table_name = intent.getStringExtra(ConstantString.table_name_from_workactivity)!!
        workout_id = intent.getStringExtra(ConstantString.workout_id_from_workactivity)!!


        txtDurationTime.text = intent.getStringExtra("Duration")

        txtTotalNoOfLevel.text = pWorkoutList.size.toString()

        calValue = ConstantString.secDurationCal * CommonUtility.timeToSecond(intent.getStringExtra("Duration")!!)

        txtBurnCaloriesValues.text = CommonUtility.getStringFormat(calValue)

//        txtLevelNo.text = ConstantString.pWorkOutCategory.catDefficultyLevel.plus(" Level Completed")
        if (ConstantString.pWorkOutCategory.dayName != "") {
            llWeekStatus.visibility = View.VISIBLE

            val dayName = ConstantString.pWorkOutCategory.dayName.replace("0", "")
            val weekName = ConstantString.pWorkOutCategory.weekName.replace("0", "")

            tvWeekName.text = "Week ".plus(weekName).plus(" - Day ").plus(dayName)
            //val completedWeekDay = ConstantString.pWorkOutCategory.dayName.replace("0","")
            tvGoalStatus.text = HtmlCompat.fromHtml("<font color='${ContextCompat.getColor(this, R.color.colorTheme)}'>$dayName</font>/7", HtmlCompat.FROM_HTML_MODE_LEGACY)

            setWeekStatusData(ConstantString.pWorkOutCategory.dayName)

            txtLevelNo.text = "Day ".plus(dayName.toInt() + ((weekName.toInt() - 1) * 7)).plus(" Completed")
        } else {
            llWeekStatus.visibility = View.GONE
        }

        setWeightValues()

        setBmiCalculation()

    }

    private fun initAction() {

        imgBack.setOnClickListener { onBackPressed() }

        btnDoItAgain.setOnClickListener {
            val intent1 = intent
            val intent = Intent(context, WorkoutActivity::class.java)
            intent.putExtra(ConstantString.workout_list, intent1.getSerializableExtra(ConstantString.workout_list))
            startActivity(intent)
            finish()
        }

        btnNext.setOnClickListener { saveData() }

        btnSaveBottom.setOnClickListener { saveData()
        interstitialAds()
        }

        btnShare.setOnClickListener {

            val link = "https://play.google.com/store/apps/details?id=$packageName"
            val strSubject = "Share ${resources.getString(R.string.app_name)} with you"
            val strText =
                    " I have finish ${txtTotalNoOfLevel.text} of ${resources.getString(R.string.app_name)} exercise.\n" +
                            "you should start working out at workout too. You'll get results in no time! \n" +
                            "Please download the app: $link"

            CommonUtility.shareStringLink(this, strSubject, strText)
        }

        btnEdit.setOnClickListener {
            setHeightWeightDialog()
        }

        /*  txtKG.setOnClickListener {
              if (LocalDB.getWeightUnit(this) != ConstantString.DEF_KG && LocalDB.getLastInputWeight(this) != 0f) {
                  edWeight.setText(String.format("%.2f", LocalDB.getLastInputWeight(this).toDouble()))
              }

              LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
              txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
              txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

              txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
              txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)
          }*/

        var boolKg = true

        txtKG.setOnClickListener {
            if (boolKg){
                try {

                    if (edWeight.text.toString() != "") {
                        edWeight.setText(CommonUtility.getStringFormat(CommonUtility.LbToKg(edWeight.text.toString().toDouble())))
                    }
                    LocalDB.setString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)

//                LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                    txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                    txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

                    txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                    txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)
                    edWeight.hint = "KG"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                boolKg = false
            }

        }


        /* txtLB.setOnClickListener {

             if (LocalDB.getWeightUnit(this) != ConstantString.DEF_LB && LocalDB.getLastInputWeight(this) != 0f) {
                 edWeight.setText(
                         String.format(
                                 "%.2f",
                                 CommonUtility.KgToLb(LocalDB.getLastInputWeight(this).toDouble())
                         )
                 )
             }

             LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
             txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
             txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

             txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
             txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)
         }*/

        txtLB.setOnClickListener {
            if (!boolKg){
                try {
                    edWeight.hint = "LB"

                    if (edWeight.text.toString() != "") {
                        edWeight.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(edWeight.text.toString().toDouble())))
                    }

                    LocalDB.setString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_LB)
                    txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
                    txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                    edWeight.hint = "LB"
                    txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
                    txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                boolKg = true
            }


        }

        /*  edWeight.setOnEditorActionListener { _, actionId, _ ->

              if (actionId == EditorInfo.IME_ACTION_DONE) {

                  if (LocalDB.getWeightUnit(this) != ConstantString.DEF_KG) {
                      LocalDB.setLastInputWeight(
                              this,
                              String.format("%.2f", CommonUtility.LbToKg(edWeight.text.toString().toDouble())).toFloat()
                      )
                  } else {
                      LocalDB.setLastInputWeight(
                              this,
                              String.format("%.2f", edWeight.text.toString().toDouble()).toFloat()
                      )
                  }

                  setBmiCalculation()
              }

              false
          }*/

        edWeight.setOnEditorActionListener { _, actionId, _ ->

            try {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (LocalDB.getWeightUnit(this) != ConstantString.DEF_KG) {
                        LocalDB.setLastInputWeight(
                                this,
                                CommonUtility.getStringFormat((CommonUtility.LbToKg(edWeight.text.toString().toDouble()))).toFloat()
                        )
                    } else {
                        LocalDB.setLastInputWeight(this, CommonUtility.getStringFormat((edWeight.text.toString().toDouble())).toFloat())
                    }

                    setBmiCalculation()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            false
        }

        rdgFeel.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.rdoFeelOne) {
                feelRate = "1"
//                Toast.makeText(this, "Radio one", Toast.LENGTH_LONG).show()
            } else if (checkedId == R.id.rdoFeelTwo) {
                feelRate = "2"
//                Toast.makeText(this, "Radio two", Toast.LENGTH_LONG).show()
            } else if (checkedId == R.id.rdoFeelThree) {
                feelRate = "3"
//                Toast.makeText(this, "Radio three", Toast.LENGTH_LONG).show()
            } else if (checkedId == R.id.rdoFeelFour) {
                feelRate = "4"
//                Toast.makeText(this, "Radio four", Toast.LENGTH_LONG).show()
            } else if (checkedId == R.id.rdoFeelFive) {
                feelRate = "5"
//                Toast.makeText(this, "Radio five", Toast.LENGTH_LONG).show()
            }

        }

        txtHideShowBmi.setOnClickListener {
            if (lnyBmiGraphMain.visibility == View.VISIBLE) {
                lnyBmiGraphMain.visibility = View.GONE
                txtHideShowBmi.text = "Show"
            } else {
                lnyBmiGraphMain.visibility = View.VISIBLE
                txtHideShowBmi.text = "Hide"
            }
        }

        btnFeedBack.setOnClickListener { CommonUtility.contactUs(this) }

    }

    private fun setWeekStatusData(numDayName: String) {
        rcyWeekStatus.adapter = CompletedDayStatusAdapter(this, numDayName.toInt())
    }

    /* Todo set bmi calculation */
    private fun setBmiCalculation() {

        if (LocalDB.getLastInputWeight(this) != 0f && LocalDB.getLastInputFoot(this) != 0 && LocalDB.getLastInputInch(this).toInt() != 0) {

            val bmiValue = CommonUtility.getBmiCalculation(
                    LocalDB.getLastInputWeight(this),
                    LocalDB.getLastInputFoot(this),
                    LocalDB.getLastInputInch(this).toInt()
            )

            val param = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (String.format(
                            "%.1f",
                            CommonUtility.calculationForBmiGraph(String.format("%.1f", bmiValue).toFloat())
                    ).toFloat())
            )

            txtBmiGrade.text = CommonUtility.getStringFormat(bmiValue)
            txtWeightString.text = CommonUtility.bmiWeightString(String.format("%.1f", bmiValue).toFloat())
            txtWeightString.setTextColor(ColorStateList.valueOf(CommonUtility.bmiWeightTextColor(this, bmiValue.toFloat())))
            blankView1.layoutParams = param

        }
    }

    /* Todo set weight values */
    private fun setWeightValues() {
        if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG && LocalDB.getLastInputWeight(this) != 0f) {
            edWeight.setText(CommonUtility.getStringFormat(LocalDB.getLastInputWeight(this).toDouble()))
            txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

            txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)
        } else if (LocalDB.getWeightUnit(this) == ConstantString.DEF_LB && LocalDB.getLastInputWeight(this) != 0f) {
            edWeight.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(this).toDouble())))
            txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

            txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)
        }
    }

    /* Todo set height weight dialog */
    /*private fun setHeightWeightDialog() {

        var boolKg = true
        var boolInch = true

        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_weight_height)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val lnyInch = dialog.findViewById(R.id.lnyInch) as LinearLayout

        val txtKG = dialog.findViewById(R.id.txtKG) as TextView
        val txtLB = dialog.findViewById(R.id.txtLB) as TextView
        val txtCM = dialog.findViewById(R.id.txtCM) as TextView
        val txtIN = dialog.findViewById(R.id.txtIN) as TextView

        val edWeight = dialog.findViewById(R.id.edWeight) as EditText
        val edCM = dialog.findViewById(R.id.edCM) as EditText
        val edFeet = dialog.findViewById(R.id.edFeet) as EditText
        val edInch = dialog.findViewById(R.id.edInch) as EditText

        val btnCancel = dialog.findViewById(R.id.btnOkay) as Button
        val btnNext = dialog.findViewById(R.id.btnNext) as Button

        if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG) {
            boolKg = true

            edWeight.setText(LocalDB.getLastInputWeight(this).toString())

            txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

            txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)
        } else {
            boolKg = false

            edWeight.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(this).toDouble())))

            txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

            txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)

        }

        if (LocalDB.getHeightUnit(this) == ConstantString.DEF_IN) {

            boolInch = true

            edCM.visibility = View.INVISIBLE
            lnyInch.visibility = View.VISIBLE

            txtIN.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            txtCM.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

            txtIN.background = resources.getDrawable(R.drawable.ract_theme_select, null)
            txtCM.background = resources.getDrawable(R.drawable.ract_gray, null)

            edFeet.setText(LocalDB.getLastInputFoot(this).toString())
            edInch.setText(LocalDB.getLastInputInch(this).toString())
        } else {
            boolInch = false

            edCM.visibility = View.VISIBLE
            lnyInch.visibility = View.INVISIBLE

            txtIN.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
            txtCM.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

            txtIN.background = resources.getDrawable(R.drawable.ract_gray, null)
            txtCM.background = resources.getDrawable(R.drawable.ract_theme_select, null)

            var inch = 0.0

            inch = CommonUtility.ftInToInch(LocalDB.getLastInputFoot(this), LocalDB.getLastInputInch(this).toDouble())

            edCM.setText(CommonUtility.inchToCm(inch).roundToInt().toDouble().toString())
        }

        txtKG.setOnClickListener {
            if (!boolKg) {
                boolKg = true

                txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

                txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)

                edWeight.hint = "KG"

                if (edWeight.text.toString() != "") {
                    edWeight.setText(CommonUtility.getStringFormat(CommonUtility.LbToKg(edWeight.text.toString().toDouble())))
                }

            }
        }

        txtLB.setOnClickListener {
            if (boolKg) {
                boolKg = false

                txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
                txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

                txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                edWeight.hint = "LB"

                if (edWeight.text.toString() != "") {
                    edWeight.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(edWeight.text.toString().toDouble())))
                }

            }
        }

        txtCM.setOnClickListener {
            if (boolInch) {
                boolInch = false

                edCM.visibility = View.VISIBLE
                lnyInch.visibility = View.INVISIBLE

                txtIN.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
                txtCM.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

                txtIN.background = resources.getDrawable(R.drawable.ract_gray, null)
                txtCM.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                var inch = 0.0
                if (edFeet.text.toString() != "" && edInch.text.toString() != "") {
                    inch = CommonUtility.ftInToInch(edFeet.text.toString().toInt(), edInch.text.toString().toDouble())
                } else if (edFeet.text.toString() != "" && edInch.text.toString() == "") {
                    inch = CommonUtility.ftInToInch(edFeet.text.toString().toInt(), 0.0)
                } else if (edFeet.text.toString() == "" && edInch.text.toString() != "") {
                    inch = CommonUtility.ftInToInch(1, edInch.text.toString().toDouble())
                }

                edCM.setText(CommonUtility.inchToCm(inch).roundToInt().toDouble().toString())

            }
        }

        txtIN.setOnClickListener {
            edCM.visibility = View.INVISIBLE
            lnyInch.visibility = View.VISIBLE

            txtIN.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            txtCM.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

            txtIN.background = resources.getDrawable(R.drawable.ract_theme_select, null)
            txtCM.background = resources.getDrawable(R.drawable.ract_gray, null)

            if (!boolInch) {
                boolInch = true

                if (edCM.text.toString() != "") {
                    val inch = CommonUtility.getStringFormat(CommonUtility.cmToInch(edCM.text.toString().toDouble()))
                    edFeet.setText(CommonUtility.calcInchToFeet(inch.toDouble()).toString())
                    edInch.setText(CommonUtility.calcInFromInch(inch.toDouble()).toString())
                }
            }

        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnNext.setOnClickListener {

            if (boolInch) {
                LocalDB.setLastInputFoot(this, edFeet.text.toString().toInt())
                LocalDB.setLastInputInch(this, edInch.text.toString().toFloat())
            } else {
                val inch = CommonUtility.getStringFormat(CommonUtility.cmToInch(edCM.text.toString().toDouble()))
                LocalDB.setLastInputFoot(this, CommonUtility.calcInchToFeet(inch.toDouble()))
                LocalDB.setLastInputInch(this, CommonUtility.calcInFromInch(inch.toDouble()).toFloat())
            }

            val dbHelper = DataHelper(this)

            val strKG: Float
            if (boolKg) {
                strKG = edWeight.text.toString().toFloat()
                LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                LocalDB.setLastInputWeight(this, strKG)
            } else {
                strKG = Math.round(CommonUtility.LbToKg(edWeight.text.toString().toDouble())).toFloat()
                LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                LocalDB.setLastInputWeight(this, strKG)
            }

            val currentDate = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())

            if (dbHelper.weightExistOrNot(currentDate)) {
                dbHelper.updateWeight(currentDate, strKG.toString(), "")
            } else {
                dbHelper.addUserWeight(strKG.toString(), currentDate, "")
            }

            if (boolKg) {
                LocalDB.setLastInputWeight(this, edWeight.text.toString().toFloat())
            } else {
                LocalDB.setLastInputWeight(this, CommonUtility.LbToKg(edWeight.text.toString().toDouble()).toFloat())
            }

            setWeightValues()

            setBmiCalculation()

            dialog.dismiss()
        }

        dialog.show()
    }*/


    private fun setHeightWeightDialog() {

        var boolKg: Boolean
        var boolInch: Boolean

        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_weight_height)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val lnyInch = dialog.findViewById(R.id.lnyInch) as LinearLayout

        val txtKG = dialog.findViewById(R.id.txtKG) as TextView
        val txtLB = dialog.findViewById(R.id.txtLB) as TextView
        val txtCM = dialog.findViewById(R.id.txtCM) as TextView
        val txtIN = dialog.findViewById(R.id.txtIN) as TextView

        val edWeight = dialog.findViewById(R.id.edWeight) as EditText
        val edCM = dialog.findViewById(R.id.edCM) as EditText
        val edFeet = dialog.findViewById(R.id.edFeet) as EditText
        val edInch = dialog.findViewById(R.id.edInch) as EditText

        val btnCancel = dialog.findViewById(R.id.btnOkay) as Button
        val btnNext = dialog.findViewById(R.id.btnNext) as Button
        boolKg = true
        boolInch = true

        try {
            if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG) == ConstantString.DEF_LB) {
                boolKg = false

                edWeight.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(this).toDouble())))

                txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
                txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

                txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)

            } else {

                edWeight.setText(LocalDB.getLastInputWeight(this).toString())

                txtKG.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

                txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        boolInch = true
        try {
            if (LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN) == ConstantString.DEF_IN) {

                edCM.visibility = View.INVISIBLE
                lnyInch.visibility = View.VISIBLE

                txtIN.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                txtCM.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

                txtIN.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                txtCM.background = resources.getDrawable(R.drawable.ract_gray, null)

                edFeet.setText(LocalDB.getLastInputFoot(this).toString())
                edInch.setText(LocalDB.getLastInputInch(this).toString())
            } else {
                boolInch = false

                edCM.visibility = View.VISIBLE
                lnyInch.visibility = View.INVISIBLE

                txtIN.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
                txtCM.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

                txtIN.background = resources.getDrawable(R.drawable.ract_gray, null)
                txtCM.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                val inch = CommonUtility.ftInToInch(LocalDB.getLastInputFoot(this), LocalDB.getLastInputInch(this).toDouble())

                edCM.setText(CommonUtility.inchToCm(inch).roundToInt().toDouble().toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        txtKG.setOnClickListener {

            LocalDB.setString(
                    this
                    , ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG
            )
            try {
                if (!boolKg) {
                    boolKg = true

                    txtKG.setTextColor(
                            ContextCompat.getColor(
                                    this
                                    , R.color.colorWhite
                            )
                    )
                    txtLB.setTextColor(
                            ContextCompat.getColor(
                                    this
                                    , R.color.colorBlack
                            )
                    )

                    txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                    txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)

                    edWeight.hint = "KG"

                    if (edWeight.text.toString() != "") {
                        edWeight.setText(CommonUtility.getStringFormat(CommonUtility.LbToKg(edWeight.text.toString().toDouble())))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        txtLB.setOnClickListener {
            LocalDB.setString(
                    this
                    , ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_LB
            )
            try {
                if (boolKg) {
                    boolKg = false

                    txtKG.setTextColor(
                            ContextCompat.getColor(
                                    this
                                    , R.color.colorBlack
                            )
                    )
                    txtLB.setTextColor(
                            ContextCompat.getColor(
                                    this
                                    , R.color.colorWhite
                            )
                    )

                    txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
                    txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                    edWeight.hint = "LB"

                    if (edWeight.text.toString() != "") {
                        edWeight.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(edWeight.text.toString().toDouble())))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        txtCM.setOnClickListener {
            try {
                if (boolInch) {
                    boolInch = false

                    edCM.visibility = View.VISIBLE
                    lnyInch.visibility = View.INVISIBLE

                    txtIN.setTextColor(
                            ContextCompat.getColor(
                                    this
                                    , R.color.colorBlack
                            )
                    )
                    txtCM.setTextColor(
                            ContextCompat.getColor(
                                    this
                                    , R.color.colorWhite
                            )
                    )

                    txtIN.background = resources.getDrawable(R.drawable.ract_gray, null)
                    txtCM.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                    var inch = 0.0
                    if (edFeet.text.toString() != "" && edInch.text.toString() != "") {
                        inch = CommonUtility.ftInToInch(edFeet.text.toString().toInt(), edInch.text.toString().toDouble())
                    } else if (edFeet.text.toString() != "" && edInch.text.toString() == "") {
                        inch = CommonUtility.ftInToInch(edFeet.text.toString().toInt(), 0.0)
                    } else if (edFeet.text.toString() == "" && edInch.text.toString() != "") {
                        inch = CommonUtility.ftInToInch(1, edInch.text.toString().toDouble())
                    }

                    edCM.setText(CommonUtility.inchToCm(inch).roundToInt().toDouble().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            LocalDB.setString(
                    this
                    , ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_CM
            )
        }

        txtIN.setOnClickListener {

            LocalDB.setString(
                    this
                    , ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN
            )


            try {
                edCM.visibility = View.INVISIBLE
                lnyInch.visibility = View.VISIBLE

                txtIN.setTextColor(
                        ContextCompat.getColor(
                                this
                                , R.color.colorWhite
                        )
                )
                txtCM.setTextColor(
                        ContextCompat.getColor(
                                this
                                , R.color.colorBlack
                        )
                )

                txtIN.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                txtCM.background = resources.getDrawable(R.drawable.ract_gray, null)

                try {
                    if (!boolInch) {
                        boolInch = true

                        if (edCM.text.toString() != "") {
                            val inch = CommonUtility.getStringFormat(CommonUtility.cmToInch(edCM.text.toString().toDouble()))
                            edFeet.setText(CommonUtility.calcInchToFeet(inch.toDouble()).toString())
                            edInch.setText(CommonUtility.calcInFromInch(inch.toDouble()).toString())
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        btnCancel.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnNext.setOnClickListener {

            if (edWeight.text.toString().isEmpty()) {
                edWeight.setText("0")
            } else if (edCM.text.toString().isEmpty() && LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN).equals(ConstantString.DEF_CM)) {
                Toast.makeText(this, "Please enter Height", Toast.LENGTH_LONG).show()
            } else if (edFeet.text.toString().isEmpty() && LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
                            .equals(ConstantString.DEF_IN)
            ) {
                Toast.makeText(this, "Please enter Height", Toast.LENGTH_LONG).show()
            } else if (edInch.text.toString().isEmpty() && LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
                            .equals(ConstantString.DEF_IN)
            ) {
                Toast.makeText(this, "Please enter Height", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG) == ConstantString.DEF_KG
                    && (edWeight.text.toString().toFloat() < ConstantString.MIN_KG || edWeight.text.toString().toFloat() > ConstantString.MAX_KG)
            ) {
                Toast.makeText(this, "Please enter proper weight in KG", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG) == ConstantString.DEF_LB
                    && (edWeight.text.toString().toFloat() < ConstantString.MIN_LB || edWeight.text.toString().toFloat() > ConstantString.MAX_LB)
                    && (edWeight.text.toString().toFloat() != ConstantString.MAX_LB.toFloat() || edWeight.text.toString()
                            .toFloat() != ConstantString.MIN_LB.toFloat())
            ) {
                Toast.makeText(this, "Please enter proper weight in LB", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN) == ConstantString.DEF_CM
                    && (edCM.text.toString().toFloat() < ConstantString.MIN_CM || edCM.text.toString().toFloat() > ConstantString.MAX_CM)
            ) {
                Toast.makeText(this, "Please enter proper height in CM", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN) ==
                    ConstantString.DEF_IN && edFeet.text.toString().toInt() == 0 && edInch.text.toString().toFloat() < 7.9
            ) {
                Toast.makeText(this, "Please enter proper height in INCH", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN) ==
                    ConstantString.DEF_IN && edFeet.text.toString().toInt() >= 13 && edInch.text.toString().toFloat() > 1.5
            ) {
                Toast.makeText(this, "Please enter proper height in INCH", Toast.LENGTH_LONG).show()
            } else if ((LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN) ==
                            ConstantString.DEF_IN && edFeet.text.toString().toInt() >= 14)
            ) {
                Toast.makeText(this, "Please enter proper height in INCH ", Toast.LENGTH_LONG).show()
            } else {
                /*try {
                    if (boolInch) {
                        LocalDB.setLastInputFoot(this, edFeet.text.toString().toInt())
                        LocalDB.setLastInputInch(this, edInch.text.toString().toFloat())
                        LocalDB.setHeightUnit(this, ConstantString.DEF_IN)

                    } else {
                        val inch = CommonUtility.getStringFormat(CommonUtility.cmToInch(edCM.text.toString().toDouble()))
                        LocalDB.setLastInputFoot(this, CommonUtility.calcInchToFeet(inch.toDouble()))
                        LocalDB.setLastInputInch(this, CommonUtility.calcInFromInch(inch.toDouble()).toFloat())
                        LocalDB.setHeightUnit(this, ConstantString.DEF_CM)

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {

                    val strKG: Float
                    if (boolKg) {
                        strKG = edWeight.text.toString().toFloat()
                        LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                        LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                        LocalDB.setLastInputWeight(this, strKG)
                    } else {
                        strKG = CommonUtility.LbToKg(edWeight.text.toString().toDouble()).roundToInt().toFloat()
                        LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                        LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                        LocalDB.setLastInputWeight(this, strKG)
                    }

                    val currentDate = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())

                    if (dbHelper.weightExistOrNot(currentDate)) {
                        dbHelper.updateWeight(currentDate, strKG.toString(), "")
                    } else {
                        dbHelper.addUserWeight(strKG.toString(), currentDate, "")
                    }

                    if (boolKg) {
                        LocalDB.setLastInputWeight(this, edWeight.text.toString().toFloat())
                    } else {
                        LocalDB.setLastInputWeight(this, CommonUtility.LbToKg(edWeight.text.toString().toDouble()).toFloat())
                    }

                    setWeightValues()

                    setBmiCalculation()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                dialog.dismiss()*/

                try {

                    if (boolInch) {
                        var edtInchstr: Float = getDecimal(edInch.text.toString().toDouble()).toFloat()
                        edtInchstr = getDecimal(edtInchstr.toDouble()).toFloat()


                        if (edtInchstr > 12.0) {
                            val totalInch = edtInchstr - 12.0

                            LocalDB.setLastInputFoot(this, (edFeet.text.toString().toInt() + 1))
                            if (edFeet.text.toString().toInt() == 12 && edtInchstr > 13.5) {
                                LocalDB.setLastInputInch(this, 1.5.toFloat())
                            } else {
                                LocalDB.setLastInputInch(this, totalInch.toFloat())
                            }
                        } else {
                            LocalDB.setLastInputFoot(this, edFeet.text.toString().toInt())
                            LocalDB.setLastInputInch(this, edtInchstr)
                        }
                        LocalDB.setString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
                        Log.e("TAG", "setHeightWeightDialog::::innnn " + LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ""))
                    } else {
                        var inch = getDecimal(edCM.text.toString().toDouble())

                        LocalDB.setLastInputFoot(this, CommonUtility.calcInchToFeet(inch.toDouble()))
                        LocalDB.setLastInputInch(this, CommonUtility.calcInFromInch(inch.toDouble()).toFloat())
                        LocalDB.setString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_CM)
                        LocalDB.setString(this, ConstantString.CENTI_METER, inch)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {


                    var strKG: Float
                    if (boolKg) {
                        strKG = edWeight.text.toString().toFloat()
                        strKG = getDecimal(strKG.toDouble()).toFloat()
                        LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                        LocalDB.setLastInputWeight(this, strKG)
                    } else {
                        strKG = Math.round(CommonUtility.LbToKg(edWeight.text.toString().toDouble())).toFloat()
                        strKG = getDecimal(strKG.toDouble()).toFloat()
                        LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                        LocalDB.setLastInputWeight(this, strKG)
                    }

                    val currentDate = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())

                    val b = dbHelper.updateWeight(currentDate, strKG.toString(), "")

                    Log.e("TAG", "setHeightWeightDialog:::curentdate::::CompleteScreen  " + currentDate + "   " + dbHelper.weightExistOrNot(currentDate) + "  " + b)

                    setWeightValues()

                    setBmiCalculation()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                dialog.dismiss()
            }

        }

        dialog.show()
    }


/*    // Todo save user history
    private fun saveData() {

        *//*if (edWeight.text.toString().isEmpty()) {
            finishData()
        } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG) == ConstantString.DEF_KG
                && (edWeight.text.toString().toFloat() < ConstantString.MIN_KG || edWeight.text.toString()
                        .toFloat() > ConstantString.MAX_KG)
        ) {
            Toast.makeText(this, "Please enter proper weight in KG", Toast.LENGTH_LONG).show()
        } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG) == ConstantString.DEF_LB
                && (edWeight.text.toString().toFloat() < ConstantString.MIN_LB || edWeight.text.toString()
                        .toFloat() > ConstantString.MAX_LB)
                && (edWeight.text.toString().toFloat() != ConstantString.MAX_LB.toFloat() || edWeight.text.toString()
                        .toFloat() != ConstantString.MIN_LB.toFloat())
        ) {
            Toast.makeText(this, "Please enter proper weight in LB", Toast.LENGTH_LONG).show()
        } else {
            finishData()
        }*/
    /*
        val edtWeight: Float
        if (edWeight.text.toString().isEmpty().not()) {
            edtWeight = getDecimal(edWeight.text.toString().toDouble()).toFloat()


            if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)
                    == ConstantString.DEF_KG
            ) {
                LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                if (edtWeight.toString().isEmpty()) {
                    LocalDB.setLastInputWeight(this, 0f)
                } else {
                    LocalDB.setLastInputWeight(this, edtWeight)
                }
            } else {
                LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                if (edtWeight.toString().isEmpty()) {
                    LocalDB.setLastInputWeight(this, 0f)
                } else {
                    LocalDB.setLastInputWeight(this, Math.round(CommonUtility.LbToKg(edtWeight.toDouble())).toFloat())
                }
            }
            finishData()
        }
        else{
            finishData()
        }
    }

    fun finishData() {
        val dbHelper = DataHelper(this)

        dbHelper.addHistory(ConstantString.pWorkOutCategory.catDefficultyLevel,
                ConstantString.pWorkOutCategory.catName,
                CommonUtility.getCurrentTimeStamp(),
                CommonUtility.timeToSecond(txtDurationTime.text.toString()).toString(),
                CommonUtility.getStringFormat(calValue),
                txtTotalNoOfLevel.text.toString(),
                LocalDB.getLastInputWeight(this).toString(),
                LocalDB.getLastInputFoot(this).toString(),
                LocalDB.getLastInputInch(this).toString(),
                feelRate,
                strDayName,
                strWeekName
        )

        if (ConstantString.pWorkOutCategory.catTableName != "" && strDayName != "" && strWeekName != "") {
            dbHelper.updateFullWorkoutDay(strDayName, strWeekName, ConstantString.pWorkOutCategory.catTableName)
        }

        LocalDB.setLastUnCompletedExPos(this, table_name, workout_id, 0)

        finish()



    }*/


    private fun saveData() {


        try {
            if (edWeight.text.toString().isEmpty()) {
                finishData()
            } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)
                    == ConstantString.DEF_KG
                    && (edWeight.text.toString().toFloat() < ConstantString.MIN_KG || edWeight.text.toString()
                            .toFloat() > ConstantString.MAX_KG)
            ) {
                Toast.makeText(this, "Please enter proper weight in KG", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)
                    == ConstantString.DEF_LB
                    && (edWeight.text.toString().toFloat() < ConstantString.MIN_LB || edWeight.text.toString()
                            .toFloat() > ConstantString.MAX_LB)
                    && (edWeight.text.toString().toFloat() != ConstantString.MAX_LB.toFloat() || edWeight.text.toString()
                            .toFloat() != ConstantString.MIN_LB.toFloat())
            ) {
                Toast.makeText(this, "Please enter proper weight in LB", Toast.LENGTH_LONG).show()
            } else {
                finishData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun finishData() {
        val edtWeight: Float
        if (edWeight.text.toString().isEmpty().not()) {
            edtWeight = getDecimal(edWeight.text.toString().toDouble()).toFloat()


            if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)
                    == ConstantString.DEF_KG
            ) {
                LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                if (edtWeight.toString().isEmpty()) {
                    LocalDB.setLastInputWeight(this, 0f)
                } else {
                    LocalDB.setLastInputWeight(this, edtWeight)
                }
            } else {
                LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                if (edtWeight.toString().isEmpty()) {
                    LocalDB.setLastInputWeight(this, 0f)
                } else {
                    LocalDB.setLastInputWeight(this, Math.round(CommonUtility.LbToKg(edtWeight.toDouble())).toFloat())
                }
            }
            loadData()
        }
        else{
            loadData()
        }

    }

    fun loadData(){
        val dbHelper = DataHelper(this)

        dbHelper.addHistory(ConstantString.pWorkOutCategory.catDefficultyLevel,
                ConstantString.pWorkOutCategory.catName,
                CommonUtility.getCurrentTimeStamp(),
                CommonUtility.timeToSecond(txtDurationTime.text.toString()).toString(),
                CommonUtility.getStringFormat(calValue),
                txtTotalNoOfLevel.text.toString(),
                LocalDB.getLastInputWeight(this).toString(),
                LocalDB.getLastInputFoot(this).toString(),
                LocalDB.getLastInputInch(this).toString(),
                feelRate,
                strDayName,
                strWeekName
        )

        if (ConstantString.pWorkOutCategory.catTableName != "" && strDayName != "" && strWeekName != "") {
            dbHelper.updateFullWorkoutDay(strDayName, strWeekName, ConstantString.pWorkOutCategory.catTableName)
        }

        LocalDB.setLastUnCompletedExPos(this, table_name, workout_id, 0)

        finish()

    }


    override fun onAdClosed() {
        finish()
    }

    override fun onAdFail() {
        finish()
    }

    fun interstitialAds() {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            CDInstrialAds.showAds()
        } else {
            onAdFail()
        }
    }

}
