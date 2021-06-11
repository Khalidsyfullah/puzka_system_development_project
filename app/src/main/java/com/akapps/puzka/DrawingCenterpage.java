package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;

public class DrawingCenterpage extends AppCompatActivity {
    GridView gridView;
    FloatingActionButton fab;
    TextView backButton, sync_text;
    SharedPreferences sharedPreferences;
    int id_num_toDelete = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_centerpage);
        sharedPreferences = new SecurePreferences(this);
        backButton = findViewById(R.id.textView67);
        gridView = findViewById(R.id.grid_draw);
        fab = findViewById(R.id.floatingActionButton4);
        sync_text = findViewById(R.id.textView71);
        updateDrawableList();
        sync_text.setOnClickListener(v -> updateDrawableList());
        backButton.setOnClickListener(v -> finish());
        fab.setOnClickListener(v -> startActivity(new Intent(DrawingCenterpage.this, DrawingActivity.class)));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void updateDrawableList()
    {
        try {
            ArrayList<DrawableSClass> list = new ArrayList<>();
            SQLiteDatabase myDatabase = DrawingCenterpage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            String TABLE_NAME = "DrawableTable";
            String COMM1 = "SELECT COUNT(*) FROM "+ TABLE_NAME;
            String COMM2 = "SELECT * FROM "+ TABLE_NAME;
            Cursor cursor = myDatabase.rawQuery(COMM1, null);
            cursor.moveToFirst();
            int cou = cursor.getInt(0);

            if(cou > 0){
                cursor = myDatabase.rawQuery(COMM2 , null);
                int id_index = cursor.getColumnIndex("ID");
                int bitmap_index = cursor.getColumnIndex("Bitmap");
                int undo_index = cursor.getColumnIndex("UndoBit");
                int main_index = cursor.getColumnIndex("MainBit");
                int name_index = cursor.getColumnIndex("Name");
                cursor.moveToFirst();

                while (!cursor.isAfterLast()){
                    DrawableSClass drawableSClass = new DrawableSClass(cursor.getString(bitmap_index),
                            cursor.getString(undo_index),
                            cursor.getString(main_index),
                            cursor.getString(name_index),
                            cursor.getInt(id_index));
                    list.add(drawableSClass);
                    cursor.moveToNext();
                }
                cursor.close();
                myDatabase.close();
            }
            gridView.setAdapter(new CustomSavedDrawable(list));

        }catch (Exception e){
            Toast.makeText(DrawingCenterpage.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    class CustomSavedDrawable extends BaseAdapter{
        ArrayList<DrawableSClass> arrayList;

        public CustomSavedDrawable(ArrayList<DrawableSClass> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            if(arrayList.size() == 0){
                return 1;
            }
            else return arrayList.size();
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
            if(arrayList.size() == 0){
                return getLayoutInflater().inflate(R.layout.grid_nuull, null);
            }
            View view = getLayoutInflater().inflate(R.layout.grid_drawvier, null );
            ImageView imageView = view.findViewById(R.id.imageView2);
            TextView textView = view.findViewById(R.id.textView69);

            imageView.setImageBitmap(getBitmapFromString(arrayList.get(position).getMainbitmap()));
            textView.setText(arrayList.get(position).getName());

            view.setOnClickListener(v -> {
                sharedPreferences.edit().putInt("drawableIDF", arrayList.get(position).getId()).apply();
                startActivity(new Intent(DrawingCenterpage.this, DrawingActivity.class));
            });

            view.setOnLongClickListener(v -> {
                id_num_toDelete = arrayList.get(position).getId();
                PopupalertDelete popupalertDelete = new PopupalertDelete(DrawingCenterpage.this);
                popupalertDelete.show();
                return true;
            });

            return view;
        }
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    class PopupalertDelete extends Dialog {


        public PopupalertDelete(@NonNull Context context) {
            super(context);
        }

        @Override
        public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_confirmsave);
            String tx1 = "Are you sure to delete this File? ";
            TextView textView = findViewById(R.id.textView41);
            TextView cancel = findViewById(R.id.textView29);
            this.setCancelable(false);

            Button bt1 = findViewById(R.id.button17);
            Button bt2 = findViewById(R.id.button15);

            textView.setText(tx1);
            bt2.setOnClickListener(v -> this.dismiss());

            cancel.setOnClickListener(v -> this.dismiss());

            bt1.setOnClickListener(v -> {
                this.dismiss();
                try {
                    if(id_num_toDelete == -1){
                        Toast.makeText(DrawingCenterpage.this, "Invalid Document!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    SQLiteDatabase myDatabase = DrawingCenterpage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String COMM = "DELETE FROM DrawableTable WHERE ID = "+id_num_toDelete;
                    myDatabase.execSQL(COMM);
                    myDatabase.close();
                    updateDrawableList();
                    Toast.makeText(DrawingCenterpage.this, "Successfully Deleted!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(DrawingCenterpage.this, "Error! "+ e.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }
    }

}