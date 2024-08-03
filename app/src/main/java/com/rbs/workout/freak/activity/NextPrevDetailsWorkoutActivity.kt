package com.rbs.workout.freak.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.ads.AdSize
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.R
import com.rbs.workout.freak.interfaces.AdsCallback
import com.rbs.workout.freak.interfaces.CallbackListener
import com.rbs.workout.freak.pojo.PWorkOutDetails
import com.rbs.workout.freak.utils.*
import kotlinx.android.synthetic.main.activity_next_prev_details_workout.*
import kotlinx.android.synthetic.main.activity_next_prev_details_workout.adMobView
import kotlinx.android.synthetic.main.activity_next_prev_details_workout.btnSkip
import kotlinx.android.synthetic.main.activity_next_prev_details_workout.progressBar
import kotlinx.android.synthetic.main.activity_next_prev_details_workout.rcyWorkoutStatus
import kotlinx.android.synthetic.main.activity_next_prev_details_workout.txtCountDown
import java.util.*

class NextPrevDetailsWorkoutActivity : BaseActivity(), CallbackListener {



    override fun onBackPressed() {
//        super.onBackPressed()
        confirmToExitDialog()
    }

    // Todo object declaration
    var timeCountDown = 0
    var timer: Timer? = null
    var workoutPos: Int = 0
    private var viewpagerCurrentItem: Int = 0
    var pWorkoutList = ArrayList<PWorkOutDetails>()
    private var flagTimerPause: Boolean = false
    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_prev_details_workout)

        setBannerAdd(adMobView)

        mContext = this

        try {
            workoutPos = intent.getIntExtra(ConstantString.key_workout_list_pos, 0)
            pWorkoutList = intent.getSerializableExtra(ConstantString.key_workout_list_array) as ArrayList<PWorkOutDetails>
            viewpagerCurrentItem = intent.getIntExtra(ConstantString.extra_workout_list_pos, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        defaultSetup()
        setAction()
        startWorkoutTimer()
        indicator()



    }
    private var recycleWorkIndicatorAdapter: RecycleWorkIndicatorAdapter? = null
    fun indicator(){
        recycleWorkIndicatorAdapter = RecycleWorkIndicatorAdapter()
        val layoutManager = FlexboxLayoutManager()
        layoutManager.flexWrap = FlexWrap.NOWRAP
        rcyWorkoutStatus.layoutManager = layoutManager
        rcyWorkoutStatus.adapter = recycleWorkIndicatorAdapter
    }

    // Todo here define adapter
    inner class RecycleWorkIndicatorAdapter : RecyclerView.Adapter<RecycleWorkIndicatorAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.row_of_recycleview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            if (viewpagerCurrentItem > position) {
                holder.viewIndicator.background = ContextCompat.getDrawable(mContext, R.drawable.view_line_theme)
            } else {
                holder.viewIndicator.background = ContextCompat.getDrawable(mContext, R.drawable.view_line_gray)
            }
        }

        override fun getItemCount(): Int {
            return pWorkoutList.size
        }

        inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            internal var viewIndicator: View = itemView.findViewById(R.id.viewIndicator) as View

//            init {
//                viewIndicator = itemView.findViewById(R.id.viewIndicator) as View
//            }

        }
    }
    /* Todo common methods */
    private fun defaultSetup() {
//        Utils.initAdd(mContext, adView)

        progressBar.progress = LocalDB.getRestTime(this)
        progressBar.max = LocalDB.getRestTime(this)
        progressBar.secondaryProgress = LocalDB.getRestTime(this)

        val restString = "Take a rest Next ${pWorkoutList[workoutPos].time} ${pWorkoutList[workoutPos].title.toLowerCase().replace("ups", "up's")}"

        AppControl.speechText(this, restString)

        if (pWorkoutList[workoutPos].time == ConstantString.workout_type_step) {
            txtWorkoutTime.text = "x".plus(pWorkoutList[workoutPos].time)
        } else {
            txtWorkoutTime.text = pWorkoutList[workoutPos].time
        }

        txtWorkoutName.text = pWorkoutList[workoutPos].title
        txtSteps.text = workoutPos.toString().plus(" / ").plus(pWorkoutList.size)

        viewfliperWorkout.removeAllViews()
        val listImg: ArrayList<String> = Utils.getAssetItems(mContext, Utils.ReplaceSpacialCharacters(pWorkoutList[workoutPos].title))

        for (i in 0 until listImg.size) {
            val imgview = ImageView(mContext)
//            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
            Glide.with(mContext).load(listImg[i]).into(imgview)
            imgview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            viewfliperWorkout.addView(imgview)
        }

        viewfliperWorkout.isAutoStart = true
        viewfliperWorkout.setFlipInterval(mContext.resources.getInteger(R.integer.viewfliper_animation))
        viewfliperWorkout.startFlipping()

    }

    private fun setAction() {
        btnSkip.setOnClickListener {
            timer?.cancel()
            finish()
        }
    }

    /* Todo Workout Timing method */
    private fun startWorkoutTimer() {
//        timeCountDown = 0

        timeCountDown = LocalDB.getRestTime(this)
        txtCountDown.text = timeCountDown.toString()

        val restTime = LocalDB.getRestTime(this)

        val handler = Handler()
        timer = Timer(false)

        val timerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    try {
                        if (!flagTimerPause) {
                           /* timeCountDown++
                            txtCountDown.text = timeCountDown.toString()
                            progressBar.progress = timeCountDown
                            if (timeCountDown == restTime) {
                                finish()
                            }*/

                            timeCountDown--
                            txtCountDown.text = timeCountDown.toString()
                            progressBar.progress = timeCountDown
                            if (timeCountDown == 0) {
                                Log.e("TAG", "run:::::startWorkoutTimer   $restTime")
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        timer?.schedule(timerTask, 1000, 1000)

    }

    override fun onStop() {
        Log.e("TAG", "onStop::::::Ex ")
        flagTimerPause = true
        super.onStop()
    }

    override fun onResume() {
        Log.e("TAG", "onResume::::Ex " )
        openInternetDialog(this,false)
        flagTimerPause = false
        super.onResume()
    }

    // Todo Quite confirmatin dialog
    private fun confirmToExitDialog(){
        flagTimerPause = true
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_exercise_exit)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val imgbtnClose = dialog.findViewById(R.id.imgbtnClose) as ImageButton
        val btnQuite = dialog.findViewById(R.id.btnQuite) as Button
        val btnContinue = dialog.findViewById(R.id.btnContinue) as Button
        val tvComebackMessage = dialog.findViewById(R.id.tvComebackMessage) as TextView


        imgbtnClose.setOnClickListener {
            try {
                flagTimerPause = false
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnQuite.setOnClickListener {


            if (LocalDB.getInteger(this,ConstantString.EXIT_BTN_COUNT,1) == 2){
                if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_GOOGLE &&
                        LocalDB.getString(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
                    CommonConstantAd.showInterstitialAdsGoogle(mContext,object : AdsCallback {
                        override fun adLoadingFailed() {
                            quiteData(dialog)
                        }

                        override fun adClose() {
                            quiteData(dialog)
                        }

                        override fun startNextScreen() {
                            quiteData(dialog)
                        }

                    })
                }
                else if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_FACEBOOK &&
                        LocalDB.getString(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
                    CommonConstantAd.showInterstitialAdsFacebook(object : AdsCallback {
                        override fun adLoadingFailed() {
                            quiteData(dialog)
                        }

                        override fun adClose() {
                            quiteData(dialog)
                        }

                        override fun startNextScreen() {
                            quiteData(dialog)
                        }

                    })
                }
                else{
                    quiteData(dialog)
                }
                LocalDB.setInteger(this,ConstantString.EXIT_BTN_COUNT,1)
            }
            else{
                if (adClickCount == 1){
                    LocalDB.setInteger(this,ConstantString.EXIT_BTN_COUNT,2)
                }
                quiteData(dialog)
            }


            /* finish()
             dialog.dismiss()*/
        }

        btnContinue.setOnClickListener {
            try {
                flagTimerPause = false
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dialog.setOnCancelListener {
            flagTimerPause = false
        }


        dialog.show()
    }

    var adClickCount :Int = 1
    fun quiteData(dialog:Dialog){
        try {
//            finish()
            val intent  = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

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
