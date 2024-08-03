package com.rbs.workout.freak.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.rbs.workout.freak.R
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantDialog
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import com.rbs.workout.freak.view.DatePickerFragment
import kotlinx.android.synthetic.main.activity_health_data.*
import kotlinx.android.synthetic.main.activity_health_data.llAdView
import kotlinx.android.synthetic.main.activity_health_data.llAdViewFacebook

class HealthDataActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

//        tvDateOfBirthVal.text = "$year-${CommonUtility.getString2DFormat(month+1)}-${CommonUtility.getString2DFormat(dayOfMonth)}"
        tvDateOfBirthVal.text = "$year"
        LocalDB.setBirthDate(this, year.toString())
//        Toast.makeText(this,"$year-${month+1}-$dayOfMonth",Toast.LENGTH_SHORT).show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_data)

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
        tvDateOfBirthVal.text = LocalDB.getBirthDate(this)
        tvGenderVal.text = LocalDB.getGender(this)
    }

    private fun initAction() {
        imgBack.setOnClickListener {
            finish()
        }

        llDateOfBirth.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "Date Picker")
        }

        llGender.setOnClickListener {
            ConstantDialog.setGenderDialog(this, tvGenderVal)
        }
    }

}
