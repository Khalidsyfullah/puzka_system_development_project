package com.akapps.puzka;

public class Transaction {
    String date, account, notes;
    double amount, extra_charges;
    int type;

    public Transaction(String date, String account, String notes, double amount, double extra_charges, int type) {
        this.date = date;
        this.account = account;
        this.notes = notes;
        this.amount = amount;
        this.extra_charges = extra_charges;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getAccount() {
        return account;
    }

    public String getNotes() {
        return notes;
    }

    public double getAmount() {
        return amount;
    }

    public double getExtra_charges() {
        return extra_charges;
    }

    public int getType() {
        return type;
    }
}
