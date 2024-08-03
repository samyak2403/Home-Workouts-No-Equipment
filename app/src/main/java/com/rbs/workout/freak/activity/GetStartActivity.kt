package com.rbs.workout.freak.activity

import android.content.Intent
import android.os.Bundle
import com.rbs.workout.freak.R
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.Utils
import kotlinx.android.synthetic.main.activity_get_started_screen.*

class GetStartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_get_started_screen)

        /*val param = llMain.layoutParams as FrameLayout.LayoutParams
        param.setMargins(0, 0, 0, getNavigationSize(this))
        llMain.layoutParams = param

        if (Build.VERSION.SDK_INT >= 23) {
            val w: Window = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }*/

        Utils.setPref(this,ConstantString.pref_key_is_first_time,false)

        initAction()
    }

    /* todo Default init methods */
    private fun initAction() {
        btnGetStarted.setOnClickListener {

            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }


}
