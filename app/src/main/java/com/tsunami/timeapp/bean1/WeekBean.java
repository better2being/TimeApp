package com.tsunami.timeapp.bean1;

import android.graphics.Bitmap;

/**
 * @author jilihao
 */
public class WeekBean {

    private String day;
    private Bitmap icon;
    private String cond;
    private String temp;

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getCond() {
        return cond;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }
}
