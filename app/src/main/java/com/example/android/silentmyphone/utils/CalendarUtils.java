package com.example.android.silentmyphone.utils;

import android.util.Log;

import com.example.android.silentmyphone.MuteJob;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {

    private final static String TAG = CalendarUtils.class.getSimpleName();

    public static String[] daysNames = new String[]
            {"Monday", "Tuesday", "Wednesday", "Thursday",
                            "Friday", "Saturday", "Sunday"};

    public static String[] getWeekFromTodayOn(){
        Calendar calendar = Calendar.getInstance();
        String[] days = new String[7];

        days[0] = "Today"; days[1] = "Tommorow";

        for(int i = 2; i < days.length; i++) {
            calendar.roll(Calendar.DAY_OF_WEEK,true);
            days[i] = daysNames[calendar.get(Calendar.DAY_OF_WEEK)-1];
        }

        return days;
    }

    public static String getPrettyHour(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return String.format(Locale.ENGLISH,"%02d",calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format(Locale.ENGLISH, "%02d",calendar.get(Calendar.MINUTE));
    }

    public static String getPrettyDate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.get(Calendar.DAY_OF_MONTH) +"\\"+
                String.format(Locale.ENGLISH,"%02d",calendar.get(Calendar.MONTH)+1) + "\\" +
                calendar.get(Calendar.YEAR);
    }

    public static String getPrettyHourAndDate (long millis) {
        return getPrettyHour(millis)+ ", " + getPrettyDate(millis);
    }

    public static boolean isBefore(long startHour, long endHour) {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startHour);

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endHour);

        return start.before(end);
    }
}
