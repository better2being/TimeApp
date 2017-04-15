package com.tsunami.timeapp.bean1;

import android.graphics.Bitmap;

/**
 * @author jilihao
 */
public class LifeBean {

    private Bitmap icon;
    private String item;
    private String condition;
    private String more;
    private Bitmap up_down;
    private boolean click;

    public LifeBean() {
        click = false;
    }

    public void setIcon(Bitmap bitmap) {
        this.icon = bitmap;
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

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public String getMore() {
        return more;
    }

    public void setUpDown(Bitmap bitmap) {
        this.up_down = bitmap;
    }

    public Bitmap getUpDown() {
        return up_down;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public boolean getClick() {
        return click;
    }
}

