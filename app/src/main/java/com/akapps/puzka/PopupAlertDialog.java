package com.akapps.puzka;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

public class PopupAlertDialog extends Dialog {
    Activity activity;
    Button bt1, bt2;
    public PopupAlertDialog(@NonNull Context context) {
        super(context);
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_alertdialog);
        bt1 = findViewById(R.id.button17);
        bt2 = findViewById(R.id.button15);
        this.setCancelable(false);
        bt1.setOnClickListener(v -> activity.finish());
        bt2.setOnClickListener(v -> this.dismiss());
    }
}
