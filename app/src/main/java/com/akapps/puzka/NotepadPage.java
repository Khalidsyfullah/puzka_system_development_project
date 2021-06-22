package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.List;

public class NotepadPage extends AppCompatActivity {
    FloatingActionButton fab1, fab2;
    ChipGroup chipGroup;
    TextView tx1, tx2, sync_text;
    GridView gridView;
    ArrayList<ChipClass> list = new ArrayList<>();
    View.OnClickListener listener;
    View.OnLongClickListener vlistner;
    Chip cur_sel_chip = null;
    ArrayList<DocumentClass> arrayList = new ArrayList<>();
    int first_chip_id = -1;
    int label_no = 1;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material4);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_page);
        fab1 = findViewById(R.id.floatingActionButton);
        fab2 = findViewById(R.id.floatingActionButton2);
        chipGroup = findViewById(R.id.chipGroup2);
        tx1 = findViewById(R.id.textView7);
        tx2 = findViewById(R.id.textView8);
        sync_text = findViewById(R.id.textView42);

        gridView = findViewById(R.id.gridview);
        sharedPreferences = new SecurePreferences(NotepadPage.this);

        getDocumentList();

        fab1.setOnClickListener(v -> {
            sharedPreferences.edit().putInt("labelID", label_no).apply();
            sharedPreferences.edit().putBoolean("isNewText", true).apply();
            startActivity(new Intent(NotepadPage.this, CreateNotepad.class));
        });

        fab2.setOnClickListener(v -> {
            sharedPreferences.edit().putInt("labelID", label_no).apply();
            sharedPreferences.edit().putBoolean("isNewText", true).apply();
            startActivity(new Intent(NotepadPage.this, NormalNotepad.class));
        });

        sync_text.setOnClickListener(v ->{
            getDocumentList();
            Toast.makeText(NotepadPage.this, "Successfully Synced!", Toast.LENGTH_LONG).show();
        });

        tx2.setOnClickListener(v -> {
            AddChip addChip = new AddChip(NotepadPage.this);
            addChip.show();
        });

        listener = v -> {
            Chip chip = (Chip) v;
            if(chip == cur_sel_chip) return;

            for(ChipClass cl :list){
                if(cl.getChip() == chip){
                    cur_sel_chip = chip;
                    label_no = cl.getId();
                    if(label_no == 1){
                        if(arrayList== null || arrayList.isEmpty()) gridView.setNumColumns(1);
                        else gridView.setNumColumns(2);

                        gridView.setAdapter(new MyCustomGrid(arrayList));
                        return;
                    }
                    ArrayList<DocumentClass> cara = new ArrayList<>();
                    for(DocumentClass doc: arrayList){
                        if(doc.getLabel() == label_no){
                            cara.add(doc);
                        }
                    }
                    if(cara.isEmpty()) gridView.setNumColumns(1);
                    else gridView.setNumColumns(2);
                    gridView.setAdapter(new MyCustomGrid(cara));
                    return;
                }
            }
        };

        vlistner = v -> {
            Chip chip = (Chip) v;
            int a1, a2;
            for(int i=0; i<list.size(); i++){
                ChipClass cl = list.get(i);
                if(cl.getChip() == chip){
                    a1 = i;
                    a2 = cl.getId();
                    UpdateChip updateChip = new UpdateChip(NotepadPage.this, a1, a2);
                    updateChip.show();
                    return true;
                }
            }
            return true;
        };

        readfromDatabase();


        tx1.setOnClickListener(v -> {
            PopupSearchView popupSearchView = new PopupSearchView(NotepadPage.this);
            popupSearchView.show();
        });
    }

    void readfromDatabase()
    {
        try{
            SQLiteDatabase myDatabase = NotepadPage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            Cursor cursor = myDatabase.rawQuery("SELECT * FROM ChipTable", null);
            int code_index = cursor.getColumnIndex("CHIP_NAME");
            int ser_index = cursor.getColumnIndex("ID");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                ChipClass chipClass = new ChipClass(cursor.getString(code_index), cursor.getInt(ser_index));
                list.add(chipClass);
                cursor.moveToNext();
            }
            cursor.close();
            myDatabase.close();
            implement_chip();
        }catch (Exception ignored){

        }
    }

    void implement_chip()
    {
        for(int i=0; i< list.size(); i++){
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_layout, chipGroup, false);
            list.get(i).setChip(chip);
            chip.setOnClickListener(listener);
            chip.setOnLongClickListener(vlistner);
            chipGroup.addView(chip);
            chip.setText(list.get(i).getName());
            if(i==0) {
                chipGroup.check(chip.getId());
                cur_sel_chip = chip;
                first_chip_id = chip.getId();
            }
        }


    }

    void getDocumentList()
    {
        try {
            if(arrayList!= null && arrayList.size() > 0){
                arrayList.clear();
            }
            if(first_chip_id != -1){
                chipGroup.check(first_chip_id);
            }

            SQLiteDatabase myDatabase = NotepadPage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            String TABLE_NAME = "DocumentTable";
            String COMM1 = "SELECT COUNT(*) FROM "+ TABLE_NAME;
            String COMM2 = "SELECT * FROM "+ TABLE_NAME;
            Cursor cursor = myDatabase.rawQuery(COMM1, null);

            cursor.moveToFirst();
            int icount = cursor.getInt(0);
            if(icount > 0){
                cursor = myDatabase.rawQuery(COMM2, null);
                int id_index = cursor.getColumnIndex("ID");
                int type_index = cursor.getColumnIndex("Type");
                int label_index = cursor.getColumnIndex("Label");
                int title_index = cursor.getColumnIndex("Title");
                int body_index = cursor.getColumnIndex("Body");
                int prev_index = cursor.getColumnIndex("Preview");
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    DocumentClass documentClass = new DocumentClass(cursor.getInt(id_index),
                            cursor.getInt(type_index),
                            cursor.getInt(label_index),
                            cursor.getString(title_index),
                            cursor.getString(body_index),
                            cursor.getString(prev_index));

                    arrayList.add(documentClass);
                    cursor.moveToNext();
                }
            }
            if(arrayList== null || arrayList.isEmpty()) gridView.setNumColumns(1);
            else gridView.setNumColumns(2);
            gridView.setAdapter(new MyCustomGrid(arrayList));
            cursor.close();
            myDatabase.close();
        }catch (Exception e){
            Toast.makeText(NotepadPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    static class ChipClass{
        String name;
        int id;
        Chip chip;

        public ChipClass(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public Chip getChip() {
            return chip;
        }

        public void setChip(Chip chip) {
            this.chip = chip;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class AddChip extends Dialog {
        Button add;
        EditText ed;
        TextView canc;
        public AddChip(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_addchip);
            canc = findViewById(R.id.textView29);
            ed = findViewById(R.id.ad1);
            add = findViewById(R.id.button10);
            this.setCancelable(false);
            canc.setOnClickListener(v -> this.dismiss());
            add.setOnClickListener(v -> {
                String text = ed.getText().toString();
                if(text.isEmpty()){
                    ed.setError("Required!");
                    return;
                }

                for(ChipClass cl :list){
                    if(cl.getName().equals(text)){
                        ed.setError("Already Exists!");
                        return;
                    }

                }
                this.dismiss();
                try{
                    SQLiteDatabase myDatabase = NotepadPage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("CHIP_NAME", text);
                    myDatabase.insert("ChipTable", null, contentValues);
                    myDatabase.close();
                    Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_layout, chipGroup, false);
                    chip.setOnClickListener(listener);
                    chip.setOnLongClickListener(vlistner);
                    chipGroup.addView(chip);
                    chip.setText(text);
                    ChipClass chipClass = new ChipClass(text, list.size()+1);
                    chipClass.setChip(chip);
                    list.add(chipClass);
                    Toast.makeText(NotepadPage.this, "Successfully added!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(NotepadPage.this, "Error! Insert Failed....", Toast.LENGTH_LONG).show();
                }

            });
        }
    }


    public class UpdateChip extends Dialog {
        Button add;
        EditText ed;
        TextView canc;
        int selected_num, selected_id;
        public UpdateChip(@NonNull Context context, int selected_num, int selected_id) {
            super(context);
            this.selected_id = selected_id;
            this.selected_num = selected_num;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_updatelabel);
            canc = findViewById(R.id.textView29);
            ed = findViewById(R.id.ad1);
            add = findViewById(R.id.button10);
            this.setCancelable(false);
            ed.setText(list.get(selected_num).getName());
            canc.setOnClickListener(v -> this.dismiss());
            add.setOnClickListener(v -> {
                String text = ed.getText().toString();
                if(text.isEmpty()){
                    ed.setError("Required!");
                    return;
                }

                for(ChipClass cl :list){
                    if(cl.getName().equals(text)){
                        return;
                    }

                }
                this.dismiss();
                try{
                    SQLiteDatabase myDatabase = NotepadPage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String COMM = "UPDATE ChipTable SET CHIP_NAME = '"+text+ "' WHERE ID = "+selected_id;
                    myDatabase.execSQL(COMM);
                    myDatabase.close();
                    list.get(selected_num).setName(text);
                    list.get(selected_num).getChip().setText(text);
                    Toast.makeText(NotepadPage.this, "Successfully Updated!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(NotepadPage.this, "Error! Insert Failed....", Toast.LENGTH_LONG).show();
                }

            });
        }
    }


    public class MyCustomGrid extends BaseAdapter{
        ArrayList<DocumentClass> listara;

        public MyCustomGrid(ArrayList<DocumentClass> listara) {
            this.listara = listara;
        }

        @Override
        public int getCount() {
            if(listara == null || listara.size() ==0){
                return 1;
            }
            return listara.size();
        }

        @Override
        public Object getItem(int position) {
            if(listara == null || listara.size() == 0){
                return null;
            }
            return listara.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(listara == null || listara.size() == 0){
                return getLayoutInflater().inflate(R.layout.grid_null, null);
            }
            View view = getLayoutInflater().inflate(R.layout.grid_document, null);
            TextView tx1 = view.findViewById(R.id.textView36);
            TextView tx2 = view.findViewById(R.id.textView37);
            String title = listara.get(position).getText();
            if(title.length() > 30){
                title = title.substring(0, 30).trim();
                title = title + "....";
            }
            tx1.setText(title);
            String body = listara.get(position).getPreview_text();
            int type = listara.get(position).getType();
            if(type == 0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tx2.setText(Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT));
                }else {
                    tx2.setText(Html.fromHtml(body));
                }
            }
            else {
                tx2.setText(body);
            }

            if(listara.get(position).getType() == 0){
                view.setBackgroundResource(R.drawable.f5);
            }

            view.setOnLongClickListener(v -> {
                PopupShowDetails popupShowDetails = new PopupShowDetails(NotepadPage.this,
                        listara.get(position));
                popupShowDetails.show();
                return true;
            });

            view.setOnClickListener(v -> {
                PopupEditOptions popupEditOptions = new PopupEditOptions(NotepadPage.this, listara.get(position));
                popupEditOptions.show();
            });

            return view;
        }
    }


    class PopupShowDetails extends Dialog{
        DocumentClass documentClass;
        Button update_button, delete_button;
        TextView cancel_text;
        MaterialSpinner spinner;
        int nuk = 0;
        public PopupShowDetails(@NonNull Context context, DocumentClass documentClass) {
            super(context);
            this.documentClass = documentClass;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_showdetailsdocument);
            setCancelable(false);
            update_button = findViewById(R.id.button11);
            delete_button = findViewById(R.id.button12);
            cancel_text = findViewById(R.id.textView29);
            spinner = findViewById(R.id.spinner);
            List<String> nameList = new ArrayList<>();
            for(int i=0; i<list.size(); i++) {
                ChipClass cl = list.get(i);
                nameList.add(cl.getName());
                if(documentClass.getLabel() == cl.getId()){
                    nuk = i;
                }
            }
            spinner.setItems(nameList);
            spinner.setSelectedIndex(nuk);


            update_button.setOnClickListener(v -> {
                this.dismiss();
                int index = spinner.getSelectedIndex();
                if(index == nuk){
                    return;
                }
                String sel_srtp = nameList.get(index);
                int num_c = documentClass.getId();
                int id = 1;

                for(int i= 0; i< list.size() ; i++){
                    if(list.get(i).getName().equals(sel_srtp)){
                        id = list.get(i).getId();
                        break;
                    }
                }

                try {
                    SQLiteDatabase myDatabase = NotepadPage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String COMM = "UPDATE DocumentTable SET Label = "+id+ " WHERE ID = "+num_c;
                    myDatabase.execSQL(COMM);
                    myDatabase.close();
                    getDocumentList();
                    Toast.makeText(NotepadPage.this, "Successfully Updated!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(NotepadPage.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                }


            });

            delete_button.setOnClickListener(v -> {
                this.dismiss();
                int id_to_delete = documentClass.getId();
                try {
                    SQLiteDatabase myDatabase = NotepadPage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String COMM = "DELETE FROM DocumentTable WHERE ID = "+id_to_delete;
                    myDatabase.execSQL(COMM);
                    myDatabase.close();
                    getDocumentList();
                    Toast.makeText(NotepadPage.this, "Successfully Deleted!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(NotepadPage.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            });

            cancel_text.setOnClickListener(v -> this.dismiss());

        }
    }

    class PopupEditOptions extends Dialog{
        TextView cancel_button;
        RadioGroup radioGroup;
        RadioButton rb1, rb2;
        DocumentClass documentClass;
        Button conti_button;
        int number = 0;
        public PopupEditOptions(@NonNull Context context, DocumentClass documentClass) {
            super(context);
            this.documentClass = documentClass;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.poup_editoption);
            cancel_button = findViewById(R.id.textView29);
            radioGroup = findViewById(R.id.rgp);
            rb1 = findViewById(R.id.radioButton11);
            rb2 = findViewById(R.id.radioButton13);
            conti_button = findViewById(R.id.button13);
            String s1, s2;
            number = documentClass.getType();
            if(documentClass.getType() == 0){
                s1 = "Styled Text (Default)";
                s2 = "Normal Text";
                radioGroup.check(R.id.radioButton11);
            }
            else{
                s1 = "Styled Text";
                s2 = "Normal Text (Default)";
                radioGroup.check(R.id.radioButton13);
            }
            rb1.setText(s1);
            rb2.setText(s2);

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioButton11){
                    number = 0;
                }
                else {
                    number = 1;
                }
            });

            conti_button.setOnClickListener(v -> {
                sharedPreferences.edit().putBoolean("isNewText", false).apply();
                int id = documentClass.getId();
                sharedPreferences.edit().putInt("idtoSearch", id).apply();
                this.dismiss();
                if(number == 0){
                    startActivity(new Intent(NotepadPage.this, CreateNotepad.class));
                }
                else {
                    startActivity(new Intent(NotepadPage.this, NormalNotepad.class));
                }

            });

            cancel_button.setOnClickListener(v -> this.dismiss());

        }
    }


    class PopupSearchView extends Dialog{
        TextView cancel_txt;
        Button search_btn;
        MaterialSpinner spinner;
        EditText editText;
        int selected_number = 0;
        public PopupSearchView(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_searchview);
            this.setCancelable(false);
            cancel_txt = findViewById(R.id.textView29);
            spinner = findViewById(R.id.spinner2);
            search_btn = findViewById(R.id.button14);
            editText = findViewById(R.id.ad);
            spinner.setItems("Search by Title", "Search by Text");
            spinner.setOnItemSelectedListener((view, position, id, item) -> selected_number = position);
            cancel_txt.setOnClickListener(v -> this.dismiss());
            search_btn.setOnClickListener(v -> {
                String keywords = editText.getText().toString().toLowerCase();
                if(keywords.isEmpty()){
                    editText.setError("Nothing To search!");
                    return;
                }
                this.dismiss();
                ArrayList<DocumentClass> newAra = new ArrayList<>();
                for(DocumentClass documentClass: arrayList){
                    String val_to_search;
                    if(selected_number == 0){
                        val_to_search = documentClass.getText().toLowerCase();
                    }
                    else{
                        val_to_search = documentClass.getPreview_text().toLowerCase();
                    }
                    if(val_to_search.contains(keywords)){
                        newAra.add(documentClass);
                    }
                }
                if(first_chip_id != -1){
                    chipGroup.check(first_chip_id);
                }
                if(newAra.isEmpty()) gridView.setNumColumns(1);
                else gridView.setNumColumns(2);
                gridView.setAdapter(new MyCustomGrid(newAra));
            });
        }
    }

}