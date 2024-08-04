package com.arrowwould.workout.man.report

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat

import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker

import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.ads.AdSize
import com.arrowwould.workout.man.Const
import com.arrowwould.workout.man.MyInterstitialAds_abc
import com.arrowwould.workout.man.R
import com.arrowwould.workout.man.activity.BaseActivity
import com.arrowwould.workout.man.database.DataHelper
import com.arrowwould.workout.man.history.HistoryActivity
import com.arrowwould.workout.man.utils.*
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.activity_report.adMobView
import kotlinx.android.synthetic.main.activity_report.imgBack
import kotlinx.android.synthetic.main.activity_workout_list_details.*
import org.joda.time.DateTime

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ReportActivity : BaseActivity() , MyInterstitialAds_abc.OnInterstitialAdListnear{
    private lateinit var CDInstrialAds: MyInterstitialAds_abc
    lateinit var dbHelper: DataHelper
    lateinit var context: Context
    private var count = 0
    lateinit var daysText: ArrayList<String>
    lateinit var daysYearText: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        CDInstrialAds = MyInterstitialAds_abc(this, this)


        context = this
        dbHelper = DataHelper(context)

        init()
        initAction()

        setBannerAdd(adMobView)


    }

    override fun onBackPressed() {

        if (sessionManager.getBooleanValue(Const.Adshow)) {
            CDInstrialAds.showAds()
        } else {
            onAdFail()
        }

    }


    // Todo Common methods
    private fun init() {
        setupGraph()
        setWeightValues()
        setBmiCalculation()

        txtTotalWorkouts.text = dbHelper.getHistoryTotalWorkout().toString()
        txtTotalKcal.text = dbHelper.getHistorytotalKcal().toInt().toString()
        txtTotalMinutes.text = ((dbHelper.getHistorytotalMinutes() / 60).toDouble()).roundToInt().toString()
//        (dbHelper.getHistorytotalMinutes() / 60).toDouble().roundToInt()
        rcyHistoryWeek.adapter = WeekDayReportAdapter(this)

    }

    private fun initAction() {

        imgBack.setOnClickListener { finish() }

        ivAddGraph.setOnClickListener { setWeightDialog() }

        btnEdit.setOnClickListener { setHeightWeightDialog() }

        btnEditHeight.setOnClickListener { setHeightWeightDialog() }

        txtRecord.setOnClickListener {
            val intent = Intent(this,HistoryActivity::class.java)
            intent.putExtra(ConstantString.HISTORY_FROM,true)
            startActivity(intent)
            finish()
        }

        txtHistoryMore.setOnClickListener {
            val intent = Intent(this,HistoryActivity::class.java)
            intent.putExtra(ConstantString.HISTORY_FROM,true)
            startActivity(intent)
            finish()
        }

    }

    fun parseTime(time: Long, pattern: String): String {
        val sdf = SimpleDateFormat(
                pattern,
                Locale.getDefault()
        )
        return sdf.format(Date(time))
    }

/*    *//* Todo Add weight dialog *//*
    private fun setWeightDialog() {

        var boolKg = true
        var dateSelect: String = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())

        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_weight_set_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dtpWeightSet = dialog.findViewById<HorizontalPicker>(R.id.dtpWeightSet)
        val edKG = dialog.findViewById<EditText>(R.id.edKG)
        val txtUnit = dialog.findViewById<TextView>(R.id.txtUnit)
        val txtLB = dialog.findViewById(R.id.txtLB) as TextView
        val btnChooseUnit = dialog.findViewById<Button>(R.id.btnChooseUnit)
        val btnCancel = dialog.findViewById<Button>(R.id.btnOkay)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)

        if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG) {
            boolKg = true

            edKG.setText(LocalDB.getLastInputWeight(this).toString())

            txtUnit.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

            txtUnit.background = resources.getDrawable(R.drawable.ract_theme_select, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)
        } else {
            boolKg = false

            edKG.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(this).toDouble())))

            txtUnit.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

            txtUnit.background = resources.getDrawable(R.drawable.ract_gray, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)
        }

        dtpWeightSet
                .setDays(369)
                .setOffset(365)
                .setListener { dateSelected ->
                    dateSelect = DateUtils.getDate(dateSelected.toDate().time, Locale.getDefault())

//                Toast.makeText(context, "Selected date is ${DateUtils.getDate(dateSelect.toDate().time, Locale.getDefault())}", Toast.LENGTH_SHORT).show()
                }
                .showTodayButton(false)
                .init()

        dtpWeightSet.setDate(DateTime.now())

        btnChooseUnit.setOnClickListener {
            dialog.cancel()

            dlUnitDialog(true)
        }

        txtLB.setOnClickListener {
            if (boolKg) {
                boolKg = false

                txtUnit.setTextColor(resources.getColor(R.color.colorBlack))
                txtLB.setTextColor(resources.getColor(R.color.colorWhite))

                txtUnit.background = resources.getDrawable(R.drawable.ract_gray, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                edKG.hint = "LB"

                if (edKG.text.toString() != "") {
                    edKG.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(edKG.text.toString().toDouble())))
                }
            }
        }

        txtUnit.setOnClickListener {
            if (!boolKg) {
                boolKg = true

                txtUnit.setTextColor(resources.getColor(R.color.colorWhite))
                txtLB.setTextColor(resources.getColor(R.color.colorBlack))

                txtUnit.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)

                edKG.hint = "KG"

                if (edKG.text.toString() != "") {
                    edKG.setText(CommonUtility.getStringFormat(CommonUtility.LbToKg(edKG.text.toString().toDouble())))
                }
            }
        }

        btnCancel.setOnClickListener {
            dialog.cancel()
        }

        btnSave.setOnClickListener {

            val strKG: Float

            if (boolKg) {
                strKG = edKG.text.toString().toFloat()
                LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                LocalDB.setLastInputWeight(this, strKG)
            } else {
                strKG = Math.round(CommonUtility.LbToKg(edKG.text.toString().toDouble())).toFloat()
                LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                LocalDB.setLastInputWeight(this, strKG)
            }

            if (dbHelper.weightExistOrNot(dateSelect)) {
                if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG) {
                    dbHelper.updateWeight(dateSelect, strKG.toString(), "")
                } else {
                    dbHelper.updateWeight(dateSelect, Math.round(CommonUtility.LbToKg(strKG.toString().toDouble())).toString(), "")
                }
            } else {
                if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG) {
                    dbHelper.addUserWeight(strKG.toString(), dateSelect, "")
                } else {
                    dbHelper.addUserWeight(Math.round(CommonUtility.LbToKg(strKG.toString().toDouble())).toString(), dateSelect, "")
                }
            }

            setupGraph()
            setWeightValues()
            setBmiCalculation()

            dialog.cancel()
        }

        dialog.show()
    }*/

    private fun setWeightDialog() {

        var boolKg: Boolean
//        var dateSelect: String = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())
        var dateSelect: String = parseTime(Date().time, ConstantString.WEIGHT_TABLE_DATE_FORMAT)

        Log.e("TAG", "setWeightDialog:Dataatat  $dateSelect")
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_weight_set_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dtpWeightSet = dialog.findViewById<HorizontalPicker>(R.id.dtpWeightSet)
        val edKG = dialog.findViewById<EditText>(R.id.edKG)
        val txtUnit = dialog.findViewById<TextView>(R.id.txtUnit)
        val txtLB = dialog.findViewById(R.id.txtLB) as TextView
        val btnChooseUnit = dialog.findViewById<Button>(R.id.btnChooseUnit)
        val btnCancel = dialog.findViewById<Button>(R.id.btnOkay)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)

//        if (LocalDB.getWeightUnit(this
//        ) == CommonString.DEF_KG) {
        if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG) == ConstantString.DEF_KG) {
            boolKg = true

            edKG.setText(LocalDB.getLastInputWeight(this).toString())

            txtUnit.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
            edKG.hint = "KG"
            txtUnit.background = resources.getDrawable(R.drawable.ract_theme_select, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)
            LocalDB.setString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)
        } else {
            boolKg = false

            edKG.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(this).toDouble())))
            edKG.hint = "LB"

            txtUnit.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
            txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

            txtUnit.background = resources.getDrawable(R.drawable.ract_gray, null)
            txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)
            LocalDB.setString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_LB)
        }


        dtpWeightSet.performClick()

        dtpWeightSet
                .setDays(369)
                .setOffset(365)
                .setListener { dateSelected ->
                    dateSelect = parseTime(dateSelected.toDate().time, ConstantString.WEIGHT_TABLE_DATE_FORMAT)
                    Log.e("TAG", "setWeightDialog::::dateSelect :::  $dateSelect")
                }
                .showTodayButton(false)
                .init()

        dtpWeightSet.setDate(DateTime.now())

        btnChooseUnit.setOnClickListener {
            dialog.cancel()

            dlUnitDialog(true)
        }

        txtLB.setOnClickListener {
            LocalDB.setString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_LB)
            try {
                if (boolKg) {
                    boolKg = false

                    txtUnit.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
                    txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))

                    txtUnit.background = resources.getDrawable(R.drawable.ract_gray, null)
                    txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                    edKG.hint = "LB"


                    if (edKG.text.toString() != "") {
                        try {
                            edKG.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(edKG.text.toString().toDouble())))
                            Log.e("TAG", "setWeightDialog:::::LBBB "+CommonUtility.getStringFormat(CommonUtility.KgToLb(edKG.text.toString().toDouble())) )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    edKG.setSelection(CommonUtility.getStringFormat(CommonUtility.KgToLb(edKG.text.toString().toDouble())).length)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        txtUnit.setOnClickListener {
            LocalDB.setString(this, ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)
            try {
                if (!boolKg) {
                    boolKg = true

                    txtUnit.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                    txtLB.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

                    txtUnit.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                    txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)

                    edKG.hint = "KG"


                    if (edKG.text.toString() != "") {
                        try {
                            edKG.setText(CommonUtility.getStringFormat(CommonUtility.LbToKg(edKG.text.toString().toDouble())))
                            Log.e("TAG", "setWeightDialog::::KGGG "+CommonUtility.getStringFormat(CommonUtility.LbToKg(edKG.text.toString().toDouble())) )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    edKG.setSelection(CommonUtility.getStringFormat(CommonUtility.LbToKg(edKG.text.toString().toDouble())).length)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnCancel.setOnClickListener {
            try {
                dialog.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnSave.setOnClickListener {
            if (edKG.text.toString().isEmpty()) {
                Toast.makeText(this, "Please fill the field", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, "") == ConstantString.DEF_KG
                    && (edKG.text.toString().toFloat() < ConstantString.MIN_KG || edKG.text.toString().toFloat() > ConstantString.MAX_KG)
            ) {
                Toast.makeText(this, "Please enter proper weight in KG", Toast.LENGTH_LONG).show()
            } else if (LocalDB.getString(this, ConstantString.PREF_KG_LB_UNIT, "") == ConstantString.DEF_LB
                    && (edKG.text.toString().toFloat() < ConstantString.MIN_LB || edKG.text.toString().toFloat() > ConstantString.MAX_LB)
                    && (edKG.text.toString().toFloat() != ConstantString.MAX_LB.toFloat() || edKG.text.toString()
                            .toFloat() != ConstantString.MIN_LB.toFloat())
            ) {
                Toast.makeText(this, "Please enter proper weight in LB", Toast.LENGTH_LONG).show()
            } else {
                try {
                    var strKG: Float

                    if (boolKg) {
                        strKG = edKG.text.toString().toFloat()
                        strKG = getDecimal(strKG.toDouble()).toFloat()
                        LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                        LocalDB.setLastInputWeight(this, strKG)
                    } else {
                        strKG = CommonUtility.LbToKg(edKG.text.toString().toDouble()).roundToInt().toFloat()
                        strKG = getDecimal(strKG.toDouble()).toFloat()
                        LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                        LocalDB.setLastInputWeight(this, strKG)
                    }

                    if (dbHelper.weightExistOrNot(dateSelect)) {
                        if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG) {
                            dbHelper.updateWeight(dateSelect, strKG.toString(), "")
                        } else {
                            dbHelper.updateWeight(dateSelect, CommonUtility.LbToKg(strKG.toString().toDouble()).roundToInt().toString(), "")
                        }
                    } else {
                        if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG) {
                            dbHelper.addUserWeight(strKG.toString(), dateSelect, "")
                        } else {
                            dbHelper.addUserWeight(CommonUtility.LbToKg(strKG.toString().toDouble()).roundToInt().toString(), dateSelect, "")
                        }
                    }

                    setupGraph()
                    setWeightValues()
                    setBmiCalculation()

                    dialog.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        dialog.show()
    }

    private fun dlUnitDialog(boolIsWeight: Boolean) {

        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_weight_unit)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val txt_dl_dialog = dialog.findViewById(R.id.txt_dl_dialog) as TextView
        val rbDlWeight1 = dialog.findViewById(R.id.rbDlWeight1) as RadioButton
        val rbDlWeight2 = dialog.findViewById(R.id.rbDlWeight2) as RadioButton
        val rgDlWeight = dialog.findViewById(R.id.rgDlWeight) as RadioGroup

        if (boolIsWeight) {

            if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                rbDlWeight2.isChecked = true
            } else {
                rbDlWeight1.isChecked = true
            }

            txt_dl_dialog.text = "Select your weight unit"
            rbDlWeight1.text = ConstantString.DEF_LB
            rbDlWeight2.text = ConstantString.DEF_KG

        } else {

            if (LocalDB.getHeightUnit(context) == ConstantString.DEF_IN) {
                rbDlWeight2.isChecked = true
            } else {
                rbDlWeight1.isChecked = true
            }

            txt_dl_dialog.text = "Select your height unit"
            rbDlWeight1.text = ConstantString.DEF_CM
            rbDlWeight2.text = ConstantString.DEF_IN

        }

        rgDlWeight.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rbDlWeight1) {
                if (boolIsWeight) {
                    LocalDB.setWeightUnit(context, ConstantString.DEF_LB)

                } else {
                    LocalDB.setHeightUnit(context, ConstantString.DEF_CM)
                }
            } else if (checkedId == R.id.rbDlWeight2) {
                if (boolIsWeight) {
                    LocalDB.setWeightUnit(context, ConstantString.DEF_KG)
                } else {
                    LocalDB.setHeightUnit(context, ConstantString.DEF_IN)
                }
            }
            dialog.dismiss()

            setWeightDialog()
        }

        dialog.show()
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

                txtKG.setTextColor(resources.getColor(R.color.colorWhite))
                txtLB.setTextColor(resources.getColor(R.color.colorBlack))

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

                txtKG.setTextColor(resources.getColor(R.color.colorBlack))
                txtLB.setTextColor(resources.getColor(R.color.colorWhite))

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

                txtIN.setTextColor(resources.getColor(R.color.colorBlack))
                txtCM.setTextColor(resources.getColor(R.color.colorWhite))

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

            txtIN.setTextColor(resources.getColor(R.color.colorWhite))
            txtCM.setTextColor(resources.getColor(R.color.colorBlack))

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

            setupGraph()

            setWeightValues()

            setBmiCalculation()

            dialog.dismiss()
        }

        dialog.show()
    }*/


    private fun setHeightWeightDialog() {

        var boolKg: Boolean
        var boolInch: Boolean

        val dialog = Dialog(this
        )
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
        try {
//            if (LocalDB.getWeightUnit(this
//            ) == CommonString.DEF_KG) {
            if (LocalDB.getString(this
                            , ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG) == ConstantString.DEF_LB) {
                boolKg = false

                edWeight.setText(CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(this
                ).toDouble())))

                txtKG.setTextColor(ContextCompat.getColor(this
                        , R.color.colorBlack))
                txtLB.setTextColor(ContextCompat.getColor(this
                        , R.color.colorWhite))

                txtKG.background = resources.getDrawable(R.drawable.ract_gray, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_theme_select, null)

            } else {

                edWeight.setText(LocalDB.getLastInputWeight(this
                ).toString())

                txtKG.setTextColor(ContextCompat.getColor(this
                        , R.color.colorWhite))
                txtLB.setTextColor(ContextCompat.getColor(this
                        , R.color.colorBlack))

                txtKG.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                txtLB.background = resources.getDrawable(R.drawable.ract_gray, null)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        boolInch = true
        try {
//            if (LocalDB.getHeightUnit(this
//            ) == ConstantString.DEF_IN) {
            if (LocalDB.getString(this
                            , ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN) == ConstantString.DEF_IN) {

                edCM.visibility = View.INVISIBLE
                lnyInch.visibility = View.VISIBLE

                txtIN.setTextColor(ContextCompat.getColor(this
                        , R.color.colorWhite))
                txtCM.setTextColor(ContextCompat.getColor(this
                        , R.color.colorBlack))

                txtIN.background = resources.getDrawable(R.drawable.ract_theme_select, null)
                txtCM.background = resources.getDrawable(R.drawable.ract_gray, null)

                edFeet.setText(LocalDB.getLastInputFoot(this
                ).toString())
                edInch.setText(LocalDB.getLastInputInch(this
                ).toString())
                LocalDB.setString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
            } else {
                boolInch = false

                edCM.visibility = View.VISIBLE
                lnyInch.visibility = View.INVISIBLE

                txtIN.setTextColor(ContextCompat.getColor(this
                        , R.color.colorBlack))
                txtCM.setTextColor(ContextCompat.getColor(this
                        , R.color.colorWhite))

                txtIN.background = resources.getDrawable(R.drawable.ract_gray, null)
                txtCM.background = resources.getDrawable(R.drawable.ract_theme_select, null)

                val inch = CommonUtility.ftInToInch(LocalDB.getLastInputFoot(this
                ), LocalDB.getLastInputInch(this
                ).toDouble())

//                edCM.setText(CommonUtility.inchToCm(inch).roundToInt().toDouble().toString())
                edCM.setText(LocalDB.getString(this,ConstantString.CENTI_METER,""))
                LocalDB.setString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_CM)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        txtKG.setOnClickListener {

            LocalDB.setString(this
                    , ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_KG)
            try {
                if (!boolKg) {
                    boolKg = true

                    txtKG.setTextColor(ContextCompat.getColor(this
                            , R.color.colorWhite))
                    txtLB.setTextColor(ContextCompat.getColor(this
                            , R.color.colorBlack))

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
            LocalDB.setString(this
                    , ConstantString.PREF_KG_LB_UNIT, ConstantString.DEF_LB)
            try {
                if (boolKg) {
                    boolKg = false

                    txtKG.setTextColor(ContextCompat.getColor(this
                            , R.color.colorBlack))
                    txtLB.setTextColor(ContextCompat.getColor(this
                            , R.color.colorWhite))

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

                    txtIN.setTextColor(ContextCompat.getColor(this
                            , R.color.colorBlack))
                    txtCM.setTextColor(ContextCompat.getColor(this
                            , R.color.colorWhite))

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



            LocalDB.setString(this
                    , ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_CM)
        }

        txtIN.setOnClickListener {

            LocalDB.setString(this
                    , ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)


            try {
                edCM.visibility = View.INVISIBLE
                lnyInch.visibility = View.VISIBLE

                txtIN.setTextColor(ContextCompat.getColor(this
                        , R.color.colorWhite))
                txtCM.setTextColor(ContextCompat.getColor(this
                        , R.color.colorBlack))

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
                Toast.makeText(this, "Please enter Weight", Toast.LENGTH_LONG).show()
            } else if (edCM.text.toString().isEmpty() && LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
                    == ConstantString.DEF_CM) {
                Toast.makeText(this, "Please enter Height", Toast.LENGTH_LONG).show()
            } else if (edFeet.text.toString().isEmpty() && LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
                    == ConstantString.DEF_IN
            ) {
                Toast.makeText(this, "Please enter Height", Toast.LENGTH_LONG).show()
            } else if (edInch.text.toString().isEmpty() && LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
                    == ConstantString.DEF_IN
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
            }
            else {
                Log.e("TAG", "setHeightWeightDialog::boolInchboolInch  $boolInch")
                try {

                    if (boolInch) {
                        var edtInchstr :Float = getDecimal(edInch.text.toString().toDouble()).toFloat()
                        edtInchstr = getDecimal(edtInchstr.toDouble()).toFloat()
                        if (edtInchstr > 12.0) {
                            val totalInch = edtInchstr - 12.0
                            LocalDB.setLastInputFoot(this
                                    , (edFeet.text.toString().toInt() + 1))
                            if (edFeet.text.toString().toInt() == 12 && edtInchstr > 13.5) {
                                LocalDB.setLastInputInch(this
                                        , 1.5.toFloat())
                            } else {
                                LocalDB.setLastInputInch(this
                                        , totalInch.toFloat())
                            }
                        } else {
                            LocalDB.setLastInputFoot(this
                                    , edFeet.text.toString().toInt())
                            LocalDB.setLastInputInch(this
                                    , edtInchstr)
                        }
                        LocalDB.setString(this
                                , ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_IN)
                        Log.e("TAG", "setHeightWeightDialog::::innnn " + LocalDB.getString(this
                                , ConstantString.PREF_IN_CM_UNIT, ""))
                    } else {
//                        val inch = CommonUtility.getStringFormat(CommonUtility.cmToInch(edCM.text.toString().toDouble()))
                        var inch = getDecimal(edCM.text.toString().toDouble())
                        LocalDB.setLastInputFoot(this, CommonUtility.calcInchToFeet(inch.toDouble()))
                        LocalDB.setLastInputInch(this, CommonUtility.calcInFromInch(inch.toDouble()).toFloat())
                        LocalDB.setHeightUnit(this, ConstantString.DEF_CM)
                        LocalDB.setString(this, ConstantString.PREF_IN_CM_UNIT, ConstantString.DEF_CM)
//                        LocalDB.setString(this, ConstantString.CENTI_METER, edCM.text.toString())
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
                        LocalDB.setWeightUnit(this
                                , ConstantString.DEF_KG)
                        LocalDB.setWeightUnit(this
                                , ConstantString.DEF_KG)
                        LocalDB.setLastInputWeight(this
                                , strKG)
                    } else {
                        strKG = CommonUtility.LbToKg(edWeight.text.toString().toDouble()).roundToInt().toFloat()
                        strKG = getDecimal(strKG.toDouble()).toFloat()
                        LocalDB.setWeightUnit(this
                                , ConstantString.DEF_LB)
                        LocalDB.setWeightUnit(this
                                , ConstantString.DEF_LB)
                        LocalDB.setLastInputWeight(this
                                , strKG)
                    }

                    val currentDate = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())

                    if (dbHelper.weightExistOrNot(currentDate)) {
                        dbHelper.updateWeight(currentDate, strKG.toString(), "")
                    } else {
                        dbHelper.addUserWeight(strKG.toString(), currentDate, "")
                    }


                    if (boolKg) {
                        LocalDB.setLastInputWeight(this
                                , edWeight.text.toString().toFloat())
                    } else {
                        LocalDB.setLastInputWeight(this
                                , CommonUtility.LbToKg(edWeight.text.toString().toDouble()).toFloat())
                    }

                    setupGraph()

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

    private fun setWeightValues() {
        if (LocalDB.getLastInputWeight(context) != 0f) {
            if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                txtCurrentKg.text =
                        CommonUtility.getStringFormat(LocalDB.getLastInputWeight(context).toDouble())
                                .plus(" ${ConstantString.DEF_KG}")
            } else {
                txtCurrentKg.text =
                        CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(context).toDouble()))
                                .plus(" ${ConstantString.DEF_LB}")
            }
        }

        if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
            txtSelWeightUnit.text = ConstantString.DEF_KG
            txtCurrentKg.text = LocalDB.getLastInputWeight(context).toString().plus(" ${ConstantString.DEF_KG}")
        } else {
            txtSelWeightUnit.text = ConstantString.DEF_LB
            txtCurrentKg.text =
                    CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(context).toDouble()))
                            .plus(" ${ConstantString.DEF_LB}")
        }

        val maxWeight = dbHelper.getMaxWeight().toFloat()
        val minWeight = dbHelper.getMinWeight().toFloat()

        if (maxWeight > 0) {
            if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                txtHeaviestKg.text = maxWeight.toString().plus(" ${ConstantString.DEF_KG}")
            } else {
                txtHeaviestKg.text = CommonUtility.getStringFormat(CommonUtility.KgToLb(maxWeight.toDouble()))
                        .plus(" ${ConstantString.DEF_LB}")
            }
        } else {
            if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                txtHeaviestKg.text = LocalDB.getLastInputWeight(context).toString().plus(" ${ConstantString.DEF_KG}")
            } else {
                txtHeaviestKg.text =
                        CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(context).toDouble()))
                                .plus(" ${ConstantString.DEF_LB}")
            }
        }

        if (minWeight > 0) {
            if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                txtLightestKg.text = minWeight.toString().plus(" ${ConstantString.DEF_KG}")
            } else {
                txtLightestKg.text = CommonUtility.getStringFormat(CommonUtility.KgToLb(minWeight.toDouble()))
                        .plus(" ${ConstantString.DEF_LB}")
            }
        } else {
            if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                txtLightestKg.text = LocalDB.getLastInputWeight(context).toString().plus(" ${ConstantString.DEF_KG}")
            } else {
                txtLightestKg.text =
                        CommonUtility.getStringFormat(CommonUtility.KgToLb(LocalDB.getLastInputWeight(context).toDouble()))
                                .plus(" ${ConstantString.DEF_KG}")
            }
        }

        if (txtHeaviestKg.text.toString().replace(" KG", "").replace(" LB", "").toFloat() > 0f) {
            chartWeight.getAxisLeft().setAxisMaximum(txtHeaviestKg.text.toString().replace(" KG", "").replace(" LB", "").toFloat() + 10f)
        } else {
            chartWeight.getAxisLeft().setAxisMaximum(240f)
        }

        if (txtLightestKg.text.toString().replace(" KG", "").replace(" LB", "").toFloat() > 0f) {
            chartWeight.getAxisLeft().setAxisMinimum(txtLightestKg.text.toString().replace(" KG", "").replace(" LB", "").toFloat() - 10f)
        } else {
            chartWeight.getAxisLeft().setAxisMinimum(30f)
        }

      /*  tvHeight.text =
                LocalDB.getLastInputFoot(context).toString().plus(" ${ConstantString.DEF_FT} ")
                        .plus(LocalDB.getLastInputInch(context))
                        .plus(" ${ConstantString.DEF_IN}")
*/

        if (LocalDB.getString(this, ConstantString.PREF_IN_CM_UNIT, "") == ConstantString.DEF_CM) {
            tvHeight.text = LocalDB.getString(this, ConstantString.CENTI_METER, "") + " CM"
        } else {
            tvHeight.text =
                    LocalDB.getLastInputFoot(this).toString().plus(" ${ConstantString.DEF_FT} ").plus(LocalDB.getLastInputInch(this))
                            .plus(" ${ConstantString.DEF_IN}")
        }
    }

    /* Todo Graph data generated methods */
    private fun setupGraph() {

        val format = SimpleDateFormat("dd/MM", Locale.getDefault())
        val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)

        calendar.time = formatDate.parse("$year-01-01")

        count = isLeapYear(year) + 1
        daysText = ArrayList()
        daysYearText = ArrayList()

        for (i in 0 until count) {
            daysText.add(format.format(calendar.time))
            daysYearText.add(formatDate.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }

        chartWeight.setDrawOrder(arrayOf(CombinedChart.DrawOrder.LINE))

        chartWeight.getDescription().setEnabled(false)
        chartWeight.description.text = "Date"
        chartWeight.setNoDataText(resources.getString(R.string.app_name))
        chartWeight.setBackgroundColor(Color.WHITE)
        chartWeight.setDrawGridBackground(false)
        chartWeight.setDrawBarShadow(false)
        chartWeight.isHighlightFullBarEnabled = false

        val l = chartWeight.getLegend()
        l.setWordWrapEnabled(false)
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        l.setDrawInside(false)

        val leftAxis = chartWeight.getAxisLeft()
        leftAxis.setDrawGridLines(true)

        val rightAxis = chartWeight.getAxisRight()
//        rightAxis.setDrawGridLines(false)
//        rightAxis.setAxisMinimum(30f)
//        rightAxis.setAxisMaximum(240f)
//        rightAxis.setLabelCount(10)
        rightAxis.setEnabled(false)

        val xAxis = chartWeight.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setAxisMinimum(0f)
        xAxis.setAxisMaximum(count.toFloat())
        xAxis.granularity = 1f
        xAxis.labelCount = 30

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                //                return daysText[(int) value % daysText.length];
                return if (value < daysText.size && value > 0) {
                    daysText[value.toInt()]
                } else ""
            }
        }

        val data = CombinedData()
        data.setData(generateLineData())

        data.setValueTypeface(Typeface.DEFAULT)
        chartWeight.setData(data)

        chartWeight.setVisibleXRange(5f, 8f)

        val strDate = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())
        val position = daysYearText.indexOf(strDate)
        chartWeight.centerViewTo(position.toFloat(), 50f, YAxis.AxisDependency.LEFT)

        setGraphTouch()

        chartWeight.invalidate()
    }

    private fun generateLineData(): LineData {

        val yAxisData = dbHelper.getUserWeightData()
        val d = LineData()

        val entries = ArrayList<Entry>()
        if (yAxisData.size > 0) {
            for (index in 0 until yAxisData.size) {
//            yAxisData[index]["KG"]
                val strDate = yAxisData[index]["DT"]
                val position = daysYearText.indexOf(strDate)
                if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                    entries.add(Entry(position.toFloat(), yAxisData[index]["KG"]!!.toFloat()))
                } else {
                    entries.add(
                            Entry(
                                    position.toFloat(),
                                    CommonUtility.KgToLb(yAxisData[index]["KG"]!!.toDouble()).toFloat()
                            )
                    )
                }
            }
        } else {
            if (LocalDB.getLastInputWeight(context) > 0) {
                val strDate = CommonUtility.convertFullDateToDate(CommonUtility.getCurrentTimeStamp())
                val position = daysYearText.indexOf(strDate)
                if (LocalDB.getWeightUnit(context) == ConstantString.DEF_KG) {
                    entries.add(Entry(position.toFloat(), LocalDB.getLastInputWeight(context)))
                } else {
                    entries.add(
                            Entry(
                                    position.toFloat(),
                                    CommonUtility.KgToLb(LocalDB.getLastInputWeight(context).toDouble()).toFloat()
                            )
                    )
                }
            }
        }

        val set = LineDataSet(entries, "Date")
        set.color = Color.rgb(130, 87, 242)
        set.lineWidth = 1.5f
        set.setCircleColor(Color.rgb(130, 130, 130))
        set.circleRadius = 5f

//        set.fillColor = Color.rgb(130, 130, 130)

        set.fillColor = Color.rgb(130, 130, 130)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawValues(true)
        set.valueTextSize = 10f
        set.valueTextColor = Color.rgb(130, 87, 242)

        set.axisDependency = YAxis.AxisDependency.LEFT
        d.addDataSet(set)

        return d
    }

    private fun setGraphTouch() {

        /*chartWeight.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    chartWeight.parent.requestDisallowInterceptTouchEvent(true)
            }
            false
        }*/

    }

    private fun isLeapYear(year: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR)
    }

    /* Todo set bmi calculation */
    private fun setBmiCalculation() {
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

        txtBmiTitleValue.text = ": ".plus(String.format("%.2f", bmiValue))
        txtBmiGrade.text = CommonUtility.getStringFormat(bmiValue)
        txtWeightString.text = CommonUtility.bmiWeightString(String.format("%.1f", bmiValue).toFloat())
        txtWeightString.setTextColor(ColorStateList.valueOf(CommonUtility.bmiWeightTextColor(this, bmiValue.toFloat())))
        blankView1.layoutParams = param

    }

    override fun onAdClosed() {
        finish()
    }

    override fun onAdFail() {
        finish()
    }


    override fun setBannerAdd(adContainer: View) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = sessionManager.getStringValue(Const.bannerAdId)
            (adContainer as RelativeLayout).addView(adView)
            adView.loadAd(adRequest)
        }
    }

}
