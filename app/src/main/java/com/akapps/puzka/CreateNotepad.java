package com.akapps.puzka;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CreateNotepad extends AppCompatActivity {
    EditText title_text, body_text;
    LinearLayout ll1, ll2, ll3, ll4, ll5, ll6;
    FrameLayout frameLayout;
    int current_selected = -1;
    String selected_color = "#E6E6E6";
    String unselected_color = "#FFFFFF";
    String[] strings = {"Remove Margin", "Add Margin"};
    boolean isLined = true;
    TextView line_controller, bg_color, txt_color, style_txt, size_txt, list_txt, cancel_tval;
    String background_color = "#FFFFFF";
    TextView lined_text, normal_text, save_note, down_txy, up_txy;
    boolean isTextcolor = false;
    String text_color = "#000000";
    int style_number = 0;
    boolean isStyled = false;
    String[] style = {"Style: Normal", "Style: Bold", "Style: Italic", "Style: Underline"};
    int text_size_number = 0;
    boolean isSized = false;
    String[] size = {"Size: Heading", "Size: Sub-Heading", "Size: Normal", "Size: Small"};
    int list_number = 0;
    boolean isListed = false;
    String[] list = {"List: Bullet", "List: English Letter", "List: Roman Letter", "List: Number"};
    ArrayList<ValueStorer> arrayList = new ArrayList<>();
    int curr_pos_val = -1;
    SharedPreferences sharedPreferences;
    boolean isNew = true;
    int id = -1;
    DocumentClass documentClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notepad);
        frameLayout = findViewById(R.id.frame_pop);
        frameLayout.setVisibility(View.GONE);
        ll1 = findViewById(R.id.lln1);
        ll2 = findViewById(R.id.lln2);
        ll3 = findViewById(R.id.lln3);
        ll4 = findViewById(R.id.lln4);
        ll5 = findViewById(R.id.lln5);
        ll6 = findViewById(R.id.lln6);
        bg_color = findViewById(R.id.tex1);
        txt_color = findViewById(R.id.tex2);
        style_txt = findViewById(R.id.tex3);
        size_txt = findViewById(R.id.tex4);
        list_txt = findViewById(R.id.tex5);
        line_controller = findViewById(R.id.tex6);
        title_text = findViewById(R.id.title);
        body_text = findViewById(R.id.edit_story);
        lined_text = findViewById(R.id.lined_textview);
        normal_text = findViewById(R.id.only_textview);
        save_note = findViewById(R.id.textView34);
        down_txy = findViewById(R.id.textView33);
        up_txy = findViewById(R.id.textView30);
        cancel_tval = findViewById(R.id.textView35);
        normal_text.setVisibility(View.GONE);
        cancel_tval.setVisibility(View.GONE);

        sharedPreferences = new SecurePreferences(CreateNotepad.this);

        isNew = sharedPreferences.getBoolean("isNewText", true);
        sharedPreferences.edit().putBoolean("isNewText", true).apply();

        if(!isNew){
            id = sharedPreferences.getInt("idtoSearch", -1);
            sharedPreferences.edit().putInt("idtoSearch", -1).apply();
            if(id != -1){
                try {
                    SQLiteDatabase myDatabase = CreateNotepad.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String TABLE_NAME = "DocumentTable";
                    String COMM = "SELECT * FROM "+ TABLE_NAME+ " WHERE ID = "+ id;
                    Cursor cursor = myDatabase.rawQuery(COMM, null);
                    int id_index = cursor.getColumnIndex("ID");
                    int type_index = cursor.getColumnIndex("Type");
                    int label_index = cursor.getColumnIndex("Label");
                    int title_index = cursor.getColumnIndex("Title");
                    int body_index = cursor.getColumnIndex("Body");
                    int prev_index = cursor.getColumnIndex("Preview");
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()){

                        if(cursor.getInt(id_index) == id){
                            documentClass = new DocumentClass(cursor.getInt(id_index),
                                    cursor.getInt(type_index),
                                    cursor.getInt(label_index),
                                    cursor.getString(title_index),
                                    cursor.getString(body_index),
                                    cursor.getString(prev_index));

                            break;
                        }
                        cursor.moveToNext();
                    }
                    foundID();
                    cursor.close();
                    myDatabase.close();

                }catch (Exception e){
                    Toast.makeText(CreateNotepad.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else{
                isNew = true;
            }
        }


        ll1.setOnClickListener(v -> {
            current_selected = 0;
            if(frameLayout.getVisibility() == View.GONE){
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_pop, new SetBackgroundColor()).commit();
            }
            else{
                frameLayout.setVisibility(View.GONE);
            }

        });


        ll2.setOnClickListener(v -> {
            if(isTextcolor){
                isTextcolor = false;
                ll2.setBackgroundColor(Color.parseColor(unselected_color));
                return;
            }

            current_selected = 1;
            if(frameLayout.getVisibility() == View.GONE){
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_pop, new SetBackgroundColor()).commit();
            }
            else{
                frameLayout.setVisibility(View.GONE);
            }

        });

        ll3.setOnClickListener(v -> {
            if(isStyled){
                isStyled = false;
                ll3.setBackgroundColor(Color.parseColor(unselected_color));
                return;
            }

            if(frameLayout.getVisibility() == View.GONE){
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_pop, new SetStyle()).commit();
            }
            else{
                frameLayout.setVisibility(View.GONE);
            }

        });

        ll4.setOnClickListener(v -> {
            if(isSized){
                isSized = false;
                ll4.setBackgroundColor(Color.parseColor(unselected_color));
                return;
            }
            if(frameLayout.getVisibility() == View.GONE){
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_pop, new SetTextsize()).commit();
            }
            else{
                frameLayout.setVisibility(View.GONE);
            }

        });

        ll5.setOnClickListener(v -> {
            if(isListed){
                isListed = false;
                ll5.setBackgroundColor(Color.parseColor(unselected_color));
                return;
            }
            if(frameLayout.getVisibility() == View.GONE){
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_pop, new SetList()).commit();
            }
            else{
                frameLayout.setVisibility(View.GONE);
            }

        });

        ll6.setOnClickListener(v -> {
            if(isLined){
                isLined = false;
                line_controller.setText(strings[1]);
                ll6.setBackgroundColor(Color.parseColor(selected_color));
                lined_text.setVisibility(View.GONE);
                normal_text.setVisibility(View.VISIBLE);
            }
            else{
                isLined = true;
                line_controller.setText(strings[0]);
                ll6.setBackgroundColor(Color.parseColor(unselected_color));
                lined_text.setVisibility(View.VISIBLE);
                normal_text.setVisibility(View.GONE);
            }
            stringController();
        });


        down_txy.setOnClickListener(v -> {
            int start, end;
            if(!isLined){
                start = normal_text.getSelectionStart();
                end = normal_text.getSelectionEnd();
            }
            else {
                start = lined_text.getSelectionStart();
                end = lined_text.getSelectionEnd();
            }
            if(start == 0 && end == 0){
                Toast.makeText(CreateNotepad.this, "No text selected!", Toast.LENGTH_LONG).show();
                return;
            }
            int array_start = 0;

            for(int i=0; i<arrayList.size(); i++){
                ValueStorer vs = arrayList.get(i);

                int tmp_len = array_start+ vs.getNormal_txt().length();
                if(start >= array_start && start<= tmp_len){
                    body_text.setText(vs.normal_txt);
                    try{
                        if(!vs.getFont_color().isEmpty()){
                            updateTextColor(vs.getFont_color());
                        }
                        else{
                            isTextcolor = false;
                            ll2.setBackgroundColor(Color.parseColor(unselected_color));
                        }

                        if(vs.getListnum() != -1){
                            update_list_style(vs.getListnum());
                        }
                        else{
                            isListed = false;
                            ll5.setBackgroundColor(Color.parseColor(unselected_color));
                        }

                        if(vs.getSizenum() != -1){
                            update_text_size(vs.getSizenum());
                        }
                        else{
                            isSized = false;
                            ll4.setBackgroundColor(Color.parseColor(unselected_color));
                        }

                        if(vs.getStylenum() != -1){
                            update_style_number(vs.getStylenum());
                        }
                        else{
                            isStyled = false;
                            ll3.setBackgroundColor(Color.parseColor(unselected_color));
                        }

                    }catch (Exception ignored){

                    }
                    curr_pos_val = i;
                    cancel_tval.setVisibility(View.VISIBLE);
                    down_txy.setVisibility(View.GONE);
                    return;
                }
                array_start+= vs.getNormal_txt().length();
            }
        });

        cancel_tval.setOnClickListener(v -> {
            curr_pos_val = -1;
            cancel_tval.setVisibility(View.GONE);
            down_txy.setVisibility(View.VISIBLE);
        });


        up_txy.setOnClickListener(v -> {
            String val_op_str = body_text.getText().toString();
            int num1 = -1, num2 = -1, num3 = -1;
            String c_sr = "";
            if(val_op_str.isEmpty()){
                if(curr_pos_val != -1){
                    arrayList.remove(curr_pos_val);
                    curr_pos_val = -1;
                    cancel_tval.setVisibility(View.GONE);
                    down_txy.setVisibility(View.VISIBLE);
                }
            }

            body_text.setText("");
            StringBuilder stringBuilder = new StringBuilder();
            if(isListed){
                num1 = list_number;
                if(list_number == 0){
                    stringBuilder.append("&#8226; ");
                }
                else if(list_number == 1){
                    stringBuilder.append("(a) ");
                }
                else if(list_number == 2){
                    stringBuilder.append("(i) ");
                }
                else {
                    stringBuilder.append("(1) ");
                }
            }
            int seq_ord = 0;
            String[] eng_ltr = {"(b) ", "(c) ", "(d) ", "(e) ", "(f) ", "(g) ", "(h) ", "(i) ", "(j) "};
            String[] roman_ltr = {"(ii) ", "(iii) ", "(iv) ", "(v) ", "(vi) ", "(vii) ", "(viii) ", "(ix) ", "(x) "};
            for(int i = 0; i<val_op_str.length(); i++){
                if(val_op_str.charAt(i) != '\n'){
                    stringBuilder.append(val_op_str.charAt(i));
                }
                else
                {
                    stringBuilder.append("<br>");
                    if(isListed && seq_ord< 10){
                        if(list_number == 0){
                            stringBuilder.append("&#8226; ");
                        }
                        else if(list_number == 1){
                            stringBuilder.append(eng_ltr[seq_ord]);
                        }
                        else if(list_number == 2){
                            stringBuilder.append(roman_ltr[seq_ord]);
                        }
                        else {
                            int yui = seq_ord+ 2;
                            String vs = "("+yui+ ") ";
                            stringBuilder.append(vs);
                        }
                        seq_ord++;
                    }
                }
            }


            if(isSized){
                if(text_size_number == 0){
                    num2 = 0;
                    stringBuilder.insert(0, "<h2>");
                    stringBuilder.append("</h2>");
                }
                else if(text_size_number == 1){
                    num2 = 1;
                    stringBuilder.insert(0, "<big>");
                    stringBuilder.append("</big>");
                }
                else if(text_size_number == 3){
                    num3 = 2;
                    stringBuilder.insert(0, "<small>");
                    stringBuilder.append("</small>");
                }
            }

            if(isStyled){
                if(style_number == 1){
                    num3 = 1;
                    stringBuilder.insert(0, "<b>");
                    stringBuilder.append("</b>");
                }
                else if(style_number == 2){
                    num3 = 2;
                    stringBuilder.insert(0, "<i>");
                    stringBuilder.append("</i>");
                }
                else if(style_number == 3){
                    num3 = 3;
                    stringBuilder.insert(0, "<u>");
                    stringBuilder.append("</u>");
                }
            }

            if(isTextcolor){
                c_sr = text_color;
                String tm_st = "<font color = '"+ text_color + "'>";
                stringBuilder.insert(0, tm_st);
                stringBuilder.append("</font>");
            }

            ValueStorer valueStorer = new ValueStorer(val_op_str, stringBuilder.toString(), c_sr, num1, num3, num2);
            if(curr_pos_val != -1){
                cancel_tval.setVisibility(View.GONE);
                down_txy.setVisibility(View.VISIBLE);
                arrayList.set(curr_pos_val, valueStorer);
                curr_pos_val = -1;
            }
            else{
                arrayList.add(valueStorer);
            }

            stringController();
        });



        save_note.setOnClickListener(v -> {

            if(arrayList.size() == 0){
                Toast.makeText(CreateNotepad.this, "Note is Empty!", Toast.LENGTH_LONG).show();
                return;
            }
            String spt = title_text.getText().toString();
            if(spt.isEmpty()){
                title_text.setError("Enter a title! ");
                return;
            }
            StringBuilder stt = new StringBuilder();
            for(ValueStorer vs: arrayList){
                stt.append(vs.getNormal_txt());
                if(stt.toString().length() > 100){
                    break;
                }
            }
            String prev;
            if(stt.toString().length() > 100){
                prev = stt.substring(0, 100).trim();
            }
            else{
                prev = stt.toString();
            }

            String gson_string = new Gson().toJson(arrayList);

            PopupConfirmSaveCC popupConfirmSaveCC = new PopupConfirmSaveCC(CreateNotepad.this, gson_string, spt, prev);
            popupConfirmSaveCC.show();
        });


    }

    @Override
    public void onBackPressed() {
        if(!save_note.isEnabled()){
            finish();
            return;
        }
        PopupAlertDialog popupAlertDialog = new PopupAlertDialog(CreateNotepad.this);
        popupAlertDialog.show();
    }

    void foundID ()
    {
        if(documentClass == null){
            isNew = true;
            return;
        }

        int v_type = documentClass.getType();
        title_text.setText(documentClass.getText());
        String text = documentClass.getBody();
        if(v_type == 0){
            Type type = new TypeToken<ArrayList<ValueStorer>>(){}.getType();
            arrayList = new Gson().fromJson(text, type);
        }
        else{
            StringBuilder str = new StringBuilder();
            for(int i=0; i< text.length(); i++){
                if(text.charAt(i) != '\n'){
                    str.append(text.charAt(i));
                }
                else{
                    str.append("<br>");
                }
            }
            ValueStorer valueStorer = new ValueStorer(text, str.toString(), "", -1, -1, -1);
            arrayList.add(valueStorer);
        }
        stringController();

    }

    void stringController()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(ValueStorer vs: arrayList){
            stringBuilder.append(vs.getHtml_txt());
        }


        if(!isLined){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                normal_text.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_COMPACT));
            }else {
                normal_text.setText(Html.fromHtml(stringBuilder.toString()));
            }
            lined_text.setText("");
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lined_text.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_COMPACT));
            }else {
                lined_text.setText(Html.fromHtml(stringBuilder.toString()));
            }
            normal_text.setText("");
        }
    }



    public void hideFrame()
    {
        frameLayout.setVisibility(View.GONE);
    }

    public int getCurrent_selected()
    {
        return current_selected;
    }

    public void updateBackground(String color_str)
    {
        background_color = color_str;
        ll1.setBackgroundColor(Color.parseColor(selected_color));
        lined_text.setBackgroundColor(Color.parseColor(background_color));
        normal_text.setBackgroundColor(Color.parseColor(background_color));
        frameLayout.setVisibility(View.GONE);
    }

    public void updateTextColor(String color_str)
    {
        isTextcolor = true;
        ll2.setBackgroundColor(Color.parseColor(selected_color));
        frameLayout.setVisibility(View.GONE);
        text_color = color_str;
    }

    public void update_style_number(int number)
    {
        isStyled = true;
        ll3.setBackgroundColor(Color.parseColor(selected_color));
        style_txt.setText(style[number]);
        style_number = number;
        frameLayout.setVisibility(View.GONE);
    }

    public void update_text_size(int num){
        isSized = true;
        ll4.setBackgroundColor(Color.parseColor(selected_color));
        size_txt.setText(size[num]);
        text_size_number = num;
        frameLayout.setVisibility(View.GONE);
    }

    public void update_list_style(int num){
        isListed = true;
        ll5.setBackgroundColor(Color.parseColor(selected_color));
        list_txt.setText(list[num]);
        list_number = num;
        frameLayout.setVisibility(View.GONE);
    }



    class PopupConfirmSaveCC extends Dialog {
        Button bt1, bt2;
        String s1, s2, s3;
        TextView cancel_txt;
        public PopupConfirmSaveCC(@NonNull Context context, String s1, String s2, String s3) {
            super(context);
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_confirmsave);
            bt1 = findViewById(R.id.button17);
            bt2 = findViewById(R.id.button15);
            cancel_txt = findViewById(R.id.textView29);

            this.setCancelable(false);
            bt2.setOnClickListener(v -> this.dismiss());
            cancel_txt.setOnClickListener(v -> this.dismiss());
            bt1.setOnClickListener(v -> {
                this.dismiss();

                if(isNew){
                    int label_ID = sharedPreferences.getInt("labelID", 1);

                    try{
                        SQLiteDatabase db = CreateNotepad.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Type", 0);
                        contentValues.put("Label", label_ID);
                        contentValues.put("Title", s2);
                        contentValues.put("Body", s1);
                        contentValues.put("Preview", s3);
                        db.insert("DocumentTable",null,  contentValues);
                        Toast.makeText(CreateNotepad.this, "Successfully Saved!", Toast.LENGTH_LONG).show();
                        save_note.setEnabled(false);
                        body_text.setEnabled(false);
                        up_txy.setEnabled(false);
                        down_txy.setEnabled(false);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(CreateNotepad.this, "Error!"+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    try{
                        SQLiteDatabase db = CreateNotepad.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        String UPDATE_COMM = "UPDATE DocumentTable SET Type = 0, Title = '"+s2+ "', Body = '"+
                                s1 + "', Preview = '"+ s3+ "' WHERE ID = "+id;
                        db.execSQL(UPDATE_COMM);
                        Toast.makeText(CreateNotepad.this, "Successfully Updated!", Toast.LENGTH_LONG).show();
                        save_note.setEnabled(false);
                        body_text.setEnabled(false);
                        up_txy.setEnabled(false);
                        down_txy.setEnabled(false);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(CreateNotepad.this, "Error!"+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}