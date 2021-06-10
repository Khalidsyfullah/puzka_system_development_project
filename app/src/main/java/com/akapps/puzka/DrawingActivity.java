package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.securepreferences.SecurePreferences;

import java.io.OutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class DrawingActivity extends AppCompatActivity {
    DrawingtoolView drawingtoolView;
    AmbilWarnaDialog.OnAmbilWarnaListener listener;
    SeekBar seekBar;
    View view;
    int current_clicked_num = 0;
    TextView download_txt;
    LinearLayout ll1, ll2, ll3, ll4, ll5, ll6, ll7;
    SharedPreferences sharedPreferences;
    int selected_color;
    int unselected_color;
    int current_draw_id = -1;
    String current_name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        drawingtoolView = findViewById(R.id.drawingtool);
        seekBar = findViewById(R.id.seekBar);
        view = findViewById(R.id.view4);
        ll1 = findViewById(R.id.llay1);
        ll2 = findViewById(R.id.llay2);
        ll3 = findViewById(R.id.llay3);
        ll4 = findViewById(R.id.llay4);
        ll5 = findViewById(R.id.llay5);
        ll6 = findViewById(R.id.llay6);
        ll7 = findViewById(R.id.llay7);
        download_txt = findViewById(R.id.textView66);
        selected_color = Color.parseColor("#8F8F8F");
        unselected_color = getResources().getColor(android.R.color.transparent, null);
        setViewSize(Color.BLACK, 20);
        seekBar.setMax(95);
        seekBar.setProgress(15);
        ll2.setBackgroundColor(selected_color);
        sharedPreferences = new SecurePreferences(this);
        current_draw_id = sharedPreferences.getInt("drawableIDF", -1);
        if(current_draw_id!= -1) sharedPreferences.edit().putInt("drawableIDF", -1).apply();

        if(current_draw_id!= -1){
            PopupLoadingScreen popupLoadingScreen = new PopupLoadingScreen(DrawingActivity.this, "Loading....");
            popupLoadingScreen.show();
            try {
                SQLiteDatabase myDatabase = DrawingActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                String TABLE_NAME = "DrawableTable";
                String COMM = "SELECT * FROM "+ TABLE_NAME+ " WHERE ID = "+ current_draw_id;
                Cursor cursor = myDatabase.rawQuery(COMM, null);
                int id_index = cursor.getColumnIndex("ID");
                int bitmap_index = cursor.getColumnIndex("Bitmap");
                int undo_index = cursor.getColumnIndex("UndoBit");
                int main_index = cursor.getColumnIndex("MainBit");
                int name_index = cursor.getColumnIndex("Name");
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    if(cursor.getInt(id_index) == current_draw_id){
                        drawingtoolView.setValueFromString(cursor.getString(bitmap_index),
                                cursor.getString(undo_index), cursor.getString(main_index));
                        current_name = cursor.getString(name_index);
                        break;
                    }
                    cursor.moveToNext();
                }
                if(current_name.isEmpty()) current_draw_id = -1;
                popupLoadingScreen.dismiss();
                cursor.close();
                myDatabase.close();

            }catch (Exception e){
                popupLoadingScreen.dismiss();
                Toast.makeText(DrawingActivity.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }



        ll1.setOnClickListener(v -> {
            drawingtoolView.onEraserController(false);
            if(current_clicked_num == 1){
                controlUnselected();
                current_clicked_num = 0;
                ll2.setBackgroundColor(selected_color);
            }

            AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(DrawingActivity.this, drawingtoolView.getCurrentColor(), listener);
            ambilWarnaDialog.show();
        });


        ll2.setOnClickListener(v -> {
            if(current_clicked_num == 0) return;
            drawingtoolView.onEraserController(false);
            drawingtoolView.removeMusks();
            controlUnselected();
            current_clicked_num = 0;
            ll2.setBackgroundColor(selected_color);
        });

        ll3.setOnClickListener(v -> {
            if(current_clicked_num == 1) return;
            drawingtoolView.onEraserController(true);
            drawingtoolView.setEmbrossed(false);
            controlUnselected();
            current_clicked_num = 1;
            ll3.setBackgroundColor(selected_color);
        });

        ll4.setOnClickListener(v -> {
            if(current_clicked_num == 2) return;
            drawingtoolView.onEraserController(false);
            drawingtoolView.setEmbrossed(true);
            controlUnselected();
            current_clicked_num = 2;
            ll4.setBackgroundColor(selected_color);
        });

        ll5.setOnClickListener(v -> {
            if(current_clicked_num == 3) return;
            drawingtoolView.onEraserController(false);
            drawingtoolView.setBlur(true);
            controlUnselected();
            current_clicked_num = 3;
            ll5.setBackgroundColor(selected_color);
        });

        ll6.setOnClickListener(v -> drawingtoolView.undoChanges());

        ll7.setOnClickListener(v -> drawingtoolView.redoChanges());

        listener= new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                drawingtoolView.setColor(color);
                setViewColor(color);
            }
        };


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float f1 = (float) ((progress+5)/ 100.0f);
                int size = (int) (f1 * 80.0f);
                setViewSize(drawingtoolView.getCurrentColor(), size);
                drawingtoolView.setStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        download_txt.setOnClickListener(v -> {
            PopupSaveMenu popupSaveMenu = new PopupSaveMenu(DrawingActivity.this);
            popupSaveMenu.show();
        });

    }

    @Override
    public void onBackPressed() {
        PopupAlertDialog popupAlertDialog = new PopupAlertDialog(DrawingActivity.this);
        popupAlertDialog.show();
    }

    void setViewSize(int color, int size)
    {
        view.getLayoutParams().height = size;
        view.getLayoutParams().width = size;
        view.requestLayout();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(color);
        view.setBackground(gradientDrawable);
    }

    void setViewColor(int color)
    {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(color);
        view.setBackground(gradientDrawable);
    }

    void controlUnselected()
    {
        if(current_clicked_num == 0){
            ll2.setBackgroundColor(unselected_color);
        }
        else if(current_clicked_num == 1){
            ll3.setBackgroundColor(unselected_color);
        }
        else if(current_clicked_num == 2){
            ll4.setBackgroundColor(unselected_color);
        }
        else {
            ll5.setBackgroundColor(unselected_color);
        }
    }


    class PopupSaveMenu extends Dialog{
        TextView canceltxt;
        EditText editText;
        Button bt1, bt2;

        public PopupSaveMenu(@NonNull Context context) {
            super(context);
        }

        @SuppressLint("InlinedApi")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_savedrawing);
            canceltxt = findViewById(R.id.textView29);
            editText = findViewById(R.id.ad2);
            bt1 = findViewById(R.id.button18);
            bt2 = findViewById(R.id.button19);
            this.setCancelable(false);
            canceltxt.setOnClickListener(v -> this.dismiss());
            if(current_draw_id != -1 && !current_name.isEmpty()){
                editText.setText(current_name);
            }
            bt1.setOnClickListener(v -> {
                String naf = editText.getText().toString();
                if(naf.isEmpty()){
                    editText.setError("Name Cannot Be Empty!");
                    return;
                }
                this.dismiss();
                PopupLoadingScreen popupLoadingScreen = new PopupLoadingScreen(DrawingActivity.this, "Saving....");
                String s1 = drawingtoolView.getBitmap();
                String s2 = drawingtoolView.getUndoBitmap();
                String s3 = drawingtoolView.getMainBitmap();

                if(current_draw_id == -1){
                    try{
                        popupLoadingScreen.show();
                        SQLiteDatabase db = DrawingActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Bitmap", s1);
                        contentValues.put("UndoBit", s2);
                        contentValues.put("MainBit", s3);
                        contentValues.put("Name", naf);
                        db.insert("DrawableTable",null,  contentValues);
                        Toast.makeText(DrawingActivity.this, "Successfully Saved!", Toast.LENGTH_LONG).show();
                        popupLoadingScreen.dismiss();
                        DrawingActivity.this.finish();
                    }catch (Exception e){
                        popupLoadingScreen.dismiss();
                        Toast.makeText(DrawingActivity.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    try{
                        SQLiteDatabase db = DrawingActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        String UPDATE_COMM = "UPDATE DrawableTable SET Bitmap = '"+s1 +"', UndoBit = '"+s2 +
                                "', MainBit = '"+s3+ "', Name = '"+naf+ "' WHERE ID = "+ current_draw_id;
                        db.execSQL(UPDATE_COMM);
                        Toast.makeText(DrawingActivity.this, "Successfully Updated!", Toast.LENGTH_LONG).show();
                        popupLoadingScreen.dismiss();
                        DrawingActivity.this.finish();
                    }catch (Exception e){
                        popupLoadingScreen.dismiss();
                        Toast.makeText(DrawingActivity.this, "Error!"+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            bt2.setOnClickListener(v -> {
                String naf = editText.getText().toString();
                if(naf.isEmpty()){
                    editText.setError("Name Cannot Be Empty!");
                    return;
                }
                naf = naf + ".png";
                Bitmap bmp = drawingtoolView.saveImage();
                OutputStream imageOutStream;
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, naf);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    imageOutStream = getContentResolver().openOutputStream(uri);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
                    imageOutStream.close();
                    Toast.makeText(DrawingActivity.this, "Successfully Saved to Storage!", Toast.LENGTH_LONG).show();
                    this.dismiss();
                } catch (Exception e) {
                    editText.setError("Name Already Exists!");
                    //Toast.makeText(DrawingActivity.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

}