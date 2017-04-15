package com.tsunami.timeapp.Activity3.Calendar.manager;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

/**
 * @author zhenglifeng
 */
public interface Formatter {

    String getDayName(@NonNull LocalDate date);
    String getHeaderText(int type, @NonNull LocalDate from, @NonNull LocalDate to);

}
