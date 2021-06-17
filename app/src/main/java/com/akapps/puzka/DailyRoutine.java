package com.akapps.puzka;

public class DailyRoutine implements Comparable<DailyRoutine>{
    int begin_hour, begin_minute, end_hour, end_minute;
    String title;
    String details;

    public DailyRoutine(int begin_hour, int begin_minute, int end_hour, int end_minute, String title, String details) {
        this.begin_hour = begin_hour;
        this.begin_minute = begin_minute;
        this.end_hour = end_hour;
        this.end_minute = end_minute;
        this.title = title;
        this.details = details;
    }

    public int getBegin_hour() {
        return begin_hour;
    }

    public int getBegin_minute() {
        return begin_minute;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public int getEnd_minute() {
        return end_minute;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public void setBegin_hour(int begin_hour) {
        this.begin_hour = begin_hour;
    }

    public void setBegin_minute(int begin_minute) {
        this.begin_minute = begin_minute;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public void setEnd_minute(int end_minute) {
        this.end_minute = end_minute;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public int compareTo(DailyRoutine o) {
        int total_time_begin1 = this.begin_hour* 60 + this.begin_minute;
        int totla_time_begin2 = o.getBegin_hour()* 60 + o.getBegin_minute();
        return total_time_begin1 - totla_time_begin2;
    }
}
