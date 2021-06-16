package com.akapps.puzka;

import android.annotation.SuppressLint;
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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragSummary extends Fragment {
    LinedTextView linedTextView;
    Button search;
    GridView gridView;
    ArrayList<Transaction> list = new ArrayList<>();
    Context myContext;
    String currentdate;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_summary, container, false);
        linedTextView = view.findViewById(R.id.textView85);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        currentdate = dateFormat.format(new Date());

        gridView = view.findViewById(R.id.grid_transaction);
        search = view.findViewById(R.id.butto6n3);

        linedTextView.setOnClickListener(v -> {
            DialogFragment newFragment = new FragTransactions.DatePickerFr(linedTextView);
            newFragment.show(getChildFragmentManager(), "datePicker");
        });

        search.setOnClickListener(v -> {
            String st = linedTextView.getText().toString();
            if(st.isEmpty() || st.equals(currentdate)) return;
            currentdate = st;
            ArrayList<Transaction> newAra = new ArrayList<>();
            for(Transaction transaction : list){
                if(transaction.getDate().equals(currentdate)){
                    newAra.add(transaction);
                }
            }
            gridView.setAdapter(new CustomGridView(newAra));

        });

        try {
            SQLiteDatabase myDatabase = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            String TABLE_NAME = "TransactionTable";
            String COMM1 = "SELECT COUNT(*) FROM "+ TABLE_NAME;
            String COMM2 = "SELECT * FROM "+ TABLE_NAME;
            Cursor cursor = myDatabase.rawQuery(COMM1, null);
            cursor.moveToFirst();
            int icount = cursor.getInt(0);
            if(icount > 0){
                cursor = myDatabase.rawQuery(COMM2, null);
                int id_index = cursor.getColumnIndex("Date");
                int type_index = cursor.getColumnIndex("Account");
                int label_index = cursor.getColumnIndex("Notes");
                int title_index = cursor.getColumnIndex("Amount");
                int body_index = cursor.getColumnIndex("Echarges");
                int prev_index = cursor.getColumnIndex("Type");
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    Transaction transaction = new Transaction(cursor.getString(id_index),
                            cursor.getString(type_index),
                            cursor.getString(label_index),
                            cursor.getDouble(title_index),
                            cursor.getDouble(body_index),
                            cursor.getInt(prev_index));

                    list.add(transaction);
                    cursor.moveToNext();
                }
            }
            gridView.setAdapter(new CustomGridView(list));
            cursor.close();
            myDatabase.close();
        }catch (Exception e){
            Toast.makeText(myContext, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }


        return view;
    }



    class CustomGridView extends BaseAdapter{
        ArrayList<Transaction> arrayList;

        public CustomGridView(ArrayList<Transaction> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            if(arrayList.size() == 0){
                return 1;
            }
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
            if(arrayList.size() == 0){
                return getLayoutInflater().inflate(R.layout.grid_tnull, null);
            }
            View view = getLayoutInflater().inflate(R.layout.grid_transactionsummary, null);
            String[] strings = new String[]{"Income", "Expense", "Transfer"};

            TextView tx1, tx2, tx3, tx4, tx5, tx6, tx7;
            tx1 = view.findViewById(R.id.textView72);
            tx2 = view.findViewById(R.id.textView90);
            tx3 = view.findViewById(R.id.textView93);
            tx4 = view.findViewById(R.id.textView94);
            tx5 = view.findViewById(R.id.textView91);
            tx6 = view.findViewById(R.id.textView92);
            tx7 = view.findViewById(R.id.textView96);

            tx1.setText(strings[arrayList.get(position).getType()]);
            tx2.setText(arrayList.get(position).getDate());
            String sw;
            if(arrayList.get(position).getType() == 2)
            {
                String[] str = arrayList.get(position).getAccount().split("#");
                sw = "<b>From: </b>"+ str[0] + "    <b>To:</b> "+ str[1];
            }
            else{
                sw = "<b> Account: </b>"+ arrayList.get(position).getAccount();
            }

            tx4.setText(arrayList.get(position).getNotes());
            try {
                double val1 = arrayList.get(position).getAmount();
                double val2 = arrayList.get(position).getExtra_charges();
                String s1 = "<b>Amount: </b>"+ val1;
                String s2 = "<b>Charges: </b>" + val2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tx3.setText(Html.fromHtml(sw, Html.FROM_HTML_MODE_COMPACT));
                    tx5.setText(Html.fromHtml(s1, Html.FROM_HTML_MODE_COMPACT));
                    tx6.setText(Html.fromHtml(s2, Html.FROM_HTML_MODE_COMPACT));
                }else {
                    tx3.setText(Html.fromHtml(sw));
                    tx5.setText(Html.fromHtml(s1));
                    tx6.setText(Html.fromHtml(s2));
                }

                if(arrayList.get(position).getType() == 0){
                    double total = val1 - val2 ;
                    String sty = String.valueOf(total);
                    sty = "+"+ sty;
                    tx7.setTextColor(Color.BLUE);
                    tx7.setText(sty);
                }
                else if(arrayList.get(position).getType() == 1){
                    double total = -val1 - val2 ;
                    String sty = String.valueOf(total);
                    tx7.setTextColor(Color.RED);
                    tx7.setText(sty);
                }
                else {
                    double total = - val2;
                    String sty = String.valueOf(total);
                    tx7.setTextColor(Color.BLUE);
                    tx7.setText(sty);
                }

            }catch (Exception ignored)
            {

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
