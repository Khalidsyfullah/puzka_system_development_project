package com.akapps.puzka;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class PopupLoadingScreen extends Dialog {
    String string;
    public PopupLoadingScreen(@NonNull Context context, String string) {
        super(context);
        this.string = string;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_loadingscreen);
        TextView textView = findViewById(R.id.textView70);
        textView.setText(string);
        this.setCancelable(false);
    }
}
