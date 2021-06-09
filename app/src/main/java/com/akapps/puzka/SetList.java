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

public class SetList extends Fragment {
    TextView cancel_text, save_txt;
    int number = 0;
    RadioGroup radioGroup;
    SharedPreferences sharedPreferences;
    Context myContext;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.childfrag_setlist, container, false);
        cancel_text = view.findViewById(R.id.textView10);
        save_txt = view.findViewById(R.id.textView24);
        radioGroup = view.findViewById(R.id.uiiif);

        sharedPreferences = new SecurePreferences(myContext);
        number = sharedPreferences.getInt("getlkkkk", 0);
        if(number == 0){
            radioGroup.check(R.id.radioButton17);
        }
        else if(number == 1){
            radioGroup.check(R.id.radioButton18);
        }
        else if(number == 2){
            radioGroup.check(R.id.radioButton19);
        }
        else{
            radioGroup.check(R.id.radioButton20);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radioButton17){
                number = 0;
            }
            else if(checkedId == R.id.radioButton18){
                number = 1;
            }
            else if(checkedId == R.id.radioButton19){
                number = 2;
            }
            else{
                number = 3;
            }
        });

        save_txt.setOnClickListener(v -> {
            sharedPreferences.edit().putInt("getlkkkk", number).apply();
            ((CreateNotepad) requireActivity()).update_list_style(number);
        });


        cancel_text.setOnClickListener(v -> ((CreateNotepad) requireActivity()).hideFrame());
        return view;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
