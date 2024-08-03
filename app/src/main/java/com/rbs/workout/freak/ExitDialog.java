package com.rbs.workout.freak;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import thehexcoders.google.android.ads.nativetemplates.TemplateView;

public class ExitDialog extends Dialog {

    public static void showExitDialog(Activity activity) {
        new ExitDialog(activity).show();
    }

    private final Activity context;
    TemplateView templateView;
    All_ads adsHelper;

    public ExitDialog(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        if (window != null)
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.exit_dialog);
        adsHelper = new All_ads(context);
        init();
    }

    private void init() {
        templateView = findViewById(R.id.my_template);
        findViewById(R.id.tv_rate).setOnClickListener(v -> {
            rateApp();
            dismissAfterSomeTime();
        });
        findViewById(R.id.tv_yes).setOnClickListener(v -> context.finishAffinity());
        findViewById(R.id.tv_no).setOnClickListener(v -> dismissAfterSomeTime());

            adsHelper.loadNativeAd(templateView);
    }

    private void rateApp() {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
    }

    private void dismissAfterSomeTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowing())
                    dismiss();
            }
        }, 250);
    }
}
