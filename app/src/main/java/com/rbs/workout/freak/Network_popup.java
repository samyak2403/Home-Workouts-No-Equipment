package com.rbs.workout.freak;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.rbs.workout.freak.databinding.NetworkPopupBinding;


public class Network_popup {

    private final Dialog dialog;
    private boolean isClicked = false;


    public Network_popup(Context context, boolean b, String s, String s1, String btn, OnPopupClickLisnter onPopupClickLisnter) {
        dialog = new Dialog(context, R.style.CustomBottomSheetStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        NetworkPopupBinding popupbinding = DataBindingUtil.inflate(inflater, R.layout.network_popup, null, false);
        dialog.setCancelable(false);
        dialog.setContentView(popupbinding.getRoot());
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupbinding.tvText.setText(s);
        popupbinding.tvText1.setText(s1);


        popupbinding.btncountinue.setText(btn);

        popupbinding.btncountinue.setOnClickListener(v -> {
            isClicked = true;
            dialog.dismiss();

        });

        dialog.setOnDismissListener(dialog -> {
            if (isClicked) {
                onPopupClickLisnter.onClickCountinue();
            } else {
                onPopupClickLisnter.onClickNo();
            }
        });
    }

    public interface OnPopupClickLisnter {
        void onClickCountinue();

        void onClickNo();
    }
}
