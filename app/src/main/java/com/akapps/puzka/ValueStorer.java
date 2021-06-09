package com.akapps.puzka;

public class ValueStorer {
    String normal_txt, html_txt, font_color;
    int listnum, stylenum, sizenum;

    public ValueStorer(String normal_txt, String html_txt, String font_color, int listnum, int stylenum, int sizenum) {
        this.normal_txt = normal_txt;
        this.html_txt = html_txt;
        this.font_color = font_color;
        this.listnum = listnum;
        this.stylenum = stylenum;
        this.sizenum = sizenum;
    }

    public String getNormal_txt() {
        return normal_txt;
    }

    public String getHtml_txt() {
        return html_txt;
    }

    public String getFont_color() {
        return font_color;
    }

    public int getListnum() {
        return listnum;
    }

    public int getStylenum() {
        return stylenum;
    }

    public int getSizenum() {
        return sizenum;
    }
}
