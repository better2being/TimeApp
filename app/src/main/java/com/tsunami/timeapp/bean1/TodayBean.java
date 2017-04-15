package com.tsunami.timeapp.bean1;

import android.graphics.Bitmap;

/**
 * @author jilihao
 */
public class TodayBean {

    private Bitmap icon;
    private String item;
    private String value;

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
