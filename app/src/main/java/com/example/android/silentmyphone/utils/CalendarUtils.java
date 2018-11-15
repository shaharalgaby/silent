package com.example.android.silentmyphone.utils;

import java.util.Calendar;

public class CalendarUtils {

    public static String[] daysNames = new String[]
            {"Monday", "Tuesday", "Wednesday", "Thursday",
                            "Friday", "Saturday", "Sunday"};

    public static String[] getWeekFromTodayOn(){
        Calendar calendar = Calendar.getInstance();
        String[] days = new String[7];

        days[0] = "Today"; days[1] = "Tommorow";
        //calendar.roll(Calendar.DAY_OF_WEEK,true);

        for(int i = 2; i < days.length; i++) {
            calendar.roll(Calendar.DAY_OF_WEEK,true);
            days[i] = daysNames[calendar.get(Calendar.DAY_OF_WEEK)-1];
        }

        return days;
    }
}
