package com.rbs.workout.freak.utils

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import com.rbs.workout.freak.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.util.*

/**
 * Created by Kuldeep on 21-Jan-19.
 */
class AppControl : Application() {

    companion object {
        var textToSpeech: TextToSpeech? = null

        fun speechText(context: Context, strSpeechText: String) {
            if (checkVoiceOnOrOff(context)) {
                if (textToSpeech != null) {
                    textToSpeech!!.setSpeechRate(0.9f)
                    textToSpeech!!.speak(strSpeechText, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }

        fun speechTextToProfile(context: Context, strSpeechText: String) {
            if (textToSpeech != null) {
                textToSpeech!!.setSpeechRate(0.9f)
                textToSpeech!!.speak(strSpeechText, TextToSpeech.QUEUE_FLUSH, null,null)
            }

        }


        private fun checkVoiceOnOrOff(context: Context): Boolean {
            return !LocalDB.getSoundMute(context) && LocalDB.getVoiceGuide(context)
        }

    }

    override fun onCreate() {
        super.onCreate()

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech!!.language = Locale.ENGLISH
//                if (boolSound) {
//                    textToSpeech!!.setSpeechRate(0.9f)
//                    textToSpeech!!.speak("Welcome to 30 day rbs app", TextToSpeech.QUEUE_FLUSH, null)
//                }
            }
        })

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("aoo_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

    }

}
