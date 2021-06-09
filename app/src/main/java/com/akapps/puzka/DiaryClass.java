package com.akapps.puzka;

public class DiaryClass {
    int year, month, day, id;
    String title, body;

    public DiaryClass(int year, int month, int day, int id, String title, String body) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
