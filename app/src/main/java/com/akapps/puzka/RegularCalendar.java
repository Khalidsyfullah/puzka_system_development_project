package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.securepreferences.SecurePreferences;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegularCalendar extends Fragment{
    TextView previous, next, month_year;
    GridView gridView;
    Calendar calendar;
    int cur_month, cur_year;
    Context myContex;
    int beginning_of_month = 0;
    int ending_of_month = 0;
    SharedPreferences sharedPreferences;
    View.OnClickListener listener1, listener2;
    ArrayList<DiaryClass> diaryClasses = new ArrayList<>();
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_regularcalendar, container, false);
        previous = view.findViewById(R.id.textView11);
        next = view.findViewById(R.id.textView12);
        month_year = view.findViewById(R.id.textView13);
        gridView = view.findViewById(R.id.grid_calendar);
        calendar = Calendar.getInstance(Locale.getDefault());
        sharedPreferences = new SecurePreferences(myContex);

        listener1 = v -> {
            cur_month--;
            if(cur_month < 0){
                cur_month = 11;
                cur_year--;
            }
            calendar.set(Calendar.MONTH, cur_month);
            calendar.set(Calendar.YEAR, cur_year);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            updateCalendar();
        };

        listener2 = v-> {
            cur_month++;
            if(cur_month > 11){
                cur_month = 0;
                cur_year++;
            }
            calendar.set(Calendar.MONTH, cur_month);
            calendar.set(Calendar.YEAR, cur_year);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            updateCalendar();
        };

        previous.setOnClickListener(listener1);
        next.setOnClickListener(listener2);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Date date = new Date();
        calendar.setTime(date);
        cur_month = calendar.get(Calendar.MONTH);
        cur_year = calendar.get(Calendar.YEAR);
        updateCalendar();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContex = context;
    }
    @SuppressLint("SimpleDateFormat")
    void updateCalendar()
    {
        int num = 0;
        ArrayList<Date> list = new ArrayList<>();
        month_year.setText(new SimpleDateFormat("MMM,yyyy").format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int count = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        calendar.set(Calendar.DAY_OF_MONTH, -count);

        while (count> -1){
            list.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            num++;
            count--;
        }
        int month = calendar.get(Calendar.MONTH);
        beginning_of_month = num;
        while (month == calendar.get(Calendar.MONTH))
        {
            list.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            num++;
        }
        ending_of_month = num - 1;
        if(num >= 35) num-= 7;
        while (num < 35){
            list.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            num++;
        }
        try {
            if(diaryClasses!= null && diaryClasses.size()> 0){
                diaryClasses.clear();
            }
            SQLiteDatabase db = myContex.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            String TABLE_NAME = "DiaryTable";
            String COMM1 = "SELECT COUNT(*) FROM "+ TABLE_NAME + " WHERE YEAR = "+ cur_year + " AND MONTH = "+
                    cur_month;
            String COMM2 = "SELECT * FROM "+ TABLE_NAME + " WHERE YEAR = "+ cur_year + " AND MONTH = "+ cur_month;
            Cursor cursor = db.rawQuery(COMM1, null);
            cursor.moveToFirst();
            int icount = cursor.getInt(0);
            if(icount > 0){
                cursor = db.rawQuery(COMM2, null);
                int mon_index = cursor.getColumnIndex("MONTH");
                int year_index = cursor.getColumnIndex("YEAR");
                int day_index = cursor.getColumnIndex("DAY");
                int id_index = cursor.getColumnIndex("ID");
                int body_index = cursor.getColumnIndex("Body");
                int title_index = cursor.getColumnIndex("Title");
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    DiaryClass diaryClass = new DiaryClass(cursor.getInt(year_index),
                            cursor.getInt(mon_index),
                            cursor.getInt(day_index),
                            cursor.getInt(id_index),
                            cursor.getString(title_index),
                            cursor.getString(body_index));
                    diaryClasses.add(diaryClass);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();
        }catch (Exception e){
            Toast.makeText(myContex, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        gridView.setAdapter(new CalendarBuilderDiary(myContex, list));
    }


    class CalendarBuilderDiary extends ArrayAdapter<Date> {
        LayoutInflater inflater;
        DiaryClass diaryClass;
        boolean isHandled = false;
        public CalendarBuilderDiary(@NonNull Context context, ArrayList<Date> days) {
            super(context, R.layout.layout_calendar_days, days);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.layout_calendar_days, parent, false);
            }

            TextView date_text = view.findViewById(R.id.textView21);
            LinearLayout linearLayout = view.findViewById(R.id.lll);
            linearLayout.setVisibility(View.INVISIBLE);

            Date date = getItem(position);
            Calendar newcalendar = Calendar.getInstance(Locale.getDefault());
            newcalendar.setTime(date);


            date_text.setTextColor(Color.BLUE);
            date_text.setGravity(Gravity.CENTER);



            if (cur_month != newcalendar.get(Calendar.MONTH) || cur_year != newcalendar.get(Calendar.YEAR)) {
                date_text.setTextColor(Color.parseColor("#C7D2E8"));
            }

            if(position < beginning_of_month){
                view.setOnClickListener(listener1);
            }
            else if(position > ending_of_month){
                view.setOnClickListener(listener2);
            }

            else {
                if(diaryClasses != null && diaryClasses.size() >0){
                    for(DiaryClass ddc: diaryClasses){
                        if(ddc.getYear() == newcalendar.get(Calendar.YEAR) &&
                                ddc.getMonth() == newcalendar.get(Calendar.MONTH) &&
                                ddc.getDay() == newcalendar.get(Calendar.DATE))
                        {
                            diaryClass = ddc;
                            isHandled = true;
                            linearLayout.setVisibility(View.VISIBLE);

                            view.setOnClickListener(v -> {
                                sharedPreferences.edit().putBoolean("diaryNew", false).apply();
                                sharedPreferences.edit().putInt("diaryIDf", diaryClass.getId()).apply();
                                ((DiaryActivity) requireActivity()).change_Activity();
                            });

                            break;
                        }
                    }
                    isHandled = false;
                }
                if(!isHandled){
                    view.setOnClickListener(v -> {
                        sharedPreferences.edit().putInt("Daydd", newcalendar.get(Calendar.DATE)).apply();
                        sharedPreferences.edit().putInt("Monthdd", newcalendar.get(Calendar.MONTH)).apply();
                        sharedPreferences.edit().putInt("Yeardd", newcalendar.get(Calendar.YEAR)).apply();
                        ((DiaryActivity) requireActivity()).change_Activity();
                    });
                }
            }


            date_text.setText(String.valueOf(newcalendar.get(Calendar.DATE)));
            return view;
        }
    }

}
