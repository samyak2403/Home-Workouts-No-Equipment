package com.rbs.workout.freak.bw

import android.content.Intent
import android.os.Bundle
import androidx.core.text.HtmlCompat
import android.view.View
import android.widget.RelativeLayout
import com.google.android.gms.ads.AdSize
import com.google.android.material.appbar.AppBarLayout
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.R
import com.rbs.workout.freak.activity.BaseActivity
import com.rbs.workout.freak.pojo.PWorkOutCategory
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_build_wider.*

import kotlinx.android.synthetic.main.activity_build_wider.flexible_example_appbar
import kotlinx.android.synthetic.main.activity_build_wider.ivMask
import kotlinx.android.synthetic.main.activity_build_wider.llText

import kotlinx.android.synthetic.main.activity_build_wider.toolbar
import kotlinx.android.synthetic.main.activity_build_wider.tvData2
import kotlinx.android.synthetic.main.activity_build_wider_workout_list.*
import java.io.Serializable

class BuildWiderActivity : BaseActivity() {

    // Todo Object declaration
    lateinit var pWorkOutCategory: PWorkOutCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_wider)


      setSmallNativeAdd()

      /*  val param = annonce_main_coordinator.layoutParams as FrameLayout.LayoutParams
        param.setMargins(0, 0, 0, getNavigationSize(this))
        annonce_main_coordinator.layoutParams = param



        if (Build.VERSION.SDK_INT >= 23) {
            val w: Window = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }*/


        pWorkOutCategory = intent.getSerializableExtra(ConstantString.key_workout_category_item) as PWorkOutCategory

        init()
        initAction()




        var isShow = true
        var scrollRange = -1
        flexible_example_appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                /*status bar colortheme*/
                llText.visibility = View.GONE
                isShow = true
            } else if (isShow) {
                /*status bar transparant*/
                llText.visibility = View.VISIBLE
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                isShow = false
            }
        })

    }

    // Todo Common methods
    private fun init() {
        tvData2.text = HtmlCompat.fromHtml(resources.getString(R.string.build_wider_text_2), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun initAction() {

        ivMask.setOnClickListener {
            if (tvData2.visibility == View.VISIBLE) {
                ivMask.setImageResource(R.drawable.ic_build_arrow_down)
                tvData2.visibility = View.GONE
            } else {
                ivMask.setImageResource(R.drawable.ic_build_arrow_up)
                tvData2.visibility = View.VISIBLE
            }
        }

        cvBeginner.setOnClickListener {
            gotoWorkoutList(ConstantString.biginner, txtBuildWiderBeginnerDetails.text.toString(), txtBuildWiderBeginnerTime.text.toString())
        }

        cvIntermediate.setOnClickListener {
            gotoWorkoutList(ConstantString.intermediate, txtBuildWiderIntermediateDetails.text.toString(), txtBuildWiderIntermediateWorkout.text.toString())
        }

        cvAdvanced.setOnClickListener {
            gotoWorkoutList(ConstantString.advance, txtBuildWiderAdvancedDetails.text.toString(), txtBuildWiderAdvancedWorkout.text.toString())
        }

        toolbar.setNavigationOnClickListener { view -> onBackPressed() }

    }

    // Todo Goto Workout list screen
    private fun gotoWorkoutList(strDifficultyLvl: String, strTime: String, strWorkout: String) {

        pWorkOutCategory.catDefficultyLevel = strDifficultyLvl

        pWorkOutCategory.BWTime = strTime
        pWorkOutCategory.BWWorkout = strWorkout

        val intent = Intent(this, BuildWiderWorkoutListActivity::class.java)
        intent.putExtra(ConstantString.key_workout_category_item, pWorkOutCategory as Serializable)
        startActivity(intent)

    }




}
