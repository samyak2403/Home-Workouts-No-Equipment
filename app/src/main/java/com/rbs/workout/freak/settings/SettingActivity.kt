package com.rbs.workout.freak.settings

import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdSize
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.R
import com.rbs.workout.freak.activity.BaseActivity
import com.rbs.workout.freak.activity.HealthDataActivity
import com.rbs.workout.freak.activity.MetricActivity
import com.rbs.workout.freak.databinding.ActivityHomeBinding
import com.rbs.workout.freak.databinding.ActivitySettingBinding
import com.rbs.workout.freak.reminder.ReminderActivity
import com.rbs.workout.freak.utils.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_setting.imgBack


class SettingActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingBinding

    var boolIsSelectEngine = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initAction()

        setBannerAdd(adMobView)


    }

    // Todo Common methods
    private fun init() {
        tvCountDown.text = LocalDB.getCountDownTime(this).toString().plus(" secs")
        tvRestTime.text = LocalDB.getRestTime(this).toString().plus(" secs")
        swtKeepScreenOn.isChecked = LocalDB.getKeepScreen(this)
    }

    private fun initAction() {

        imgBack.setOnClickListener {
            finish()
        }

        rlTestVoice.setOnClickListener {
            boolIsSelectEngine = false
            AppControl.speechTextToProfile(this, getString(R.string.did_you_hear_test_voice))
            Thread.sleep(1000)
            confirmVoiceTest(this, "", getString(R.string.did_you_hear_test_voice), ConstantString.CONFIRM_VOICE_TEST)
        }

        rlSelectTTSEngine.setOnClickListener {
            boolIsSelectEngine = true
            selectTTSEngine()
        }

        rlDownloadTTSEngine.setOnClickListener {
            CommonUtility.DownloadTTSEngine(this)
        }

        rlDeviceTTSSetting.setOnClickListener {
            val intent = Intent("com.android.settings.TTS_SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        rlHealthData.setOnClickListener {
            val intent = Intent(this, HealthDataActivity::class.java)
            startActivity(intent)
        }

        rlReminder.setOnClickListener {
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }

        rlMetricImperialUnit.setOnClickListener {
            val intent = Intent(this, MetricActivity::class.java)
            startActivity(intent)
        }

        rlShareWithFriend.setOnClickListener {
            val link = "https://play.google.com/store/apps/details?id=${"com.rbs.workout.freak"}"
            val strSubject = ""
            val strText =
                    "I'm training with ${getString(R.string.app_name)} and am getting great results." +
                            "\n\n" +
                            "Here in Workout Freak focus on all your main muscle groups to help you build and tone muscles - no equipment needed. Challenge yourself!" +
                            "\n\n" +
                            "Download the app:$link"

            CommonUtility.shareStringLink(this, strSubject, strText)
        }

        rlRateUs.setOnClickListener {
            CommonUtility.rateUs(this)
        }

        rlFeedBack.setOnClickListener {
            CommonUtility.contactUs(this)
        }

        rlPrivacyPolicy.setOnClickListener {
            CommonUtility.openUrl(this, getString(R.string.privacy_policy_link))
        }

        rlSoundOption.setOnClickListener { soundOptionDialog(this) }

        swtKeepScreenOn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
//                CommonUtility.keepScreenOn(this)
                LocalDB.setKeepScreen(this, true)
            } else {
                LocalDB.setKeepScreen(this, false)
            }
        }

        rlCountDown.setOnClickListener {
            ConstantDialog.setDurationDialog(this, ConstantString.DL_COUNT_DOWN_TIME, tvCountDown)
        }

        rlRestSet.setOnClickListener {
            ConstantDialog.setDurationDialog(this, ConstantString.DL_REST_SET, tvRestTime)
        }
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

        swtMute.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (!boolOtherClick) {
                    boolMuteClick = true
                    if (isChecked) {
                        LocalDB.setSoundMute(context, true)
                        swtVoiceGuide.isChecked = false
                        swtCoachTips.isChecked = false
                        LocalDB.setVoiceGuide(context, false)
                        LocalDB.setCoachTips(context, false)
//                        imgSound.setImageResource(R.drawable.ic_sound_off)

                    } else {
                        LocalDB.setSoundMute(context, false)
                        swtVoiceGuide.isChecked = LocalDB.getVoiceGuide(context)
                        swtCoachTips.isChecked = LocalDB.getCoachTips(context)
                        /*if (!LocalDB.getVoiceGuide(this) && !LocalDB.getCoachTips(this)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        } else {
                            imgSound.setImageResource(R.drawable.ic_sound_on)
                        }*/
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
//                        imgSound.setImageResource(R.drawable.ic_sound_on)
                    } else {
                        LocalDB.setVoiceGuide(context, false)
                        /*if (!LocalDB.getCoachTips(this) && !LocalDB.getSoundMute(this)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        }*/
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
//                        imgSound.setImageResource(R.drawable.ic_sound_on)
                    } else {
                        LocalDB.setCoachTips(context, false)
                       /* if (!LocalDB.getVoiceGuide(this) && !LocalDB.getSoundMute(this)) {
                            imgSound.setImageResource(R.drawable.ic_sound_off)
                        }*/
                    }
                    boolOtherClick = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnOk.setOnClickListener {
            try {
                dialog.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dialog.show()
    }


    /* Todo Confirmation dialog */
    private fun confirmVoiceTest(content: Context, strTitle: String, strMsg: String, strType: String): Boolean {

        val builder = AlertDialog.Builder(content)
//        builder.setTitle(strTitle)
        builder.setMessage(strMsg)
        builder.setCancelable(true)

        builder.setPositiveButton("Yes") { dialog, id ->

            dialog.cancel()
        }

        builder.setNegativeButton("No") { dialog, id ->
            if (ConstantString.CONFIRM_VOICE_TEST == strType) {
                TestVoiceFailDialog()
            }
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()

        return false
    }

    // Todo Faild test voice dialog
    private fun TestVoiceFailDialog() {

        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_test_voice_fail)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnDownloadTTSEngine = dialog.findViewById(R.id.btnDownloadTTSEngine) as Button
        val btnSelectTTSEngine = dialog.findViewById(R.id.btnSelectTTSEngine) as Button

        btnDownloadTTSEngine.setOnClickListener {
            CommonUtility.DownloadTTSEngine(this)
            dialog.cancel()
        }

        btnSelectTTSEngine.setOnClickListener {
            selectTTSEngine()
            dialog.cancel()
        }

        dialog.show()
    }

    // Todo select TTS Engine dialog
    private fun selectTTSEngine() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_select_tts_engine)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val rdoGoogleEngine = dialog.findViewById(R.id.rdoGoogleEngine) as RadioButton

        rdoGoogleEngine.setOnCheckedChangeListener { buttonView, isChecked ->
            dialog.cancel()
            if (boolIsSelectEngine) {
                AppControl.speechTextToProfile(
                    this@SettingActivity,
                    getString(R.string.did_you_hear_test_voice)
                )

                Thread.sleep(1000)
                confirmVoiceTest(
                    this@SettingActivity,
                    "",
                    getString(R.string.did_you_hear_test_voice),
                    ConstantString.CONFIRM_VOICE_TEST
                )

                boolIsSelectEngine = false
            }
        }

        dialog.show()
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

