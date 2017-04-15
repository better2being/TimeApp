package com.tsunami.timeapp.Activity3.Calendar.manager;

import android.support.annotation.NonNull;

import com.tsunami.timeapp.Activity3.Calendar.manager.CalendarUnit.CalendarType;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author zhenglifeng
 */
public class DefaultFormatter implements Formatter {

    private final DateTimeFormatter dayFormatter;
    private final DateTimeFormatter weekHeaderFormatter;
    private final DateTimeFormatter monthHeaderFormatter;

    public DefaultFormatter() {
        this("E", "'week' w", "MMMM yyyy");
    }

    public DefaultFormatter(@NonNull String dayPattern, @NonNull String weekPattern, @NonNull String monthPattern) {
        dayFormatter = DateTimeFormat.forPattern(dayPattern);
        weekHeaderFormatter = DateTimeFormat.forPattern(weekPattern);
        monthHeaderFormatter = DateTimeFormat.forPattern(monthPattern);
    }

    @Override public String getDayName(@NonNull LocalDate date) {
        return date.toString(dayFormatter);
    }

    @Override public String getHeaderText(@CalendarType int type, @NonNull LocalDate from, @NonNull LocalDate to) {
        switch (type) {
            case CalendarUnit.TYPE_WEEK:
                return from.toString(weekHeaderFormatter);
            case CalendarUnit.TYPE_MONTH:
                return from.toString(monthHeaderFormatter);
            default:
                throw new IllegalStateException("Unknown calendar type");
        }
    }
}
