package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class FragTransactions extends Fragment {
    LinearLayout ll1, ll2, ll3, ll4;
    TextView total_account, account_balance, total_balance, tx1, tx2, tx3;

    Button add_account, show_details, add_transaction, grid_button, add_trans, cancel_trans;

    TextInputLayout amount, extra_charges, add_note, hide_portion;
    Context myContext;
    LinedTextView linedTextView;
    GridView gridView;

    ScrollView sc1, sc2;

    AutoCompleteTextView fromAccount, toAccount;

    double total_balance_amount = 0.0;
    double total_cash_balance = 0.0;
    int total_account_number = 0;

    String from_account_name = "";
    String to_account_name = "";

    int currently_selected_method = 0;

    ArrayList<Account> accountList = new ArrayList<>();
    double current_toaccount_balance = 0.0, current_from_account_balance = 0.0;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_transaction, container, false);
        ll1 = view.findViewById(R.id.llo1);
        ll2 = view.findViewById(R.id.llo2);
        ll3 = view.findViewById(R.id.llo3);
        ll4 = view.findViewById(R.id.llo4);

        gridView = view.findViewById(R.id.grid_details);
        grid_button = view.findViewById(R.id.buttoPo);
        gridView.setVisibility(View.GONE);
        grid_button.setVisibility(View.GONE);

        tx1 = view.findViewById(R.id.textView80);
        tx2 = view.findViewById(R.id.textView81);
        tx3 = view.findViewById(R.id.textView82);

        total_account = view.findViewById(R.id.textView76);
        account_balance = view.findViewById(R.id.textView77);
        total_balance = view.findViewById(R.id.textVie2);

        hide_portion = view.findViewById(R.id.tyyyyy);
        hide_portion.setVisibility(View.GONE);

        show_details = view.findViewById(R.id.button25);
        add_account = view.findViewById(R.id.button26);
        add_transaction = view.findViewById(R.id.butto6n25);

        sc1 = view.findViewById(R.id.scroll);
        sc2 = view.findViewById(R.id.scroll2);

        fromAccount = view.findViewById(R.id.filled_exposed_dropdown);
        toAccount = view.findViewById(R.id.filled_exposed_dropdown1);


        sc2.setVisibility(View.GONE);


        amount = view.findViewById(R.id.outlinedTextField);
        extra_charges = view.findViewById(R.id.outlinedTextField2);
        add_note = view.findViewById(R.id.outlinedTextField1);

        add_trans = view.findViewById(R.id.butto6n);
        cancel_trans = view.findViewById(R.id.butto6n2);


        linedTextView = view.findViewById(R.id.textView85);

        linedTextView.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFr(linedTextView);
            newFragment.show(getChildFragmentManager(), "datePicker");
        });


        updateAccountDatabase();

        fromAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from_account_name = accountList.get(position).getName();
                amount.setError("Available Balance ( "+ accountList.get(position).getBalance()+ " )");
                current_from_account_balance = accountList.get(position).getBalance();
            }
        });

        toAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                to_account_name = accountList.get(position).getName();
                current_toaccount_balance = accountList.get(position).getBalance();
            }
        });



        add_transaction.setOnClickListener(v -> {
            if(sc1.getVisibility() == View.VISIBLE) sc1.setVisibility(View.GONE);
            if(sc2.getVisibility() == View.GONE) sc2.setVisibility(View.VISIBLE);
            if(grid_button.getVisibility() == View.VISIBLE) grid_button.setVisibility(View.GONE);
            if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);

            currently_selected_method = 0;
            tx1.setBackgroundResource(R.drawable.f9);
            tx2.setBackgroundResource(R.drawable.f6);
            tx3.setBackgroundResource(R.drawable.f6);

            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            linedTextView.setText(dateFormat.format(new Date()));
        });

        tx1.setOnClickListener(v -> {
            if(currently_selected_method == 0) return;
            currently_selected_method = 0;
            tx1.setBackgroundResource(R.drawable.f9);
            tx2.setBackgroundResource(R.drawable.f6);
            tx3.setBackgroundResource(R.drawable.f6);
            if(hide_portion.getVisibility() == View.VISIBLE) hide_portion.setVisibility(View.GONE);
        });

        tx2.setOnClickListener(v -> {
            if(currently_selected_method == 1) return;
            currently_selected_method = 1;
            tx1.setBackgroundResource(R.drawable.f6);
            tx2.setBackgroundResource(R.drawable.f9);
            tx3.setBackgroundResource(R.drawable.f6);
            if(hide_portion.getVisibility() == View.VISIBLE) hide_portion.setVisibility(View.GONE);
        });

        tx3.setOnClickListener(v -> {
            if(currently_selected_method == 2) return;
            currently_selected_method = 2;
            tx1.setBackgroundResource(R.drawable.f6);
            tx2.setBackgroundResource(R.drawable.f6);
            tx3.setBackgroundResource(R.drawable.f9);
            if(hide_portion.getVisibility() == View.GONE) hide_portion.setVisibility(View.VISIBLE);
        });


        show_details.setOnClickListener(v -> {
            if(sc1.getVisibility() == View.VISIBLE) sc1.setVisibility(View.GONE);
            if(sc2.getVisibility() == View.VISIBLE) sc2.setVisibility(View.GONE);
            if(grid_button.getVisibility() == View.GONE) grid_button.setVisibility(View.VISIBLE);
            if(gridView.getVisibility() == View.GONE) gridView.setVisibility(View.VISIBLE);

        });

        add_account.setOnClickListener(v -> {
            PopupAccountSave popupAccountSave = new PopupAccountSave(myContext, false, null);
            popupAccountSave.show();
        });

        grid_button.setOnClickListener(v -> {
            if(sc1.getVisibility() == View.GONE) sc1.setVisibility(View.VISIBLE);
            if(sc2.getVisibility() == View.VISIBLE) sc2.setVisibility(View.GONE);
            if(grid_button.getVisibility() == View.VISIBLE) grid_button.setVisibility(View.GONE);
            if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);

        });

        cancel_trans.setOnClickListener(v -> {
            if(sc1.getVisibility() == View.GONE) sc1.setVisibility(View.VISIBLE);
            if(sc2.getVisibility() == View.VISIBLE) sc2.setVisibility(View.GONE);
            if(grid_button.getVisibility() == View.VISIBLE) grid_button.setVisibility(View.GONE);
            if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
        });

        add_trans.setOnClickListener(v -> {
            String s1 = linedTextView.getText().toString();
            String s2 = Objects.requireNonNull(amount.getEditText()).getText().toString();
            if(s2.isEmpty()){
                amount.setError("Balance Can't be Empty!");
                return;
            }
            double val;
            try {
                val = Double.parseDouble(s2);
            }catch (Exception e){
                amount.setError("Invalid Amount!");
                return;
            }

            if(currently_selected_method == 2 && from_account_name.equals(to_account_name)){
                Toast.makeText(myContext, "Both Account Can't Be Same!", Toast.LENGTH_LONG).show();
                return;
            }

            String s3 = Objects.requireNonNull(extra_charges.getEditText()).getText().toString();
            double val1 = 0.0;
            if(!s3.isEmpty()){
                try {
                    val1 = Double.parseDouble(s3);
                }catch (Exception e){
                    extra_charges.setError("Invalid Amount!");
                    return;
                }
            }
            String s4 = Objects.requireNonNull(add_note.getEditText()).getText().toString();
            if(s4.isEmpty())
            {
                add_note.setError("Required!");
                return;
            }

            try {
                SQLiteDatabase myDatabase = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("Date", s1);
                contentValues.put("Notes", s4);
                contentValues.put("Amount", val);
                contentValues.put("Echarges", val1);


                if(currently_selected_method != 2)
                {
                    double newbalance;
                    if(currently_selected_method == 0){
                        newbalance = current_from_account_balance + val - val1;
                        contentValues.put("Type", 0);
                    }
                    else{
                        newbalance = current_from_account_balance - val - val1;
                        if(newbalance< 0.0){
                            amount.setError("Balance Exceeding From Account!");
                            return;
                        }
                        contentValues.put("Type", 1);
                    }

                    String COMM1 = "UPDATE AccountTable SET Balance = "+ newbalance + " WHERE Name = '"+ from_account_name+ "'";
                    myDatabase.execSQL(COMM1);
                    contentValues.put("Account", from_account_name);
                }
                else
                {
                    double amo = current_from_account_balance - val - val1;
                    if(amo < 0.0)
                    {
                        amount.setError("Balance Exceeding From Account!");
                        return;
                    }
                    double amo1 = current_toaccount_balance + val;
                    String COMM1 = "UPDATE AccountTable SET Balance = "+ amo + " WHERE Name = '"+ to_account_name + "'";
                    String COMM2 = "UPDATE AccountTable SET Balance = "+ amo1 + " WHERE Name = '"+ from_account_name+ "'";
                    String nam = from_account_name + "#"+ to_account_name;
                    myDatabase.execSQL(COMM1);
                    myDatabase.execSQL(COMM2);
                    contentValues.put("Type", 2);
                    contentValues.put("Account", nam);
                }
                myDatabase.insert("TransactionTable", null, contentValues);
                myDatabase.close();
                Toast.makeText(myContext, "Successfully Added!", Toast.LENGTH_LONG).show();
                updateAccountDatabase();
                if(sc1.getVisibility() == View.GONE) sc1.setVisibility(View.VISIBLE);
                if(sc2.getVisibility() == View.VISIBLE) sc2.setVisibility(View.GONE);
                if(grid_button.getVisibility() == View.VISIBLE) grid_button.setVisibility(View.GONE);
                if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);

            }catch (Exception e)
            {
                Toast.makeText(myContext, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });



        return view;
    }

    void updateAccountDatabase()
    {
        try {
            if(accountList.size() > 0)
            {
                accountList.clear();
            }
            total_balance_amount = 0.0;
            total_account_number = 0;
            total_cash_balance = 0.0;
            SQLiteDatabase myDatabase = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            String TABLE_NAME1 = "AccountTable";
            String COMM1 = "SELECT * FROM "+ TABLE_NAME1;
            Cursor cursor = myDatabase.rawQuery(COMM1, null);
            int id_index = cursor.getColumnIndex("ID");
            int name_index = cursor.getColumnIndex("Name");
            int type_index = cursor.getColumnIndex("Type");
            int balance_index = cursor.getColumnIndex("Balance");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                Account account = new Account(cursor.getString(name_index),
                        cursor.getString(type_index),
                        cursor.getDouble(balance_index),
                        cursor.getInt(id_index));
                accountList.add(account);
                total_balance_amount += account.getBalance();
                if(account.getType().equals("Cash-Account") || account.getType().equals("Main-Account"))
                {
                    total_cash_balance+= account.getBalance();
                }
                total_account_number++;
                cursor.moveToNext();
            }
            cursor.close();
            myDatabase.close();

            total_balance.setText(String.valueOf(total_balance_amount));
            total_account.setText(String.valueOf(total_account_number));
            account_balance.setText(String.valueOf(total_cash_balance));

            ArrayList<String> oList = new ArrayList<>();
            for(Account ac: accountList)
            {
                oList.add(ac.getName());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(myContext,
                    R.layout.dropdown_menu_popup_item, oList);
            fromAccount.setAdapter(arrayAdapter);
            toAccount.setAdapter(arrayAdapter);

            from_account_name = accountList.get(0).getName();
            to_account_name = accountList.get(0).getName();

            gridView.setAdapter(new FuGridView(accountList));

        }catch (Exception e){
            Toast.makeText(myContext, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public static class DatePickerFr extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        LinedTextView text;
        public DatePickerFr(LinedTextView text) {
            this.text = text;
        }

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String sd;
            month = month+1;
            if(month< 10) sd = "0"+ month;
            else sd = String.valueOf(month);

            String str = day + "/" + sd + "/" + year;
            text.setText(str);
        }
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }


    class FuGridView extends BaseAdapter{
        ArrayList<Account> arrayAccount;

        public FuGridView(ArrayList<Account> arrayAccount) {
            this.arrayAccount = arrayAccount;
        }

        @Override
        public int getCount() {
            return arrayAccount.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayAccount.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_account_details, null);
            TextView textView1 = view.findViewById(R.id.textView88);
            TextView textView2 = view.findViewById(R.id.textView89);
            int num = position+ 1;
            textView2.setText(String.valueOf(num));

            String str = "<b>Account Name: </b><br>"+ arrayAccount.get(position).getName() + "<br>" +
                    "<b>Account Type: </b><br>"+ arrayAccount.get(position).getType() + "<br>" +
                    "<b>Balance: </b> <i> "+ arrayAccount.get(position).getBalance();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView1.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
            }else {
                textView1.setText(Html.fromHtml(str));
            }

            view.setOnClickListener(v -> {
                PopupAccountSave popupAccountSave = new PopupAccountSave(myContext, true, arrayAccount.get(position));
                popupAccountSave.show();
            });

            return view;
        }
    }


    class PopupAccountSave extends Dialog{
        TextView cancel_text;
        EditText ed1, ed2;
        MaterialSpinner spinner;
        String accout_name = "Main-Account";
        boolean isUpdate;
        Button add_btn;
        Account account;

        public PopupAccountSave(@NonNull Context context, boolean isUpdate, Account account) {
            super(context);
            this.isUpdate = isUpdate;
            this.account = account;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_accountsetup);
            spinner = findViewById(R.id.spinner3);
            ed1 = findViewById(R.id.ad3);
            ed2 = findViewById(R.id.ad4);
            cancel_text = findViewById(R.id.textView29);
            add_btn = findViewById(R.id.button22);

            cancel_text.setOnClickListener(v -> this.dismiss());
            String s1 = "Update";
            String[] itemsVal = new String[] {"Main-Account", "Cash-Account", "Bank-Account",
                            "Mobile-Banking-Account", "Cards", "Others"};

            spinner.setItems(itemsVal);
            spinner.setOnItemSelectedListener((view, position, id, item) -> accout_name = String.valueOf(item));

            if(isUpdate)
            {
                add_btn.setText(s1);
                ed1.setText(account.getName());
                ed2.setText(String.valueOf(account.getBalance()));
                for(int i=0; i<itemsVal.length; i++){
                    if(itemsVal[i].equals(account.getType())){
                        spinner.setSelectedIndex(i);
                        accout_name = itemsVal[i];
                        break;
                    }
                }

            }

            this.setCancelable(false);

            add_btn.setOnClickListener(v -> {
                String str1 = ed1.getText().toString();
                if(str1.isEmpty()){
                    ed1.setError("Required!");
                    return;
                }
                if(str1.contains("#")){
                    ed1.setError("Name Doesn't support '#'");
                    return;
                }
                String str2 = ed2.getText().toString();
                if(str2.isEmpty()){
                    ed2.setError("Required!");
                    return;
                }
                double bala;
                try {
                    bala = Double.parseDouble(str2);
                }catch (Exception e){
                    ed2.setError("Invalid Balance!");
                    return;
                }

                this.dismiss();

                if(!isUpdate){
                    for(Account ac: accountList){
                        if(str1.equals(ac.getName())){
                            ed1.setError("Account Name Already Exists!");
                            return;
                        }
                    }
                    try {
                        SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Name", str1);
                        contentValues.put("Type", accout_name);
                        contentValues.put("Balance", bala);
                        db.insert("AccountTable",null,  contentValues);
                        Toast.makeText(myContext, "Successfully Saved!", Toast.LENGTH_LONG).show();
                        updateAccountDatabase();
                    }catch (Exception e){
                        Toast.makeText(myContext, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                else{
                    try {
                        SQLiteDatabase db = myContext.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        String UPDATE_COMM = "UPDATE AccountTable SET Name = '"+str1+ "', Type = '"+
                                accout_name + "', Balance = '"+ bala+ "' WHERE ID = "+account.id;
                        db.execSQL(UPDATE_COMM);
                        Toast.makeText(myContext, "Successfully updated!", Toast.LENGTH_LONG).show();
                        updateAccountDatabase();
                    }catch (Exception e){
                        Toast.makeText(myContext, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            });



        }
    }


}
