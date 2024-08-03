package com.rbs.workout.freak.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static long getDateZeroZone(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDateWithTimeZone(long time) {
        return ((long) Calendar.getInstance().get(15)) + time;
    }

    public static long getTimeZeroZone() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return calendar.getTimeInMillis();
    }

    public static String getDate(long time, Locale locale) {
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE,MMM dd", locale);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        Date date = new Date();
        date.setTime(time);
        return sdf.format(date);
    }

    public static String getYearMonthString(Context context, int year, int month) {
        DateFormat sdf;
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, year);
        calendar.set(2, month);
        if (locale.getLanguage().equals("en")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("fr")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("it")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("de")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("es")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("ko")) {
            sdf = new SimpleDateFormat("yyyy.MM.", locale);
        } else if (locale.getLanguage().equals("ja")) {
            sdf = new SimpleDateFormat("yyyy/MM", locale);
        } else if (locale.getLanguage().equals("th")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("zh")) {
            if ((locale.getCountry() + "").equals("TW")) {
                sdf = new SimpleDateFormat("yyyy/MM", locale);
            } else {
                sdf = new SimpleDateFormat("yyyy-MM", locale);
            }
        } else if (locale.getLanguage().equals("ar")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("ru")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("in")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("tr")) {
            sdf = new SimpleDateFormat("MM yyyy", locale);
        } else if (locale.getLanguage().equals("pt")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("el")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("sr")) {
            sdf = new SimpleDateFormat("MM.yyyy.", locale);
        } else if (locale.getLanguage().equals("bg")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("uk")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("fa")) {
            sdf = new SimpleDateFormat("yyyy/MM", locale);
        } else if (locale.getLanguage().equals("nl")) {
            sdf = new SimpleDateFormat("MM-yyyy", locale);
        } else if (locale.getLanguage().equals("pl")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("sk")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("da")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("hu")) {
            sdf = new SimpleDateFormat("yyyy.MM.", locale);
        } else if (locale.getLanguage().equals("ro")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("my")) {
            sdf = new SimpleDateFormat("yyyy/MM", Locale.ENGLISH);
        } else if (locale.getLanguage().equals("sq")) {
            sdf = new SimpleDateFormat("yyyy-MM", locale);
        } else if (locale.getLanguage().equals("vi")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("mk")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("hr")) {
            sdf = new SimpleDateFormat("MM.yyyy.", locale);
        } else if (locale.getLanguage().equals("hi")) {
            sdf = new SimpleDateFormat("MM-yyyy", locale);
        } else if (locale.getLanguage().equals("iw")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("ur")) {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        } else if (locale.getLanguage().equals("sv")) {
            sdf = new SimpleDateFormat("yyyy-MM", locale);
        } else if (locale.getLanguage().equals("cs")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else if (locale.getLanguage().equals("nb")) {
            sdf = new SimpleDateFormat("MM.yyyy", locale);
        } else {
            sdf = new SimpleDateFormat("MM/yyyy", locale);
        }
        return sdf.format(calendar.getTime());
    }

    public static int getIntervalDay(long startTime, long endTime) {
        return (int) ((getDateZeroZone(endTime) - getDateZeroZone(startTime)) / 86400000);
    }
}
