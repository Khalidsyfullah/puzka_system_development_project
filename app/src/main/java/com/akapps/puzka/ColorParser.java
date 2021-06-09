package com.akapps.puzka;

public class ColorParser {
    String code;
    int serial;

    public ColorParser(String code, int serial) {
        this.code = code;
        this.serial = serial;
    }

    public String getCode() {
        return code;
    }

    public int getSerial() {
        return serial;
    }
}
