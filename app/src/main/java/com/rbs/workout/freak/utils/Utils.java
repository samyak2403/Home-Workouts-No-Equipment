package com.rbs.workout.freak.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rbs.workout.freak.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {

    public static void setPref(Context c, String pref, Boolean val) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.putBoolean(pref, val);
        e.apply();
    }

    public static boolean getPref(Context c, String pref, Boolean val) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(pref, val);
    }

    public static void setPref(Context context, String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value);
        editor.apply();

    }

    public static String getPref(Context context, String key, String value) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, value);
    }

    public static class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.rcy_divider_line);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    public static int REQUEST_WRITE_STORAGE_REQUEST_CODE = 111;

    public static boolean checkPermission(Context context) {

        if (hasReadPermissions(context) && hasWritePermissions(context)) {
            return true;
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
            return false;
        }
    }

    private static boolean hasReadPermissions(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private static boolean hasWritePermissions(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static File createPackageDir(Context context, String dirName) {
        File file = new File(context.getFilesDir() + File.separator + dirName);
        if (!file.exists()) {
            file.mkdir();
            Utils.setPref(context, ConstantString.SHARE_IMAGE_PATH, file.getAbsolutePath());
        }
        return file;
    }

    /**
     * TODO Get Images From Assets
     */
    public static ArrayList<String> getAssetItems(Context mContext, String categoryName) {
        ArrayList<String> arrayList = new ArrayList<>();
        String[] imgPath;
        AssetManager assetManager = mContext.getAssets();
        try {
            imgPath = assetManager.list(categoryName);
            if (imgPath != null) {
                for (String anImgPath : imgPath) {
                    arrayList.add("///android_asset/" + categoryName + "/" + anImgPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * Todo Dynamic adMod ad initialize, show and request to load methods
     **/


    public static String ReplaceSpacialCharacters(String string) {
        return string.replace(" ", "").replace("&", "").replace("-", "").replace("'", "");
    }

    public static void openWebsite(Context c, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        c.startActivity(browserIntent);
    }





}
