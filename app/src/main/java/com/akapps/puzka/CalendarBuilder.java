package com.akapps.puzka;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarBuilder extends ArrayAdapter<Date> {

    LayoutInflater inflater;
    public CalendarBuilder(@NonNull Context context, ArrayList<Date> days) {
        super(context, R.layout.layout_calendar_days, days);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null){
            view = inflater.inflate(R.layout.layout_calendar_days, parent, false);
        }
        TextView date_text = view.findViewById(R.id.textView21);
        Calendar calendar = Calendar.getInstance();
        Date date = getItem(position);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        date_text.setTextColor(Color.BLUE);

        Date today = new Date();
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(today);

        if (month != calendarToday.get(Calendar.MONTH) || year != calendarToday.get(Calendar.YEAR)) {
            date_text.setTextColor(Color.parseColor("#C7D2E8"));
        } else if (day == calendarToday.get(Calendar.DATE)) {
            date_text.setTextColor(Color.MAGENTA);
            date_text.setGravity(Gravity.CENTER);
        }


        date_text.setText(String.valueOf(calendar.get(Calendar.DATE)));
        return view;
    }
}
