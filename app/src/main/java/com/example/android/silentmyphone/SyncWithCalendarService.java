package com.example.android.silentmyphone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.example.android.silentmyphone.utils.CalendarUtils;

public class SyncWithCalendarService extends JobIntentService {

    private static final String TAG = SyncWithCalendarService.class.getSimpleName();

    private final static String EMAIL_ADDRESS = "shal@calldorado.com";

    private final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT,
    };
    // The indices for the projection array above.
    private static final int CALENDAR_ID_INDEX = 0;
    private static final int CALENDAR_ACCOUNT_NAME_INDEX = 1;
    private static final int CALENDAR_DISPLAY_NAME_INDEX = 2;
    private static final int CALENDAR_OWNER_ACCOUNT_INDEX = 3;

    private final String[] EVENTS_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
    };
    // The indices for the projection array above.
    private static final int EVENTS_ID_INDEX = 0;
    private static final int EVENTS_START_DATE = 1;
    private static final int EVENTS_END_DATE = 2;
    private static final int EVENTS_TITLE_INDEX = 3;
    private static final int EVENTS_LOCATION_INDEX = 4;

    public void getCalendars() {
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{EMAIL_ADDRESS,
                EMAIL_ADDRESS};

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
            return;

        cur = cr.query(calendarUri, CALENDAR_PROJECTION, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(CALENDAR_ID_INDEX);
            displayName = cur.getString(CALENDAR_DISPLAY_NAME_INDEX);
            accountName = cur.getString(CALENDAR_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(CALENDAR_OWNER_ACCOUNT_INDEX);

            Log.i(TAG,displayName +", " + calID+", "+accountName+", "+ownerName);
            getEvents(calID);
        }
    }

    public void getEvents(long calendarId){
        Cursor cursor = null;
        ContentResolver contentResolver = getContentResolver();
        Uri eventsUri = CalendarContract.Events.CONTENT_URI;

        String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?))";
        String[] selectionArgs = new String[]{Long.toString(calendarId)};

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
            return;
        cursor = contentResolver.query(eventsUri, EVENTS_PROJECTION, selection, selectionArgs, null);

        while (cursor.moveToNext()) {
            String id = null;
            String startDate = null;
            String endDate = null;
            String title = null;
            String location = null;

            // Get the field values
            id = cursor.getString(EVENTS_ID_INDEX);
            startDate = cursor.getString(EVENTS_START_DATE);
            endDate = cursor.getString(EVENTS_END_DATE);
            title = cursor.getString(EVENTS_TITLE_INDEX);
            location = cursor.getString(EVENTS_LOCATION_INDEX);

            Log.i(TAG,id +", " + CalendarUtils.getPrettyHourAndDate(Long.parseLong(startDate))
                    +", " + CalendarUtils.getPrettyHourAndDate(Long.parseLong(endDate)) +", " + title
            + ", " + location);
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "Start working on the service");

        getCalendars();
//        Cursor cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
//                new String[]{CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
//                             CalendarContract.Calendars.NAME,
//                             CalendarContract.Calendars.SYNC_EVENTS},
//                null,
//                null,
//                null);
//        if (cursor != null && cursor.moveToFirst()) {
//            String[] calNames = new String[cursor.getCount()];
//            int[] calIds = new int[cursor.getCount()];
//            for (int i = 0; i < calNames.length; i++) {
//                // RETRIEVE THE CALENDAR NAMES AND IDS
//                calNames[i] = cursor.getString(1);
//                Log.i(TAG,  calNames[i]+"");
//
//                if(calNames[i] != null && calNames[i].equals("shaharalgaro@gmail.com")) {
//                    Uri.Builder builder = Uri.parse("content://com.android.calendar/events").buildUpon();
//                    Cursor eventCursor = getContentResolver().query(builder.build(),
//                            new String[]  { CalendarContract.Events.TITLE,
//                                    CalendarContract.Events.DTSTART,
//                                    CalendarContract.Events.DTEND},
//                            CalendarContract.Calendars._ID+"=:" + calIds[i],
//                            null, null);
//
//                    Log.i(TAG,"There are :" +eventCursor.getCount() + " events");
//
//                    if(eventCursor!= null && eventCursor.moveToFirst()) {
//                    }
//                }
//
//                cursor.moveToNext();
//            }
//            cursor.close();
//            if (calIds.length > 0) {
//                // WE'RE SAFE HERE TO DO ANY FURTHER WORK
//            }
//        }
    }
}
