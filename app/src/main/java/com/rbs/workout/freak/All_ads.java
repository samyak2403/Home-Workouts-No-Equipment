package com.rbs.workout.freak;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import thehexcoders.google.android.ads.nativetemplates.NativeTemplateStyle;
import thehexcoders.google.android.ads.nativetemplates.TemplateView;

public class All_ads {
    private final Context context;
    private InterstitialAd interstitialAd1;
    public All_ads(Context context) {
        this.context = context;
        MobileAds.initialize(context, initializationStatus -> {
        });
    }





    public void loadNativeAd(TemplateView template) {
        MobileAds.initialize(context);
        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.native_advanced_ad_keys))
                .forNativeAd(nativeAd -> {
                    ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.white));
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
//                        TemplateView template = findViewById(R.id.my_template);
                    template.setVisibility(View.VISIBLE);
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                })
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }


//    public void loadNativeSmallAd(TemplateView template) {
//        MobileAds.initialize(context);
//        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.small_native_advanced_ad_keys))
//                .forNativeAd(nativeAd -> {
//                    ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.white));
//                    NativeTemplateStyle styles = new
//                            NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
////                        TemplateView template = findViewById(R.id.my_template);
//                    template.setVisibility(View.VISIBLE);
//                    template.setStyles(styles);
//                    template.setNativeAd(nativeAd);
//                })
//                .build();
//        adLoader.loadAd(new AdRequest.Builder().build());
//    }



}
