package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class RoutinemainRoute extends Fragment {
    Chip[] chips = new Chip[7];
    View.OnClickListener listener, cancel_listner, update_listner, delete_listner;
    View.OnLongClickListener listener1;
    LinearLayout linearLayout, llay;
    Context myContext;
    TextView add_time, end_time;
    EditText title, description;
    Button add_btn, cancel_btn;

    int currently_editable = 0;
    int c_selected_index = 1;
    ArrayList<ComplexView> arrayList = new ArrayList<>();
    ArrayList<DailyRoutine> araBuilder = new ArrayList<>();
    boolean isEnabled = false;
    ChipGroup chipGroup;
    FloatingActionButton fab;
    SimpleArra simpleArra;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_routinemainroute, container, false);
        chips[0] = view.findViewById(R.id.chip4);
        chips[1] = view.findViewById(R.id.chip5);
        chips[2] = view.findViewById(R.id.chip6);
        chips[3] = view.findViewById(R.id.chip7);
        chips[4] = view.findViewById(R.id.chip8);
        chips[5] = view.findViewById(R.id.chip9);
        chips[6] = view.findViewById(R.id.chip10);
        linearLayout = view.findViewById(R.id.lin);
        fab = view.findViewById(R.id.floatingActionButton3);
        llay = view.findViewById(R.id.lin1);
        add_btn = view.findViewById(R.id.button29);
        cancel_btn = view.findViewById(R.id.button32);
        add_time = view.findViewById(R.id.textView108);
        end_time = view.findViewById(R.id.textView112);
        title = view.findViewById(R.id.editTextTime);
        description = view.findViewById(R.id.editTextTime2);
        chipGroup = view.findViewById(R.id.chu);
        llay.setVisibility(View.GONE);

        chipGroup.check(R.id.chip4);
        fab.setOnClickListener(v -> {
            if(isEnabled) return;
            if(llay.getVisibility() == View.GONE) llay.setVisibility(View.VISIBLE);
            else llay.setVisibility(View.GONE);

        });


        add_time.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment1(add_time);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });
        end_time.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment1(end_time);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });

        add_btn.setOnClickListener(v -> {
            String sr1 = title.getText().toString();
            if(sr1.isEmpty()){
                title.setError("Required!");
                return;
            }

            String sr4 = description.getText().toString();
            if(sr4.isEmpty()){
                description.setError("Required!");
                return;
            }

            String sr2 = add_time.getText().toString();
            String sr3 = end_time.getText().toString();

            String[] psr1 = sr2.split(":");
            int t1 = Integer.parseInt(psr1[0]);
            int t2 = Integer.parseInt(psr1[1]);

            String[] psr2 = sr3.split(":");
            int t3 = Integer.parseInt(psr2[0]);
            int t4 = Integer.parseInt(psr2[1]);

            DailyRoutine dailyRoutine = new DailyRoutine(t1, t2, t3, t4, sr1, sr4);
            araBuilder.add(dailyRoutine);

            String str = new Gson().toJson(araBuilder);

            SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            final String TABLE_NAME = "WeeklyRoutineTable";
            String COMM = "UPDATE "+TABLE_NAME+ " SET Value = '"+ str+ "' WHERE ID = "+ c_selected_index;
            db.execSQL(COMM);
            Toast.makeText(myContext, "Successfully added!", Toast.LENGTH_LONG).show();
            llay.setVisibility(View.GONE);
            databaseList();
            title.setText("");
            description.setText("");
            add_time.setText("");
            end_time.setText("");
        });

        cancel_btn.setOnClickListener(v -> llay.setVisibility(View.GONE));


        listener = v-> {
            isEnabled = false;
            Chip chip = (Chip) v;
            for(int i=0; i<7; i++){
                if(chips[i] == chip){
                    c_selected_index = i+1;
                    break;
                }
            }
            databaseList();
        };

        listener1 = v -> {
            if(isEnabled) return true;
            TextView textView = (TextView) v;
            for(int i=0; i< arrayList.size(); i++){
                if(arrayList.get(i).getEdit_text() == textView){
                    currently_editable = i;
                    break;
                }
            }
            isEnabled = true;
            llay.setVisibility(View.GONE);
            arrayList.get(currently_editable).getLinearLayout().setVisibility(View.VISIBLE);
            arrayList.get(currently_editable).getStart().setEnabled(true);
            arrayList.get(currently_editable).getEnd().setEnabled(true);
            arrayList.get(currently_editable).getTitle().setEnabled(true);
            arrayList.get(currently_editable).getDetails().setEnabled(true);
            return true;
        };


        cancel_listner = v -> {
            int num = 0;
            Button button = (Button) v;
            for(int i = 0; i< arrayList.size(); i++){
                if(button == arrayList.get(i).getCancel()){
                    num = i;
                    break;
                }
            }
            String st1 = arrayList.get(num).getDailyRoutine().getBegin_hour() + ":"+ arrayList.get(num).getDailyRoutine().getBegin_minute();
            String st2 = arrayList.get(num).getDailyRoutine().getEnd_hour() + ":"+ arrayList.get(num).getDailyRoutine().getEnd_minute();
            isEnabled = false;
            arrayList.get(num).getLinearLayout().setVisibility(View.GONE);
            arrayList.get(num).getStart().setText(st1);
            arrayList.get(num).getEnd().setText(st2);
            arrayList.get(num).getTitle().setText(arrayList.get(num).getDailyRoutine().getTitle());
            arrayList.get(num).getDetails().setText(arrayList.get(num).getDailyRoutine().getDetails());
            arrayList.get(num).getStart().setEnabled(false);
            arrayList.get(num).getEnd().setEnabled(false);
            arrayList.get(num).getTitle().setEnabled(false);
            arrayList.get(num).getDetails().setEnabled(false);
        };

        delete_listner = v -> {
            isEnabled = false;
            int num = 0;
            Button button = (Button) v;
            for(int i = 0; i< arrayList.size(); i++){
                if(button == arrayList.get(i).getDelete()){
                    num = i;
                    break;
                }
            }

            SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            final String TABLE_NAME = "WeeklyRoutineTable";
            String sd;

            if(araBuilder.size() == 1){
                sd = "nil";
            }
            else{
                araBuilder.remove(num);
                sd = new Gson().toJson(araBuilder);
            }

            String COMM = "UPDATE "+TABLE_NAME+ " SET Value = '"+ sd+ "' WHERE ID = "+ c_selected_index;
            db.execSQL(COMM);
            db.close();
            Toast.makeText(myContext, "Successfully Deleted!", Toast.LENGTH_LONG).show();
            linearLayout.removeView(arrayList.get(num).getView());
            arrayList.remove(num);
        };

        update_listner = v -> {

            int num = 0;
            Button button = (Button) v;
            for(int i = 0; i< arrayList.size(); i++){
                if(button == arrayList.get(i).getUpdate()){
                    num = i;
                    break;
                }
            }

            String sr1 = arrayList.get(num).getTitle().getText().toString();
            if(sr1.isEmpty()){
                arrayList.get(num).getTitle().setError("Required!");
                return;
            }

            String sr4 = arrayList.get(num).getDetails().getText().toString();
            if(sr4.isEmpty()){
                arrayList.get(num).getDetails().setError("Required!");
                return;
            }
            String sr2 = arrayList.get(num).getStart().getText().toString();
            String sr3 = arrayList.get(num).getEnd().getText().toString();

            String[] psr1 = sr2.split(":");
            int t1 = Integer.parseInt(psr1[0]);
            int t2 = Integer.parseInt(psr1[1]);

            String[] psr2 = sr3.split(":");
            int t3 = Integer.parseInt(psr2[0]);
            int t4 = Integer.parseInt(psr2[1]);


            araBuilder.get(num).setBegin_hour(t1);
            araBuilder.get(num).setBegin_minute(t2);
            araBuilder.get(num).setEnd_hour(t3);
            araBuilder.get(num).setEnd_minute(t4);
            araBuilder.get(num).setDetails(sr4);
            araBuilder.get(num).setTitle(sr1);

            DailyRoutine dailyRoutine = new DailyRoutine(t1, t2, t3, t4, sr1, sr4);
            arrayList.get(num).setDailyRoutine(dailyRoutine);

            isEnabled = false;
            arrayList.get(num).getLinearLayout().setVisibility(View.GONE);
            arrayList.get(num).getStart().setEnabled(false);
            arrayList.get(num).getEnd().setEnabled(false);
            arrayList.get(num).getTitle().setEnabled(false);
            arrayList.get(num).getDetails().setEnabled(false);

            SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            final String TABLE_NAME = "WeeklyRoutineTable";
            String sd = new Gson().toJson(araBuilder);
            String COMM = "UPDATE "+TABLE_NAME+ " SET Value = '"+ sd+ "' WHERE ID = "+ c_selected_index;
            db.execSQL(COMM);
            Toast.makeText(myContext, "Successfully Updated!", Toast.LENGTH_LONG).show();
        };

        for(int i=0; i<7; i++){
            chips[i].setOnClickListener(listener);
        }
        databaseList();
        return view;
    }

    void databaseList()
    {
        SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
        final String TABLE_NAME = "WeeklyRoutineTable";
        String COMM = "SELECT * FROM "+ TABLE_NAME + " WHERE ID = "+ c_selected_index;
        Cursor cursor = db.rawQuery(COMM, null);
        int id_index = cursor.getColumnIndex("ID");
        int value_index = cursor.getColumnIndex("Value");
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            if(cursor.getInt(id_index) == c_selected_index){
                simpleArra = new SimpleArra( cursor.getString(value_index), cursor.getInt(id_index));
                break;
            }
            cursor.moveToNext();
        }

        setList(simpleArra);

        cursor.close();
        db.close();
    }


    void setList(SimpleArra simpleArra)
    {
        if(araBuilder.size() > 0){
            araBuilder.clear();
        }

        if(arrayList.size() > 0){
            arrayList.clear();
        }

        isEnabled = false;
        llay.setVisibility(View.GONE);
        linearLayout.removeAllViews();
        String str = simpleArra.getStr();
        Type type = new TypeToken<ArrayList<DailyRoutine>>(){}.getType();
        if(str.equals("nil")){
            llay.setVisibility(View.VISIBLE);
            return;
        }

        araBuilder = new Gson().fromJson(str, type);
        Collections.sort(araBuilder);
        for(int i=0; i<araBuilder.size(); i++){
            addView(araBuilder.get(i));
        }


    }

    void addView(DailyRoutine dailyRoutine)
    {
        LayoutInflater inflater1 = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view1 = inflater1.inflate(R.layout.layout_dailyroutine, null);

        linearLayout.addView(view1);
        setMargins(view1, 5,30,5, 20);
        LinearLayout ll = view1.findViewById(R.id.linear);
        Button update, delete, cancel;
        update = view1.findViewById(R.id.button29);
        delete = view1.findViewById(R.id.button31);
        cancel = view1.findViewById(R.id.button30);
        EditText title, des;
        title = view1.findViewById(R.id.editTextTime);
        des = view1.findViewById(R.id.editTextTime2);
        TextView tx1 , tx2, tx3;
        tx1 = view1.findViewById(R.id.textView108);
        tx2 = view1.findViewById(R.id.textView112);
        tx3 = view1.findViewById(R.id.textView117);
        tx1.setEnabled(false);
        tx2.setEnabled(false);
        title.setEnabled(false);
        des.setEnabled(false);
        String st1 = dailyRoutine.getBegin_hour() + ":"+ dailyRoutine.getBegin_minute();
        String st2 = dailyRoutine.getEnd_hour() + ":"+ dailyRoutine.getEnd_minute();
        tx1.setText(st1);
        tx2.setText(st2);
        title.setText(dailyRoutine.getTitle());
        des.setText(dailyRoutine.getDetails());
        ll.setVisibility(View.GONE);
        tx1.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment1(tx1);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });
        tx2.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment1(tx2);
            newFragment.show(getChildFragmentManager(), "timePicker");
        });

        cancel.setOnClickListener(cancel_listner);
        delete.setOnClickListener(delete_listner);
        update.setOnClickListener(update_listner);

        tx3.setOnLongClickListener(listener1);

        ComplexView complexView = new ComplexView(update, cancel, delete, title, des, tx1, tx2, ll,
                dailyRoutine, tx3, view1);
        arrayList.add(complexView);
    }


    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }



    static class ComplexView{
        Button update, cancel, delete;
        EditText title, details;
        TextView start, end, edit_text;
        LinearLayout linearLayout;
        DailyRoutine dailyRoutine;
        View view;

        public ComplexView(Button update, Button cancel, Button delete, EditText title, EditText details,
                           TextView start, TextView end, LinearLayout linearLayout, DailyRoutine dailyRoutine,
                           TextView edit_text, View view) {
            this.update = update;
            this.cancel = cancel;
            this.delete = delete;
            this.title = title;
            this.details = details;
            this.start = start;
            this.end = end;
            this.linearLayout = linearLayout;
            this.dailyRoutine = dailyRoutine;
            this.edit_text = edit_text;
            this.view = view;
        }

        public Button getCancel() {
            return cancel;
        }

        public Button getDelete() {
            return delete;
        }

        public EditText getTitle() {
            return title;
        }

        public EditText getDetails() {
            return details;
        }

        public TextView getStart() {
            return start;
        }

        public TextView getEnd() {
            return end;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public DailyRoutine getDailyRoutine() {
            return dailyRoutine;
        }

        public TextView getEdit_text() {
            return edit_text;
        }

        public Button getUpdate() {
            return update;
        }

        public View getView() {
            return view;
        }

        public void setDailyRoutine(DailyRoutine dailyRoutine) {
            this.dailyRoutine = dailyRoutine;
        }
    }


    public static class TimePickerFragment1 extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        TextView textView;

        public TimePickerFragment1(TextView textView) {
            this.textView = textView;
        }

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String str = hourOfDay + ":"+ minute;
            textView.setText(str);
        }
    }
}
