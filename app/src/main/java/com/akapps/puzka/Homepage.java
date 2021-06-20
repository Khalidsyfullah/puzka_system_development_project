package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class Homepage extends Fragment {
    TextView date_text, balance_text;
    GridView gridView;
    EditText notes_text;
    Button view_routine, view_balance, add_notes;
    final String TABLE_NAME1 = "DailyRoutineTable";
    final String TABLE_NAME2 = "WeeklyRoutineTable";
    final String TABLE_NAME3 = "AccountTable";
    final String TABLE_NAME4 = "NotesTable";
    Calendar calendar;
    int current_month = 0;
    int current_day = 0;
    int current_year = 0;
    int current_day_of_week = 1;
    ArrayList<Unordered> arrayList = new ArrayList<>();
    Type type1, type2;
    Context myContext;
    double total_balance_amount = 0.0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_homepage, container, false);
        date_text = view.findViewById(R.id.textView129);
        balance_text = view.findViewById(R.id.textVie2);
        gridView = view.findViewById(R.id.grid_layout);
        view_balance = view.findViewById(R.id.button35);
        view_routine = view.findViewById(R.id.button34);
        add_notes = view.findViewById(R.id.button36);
        notes_text = view.findViewById(R.id.editTextTextPersonName);
        calendar = Calendar.getInstance(Locale.getDefault());
        Date date = new Date();
        calendar.setTime(date);
        current_day = calendar.get(Calendar.DAY_OF_MONTH);
        current_month = calendar.get(Calendar.MONTH);
        current_year = calendar.get(Calendar.YEAR);
        current_day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        @SuppressLint("SimpleDateFormat") String datetext = new SimpleDateFormat("EEE, dd MMM yyyy")
                .format(calendar.getTime());
        date_text.setText(datetext);
        type1 = new TypeToken<ArrayList<RoutineController>>(){}.getType();
        type2 = new TypeToken<ArrayList<DailyRoutine>>(){}.getType();
        boolean isFirstTableEmpty = false;
        SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
        String str = "";
        String COMM1 = "SELECT COUNT(*) FROM "+TABLE_NAME1 + " WHERE Month = "+ current_month + " AND Year ="+ current_year
                + " AND Day ="+ current_day;
        String COMM2 = "SELECT * FROM "+TABLE_NAME1 + " WHERE Month = "+current_month + " AND Year ="+ current_year +
                " AND Day = "+ current_day;
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
                if(current_day == cursor.getInt(day_index)){
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
        String COMM = "SELECT * FROM "+ TABLE_NAME2 + " WHERE ID = "+ current_day_of_week;
        cursor = db.rawQuery(COMM, null);
        int id_index = cursor.getColumnIndex("ID");
        int value_index = cursor.getColumnIndex("Value");
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            if(cursor.getInt(id_index) == current_day_of_week){
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

            Collections.sort(arrayList);
        }

        gridView.setAdapter(new GridValFg());



        String COMM3 = "SELECT * FROM "+ TABLE_NAME3;

        cursor = db.rawQuery(COMM3, null);
        int balance_index = cursor.getColumnIndex("Balance");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            double dval = cursor.getDouble(balance_index);
            total_balance_amount += dval;
            cursor.moveToNext();
        }

        balance_text.setText(String.valueOf(total_balance_amount));
        cursor.close();
        db.close();

        add_notes.setOnClickListener(v -> {
            String string = notes_text.getText().toString();
            if(string.isEmpty()){
                notes_text.setError("Enter Valid Text!");
                return;
            }
            SQLiteDatabase db1 = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dr = formatter.format(date);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Date", dr);
            contentValues.put("Notes", string);
            db1.insert(TABLE_NAME4, null, contentValues);
            Toast.makeText(myContext, "Successfully Added!", Toast.LENGTH_SHORT).show();
            notes_text.setText("");
            db1.close();
        });

        view_routine.setOnClickListener(v -> ((Centerpage) requireActivity()).routinePage());

        view_balance.setOnClickListener(v -> ((Centerpage) requireActivity()).walletPage());



        return view;
    }

    class GridValFg extends BaseAdapter{

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(arrayList.get(position).isNoFound()){
                return getLayoutInflater().inflate(R.layout.grid_nullkkj, null);
            }
            View view = getLayoutInflater().inflate(R.layout.grid_schedule_routine, null);
            TextView tx1, tx2;
            RatingBar ratingBar;
            tx1 = view.findViewById(R.id.textView120);
            tx2 = view.findViewById(R.id.textView121);
            ratingBar = view.findViewById(R.id.ratingBar2);
            tx1.setTextColor(Color.BLACK);
            tx2.setTextColor(Color.BLACK);
            tx1.setTextSize(12);
            tx2.setTextSize(12);
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
