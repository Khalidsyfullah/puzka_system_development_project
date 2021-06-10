package com.akapps.puzka;

public class DrawableSClass {
    String bitmap, undobitmap, mainbitmap, name;
    int id;

    public DrawableSClass(String bitmap, String undobitmap, String mainbitmap, String name, int id) {
        this.bitmap = bitmap;
        this.undobitmap = undobitmap;
        this.mainbitmap = mainbitmap;
        this.name = name;
        this.id = id;
    }

    public String getBitmap() {
        return bitmap;
    }

    public String getUndobitmap() {
        return undobitmap;
    }

    public String getMainbitmap() {
        return mainbitmap;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
