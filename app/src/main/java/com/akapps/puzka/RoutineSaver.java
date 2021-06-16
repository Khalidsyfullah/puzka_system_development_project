package com.akapps.puzka;

import java.util.ArrayList;

public class RoutineSaver {
    int day, month, year;
    String list;

    public RoutineSaver(int day, int month, int year, String list) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.list = list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getList() {
        return list;
    }
}
