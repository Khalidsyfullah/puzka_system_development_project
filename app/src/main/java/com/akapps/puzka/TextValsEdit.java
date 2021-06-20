package com.akapps.puzka;

public class TextValsEdit {
    int id;
    String dates, val;

    public TextValsEdit(int id, String dates, String val) {
        this.id = id;
        this.dates = dates;
        this.val = val;
    }


    public int getId() {
        return id;
    }

    public String getDates() {
        return dates;
    }

    public String getVal() {
        return val;
    }
}
