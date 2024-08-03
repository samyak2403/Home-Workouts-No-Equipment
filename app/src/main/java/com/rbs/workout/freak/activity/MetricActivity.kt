package com.rbs.workout.freak.activity

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.rbs.workout.freak.R
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_metric.*
import kotlinx.android.synthetic.main.activity_metric.llAdView
import kotlinx.android.synthetic.main.activity_metric.llAdViewFacebook

class MetricActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metric)

        init()
        initAction()

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

    // Todo Common methods
    private fun init() {

        txtWeightValue.text = LocalDB.getWeightUnit(this)
        txtHeightValue.text = LocalDB.getHeightUnit(this)

    }

    private fun initAction() {
        imgBack.setOnClickListener {
            finish()
        }

        llWeight.setOnClickListener {
            dlUnitDialog(true)
        }

        llHeight.setOnClickListener {
            dlUnitDialog(false)
        }
    }

    // Todo Here Unit Dialog
    private fun dlUnitDialog(boolIsWeight: Boolean) {

        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_weight_unit)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val txt_dl_dialog = dialog.findViewById(R.id.txt_dl_dialog) as TextView
        val rbDlWeight1 = dialog.findViewById(R.id.rbDlWeight1) as RadioButton
        val rbDlWeight2 = dialog.findViewById(R.id.rbDlWeight2) as RadioButton
        val rgDlWeight = dialog.findViewById(R.id.rgDlWeight) as RadioGroup

        if (boolIsWeight) {

            if (LocalDB.getWeightUnit(this) == ConstantString.DEF_KG) {
                rbDlWeight2.isChecked = true
            } else {
                rbDlWeight1.isChecked = true
            }

            txt_dl_dialog.text = getString(R.string.select_your_weight_unit)
            rbDlWeight1.text = ConstantString.DEF_LB
            rbDlWeight2.text = ConstantString.DEF_KG

        } else {

            if (LocalDB.getHeightUnit(this) == ConstantString.DEF_IN) {
                rbDlWeight2.isChecked = true
            } else {
                rbDlWeight1.isChecked = true
            }

            txt_dl_dialog.text = getString(R.string.select_your_height_unit)
            rbDlWeight1.text = ConstantString.DEF_CM
            rbDlWeight2.text = ConstantString.DEF_IN

        }

        rgDlWeight.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.rbDlWeight1) {
                if (boolIsWeight) {
                    LocalDB.setWeightUnit(this, ConstantString.DEF_LB)
                    txtWeightValue.text = ConstantString.DEF_LB
                } else {
                    LocalDB.setHeightUnit(this, ConstantString.DEF_CM)
                    txtHeightValue.text = ConstantString.DEF_CM
                }
            } else if (checkedId == R.id.rbDlWeight2) {
                if (boolIsWeight) {
                    LocalDB.setWeightUnit(this, ConstantString.DEF_KG)
                    txtWeightValue.text = ConstantString.DEF_KG
                } else {
                    LocalDB.setHeightUnit(this, ConstantString.DEF_IN)
                    txtHeightValue.text = ConstantString.DEF_IN
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

}
