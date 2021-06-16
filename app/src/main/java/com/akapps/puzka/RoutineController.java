package com.akapps.puzka;

public class RoutineController implements Comparable<RoutineController>{
    int begin_hour, begin_minute, end_hour, end_minute;
    String title;
    int importance_level;

    public RoutineController(int begin_hour, int begin_minute, int end_hour, int end_minute, String title, int importance_level) {
        this.begin_hour = begin_hour;
        this.begin_minute = begin_minute;
        this.end_hour = end_hour;
        this.end_minute = end_minute;
        this.title = title;
        this.importance_level = importance_level;
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

    public void setImportance_level(int importance_level) {
        this.importance_level = importance_level;
    }

    public int getImportance_level() {
        return importance_level;
    }

    @Override
    public int compareTo(RoutineController o) {
        int total_time_begin1 = this.begin_hour* 60 + this.begin_minute;
        int totla_time_begin2 = o.getBegin_hour()* 60 + o.getBegin_minute();
        return total_time_begin1 - totla_time_begin2;
    }
}
