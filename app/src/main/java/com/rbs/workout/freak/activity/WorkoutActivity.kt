package com.rbs.workout.freak.activity

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.ankushgrover.hourglass.Hourglass
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.rbs.workout.freak.R
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.interfaces.AdsCallback
import com.rbs.workout.freak.pojo.PWorkOutDetails
import com.rbs.workout.freak.utils.*
import kotlinx.android.synthetic.main.activity_workout.*
import java.util.*

class WorkoutActivity : BaseActivity(), AdsCallback {


    // Todo Override methods
    override fun onPause() {
        super.onPause()
        if (!boolIsReadyToGo) {
            pauseWorkOutTime()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (!boolIsReadyToGo) {
                if (!flagGotoVideo) {

                    val startExercise = "To the exercise ${arrPWorkoutList[viewPagerWorkout.currentItem].time} ${arrPWorkoutList[viewPagerWorkout.currentItem].title.toLowerCase().replace("ups", "up's")}"

                    AppControl.speechText(this, startExercise)

                    mySoundUtil.playSound(0)
                }

                resumeWorkOutTime()
                flagGotoVideo = false
                flagTimerPause = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (textToSpeech != null) {
//            textToSpeech!!.stop()
//            textToSpeech!!.shutdown()
//        }
        if (timerTask != null) {
            timerTask!!.cancel()
        }
        if (timer != null) {
            timer!!.cancel()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        confirmToExitDialog()

    }

    // Todo Object declaration
    lateinit var arrPWorkoutList: ArrayList<PWorkOutDetails>
    lateinit var mContext: Context
    private var recycleWorkIndicatorAdapter: RecycleWorkIndicatorAdapter? = null
    var recycleWorkoutTimeIndicatorAdapter: RecycleWorkoutTimeIndicatorAdapter? = null

    var strDayName: String = ""
    var strWeekName: String = ""

    var timeCountDown = 0
    var timer: Timer? = null
    var flagTimerPause: Boolean = false
    var flagGotoVideo: Boolean = false
    var textToSpeech: TextToSpeech? = null
    var boolSound: Boolean = true
    lateinit var mySoundUtil: MySoundUtil
    var boolIsReadyToGo = true
    var tableName :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        mContext = this
        mySoundUtil = MySoundUtil(this)

        try {
            arrPWorkoutList = intent.getSerializableExtra(ConstantString.workout_list) as ArrayList<PWorkOutDetails>

            strDayName = intent.getStringExtra(ConstantString.key_day_name)!!
            strWeekName = intent.getStringExtra(ConstantString.key_week_name)!!
            tableName = intent.getStringExtra(ConstantString.work_table_name)!!

            Log.e("TAG", "onCreate:::::Array:::  "+Gson().toJson(arrPWorkoutList) )
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        Utils.initFullAdd(this)

        this.startTime = System.currentTimeMillis()

        readyToGoSetup()
        defaultSetup()
        initAction()
        pDialog=adDimLightProgressDialog(mContext)


        if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_GOOGLE
                && LocalDB.getString(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
            CommonConstantAd.googlebeforloadAd(this)
        } else if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_FACEBOOK
                && LocalDB.getString(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
            CommonConstantAd.facebookbeforeloadFullAd(this)
        }

    }

    // Todo Common methods
    private fun defaultSetup() {

        recycleWorkIndicatorAdapter = RecycleWorkIndicatorAdapter()
        val layoutManager = FlexboxLayoutManager()
        layoutManager.flexWrap = FlexWrap.NOWRAP
        rcyWorkoutStatus.layoutManager = layoutManager
        rcyWorkoutStatus.adapter = recycleWorkIndicatorAdapter

        val doWorkOutPgrAdpt = DoWorkoutPagerAdapter()
        viewPagerWorkout.adapter = doWorkOutPgrAdpt
//        viewPagerWorkout.currentItem = 0
        viewPagerWorkout.currentItem = LocalDB.getLastUnCompletedExPos(this,tableName,arrPWorkoutList[0].workout_id.toString())

        Log.e("TAG", "onCreate:Work Data:::::::   $tableName   ${arrPWorkoutList[viewPagerWorkout.currentItem].workout_id}")
    }

    fun saveData() {
        LocalDB.setLastUnCompletedExPos(this, tableName,arrPWorkoutList[0].workout_id.toString(), viewPagerWorkout.currentItem)
    }

    private fun initAction() {

        /*if ((LocalDB.getSoundMute(this)) || (!LocalDB.getCoachTips(this) && !LocalDB.getVoiceGuide(this) &&
                        !LocalDB.getSoundMute(this))) {
            imgSound.setImageResource(R.drawable.ic_sound_off)
        } else {
            imgSound.setImageResource(R.drawable.ic_sound_on)
        }*/

        if (LocalDB.getSoundMute(this)) {
            imgSound.setImageResource(R.drawable.ic_sound_off)
        } else {
            imgSound.setImageResource(R.drawable.ic_sound_on)
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgbtnNext.setOnClickListener {
            workoutCompleted(viewPagerWorkout.currentItem + 1)
//            viewPagerWorkout.currentItem = viewPagerWorkout.currentItem + 1
        }

        imgbtnPrev.setOnClickListener {
            if (viewPagerWorkout.currentItem != 0) {
                workoutCompleted(viewPagerWorkout.currentItem - 1)
            }
//            viewPagerWorkout.currentItem = viewPagerWorkout.currentItem - 1
        }

        imgbtnDone.setOnClickListener {
            workoutCompleted(viewPagerWorkout.currentItem + 1)
        }

        imgbtnPause.setOnClickListener {
            showWorkoutDetails()
        }

        imgInfo.setOnClickListener {
            showWorkoutDetails()
        }

        imgVideo.setOnClickListener {
            try {
                val dbHelper = DataHelper(mContext)
                val strVideoLink = dbHelper.getVideoLink(Utils.ReplaceSpacialCharacters(arrPWorkoutList[viewPagerWorkout.currentItem].title))
                if (strVideoLink != "") {
                    flagGotoVideo = true
                    flagTimerPause = true
                    val str = "https://www.youtube.com/watch?v=$strVideoLink"
                    openYoutube(str)

    //                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$strVideoLink")))
                } else {
                    Toast.makeText(this, getString(R.string.error_video_not_exist), Toast.LENGTH_SHORT).show()
                }
            } catch (e: ActivityNotFoundException){
                e.printStackTrace()
                Toast.makeText(this,"Youtube player not available on this device",Toast.LENGTH_LONG).show()
            }
        }

        imgSound.setOnClickListener {
            soundOptionDialog(this)

//            boolSound = Utils.getPref(mContext, ConstantString.key_workout_sound, true)
//            if (boolSound) {
//                imgSound.setImageResource(R.drawable.ic_sound_off)
//                boolSound = false
//                Utils.setPref(mContext, ConstantString.key_workout_sound, boolSound)
//            } else {
//                imgSound.setImageResource(R.drawable.ic_sound_on)
//                boolSound = true
//                Utils.setPref(mContext, ConstantString.key_workout_sound, boolSound)
//            }
        }

        viewPagerWorkout.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(pos: Int) {
                workoutSetup(pos)
                if (boolSound) {
                    if (textToSpeech != null) {
                        textToSpeech!!.setSpeechRate(0.5f)
                        textToSpeech!!.speak(arrPWorkoutList[viewPagerWorkout.currentItem].title.toLowerCase().replace("ups", "up's"), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }

        })

    }

    fun soundOptionDialog(context: Context) {

        var boolOtherClick = false
        var boolMuteClick = false
        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_sound_option)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val swtMute = dialog.findViewById(R.id.swtMute) as Switch
        val swtVoiceGuide = dialog.findViewById(R.id.swtVoiceGuide) as Switch
        val swtCoachTips = dialog.findViewById(R.id.swtCoachTips) as Switch
        val btnOk = dialog.findViewById(R.id.btnOk) as TextView

        swtVoiceGuide.isChecked = LocalDB.getVoiceGuide(context)

        swtCoachTips.isChecked = LocalDB.getCoachTips(context)

        if (LocalDB.getSoundMute(context)) {
            swtMute.isChecked = true
            swtCoachTips.isChecked = false
            swtVoiceGuide.isChecked = false
        }

       /* swtMute.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (!boolOtherClick) {
                    boolMuteClick = true
                    if (isChecked) {
                        LocalDB.setSoundMute(context, true)
                        swtVoiceGuide.isChecked = false
                        swtCoachTips.isChecked = false
                        LocalDB.setVoiceGuide(context, false)
                        LocalDB.setCoachTips(context, false)
                        imgSound.setImageResource(R.drawable.ic_sound_off)

                    } else {
                        LocalDB.setSoundMute(context, false)
                        swtVoiceGuide.isChecked = LocalDB.getVoiceGuide(context)
                        swtCoachTips.isChecked = LocalDB.getCoachTips(context)
                        if (!LocalDB.getVoiceGuide(this) && !LocalDB.getCoachTips(this)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        } else {
                            imgSound.setImageResource(R.drawable.ic_sound_on)
                        }
                    }
                    boolMuteClick = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        swtVoiceGuide.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (!boolMuteClick) {
                    boolOtherClick = true
                    if (isChecked) {
                        swtMute.isChecked = false
                        LocalDB.setSoundMute(context, false)
                        LocalDB.setVoiceGuide(context, true)
                        imgSound.setImageResource(R.drawable.ic_sound_on)
                    } else {
                        LocalDB.setVoiceGuide(context, false)
                        if (!LocalDB.getCoachTips(this) && !LocalDB.getSoundMute(this)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        }
                    }
                    boolOtherClick = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        swtCoachTips.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (!boolMuteClick) {
                    boolOtherClick = true
                    if (isChecked) {
                        swtMute.isChecked = false
                        LocalDB.setSoundMute(context, false)
                        LocalDB.setCoachTips(context, true)
                        imgSound.setImageResource(R.drawable.ic_sound_on)
                    } else {
                        LocalDB.setCoachTips(context, false)
                        if (!LocalDB.getVoiceGuide(this) && !LocalDB.getSoundMute(this)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        }
                    }
                    boolOtherClick = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/


        swtMute.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!boolOtherClick) {
                    boolMuteClick = true
                    if (isChecked) {
                        LocalDB.setSoundMute(context, true)
                        swtVoiceGuide.isChecked = false
                        swtCoachTips.isChecked = false
                        LocalDB.setVoiceGuide(context, false)
                        LocalDB.setCoachTips(context, false)
                        imgSound.setImageResource(R.drawable.ic_sound_off)

                    } else {
                        LocalDB.setSoundMute(context, false)
                        swtVoiceGuide.isChecked = LocalDB.getVoiceGuide(context)
                        swtCoachTips.isChecked = LocalDB.getCoachTips(context)
                        imgSound.setImageResource(R.drawable.ic_sound_on)
                    }
                    boolMuteClick = false
                }
            }
        })

        swtVoiceGuide.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!boolMuteClick) {
                    boolOtherClick = true
                    if (isChecked) {
                        swtMute.isChecked = false
                        LocalDB.setSoundMute(context, false)
                        LocalDB.setVoiceGuide(context, true)
                        imgSound.setImageResource(R.drawable.ic_sound_on)
                    } else {
                        LocalDB.setVoiceGuide(context, false)
//                        imgSound.setImageResource(R.drawable.ic_sound_on)
                       /* if (!LocalDB.getCoachTips(this@WorkoutActivity) && !LocalDB.getSoundMute(this@WorkoutActivity)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        }*/
                    }
                    boolOtherClick = false
                }
            }
        })

        swtCoachTips.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!boolMuteClick) {
                    boolOtherClick = true
                    if (isChecked) {
                        swtMute.isChecked = false
                        LocalDB.setSoundMute(context, false)
                        LocalDB.setCoachTips(context, true)
//                        imgSound.setImageResource(R.drawable.ic_sound_off)
                        imgSound.setImageResource(R.drawable.ic_sound_on)
                    } else {
                        LocalDB.setCoachTips(context, false)
                        /*if (!LocalDB.getVoiceGuide(this@WorkoutActivity) && !LocalDB.getSoundMute(this@WorkoutActivity)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        }*/
                    }
                    boolOtherClick = false
                }
            }
        })
        btnOk.setOnClickListener {
            try {
                dialog.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dialog.show()
    }

    private var startTime: Long = 0
    private var running = false
    private var currentTime: Long = 0
    fun start() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                setTime()
                handler.postDelayed(this, 1000)
            }
        }, 1000)
        this.startTime = System.currentTimeMillis()
        this.running = true
    }

    private var totalSec = 0
    private fun setTime() {
        /*if (getElapsedTimeMin() == 0L) {
            txtTimer.text = "0" + getElapsedTimeMin() + ":" + String.format("%02d", getElapsedTimeSecs())
        } else {
            if (getElapsedTimeMin() + "".length > 1) {
                txtTimer.text = "" + getElapsedTimeMin() + ":" + String.format("%02d", getElapsedTimeSecs())
            } else {
                txtTimer.text = "0" + getElapsedTimeMin() + ":" + String.format("%02d", getElapsedTimeSecs())
            }
        }*/
        try {
            if (!flagTimerPause) {
                totalSec += 1
                txtTimer.text = CommonUtility.secToTime(totalSec)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Todo Workout Timing method
    private var timerTask: TimerTask? = null
    private fun startWorkoutTimer(totalTime: Int) {
        timeCountDown = 0
        txtTimeCountDown.text = "".plus(timeCountDown.toString()).plus(" / ").plus(totalTime.toString())

        recycleWorkoutTimeIndicatorAdapter = RecycleWorkoutTimeIndicatorAdapter(totalTime)
        val layoutManagerTimer = FlexboxLayoutManager()
        layoutManagerTimer.flexWrap = FlexWrap.NOWRAP
        rcyBottomWorkoutTimeStatus.layoutManager = layoutManagerTimer
        rcyBottomWorkoutTimeStatus.adapter = recycleWorkoutTimeIndicatorAdapter

        val handler = Handler()
        timer = Timer(false)

        timerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    if (!flagTimerPause) {
                        timeCountDown++
//                    viewPagerWorkout.setCurrentItem(timeCountDown)
                        txtTimeCountDown.text = "".plus(timeCountDown.toString()).plus(" / ").plus(totalTime.toString())
                        recycleWorkoutTimeIndicatorAdapter?.notifyDataSetChanged()

                        if (timeCountDown.equals(totalTime)) {
                            timer?.cancel()
                            workoutCompleted(viewPagerWorkout.currentItem + 1)
                            //viewPagerWorkout.currentItem = viewPagerWorkout.currentItem + 1
                        }
                    }
                }
            }
        }
        timer?.schedule(timerTask, 1000, 1000)
    }

    private fun pauseWorkOutTime() {
        this.running = false
        currentTime = System.currentTimeMillis() - startTime
    }

    private fun resumeWorkOutTime() {
        this.running = true
        this.startTime = System.currentTimeMillis() - currentTime
    }

    internal var progress = 0
    private var countDownTimer: Hourglass? = null
    private fun setupCountDown() {
        pauseWorkOutTime()
        progress = 0
        countDownTimer = object : Hourglass(30000, 1000) {
            override fun onTimerTick(timeRemaining: Long) {
            }

            override fun onTimerFinish() {
                resumeWorkOutTime()
            }
        }
        (countDownTimer as Hourglass).startTimer()
    }

    // Todo show current rbs details
    private fun showWorkoutDetails() {
        flagTimerPause = true
        val intent = Intent(mContext, WorkoutListDetailsActivity::class.java)
        intent.putExtra(ConstantString.key_workout_details_type, ConstantString.val_is_workout)
        intent.putExtra(ConstantString.key_workout_list_array, arrPWorkoutList)
        intent.putExtra(ConstantString.key_workout_list_pos, viewPagerWorkout.currentItem)
        mContext.startActivity(intent)
        overridePendingTransition(R.anim.slide_up, R.anim.none)
    }

    // Todo set current rbs setup
    private fun workoutSetup(pos: Int) {
        val pworkDetails: PWorkOutDetails = arrPWorkoutList[pos]
        if (timer != null) {
            timer?.cancel()
        }

        if (pworkDetails.time_type == ConstantString.workout_type_time) {
            rltStepTypeWorkOut.visibility = View.GONE
            rltTimeTypeWorkOut.visibility = View.VISIBLE
            startWorkoutTimer(pworkDetails.time.substring(pworkDetails.time.indexOf(":") + 1).toInt())
        } else {
            rltStepTypeWorkOut.visibility = View.VISIBLE
            rltTimeTypeWorkOut.visibility = View.GONE
        }

        recycleWorkIndicatorAdapter?.notifyDataSetChanged()
    }

    var timeTotal: String = ""
    lateinit var pDialog : Dialog

    // Todo rbs completed method
    private fun workoutCompleted(pos: Int) {
        if (viewPagerWorkout.currentItem == (arrPWorkoutList.size - 1)) {
            timeTotal = txtTimer.text.toString()
            pDialog.show()
            if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_GOOGLE &&
                    LocalDB.getString(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
                CommonConstantAd.showInterstitialAdsGoogle(this,this)
            } else if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_FACEBOOK &&
                    LocalDB.getString(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
                CommonConstantAd.showInterstitialAdsFacebook(this)
            }else{
                nextActivityStart()
            }

        } else {
            mySoundUtil.playSound(1)
            flagTimerPause = true
            val intent = Intent(mContext, NextPrevDetailsWorkoutActivity::class.java)
            intent.putExtra(ConstantString.key_workout_list_pos, pos)
            intent.putExtra(ConstantString.key_workout_list_array, arrPWorkoutList)
            intent.putExtra(ConstantString.extra_workout_list_pos, (viewPagerWorkout.currentItem + 1))
            startActivity(intent)
            viewPagerWorkout.currentItem = pos
        }
    }

    private fun nextActivityStart() {

        finish()
        val intent = Intent(mContext, CompletedActivity::class.java)
        intent.putExtra(ConstantString.workout_list, arrPWorkoutList)
        intent.putExtra("Duration", txtTimer.text)
        intent.putExtra(ConstantString.key_day_name, strDayName)
        intent.putExtra(ConstantString.key_week_name, strWeekName)
        intent.putExtra(ConstantString.workout_id_from_workactivity,arrPWorkoutList[0].workout_id.toString())
        intent.putExtra(ConstantString.table_name_from_workactivity,tableName)
        startActivity(intent)
        pauseWorkOutTime()
        setupCountDown()
    }

    override fun adLoadingFailed() {
        pDialog.dismiss()
        nextActivityStart()
    }

    override fun adClose() {
        pDialog.dismiss()
        nextActivityStart()
    }

    override fun startNextScreen() {
        pDialog.dismiss()
        nextActivityStart()
    }


    // Todo here define adapter
    inner class RecycleWorkIndicatorAdapter : RecyclerView.Adapter<RecycleWorkIndicatorAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.row_of_recycleview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            if (viewPagerWorkout.currentItem > position) {
                holder.viewIndicator.background = ContextCompat.getDrawable(mContext, R.drawable.view_line_theme)
            } else {
                holder.viewIndicator.background = ContextCompat.getDrawable(mContext, R.drawable.view_line_gray)
            }
        }

        override fun getItemCount(): Int {
            return arrPWorkoutList.size
        }

        inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            internal var viewIndicator: View = itemView.findViewById(R.id.viewIndicator) as View

//            init {
//                viewIndicator = itemView.findViewById(R.id.viewIndicator) as View
//            }

        }
    }

    inner class RecycleWorkoutTimeIndicatorAdapter(val totalWorkOut: Int) : RecyclerView.Adapter<RecycleWorkoutTimeIndicatorAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.row_of_recycle_time, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            if (timeCountDown > position) {
                holder.viewIndicator.setBackgroundColor(mContext.resources.getColor(R.color.colorTheme))
            } else {
                holder.viewIndicator.setBackgroundColor(mContext.resources.getColor(R.color.colorGray))
            }

        }

        override fun getItemCount(): Int {
            return totalWorkOut
        }

        inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            internal var viewIndicator: View = itemView.findViewById(R.id.viewIndicator) as View
//            init {
//                viewIndicator = itemView.findViewById(R.id.viewIndicator) as View
//            }
        }
    }

    override fun onStop() {
        saveData()
        flagTimerPause = true
        Log.e("TAG", "onStop::::::::: ")
        super.onStop()
    }

    inner class DoWorkoutPagerAdapter : androidx.viewpager.widget.PagerAdapter() {

        override fun isViewFromObject(convertView: View, anyObject: Any): Boolean {
            return convertView === anyObject as RelativeLayout
        }

        override fun getCount(): Int {
            return arrPWorkoutList.size
        }

        private fun getItem(pos: Int): PWorkOutDetails {
            return arrPWorkoutList[pos]
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item: PWorkOutDetails = getItem(position)
            val itemView = LayoutInflater.from(mContext).inflate(R.layout.start_workout_row, container, false)
//            val txtWorkoutTime: TextView = itemView.findViewById(R.id.txtWorkoutTime)
            val txtWorkoutTitle: TextView = itemView.findViewById(R.id.txtWorkoutTitle)
            val txtWorkoutDetails: TextView = itemView.findViewById(R.id.txtWorkoutDetails)
            val viewfliperWorkout: ViewFlipper = itemView.findViewById(R.id.viewfliperWorkout)

//            txtWorkoutTime.text = item.time
            if (boolIsReadyToGo) {
                txtWorkoutTitle.text = ""
                txtWorkoutDetails.text = ""
            } else {
                if (item.time_type == ConstantString.workout_type_step) {
                    txtWorkoutTitle.text = "x ".plus(item.time)
                } else {
                    txtWorkoutTitle.text = item.time
                }

                txtWorkoutDetails.text = item.title
            }

            viewfliperWorkout.removeAllViews()
            val listImg: ArrayList<String> = Utils.getAssetItems(mContext, Utils.ReplaceSpacialCharacters(item.title))

            for (i in 0 until listImg.size) {
                val imgview = ImageView(mContext)
//                Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(mContext).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

                viewfliperWorkout.addView(imgview)
            }

            viewfliperWorkout.isAutoStart = true
            viewfliperWorkout.setFlipInterval(mContext.resources.getInteger(R.integer.viewfliper_animation))
            viewfliperWorkout.startFlipping()

            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as RelativeLayout)
        }
    }

    // Todo Time in seconds
    private fun getElapsedTimeSecs(): Long {
        var elapsed: Long = 0
        if (running) {
            elapsed = (System.currentTimeMillis() - startTime) / 1000 % 60
        }
        return elapsed
    }

    // Todo Time in minutes
    private fun getElapsedTimeMin(): Long {
        var elapsed: Long = 0
        if (running) {
            elapsed = (System.currentTimeMillis() - startTime) / 1000 / 60 % 60
        }
        return elapsed
    }

    // Todo ready to go
    private fun readyToGoSetup() {
        txtWorkoutTitle.text = arrPWorkoutList[0].title

        llReadyToGo.visibility = View.VISIBLE
        rltBottomReadyToGo.visibility = View.VISIBLE
        txtTimer.visibility = View.GONE
        rltBottomControl.visibility = View.GONE

        countDownReadyToGo()

        val readyToGoText = "Ready to go start with ${arrPWorkoutList[viewPagerWorkout.currentItem].title.toLowerCase().replace("ups", "up's")}"

        AppControl.speechText(this, readyToGoText)

        btnSkip.setOnClickListener {
            startExercise()
        }

    }

    // Todo count ready to go count down timing
    private fun countDownReadyToGo() {

//        var timeCountDown = 0

        val readyToGoTime = LocalDB.getCountDownTime(this)
//        progressBar.max = readyToGoTime
//        progressBar.secondaryProgress = readyToGoTime

        txtCountDown.text = readyToGoTime.toString()
        var timeCountDown = readyToGoTime
        progressBar.max = readyToGoTime
        progressBar.progress = readyToGoTime
        progressBar.secondaryProgress = readyToGoTime

        val handler = Handler()
        timer = Timer(false)

        val timerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    /*timeCountDown++
                    txtCountDown.text = timeCountDown.toString()
                    progressBar.progress = timeCountDown

                    if (timeCountDown == readyToGoTime) {
                        startExercise()
                    } else if ((readyToGoTime - timeCountDown) < 4) {
                        AppControl.speechText(this@WorkoutActivity, (readyToGoTime - timeCountDown).toString())
                    }*/

                    timeCountDown--
                    txtCountDown.text = timeCountDown.toString()
                    progressBar.progress = timeCountDown
                    Log.e("TAG", "run:::timeCountDown $timeCountDown")

                    if (timeCountDown == 0) {
                        startExercise()
                    } else if (timeCountDown < 4) {
                        AppControl.speechText(mContext, (timeCountDown).toString())
                    }
                }
            }
        }
        timer?.schedule(timerTask, 1000, 1000)

    }

    // Todo Stop ready to go
    private fun startExercise() {
        boolIsReadyToGo = false

        llReadyToGo.visibility = View.GONE
        rltBottomReadyToGo.visibility = View.GONE
        txtTimer.visibility = View.VISIBLE
        rltBottomControl.visibility = View.VISIBLE

        if (timer != null) {
            timer!!.cancel()
        }

        defaultSetup()
        start()
        workoutSetup(0)
        onResume()

    }

    // Todo Quite confirmatin dialog
    private fun confirmToExitDialog() {
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


            if (LocalDB.getInteger(this, ConstantString.EXIT_BTN_COUNT, 1) == 2) {
                if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
                        LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
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
                } else if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK &&
                        LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
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
                } else {
                    quiteData(dialog)
                }
                LocalDB.setInteger(this, ConstantString.EXIT_BTN_COUNT, 1)
            } else {
                if (adClickCount == 1) {
                    LocalDB.setInteger(this, ConstantString.EXIT_BTN_COUNT, 2)
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

    var adClickCount: Int = 1
    fun quiteData(dialog: Dialog) {
        try {
            saveData()
            finish()
            dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
