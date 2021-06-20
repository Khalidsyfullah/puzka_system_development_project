package com.akapps.puzka;

class Unordered implements Comparable<Unordered>{
    boolean isNoFound, isDaysIndicator, isDailyRoutine;
    String date;
    int begin_hour, begin_minute, end_hour, end_minute;
    String title, details;
    int importance_level;

    public Unordered(boolean isNoFound, boolean isDaysIndicator, boolean isDailyRoutine, String date, int begin_hour, int begin_minute,
                     int end_hour, int end_minute, String title, String details, int importance_level) {
        this.isNoFound = isNoFound;
        this.isDaysIndicator = isDaysIndicator;
        this.isDailyRoutine = isDailyRoutine;
        this.date = date;
        this.begin_hour = begin_hour;
        this.begin_minute = begin_minute;
        this.end_hour = end_hour;
        this.end_minute = end_minute;
        this.title = title;
        this.details = details;
        this.importance_level = importance_level;
    }

    public Unordered(boolean isDaysIndicator, String date) {
        this.isDaysIndicator = isDaysIndicator;
        this.date = date;
    }

    public Unordered(boolean isNoFound) {
        this.isNoFound = isNoFound;
    }


    public boolean isNoFound() {
        return isNoFound;
    }

    public boolean isDaysIndicator() {
        return isDaysIndicator;
    }

    public boolean isDailyRoutine() {
        return isDailyRoutine;
    }

    public String getDate() {
        return date;
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

    public int getImportance_level() {
        return importance_level;
    }

    @Override
    public int compareTo(Unordered o) {
        int total_time_begin1 = this.begin_hour* 60 + this.begin_minute;
        int totla_time_begin2 = o.getBegin_hour()* 60 + o.getBegin_minute();
        return total_time_begin1 - totla_time_begin2;
    }
}
