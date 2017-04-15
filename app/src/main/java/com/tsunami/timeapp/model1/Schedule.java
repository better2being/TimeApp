package com.tsunami.timeapp.model1;

/**
 * @author shenxiaoming
 */
public class Schedule {

    private String workname;
    private String worktext;
    private String date;
    private int starttime;
    private int endtime;

    public Schedule() {
    }

    public String getWorkname() {
        return workname;
    }
    public void setWorkname(String workname) {
        this.workname = workname;
    }
    public String getWorktext() {
        return worktext;
    }
    public void setWorktext(String worktext) {
        this.worktext = worktext;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getStarttime() {
        return starttime;
    }
    public void setStarttime(int starttime) {
        this.starttime = starttime;
    }
    public int getEndtime() {
        return endtime;
    }
    public void setEndtime(int endtime) {
        this.endtime = endtime;
    }

    public String toString() {
        return date + ", " + workname + ", " + worktext;
    }

}
