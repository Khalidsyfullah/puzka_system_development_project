package com.akapps.puzka;

public class Account {
    int id;
    String name, type;
    double balance;

    public Account(String name, String type, double balance, int id) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getBalance() {
        return balance;
    }
}
