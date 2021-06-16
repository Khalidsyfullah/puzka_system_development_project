package com.akapps.puzka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Objects;

public class FragChildRegular extends Fragment {
    String[] importance_level1 = new String[]{"Yellow (Less Important)", "Blue (Moderate Important)", "Red (Most Important)"};
    TextInputLayout title1, date1, start_time1, end_time1;
    TextInputEditText s_text1, t_text1, d_text1;
    Button add_btn;
    AutoCompleteTextView importance1;
    Context myContex;
    int importance_count = 1;
    RoutineSaver routineSaver;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragchild_regular, container, false);
        title1 = view.findViewById(R.id.outlinedTextField);
        date1 = view.findViewById(R.id.outlinedTextField6);
        start_time1 = view.findViewById(R.id.outlinedTextField10);
        end_time1 = view.findViewById(R.id.outlinedTextField20);
        s_text1 = view.findViewById(R.id.jk1);
        t_text1 = view.findViewById(R.id.jk2);
        d_text1 = view.findViewById(R.id.jk3);
        importance1 = view.findViewById(R.id.filled_exposed_dropdown2);
        add_btn = view.findViewById(R.id.button160);
        importance1.setOnItemClickListener((parent, view1, position, id) -> importance_count = position+1);

        ArrayAdapter<String > arrayAdapter = new ArrayAdapter<>(myContex, R.layout.dropdown_menu_popup_item,
                importance_level1);
        importance1.setAdapter(arrayAdapter);

        s_text1.setOnClickListener(v -> {
            DialogFragment newFragment = new RoutineCerterpage.TimePickerFragment(start_time1);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });

        t_text1.setOnClickListener(v -> {
            DialogFragment newFragment = new RoutineCerterpage.TimePickerFragment(end_time1);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });

        d_text1.setOnClickListener(v -> {
            DialogFragment newFragment = new RoutineCerterpage.DatePickerFragment(date1);
            newFragment.show(getChildFragmentManager(), "datePicker");
        });


        add_btn.setOnClickListener(v -> {
            String s1 = Objects.requireNonNull(title1.getEditText()).getText().toString();
            if(s1.isEmpty()){
                title1.setError("Required!");
                return;
            }
            String sr2 = Objects.requireNonNull(start_time1.getEditText()).getText().toString();
            String sr3 = Objects.requireNonNull(end_time1.getEditText()).getText().toString();

            if(sr2.isEmpty()){
                start_time1.setError("Required!");
                return;
            }
            if(sr3.isEmpty()){
                end_time1.setError("Required!");
                return;
            }

            String[] psr1 = sr2.split(":");
            int t1 = Integer.parseInt(psr1[0]);
            int t2 = Integer.parseInt(psr1[1]);

            String[] psr2 = sr3.split(":");
            int t3 = Integer.parseInt(psr2[0]);
            int t4 = Integer.parseInt(psr2[1]);

            String date = Objects.requireNonNull(date1.getEditText()).getText().toString();
            if(date.isEmpty()){
                date1.setError("Required!");
                return;
            }

            String[] dates = date.split("/");
            int day = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int year = Integer.parseInt(dates[2]);
            month = month - 1;
            SQLiteDatabase db = myContex.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            final String TABLE_NAME = "DailyRoutineTable";

            String COMM1 = "SELECT COUNT(*) FROM "+TABLE_NAME + " WHERE Month = "+month + " AND Year ="+ year + " AND Day = "+ day;
            String COMM2 = "SELECT * FROM "+TABLE_NAME + " WHERE Month = "+month + " AND Year ="+ year + " AND Day = "+ day;
            Cursor cursor = db.rawQuery(COMM1, null);
            cursor.moveToFirst();
            int icount = cursor.getInt(0);
            boolean flag = false;
            if(icount > 0){
                cursor = db.rawQuery(COMM2, null);
                int day_index = cursor.getColumnIndex("Day");
                int month_index = cursor.getColumnIndex("Month");
                int year_index = cursor.getColumnIndex("Year");
                int title_index = cursor.getColumnIndex("Routine");
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    routineSaver = new RoutineSaver(cursor.getInt(day_index),
                            cursor.getInt(month_index),
                            cursor.getInt(year_index),
                            cursor.getString(title_index));
                    flag = true;
                    cursor.moveToNext();
                }
            }
            RoutineController routineController = new RoutineController(t1, t2, t3, t4, s1, importance_count);
            Type type = new TypeToken<ArrayList<RoutineController>>(){}.getType();
            if(flag){
                String str = routineSaver.getList();

                ArrayList<RoutineController> arrayList = new Gson().fromJson(str, type);
                arrayList.add(routineController);
                String gson_str = new Gson().toJson(arrayList);
                routineSaver.setList(gson_str);

                String COMM = "UPDATE "+TABLE_NAME+ " SET Routine = '"+ gson_str + "' WHERE Day = "+ day + " AND " +
                        "Month = "+month + " AND Year = "+ year;

                db.execSQL(COMM);

            }
            else{
                ContentValues contentValues = new ContentValues();
                ArrayList<RoutineController> arrayList = new ArrayList<>();
                arrayList.add(routineController);
                String gson_str = new Gson().toJson(arrayList);
                contentValues.put("Day", day);
                contentValues.put("Month", month);
                contentValues.put("Year", year);
                contentValues.put("Routine", gson_str);
                db.insert(TABLE_NAME, null, contentValues);

            }
            Toast.makeText(myContex, "Successfully Added!", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();

        });

        return view;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContex = context;
    }
}
