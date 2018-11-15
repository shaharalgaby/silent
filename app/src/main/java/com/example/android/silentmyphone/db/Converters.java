package com.example.android.silentmyphone.db;

import android.arch.persistence.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static boolean[] fromString(String value){
        boolean[] array = new boolean[7];

        if(value.length() == 7) {
            for (int i = 0; i < array.length; i++) {
                array[i] = value.charAt(i) == '0' ? false : true;
            }
        }

        return array;
    }

    @TypeConverter
    public static String fromArray(boolean[] array){
        String s = "";
        if(array != null) {
            for (int i = 0; i < array.length; i++) {
                s += array[i] ? "1" : "0";
            }
        }
        return s;
    }
}
