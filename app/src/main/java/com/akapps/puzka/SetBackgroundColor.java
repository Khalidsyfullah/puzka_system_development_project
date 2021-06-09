package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SetBackgroundColor extends Fragment {
    GridView gridView;
    TextView text1, text2, text3;
    Context myContext;
    int get_selected = -1;
    SharedPreferences sharedPreferences;
    int cur_color_selected = 0;
    int num_uu = -1;
    String color_code = "";
    View prev_view = null;
    AmbilWarnaDialog.OnAmbilWarnaListener listener;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.childfrag_image, container, false);
        gridView = view.findViewById(R.id.grid_color);
        text1 = view.findViewById(R.id.textView25);
        text2 = view.findViewById(R.id.textView26);
        text3 = view.findViewById(R.id.textView10);
        get_selected = ((CreateNotepad) requireActivity()).getCurrent_selected();
        sharedPreferences = new SecurePreferences(myContext);

        if(get_selected == 0){
            cur_color_selected = sharedPreferences.getInt("crrrselected", 1);
        }
        else{
            cur_color_selected = sharedPreferences.getInt("txttselected", 2);
        }
        num_uu = cur_color_selected;

        text1.setOnClickListener(v -> {
            AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(myContext, Color.BLUE, listener);
            ambilWarnaDialog.show();
        });

        text2.setOnClickListener(v -> {
            if(num_uu == -1 || color_code.isEmpty()){
                ((CreateNotepad) requireActivity()).hideFrame();
                return;
            }
            if(get_selected == 0){
                sharedPreferences.edit().putInt("crrrselected", num_uu).apply();
                ((CreateNotepad) requireActivity()).updateBackground(color_code);
            }
            else{
                sharedPreferences.edit().putInt("txttselected", num_uu).apply();
                ((CreateNotepad) requireActivity()).updateTextColor(color_code);
            }
        });

        text3.setOnClickListener(v -> ((CreateNotepad) requireActivity()).hideFrame());

        listener= new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                String value = String.format("#%06X", (0xFFFFFF & color));
                try {
                    SQLiteDatabase myDatabase = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String TABLE_NAME = "ColorTable";
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("COLOR_CODE", value);
                    myDatabase.insert(TABLE_NAME, null, contentValues);
                    myDatabase.close();
                    buildColorList();
                }catch (Exception e){
                    Toast.makeText(myContext, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };



        buildColorList();
        return view;
    }

    void buildColorList()
    {
        try {
            ArrayList<ColorParser> arrayList = new ArrayList<>();
            SQLiteDatabase myDatabase = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            Cursor cursor = myDatabase.rawQuery("SELECT * FROM ColorTable", null);
            int code_index = cursor.getColumnIndex("COLOR_CODE");
            int ser_index = cursor.getColumnIndex("ID");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                ColorParser colorParser = new ColorParser(cursor.getString(code_index), cursor.getInt(ser_index));
                arrayList.add(colorParser);
                cursor.moveToNext();
            }
            cursor.close();
            myDatabase.close();
            if(arrayList.size() != 0){
                gridView.setAdapter(new BuildGrid(arrayList));
            }
        }catch (Exception e){
            Toast.makeText(myContext, "Error! "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    class BuildGrid extends BaseAdapter{
        ArrayList<ColorParser> list;

        public BuildGrid(ArrayList<ColorParser> list) {
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

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = getLayoutInflater().inflate(R.layout.layout_colorpicker,parent, false);
            View view1 = view.findViewById(R.id.view6);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(Color.parseColor(list.get(position).getCode()));
            drawable.setStroke(3, Color.parseColor("#E6E6E6"));
            view1.setBackground(drawable);

            view1.setOnClickListener(v -> {
                if(position == cur_color_selected - 1){
                    return;
                }
                if(prev_view != null){
                    prev_view.setForeground(null);
                }
                view1.setForeground(getResources().getDrawable(R.drawable.ic_save_img, null));
                prev_view = view1;
                num_uu = list.get(position).getSerial();
                color_code = list.get(position).getCode();
            });

            if(position == cur_color_selected - 1){
                view1.setForeground(getResources().getDrawable(R.drawable.ic_save_img, null));
                prev_view = view1;
                color_code = list.get(position).getCode();
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
