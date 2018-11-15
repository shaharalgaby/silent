package com.example.android.silentmyphone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.JobIntentService;
import android.util.Log;

public class SyncWithCalendarService extends JobIntentService {

    private static final String TAG = SyncWithCalendarService.class.getSimpleName();

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "Start working on the service");
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions
                        ((Activity)getApplicationContext(),
                                new String[]{Manifest.permission.READ_CALENDAR},
                                200);
        }

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
                new String[]{CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                             CalendarContract.Calendars.NAME},
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String[] calNames = new String[cursor.getCount()];
            int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                // RETRIEVE THE CALENDAR NAMES AND IDS
                calNames[i] = cursor.getString(1);
                Log.i(TAG,"Name is: " + calNames[i]);
                cursor.moveToNext();
            }
            cursor.close();
            if (calIds.length > 0) {
                // WE'RE SAFE HERE TO DO ANY FURTHER WORK
            }
        }
    }
}
