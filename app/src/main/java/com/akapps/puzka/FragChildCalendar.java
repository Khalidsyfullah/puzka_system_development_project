package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FragChildCalendar extends Fragment {
    TextView previous_month, next_month, monthtext, date_text, no_textFound;
    GridView gridView, all_details;
    Calendar calendar;
    Button save_btn, cancel_btn, delete_btn;
    int cur_month, cur_year;
    Context myContex;
    LinearLayout linearLayout;
    int beginning_of_month = 0;
    int ending_of_month = 0;
    View.OnClickListener listener1, listener2;
    TextInputLayout title, date, start_time, end_time;
    TextInputEditText s_text, t_text;
    AutoCompleteTextView importance;
    ArrayList<RoutineSaver> saverList = new ArrayList<>();
    int importance_count = 1;
    int month_to_update = 0;
    int day_to_update = 0;
    int year_to_update = 0;
    int selected_index = 0;
    ArrayList<RoutineController> rcList = new ArrayList<>();
    String[] importance_level = new String[]{"Yellow (Less Important)", "Blue (Moderate Important)", "Red (Most Important)"};
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragchild_calendar, container ,false);
        monthtext = view.findViewById(R.id.textView13);
        previous_month = view.findViewById(R.id.textView11);
        next_month = view.findViewById(R.id.textView12);
        date_text = view.findViewById(R.id.textView104);
        no_textFound = view.findViewById(R.id.textView107);
        gridView = view.findViewById(R.id.grid_calendar);
        all_details = view.findViewById(R.id.grid_details);
        title = view.findViewById(R.id.outlinedTextField);
        date = view.findViewById(R.id.outlinedTextField4);
        save_btn = view.findViewById(R.id.button16);
        cancel_btn = view.findViewById(R.id.button20);
        delete_btn = view.findViewById(R.id.button21);
        start_time = view.findViewById(R.id.outlinedTextField1);
        end_time = view.findViewById(R.id.outlinedTextField2);
        linearLayout = view.findViewById(R.id.ln);
        s_text = view.findViewById(R.id.ty1);
        t_text = view.findViewById(R.id.ty2);
        importance = view.findViewById(R.id.filled_exposed_dropdown1);
        all_details.setVisibility(View.GONE);
        no_textFound.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        ArrayAdapter<String > arrayAdapter = new ArrayAdapter<>(myContex, R.layout.dropdown_menu_popup_item,
                importance_level);
        importance.setAdapter(arrayAdapter);

        importance.setOnItemClickListener((parent, view1, position, id) -> importance_count = position+1);

        calendar = Calendar.getInstance(Locale.getDefault());
        Date date = new Date();
        calendar.setTime(date);
        cur_month = calendar.get(Calendar.MONTH);
        cur_year = calendar.get(Calendar.YEAR);
        buildUpdatedList(cur_year, cur_month);

        edit_Calendar();
        listener1 = v -> {
            cur_month--;
            if(cur_month < 0){
                cur_month = 11;
                cur_year--;
            }
            calendar.set(Calendar.MONTH, cur_month);
            calendar.set(Calendar.YEAR, cur_year);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            buildUpdatedList(cur_year, cur_month);
            edit_Calendar();
        };

        listener2 = v -> {
            cur_month++;
            if(cur_month > 11){
                cur_month = 0;
                cur_year++;
            }
            calendar.set(Calendar.MONTH, cur_month);
            calendar.set(Calendar.YEAR, cur_year);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            buildUpdatedList(cur_year, cur_month);
            edit_Calendar();
        };

        previous_month.setOnClickListener(listener1);
        next_month.setOnClickListener(listener2);

        s_text.setOnClickListener(v -> {
            DialogFragment newFragment = new RoutineCerterpage.TimePickerFragment(start_time);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });

        t_text.setOnClickListener(v -> {
            DialogFragment newFragment = new RoutineCerterpage.TimePickerFragment(end_time);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });

        save_btn.setOnClickListener(v -> {
            String sr1 = Objects.requireNonNull(title.getEditText()).getText().toString();
            if(sr1.isEmpty()){
                title.setError("Required!");
                return;
            }
            String sr2 = Objects.requireNonNull(start_time.getEditText()).getText().toString();
            String sr3 = Objects.requireNonNull(end_time.getEditText()).getText().toString();

            String[] psr1 = sr2.split(":");
            int t1 = Integer.parseInt(psr1[0]);
            int t2 = Integer.parseInt(psr1[1]);

            String[] psr2 = sr3.split(":");
            int t3 = Integer.parseInt(psr2[0]);
            int t4 = Integer.parseInt(psr2[1]);

            rcList.get(selected_index).setBegin_hour(t1);
            rcList.get(selected_index).setBegin_minute(t2);
            rcList.get(selected_index).setEnd_hour(t3);
            rcList.get(selected_index).setEnd_minute(t4);
            rcList.get(selected_index).setImportance_level(importance_count);
            rcList.get(selected_index).setTitle(sr1);

            String gson_str = new Gson().toJson(rcList);
            SQLiteDatabase db = myContex.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            final String TABLE_NAME = "DailyRoutineTable";

            String COMM = "UPDATE "+TABLE_NAME+ " SET Routine = '"+ gson_str + "' WHERE Day = "+ day_to_update + " AND " +
                    "Month = "+month_to_update + " AND Year = "+ year_to_update;

            db.execSQL(COMM);
            Toast.makeText(myContex, "Successfully Updated!", Toast.LENGTH_LONG).show();
            linearLayout.setVisibility(View.GONE);


        });

        delete_btn.setOnClickListener(v -> Toast.makeText(myContex, "Hold To Delete!", Toast.LENGTH_SHORT).show());

        delete_btn.setOnLongClickListener(v -> {
            SQLiteDatabase db = myContex.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            final String TABLE_NAME = "DailyRoutineTable";
            if(rcList.size() == 1){
                String COMM1 = "DELETE FROM "+TABLE_NAME + " WHERE Day = "+day_to_update + " AND Month = "+ month_to_update+
                        " AND Year = "+ year_to_update;
                db.execSQL(COMM1);
            }
            else{
                rcList.remove(selected_index);
                String gson_str = new Gson().toJson(rcList);
                String COMM = "UPDATE "+TABLE_NAME+ " SET Routine = '"+ gson_str + "' WHERE Day = "+ day_to_update + " AND " +
                        "Month = "+month_to_update + " AND Year = "+ year_to_update;

                db.execSQL(COMM);
            }
            Toast.makeText(myContex, "Successfully Deleted!", Toast.LENGTH_LONG).show();
            linearLayout.setVisibility(View.GONE);
            return true;
        });

        cancel_btn.setOnClickListener(v -> linearLayout.setVisibility(View.GONE));

        return view;
    }



    void buildUpdatedList(int y, int m)
    {
        if(saverList.size() > 0){
            saverList.clear();
        }
        all_details.setVisibility(View.GONE);
        no_textFound.setVisibility(View.GONE);
        String text = "";
        SQLiteDatabase db = myContex.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
        final String TABLE_NAME = "DailyRoutineTable";
        String COMM1 = "SELECT COUNT(*) FROM "+TABLE_NAME + " WHERE Month = "+m + " AND Year ="+ y;
        String COMM2 = "SELECT * FROM "+TABLE_NAME + " WHERE Month = "+m + " AND Year ="+ y;
        Cursor cursor = db.rawQuery(COMM1, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount > 0){
            cursor = db.rawQuery(COMM2, null);
            int day_index = cursor.getColumnIndex("Day");
            int month_index = cursor.getColumnIndex("Month");
            int year_index = cursor.getColumnIndex("Year");
            int title_index = cursor.getColumnIndex("Routine");
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                RoutineSaver routineSaver = new RoutineSaver(cursor.getInt(day_index),
                        cursor.getInt(month_index),
                        cursor.getInt(year_index),
                        cursor.getString(title_index));
                saverList.add(routineSaver);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
    }



    @SuppressLint("SimpleDateFormat")
    void edit_Calendar()
    {
        int num = 0;
        ArrayList<Date> list = new ArrayList<>();
        monthtext.setText(new SimpleDateFormat("MMM,yyyy").format(calendar.getTime()));
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
        while (num < 42){
            list.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            num++;
        }

        gridView.setAdapter(new GridClass(list));
    }


    class GridClass extends BaseAdapter {
        ArrayList<Date> list;
        ArrayList<Boolean> flag = new ArrayList<>();
        public GridClass(ArrayList<Date> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_advancedcalendar, null);
            TextView textView = view.findViewById(R.id.textView106);
            View red, yellow, blue;
            red = view.findViewById(R.id.view11);
            yellow = view.findViewById(R.id.view12);
            blue = view.findViewById(R.id.view10);
            Date date = list.get(position);
            flag.add(false);
            Calendar newcalendar = Calendar.getInstance(Locale.getDefault());
            newcalendar.setTime(date);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            red.setVisibility(View.INVISIBLE);
            blue.setVisibility(View.INVISIBLE);
            yellow.setVisibility(View.INVISIBLE);

            if (cur_month != newcalendar.get(Calendar.MONTH) || cur_year != newcalendar.get(Calendar.YEAR)) {
                textView.setTextColor(Color.parseColor("#C7D2E8"));
            }

            if (position < beginning_of_month) {
                view.setOnClickListener(listener1);
                red.setVisibility(View.INVISIBLE);
                yellow.setVisibility(View.INVISIBLE);
                blue.setVisibility(View.INVISIBLE);
            }
            else if (position > ending_of_month) {
                view.setOnClickListener(listener2);
                red.setVisibility(View.INVISIBLE);
                yellow.setVisibility(View.INVISIBLE);
                blue.setVisibility(View.INVISIBLE);
            }
            else {
                String text = "";
                ArrayList<RoutineController> sdL;
                Type type = new TypeToken<ArrayList<RoutineController>>(){}.getType();
                for(int i = 0; i<saverList.size(); i++){
                    if(saverList.get(i).getDay() == newcalendar.get(Calendar.DAY_OF_MONTH)){
                        text = saverList.get(i).getList();
                        break;
                    }
                }
                if(!text.isEmpty()){
                    sdL = new Gson().fromJson(text, type);
                    if(sdL.size()> 0) {
                        flag.set(position, true);
                        for(RoutineController rc: sdL){
                            if(rc.getImportance_level() == 3){
                                if(red.getVisibility() == View.INVISIBLE) red.setVisibility(View.VISIBLE);
                            }
                            else if(rc.getImportance_level() == 2){
                                if(blue.getVisibility() == View.INVISIBLE) blue.setVisibility(View.VISIBLE);
                            }
                            else if(rc.getImportance_level() == 1){
                                if(yellow.getVisibility() == View.INVISIBLE) yellow.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }



                view.setOnClickListener(v -> {
                    date_text.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(newcalendar.getTime()));

                    if(flag.get(position)){
                        if(rcList.size() > 0) rcList.clear();
                        String texttr = "";
                        int num = 0;
                        for(int i = 0; i<saverList.size(); i++){
                            if(saverList.get(i).getDay() == newcalendar.get(Calendar.DAY_OF_MONTH)){
                                texttr = saverList.get(i).getList();
                                num = i;
                                break;
                            }
                        }

                        rcList = new Gson().fromJson(texttr, type);
                        if(rcList.size() > 0) Collections.sort(rcList);
                        if(no_textFound.getVisibility() == View.VISIBLE) no_textFound.setVisibility(View.GONE);
                        if(all_details.getVisibility() == View.GONE) all_details.setVisibility(View.VISIBLE);
                        all_details.setAdapter(new DayScheduleGrid(rcList));
                        month_to_update = saverList.get(num).getMonth();
                        year_to_update = saverList.get(num).getYear();
                        day_to_update = saverList.get(num).getDay();
                    }
                    else{
                        if(all_details.getVisibility() == View.VISIBLE) all_details.setVisibility(View.GONE);
                        if(no_textFound.getVisibility() == View.GONE) no_textFound.setVisibility(View.VISIBLE);
                    }
                });

            }
            textView.setText(String.valueOf(newcalendar.get(Calendar.DATE)));

            return view;
        }
    }



    class DayScheduleGrid extends BaseAdapter{
        ArrayList<RoutineController> bList;

        public DayScheduleGrid(ArrayList<RoutineController> bList) {
            this.bList = bList;
        }

        @Override
        public int getCount() {
            return bList.size();
        }

        @Override
        public Object getItem(int position) {
            return bList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_tasks, null);
            RatingBar ratingBar = view.findViewById(R.id.ratingBar);
            TextView textView = view.findViewById(R.id.textView110);
            String str = "<u><font color='blue'>"+ bList.get(position).getTitle() + "</font></u><br><br><b>Start Time: </b>"+
                    bList.get(position).getBegin_hour()+ ":"+ bList.get(position).getBegin_minute()+ "<br><b> End Time:  </b>"+
                    bList.get(position).getEnd_hour() + ":"+bList.get(position).getEnd_minute();
            ratingBar.setRating(bList.get(position).getImportance_level());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
            }else {
                textView.setText(Html.fromHtml(str));
            }
            view.setOnClickListener(v -> {
                date.setEnabled(false);
                linearLayout.setVisibility(View.VISIBLE);
                String sd = day_to_update + "/"+month_to_update+ "/"+year_to_update;
                Objects.requireNonNull(date.getEditText()).setText(sd);
                String sdf;
                selected_index = position;
                sdf = bList.get(position).getBegin_hour()+ ":"+bList.get(position).getBegin_minute();
                Objects.requireNonNull(start_time.getEditText()).setText(sdf);
                sdf = bList.get(position).getEnd_hour() + ":"+ bList.get(position).getEnd_minute();
                Objects.requireNonNull(end_time.getEditText()).setText(sdf);
                Objects.requireNonNull(title.getEditText()).setText(bList.get(position).getTitle());
                importance.setText(importance_level[bList.get(position).getImportance_level()-1], false);
                importance_count = bList.get(position).getImportance_level();
            });

            return view;
        }
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContex = context;
    }
}
