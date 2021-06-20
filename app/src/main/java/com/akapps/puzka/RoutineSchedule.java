package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

public class RoutineSchedule extends Fragment {
    ChipGroup chipGroup;
    Chip[] chips = new Chip[4];
    GridView gridView;
    final String TABLE_NAME1 = "DailyRoutineTable";
    final String TABLE_NAME2 = "WeeklyRoutineTable";
    Calendar calendar;
    Context myContext;
    int current_selected_num = 1;
    int current = 0;
    Type type1, type2;
    ArrayList<Unordered> arrayList = new ArrayList<>();
    View.OnClickListener listener;
    Date date;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.rfrag_schedulepage, container, false);
        chips[0] = view.findViewById(R.id.chip4);
        chips[1] = view.findViewById(R.id.chip5);
        chips[2] = view.findViewById(R.id.chip6);
        chips[3] = view.findViewById(R.id.chip7);
        chipGroup = view.findViewById(R.id.chu);
        chipGroup.check(R.id.chip4);

        type1 = new TypeToken<ArrayList<RoutineController>>(){}.getType();
        type2 = new TypeToken<ArrayList<DailyRoutine>>(){}.getType();


        gridView = view.findViewById(R.id.grid_lay);
        calendar = Calendar.getInstance(Locale.getDefault());
        date = new Date();
        calendar.setTime(date);

        listener = v -> {
            int num = 0;
            Chip chip = (Chip) v;
            for(int i=0 ; i<4; i++){
                if(chip == chips[i]){
                    num = i;
                    if(current == i) return;
                    current = i;
                    break;
                }
            }
            if(arrayList.size() > 0)
            {
                arrayList.clear();
            }

            calendar.setTime(date);
            if(num == 0){
                current_selected_num = 1;
            }
            else if(num == 1){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                current_selected_num = 1;
            }
            else if(num == 2){
                current_selected_num = 3;
            }
            else{
                current_selected_num = 7;
            }
            getReady();
        };

        for(int i=0; i<4; i++){
            chips[i].setOnClickListener(listener);
        }

        getReady();



        return view;
    }

    void getReady()
    {
        int current_month = calendar.get(Calendar.MONTH);
        int current_year = calendar.get(Calendar.YEAR);
        int current_day = calendar.get(Calendar.DAY_OF_MONTH);
        int week_of_day = calendar.get(Calendar.DAY_OF_WEEK);
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("EEE, dd MMM yyyy")
                .format(calendar.getTime());

        arrayList.add(new Unordered(true, date));
        int current_beginning = arrayList.size();
        getLayoutList(current_beginning, current_month, current_year, current_day, week_of_day);
    }


    void getLayoutList(int current_beginning, int m, int y, int d, int w)
    {
        boolean isFirstTableEmpty = false;
        SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
        String str = "";
        String COMM1 = "SELECT COUNT(*) FROM "+TABLE_NAME1 + " WHERE Month = "+m + " AND Year ="+ y + " AND Day ="+ d;
        String COMM2 = "SELECT * FROM "+TABLE_NAME1 + " WHERE Month = "+m + " AND Year ="+ y + " AND Day = "+d;
        Cursor cursor = db.rawQuery(COMM1, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount > 0){
            cursor = db.rawQuery(COMM2, null);
            int day_index = cursor.getColumnIndex("Day");
            int title_index = cursor.getColumnIndex("Routine");
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                if(d == cursor.getInt(day_index)){
                    str = cursor.getString(title_index);
                    break;
                }
                cursor.moveToNext();
            }
        }
        if(str.isEmpty()){
            isFirstTableEmpty = true;
        }
        else{
            ArrayList<RoutineController> list = new Gson().fromJson(str, type1);
            for(RoutineController rc: list){
                arrayList.add(new Unordered(false, false, false, "", rc.getBegin_hour(), rc.getBegin_minute(),
                        rc.getEnd_hour(), rc.getEnd_minute(), rc.getTitle(), "", rc.getImportance_level()));
            }
        }
        str = "";
        String COMM = "SELECT * FROM "+ TABLE_NAME2 + " WHERE ID = "+ w;
        cursor = db.rawQuery(COMM, null);
        int id_index = cursor.getColumnIndex("ID");
        int value_index = cursor.getColumnIndex("Value");
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            if(cursor.getInt(id_index) == w){
                str = cursor.getString(value_index);
                break;
            }
            cursor.moveToNext();
        }

        if(str.equals("nil") && isFirstTableEmpty){
            arrayList.add(new Unordered(true));
        }
        else{
            ArrayList<DailyRoutine> list = new Gson().fromJson(str, type2);
            for(DailyRoutine dr: list){
                arrayList.add(new Unordered(false, false, true, "", dr.getBegin_hour(), dr.getBegin_minute(),
                        dr.getEnd_hour(), dr.getEnd_minute(), dr.getTitle(), dr.getDetails(), 0));
            }

            Collections.sort(arrayList.subList(current_beginning, arrayList.size()));
        }
        current_selected_num--;
        if(current_selected_num > 0){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            getReady();
        }
        else{
            gridView.setAdapter(new GridFDF(arrayList));
        }

        cursor.close();
        db.close();

    }

    class GridFDF extends BaseAdapter{
        ArrayList<Unordered> oishyList;

        public GridFDF(ArrayList<Unordered> oishyList) {
            this.oishyList = oishyList;
        }

        @Override
        public int getCount() {
            return oishyList.size();
        }

        @Override
        public Object getItem(int position) {
            return oishyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(oishyList.get(position).isNoFound()){
                return getLayoutInflater().inflate(R.layout.grid_nullkkj, null);
            }
            if(oishyList.get(position).isDaysIndicator()){
                View view = getLayoutInflater().inflate(R.layout.grid_schedule_days, null);
                TextView textView = view.findViewById(R.id.textView119);
                textView.setText(arrayList.get(position).getDate());
                return view;
            }
            View view = getLayoutInflater().inflate(R.layout.grid_schedule_routine, null);
            TextView tx1, tx2;
            RatingBar ratingBar;
            tx1 = view.findViewById(R.id.textView120);
            tx2 = view.findViewById(R.id.textView121);
            ratingBar = view.findViewById(R.id.ratingBar2);

            if(!arrayList.get(position).isDailyRoutine()){
                ratingBar.setRating(arrayList.get(position).getImportance_level());
            }
            else{
                ratingBar.setNumStars(1);
                ratingBar.setRating(1);
                String st = "From Weekly Routine: ";
                tx2.setText(st);
            }
            String begin_time = arrayList.get(position).getBegin_hour() + ":"+ arrayList.get(position)
                    .getBegin_minute();
            String end_time = arrayList.get(position).getEnd_hour() + ":"+ arrayList.get(position)
                    .getEnd_minute();

            String string_final = "<b>Title: </b> "+arrayList.get(position).getTitle() +"<br>"+ "<b>Start " +
                    "Time: </b>"+ begin_time + "<br><b>End Time: </b>"+ end_time;


            if(arrayList.get(position).isDailyRoutine()){
                string_final = string_final +"<br><b>Details: </b><i>"+ arrayList.get(position).getDetails() +
                        "</i>";
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tx1.setText(Html.fromHtml(string_final, Html.FROM_HTML_MODE_COMPACT));
            }else {
                tx1.setText(Html.fromHtml(string_final));
            }

            return view;
        }
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }



}
