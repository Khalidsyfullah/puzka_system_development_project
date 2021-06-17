package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

public class RoutineCerterpage extends AppCompatActivity {
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_routine_certerpage);
        tabLayout = findViewById(R.id.tablayout);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_lay, new RoutineSchedule()).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_lay, new RoutineSchedule()).commit();
                }

                else if(tab.getPosition() == 1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_lay, new RoutinemainRoute()).commit();
                }
                else if(tab.getPosition() == 2){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_lay, new RoutineAdjust()).commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        TextInputLayout textInputLayout;

        public TimePickerFragment(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String str = hourOfDay + ":"+ minute;
            Objects.requireNonNull(textInputLayout.getEditText()).setText(str);
        }
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        TextInputLayout textInputLayout;

        public DatePickerFragment(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            int m = month + 1;
            String mont = String.valueOf(m);
            if(m< 10){
                mont = "0"+mont;
            }
            String ds = String.valueOf(day);
            if(day< 10){
                ds = "0"+ds;
            }
            String date = ds + "/"+mont + "/"+ year;
            Objects.requireNonNull(textInputLayout.getEditText()).setText(date);
        }
    }


}