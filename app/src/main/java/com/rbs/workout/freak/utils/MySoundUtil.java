package com.rbs.workout.freak.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.rbs.workout.freak.R;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.AUDIO_SERVICE;

public class MySoundUtil {

    public static int SOUND_DING = 1;
    public static int SOUND_WHISTLE = 0;
    public static MySoundUtil soundUtil;
    private static MySoundUtil utils;
    private AudioManager audioManager;
    private Map<Integer, Integer> soundMap;
    private SoundPool soundPool;
    private SoundPool ttsSoundPool;
    private Context context;

    public MySoundUtil(Context context) {
        init(context);
        this.context = context;
    }

    public static synchronized MySoundUtil getInstance(Context context) {
        MySoundUtil mySoundUtil;
        synchronized (MySoundUtil.class) {
            if (utils == null) {
                utils = new MySoundUtil(context);
            }
            mySoundUtil = utils;
        }
        return mySoundUtil;
    }

    public void init(Context context) {
        try {
            soundPool = new SoundPool(3, 3, 0);
            soundMap = new HashMap();
            soundMap.put(SOUND_WHISTLE, soundPool.load(context, R.raw.whistle, 1));
            soundMap.put(SOUND_DING, soundPool.load(context, R.raw.ding, 1));
            audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            ttsSoundPool = new SoundPool(1, 3, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e2) {
            e2.printStackTrace();
        }
    }

    public void playSound(int sound_index) {
        if (!LocalDB.INSTANCE.getSoundMute(context) && LocalDB.INSTANCE.getCoachTips(context)  ||
                LocalDB.INSTANCE.getSoundMute(context) && LocalDB.INSTANCE.getCoachTips(context)) {
            if (soundPool != null && soundMap != null && audioManager != null) {
                soundPool.play((soundMap.get(sound_index))
                        , 1.0F
                        , 1.0F, 1, 0, 1.0f);

//            Log.e("--play sound-", "--played--");
            }
        }
    }

    public void unInit() {
        if (ttsSoundPool != null) {
            ttsSoundPool.release();
        }
    }

}
