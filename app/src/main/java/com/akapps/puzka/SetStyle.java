package com.akapps.puzka;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.securepreferences.SecurePreferences;

import org.jetbrains.annotations.NotNull;

public class SetStyle extends Fragment {
    TextView cancel, save_txt;
    SharedPreferences sharedPreferences;
    RadioGroup radioGroup;
    int number = 0;
    Context myContext;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.childfrag_setstyle, container, false);
        cancel = view.findViewById(R.id.textView10);
        save_txt = view.findViewById(R.id.textView27);
        cancel.setOnClickListener(v -> ((CreateNotepad) requireActivity()).hideFrame());
        radioGroup = view.findViewById(R.id.radiogrp);
        sharedPreferences = new SecurePreferences(myContext);
        number = sharedPreferences.getInt("getStyuuu", 0);
        if(number == 0){
            radioGroup.check(R.id.radioButton);
        }
        else if(number == 1){
            radioGroup.check(R.id.radioButton8);
        }
        else if(number == 2){
            radioGroup.check(R.id.radioButton9);
        }
        else{
            radioGroup.check(R.id.radioButton10);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton){
                    number = 0;
                }
                else if(checkedId == R.id.radioButton8){
                    number = 1;
                }
                else if(checkedId == R.id.radioButton9){
                    number = 2;
                }
                else{
                    number = 3;
                }

            }
        });

        save_txt.setOnClickListener(v -> {
            sharedPreferences.edit().putInt("getStyuuu", number).apply();
            ((CreateNotepad) requireActivity()).update_style_number(number);
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
