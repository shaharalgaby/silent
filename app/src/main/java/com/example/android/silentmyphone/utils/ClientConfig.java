package com.example.android.silentmyphone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ClientConfig {

    Context context;

    private boolean vibrateOnMute;
    private boolean deleteFinishJobs;
    private String chosenCalendars;

    public ClientConfig(Context context) {
        this.context = context;
        initializeValues();
    }

    public void initializeValues(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        chosenCalendars = preferences.getString("chosenCalendars","()");
        deleteFinishJobs = preferences.getBoolean("deleteFinishJobs",true);
        vibrateOnMute = preferences.getBoolean("vibrateOnMute",false);
    }

    public void setDeleteFinishJobs(boolean deleteFinishJobs) {
        this.deleteFinishJobs = deleteFinishJobs;
        saveConfig("deleteFinishJobs ",deleteFinishJobs );
    }
    public boolean isDeleteFinishJobs() {return deleteFinishJobs;}

    public void setVibrateOnMute(boolean vibrateOnMute) {
        this.vibrateOnMute = vibrateOnMute;
        saveConfig("vibrateOnMute",vibrateOnMute);
    }
    public boolean isVibrateOnMute() {return vibrateOnMute;}

    public void setChosenCalendars(String chosenCalendars) {
        this.chosenCalendars = chosenCalendars;
        saveConfig("chosenCalendars",chosenCalendars);
    }
    public String getChosenCalendars(){
        return chosenCalendars;
    }


    public void saveConfig(String key, Object value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        if(value instanceof String){
            editor.putString(key,(String)value);
        } else if(value instanceof Integer) {
            editor.putInt(key,(Integer)value);
        } else if(value instanceof Boolean) {
            editor.putBoolean(key,(Boolean)value);
        } else if(value instanceof Long) {
            editor.putLong(key, (Long)value);
        }
        editor.apply();
    }
}
