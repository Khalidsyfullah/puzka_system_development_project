package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

public class RegularDiary extends Fragment {
    GridView gridView;
    TextView prev, next, title;
    Calendar calendar;
    int cur_month, cur_year;
    Context myContex;
    SharedPreferences sharedPreferences;
    View.OnClickListener listener1, listener2;
    String[] months = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_regulardiary, container, false);
        prev = view.findViewById(R.id.tex11);
        next = view.findViewById(R.id.tex12);
        title = view.findViewById(R.id.tex13);
        gridView = view.findViewById(R.id.grid_jkl);
        sharedPreferences = new SecurePreferences(myContex);
        calendar = Calendar.getInstance(Locale.getDefault());

        listener1 = v -> {
            cur_month--;
            if(cur_month < 0){
                cur_month = 11;
                cur_year--;
            }
            calendar.set(Calendar.MONTH, cur_month);
            calendar.set(Calendar.YEAR, cur_year);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            getDatabaseList();
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
            getDatabaseList();
        };

        prev.setOnClickListener(listener1);
        next.setOnClickListener(listener2);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Date date = new Date();
        calendar.setTime(date);
        getDatabaseList();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContex = context;
    }

    @SuppressLint("SimpleDateFormat")
    void getDatabaseList()
    {
        try {
            cur_month = calendar.get(Calendar.MONTH);
            cur_year = calendar.get(Calendar.YEAR);

            title.setText(new SimpleDateFormat("MMM,yyyy").format(calendar.getTime()));
            ArrayList<DiaryClass> arrayList = new ArrayList<>();
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
                    arrayList.add(diaryClass);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();
            if(arrayList.size() == 0) gridView.setNumColumns(1);
            else gridView.setNumColumns(2);

            gridView.setAdapter(new MyCustomGrid(arrayList));
        }catch (Exception e){
            Toast.makeText(myContex, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    class MyCustomGrid extends BaseAdapter{
        ArrayList<DiaryClass> aList;

        public MyCustomGrid(ArrayList<DiaryClass> aList) {
            this.aList = aList;
        }

        @Override
        public int getCount() {
            if(aList == null || aList.size() == 0){
                return 1;
            }
            else return aList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(aList == null || aList.size() == 0){
                return getLayoutInflater().inflate(R.layout.grid_null, null);
            }
            View view = getLayoutInflater().inflate(R.layout.grid_regulardiary, null);
            TextView tx1, tx2, tx3;
            tx1 = view.findViewById(R.id.textView48);
            tx2 = view.findViewById(R.id.textView49);
            tx3 = view.findViewById(R.id.textView50);

            String date = aList.get(position).getDay() + " "+ months[aList.get(position).getMonth()] +
                            ", "+ aList.get(position).getYear();

            tx3.setText(date);
            tx1.setText(aList.get(position).getTitle());
            tx2.setText(aList.get(position).getBody());

            view.setOnClickListener(v -> {
                sharedPreferences.edit().putBoolean("diaryNew", false).apply();
                sharedPreferences.edit().putInt("diaryIDf", aList.get(position).getId()).apply();
                ((DiaryActivity) requireActivity()).change_Activity();
            });

            return view;
        }
    }
}
