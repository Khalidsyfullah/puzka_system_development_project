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

public class SetTextsize extends Fragment {
    TextView cancelb, save_txt;
    int number = 0;
    RadioGroup radioGroup;
    SharedPreferences sharedPreferences;
    Context myContext;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.childfrag_settextsize, container, false);
        cancelb = view.findViewById(R.id.textView10);
        save_txt = view.findViewById(R.id.textView28);
        sharedPreferences = new SecurePreferences(myContext);
        number = sharedPreferences.getInt("texsdrt", 0);
        radioGroup = view.findViewById(R.id.rdgrp);

        if(number == 0){
            radioGroup.check(R.id.radioButton12);
        }
        else if(number == 1){
            radioGroup.check(R.id.radioButton14);
        }
        else if(number == 2){
            radioGroup.check(R.id.radioButton15);
        }
        else{
            radioGroup.check(R.id.radioButton16);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radioButton12){
                number = 0;
            }
            else if(checkedId == R.id.radioButton14){
                number = 1;
            }
            else if(checkedId == R.id.radioButton15){
                number = 2;
            }
            else{
                number = 3;
            }

        });

        save_txt.setOnClickListener(v -> {
            sharedPreferences.edit().putInt("texsdrt", number).apply();
            ((CreateNotepad) requireActivity()).update_text_size(number);
        });

        cancelb.setOnClickListener(v -> ((CreateNotepad) requireActivity()).hideFrame());
        return view;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
