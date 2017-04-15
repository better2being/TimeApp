package com.tsunami.timeapp;

import com.kelin.calendarlistview.library.BaseCalendarItemModel;

/**
 * @author shenxiaoming
 */
public class CustomCalendarItemModel  extends BaseCalendarItemModel{
    private int newsCount;
    private boolean isFav;

    public int getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(int newsCount) {
        this.newsCount = newsCount;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }
}
