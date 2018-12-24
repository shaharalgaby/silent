package com.example.android.silentmyphone.utils;

import android.Manifest;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.R;
import com.example.android.silentmyphone.SyncService;
import com.example.android.silentmyphone.SyncWithCalendarService;
import com.example.android.silentmyphone.UserCalendar;
import com.example.android.silentmyphone.UserEvent;
import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.muteService.MuteJobsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ServiceUtils {

    private final static String TAG = ServiceUtils.class.getSimpleName();
    private final static String EMAIL_ADDRESS = "shal@calldorado.com";
    private final static int DAYS_TO_ADD = 7;
    private Context context;

    private static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.IS_PRIMARY
    };
    // The indices for the projection array above.
    private static final int CALENDAR_ID_INDEX = 0;
    private static final int CALENDAR_ACCOUNT_NAME_INDEX = 1;
    private static final int CALENDAR_DISPLAY_NAME_INDEX = 2;
    private static final int CALENDAR_OWNER_ACCOUNT_INDEX = 3;
    private static final int CALENDAR_ACCOUNT_TYPE_INDEX = 4;
    private static final int CALENDAR_IS_PRIMARY_INDEX = 5;


    private static final String[] EVENTS_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.CALENDAR_ID
    };
    // The indices for the projection array above.
    private static final int EVENTS_ID_INDEX = 0;
    private static final int EVENTS_START_DATE = 1;
    private static final int EVENTS_END_DATE = 2;
    private static final int EVENTS_TITLE_INDEX = 3;
    private static final int EVENTS_LOCATION_INDEX = 4;
    private static final int EVENTS_CALENDAR_ID = 5;
    private static final int EVENT_NOT_FOUND = -1;


    public static void setUriObserver(Context context) {
        if(Build.VERSION.SDK_INT>=24) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(new JobInfo.Builder(SyncService.JOB_ID,
                    new ComponentName("com.example.android.silentmyphone", SyncService.class.getName()))
                    .addTriggerContentUri(new JobInfo.TriggerContentUri(
                            CalendarContract.Calendars.CONTENT_URI, 0
                    ))
                    .build());
        }
    }

    public static ArrayList<UserEvent> getEvents(List<Long> calendarIds, Context context) {

        if(calendarIds == null || calendarIds.isEmpty())
            return null;

        ArrayList<UserEvent> events = new ArrayList<>();

        Cursor cursor;
        ContentResolver contentResolver = context.getContentResolver();
        Uri eventsUri = CalendarContract.Events.CONTENT_URI;
        String ids = toCommaSeparatedString(calendarIds);

        String selection = "((" + CalendarContract.Events.CALENDAR_ID + " in "+ids+") AND ("
                + CalendarContract.Events.DTSTART + " > ?) AND ("
                + CalendarContract.Events.DTEND   + " < ?))";

        Calendar oneWeekFromNow = Calendar.getInstance();
        oneWeekFromNow.setTimeInMillis(System.currentTimeMillis());
        oneWeekFromNow.add(Calendar.DAY_OF_YEAR,DAYS_TO_ADD);

        String[] selectionArgs = new String[]{
                System.currentTimeMillis()+"",
                oneWeekFromNow.getTimeInMillis()+""
        };

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
            return null;
        cursor = contentResolver.query(eventsUri, EVENTS_PROJECTION, selection, selectionArgs,
                CalendarContract.Events.DTSTART);

        if(cursor!= null) {
            while (cursor.moveToNext()) {
                String id;
                String startDate;
                String endDate;
                String title;
                String location;
                String calendarId;

                // Get the field values
                id = cursor.getString(EVENTS_ID_INDEX);
                startDate = cursor.getString(EVENTS_START_DATE);
                endDate = cursor.getString(EVENTS_END_DATE);
                title = cursor.getString(EVENTS_TITLE_INDEX);
                location = cursor.getString(EVENTS_LOCATION_INDEX);
                calendarId = cursor.getString(EVENTS_CALENDAR_ID);

                events.add(new UserEvent(startDate,endDate,id,title,location,calendarId));
                Log.i(TAG, id + ", " + CalendarUtils.getPrettyHourAndDate(Long.parseLong(startDate))
                        + ", " + CalendarUtils.getPrettyHourAndDate(Long.parseLong(endDate)) + ", " + title
                        + ", " + location);
            }
            cursor.close();
        }
        return events;
    }

    public static ArrayList<UserCalendar> getCalendars(Context context) {

        ArrayList<UserCalendar> calendars = new ArrayList<>();

        // Run query
        Cursor cur;
        ContentResolver cr = context.getContentResolver();
        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
            return null;

        String selection = "((" + CalendarContract.Calendars.IS_PRIMARY + " = 1) OR " +
                "(" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " like '%local%'))";

        cur = cr.query(calendarUri, CALENDAR_PROJECTION, selection, null, null);

        if(cur != null) {
            while (cur.moveToNext()) {
                long calID;
                String displayName;
                String accountName;
                String ownerName;
                String accountType;
                String isPrimary;

                // Get the field values
                calID = cur.getLong(CALENDAR_ID_INDEX);
                displayName = cur.getString(CALENDAR_DISPLAY_NAME_INDEX);
                accountName = cur.getString(CALENDAR_ACCOUNT_NAME_INDEX);
                ownerName = cur.getString(CALENDAR_OWNER_ACCOUNT_INDEX);
                accountType = cur.getString(CALENDAR_ACCOUNT_TYPE_INDEX);
                isPrimary = cur.getString(CALENDAR_IS_PRIMARY_INDEX);

                calendars.add(new UserCalendar(calID,displayName,accountName,displayName));
                Log.i(TAG, displayName + ", " + calID + ", " + accountName + ", " + ownerName +
                          accountType +  ", " + isPrimary);
            }
            cur.close();
        }
        return calendars;
    }

    public static void buildCalendarsDialog(ArrayList<UserCalendar> calendars, Context context) {

        ClientConfig clientConfig = new ClientConfig(context);

        CharSequence[] items = new CharSequence[calendars.size()];
        boolean[] checked = new boolean[calendars.size()];
        ArrayList<Long> calendarIds = new ArrayList<>();
        List<Long> lastCalendars = fromCommaSeperatedString(clientConfig.getChosenCalendars());
        Log.i(TAG,"previous calendars: " + clientConfig.getChosenCalendars());

        //Set the calendar items for the multi choices
        for(int i=0;i<calendars.size();i++){
            String name = calendars.get(i).getDisplayName();
            if(name.contains("local"))
                items[i] = context.getResources().getString(R.string.local_calendar);
            else
                items[i] = calendars.get(i).getCalendarOwner();
            calendarIds.add(calendars.get(i).getId());
            Log.i(TAG,calendars.get(i).getId()+"");
        }

        //Set the checked status from previous choices.
        for(int i=0; i<calendarIds.size();i++){
            if(lastCalendars.contains(calendarIds.get(i))) {
                checked[i] = true;
            }
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checked[i] = b;
            }
        });
        adb.setPositiveButton("Sync", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<Long> chosenCalendars = new ArrayList<>();
                for(int index = 0; index < calendars.size(); index++){
                    if(checked[index])
                        chosenCalendars.add(calendars.get(index).getId());
                }
                long[] chosenCalendarsArray = listToArray(chosenCalendars);
                clientConfig.setChosenCalendars(toCommaSeparatedString(chosenCalendars));

                Intent mServiceIntent = new Intent();
                mServiceIntent.putExtra("chosenCalendars",chosenCalendarsArray);

                SyncWithCalendarService.enqueueWork(
                context,
                SyncWithCalendarService.class,
                200,
                mServiceIntent);

            }
        });
        adb.setNegativeButton("Cancel", null);
        adb.setTitle("Choose the calendars to sync");
        adb.show();
    }

    public void updateContentChanges(Context context) {
        this.context = context;
        SyncAsyncTask task = new SyncAsyncTask();
        task.execute();
    }

    private class SyncAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.i(TAG,"Synching the content changes");

            JobsViewModel viewModel = new JobsViewModel((Application)context);
            ClientConfig clientConfig = new ClientConfig(context);

            List<MuteJob> activeJobs = viewModel.getJobs();
            List<Long> chosenCalendars = fromCommaSeperatedString(clientConfig.getChosenCalendars());
            ArrayList<UserEvent> freshEvents = getEvents(chosenCalendars,context);

            ArrayList<MuteJob> activeBusinessJobs = new ArrayList<>();
            for(MuteJob job : activeJobs) {
                if(job.isBusiness())
                    activeBusinessJobs.add(job);
            }

            for(MuteJob job : activeBusinessJobs) {
                int index = getIndexOfEvent(freshEvents,job.getId());
                if(index == EVENT_NOT_FOUND) {
                    //Its a remove job event
                    MuteJobsModel.removeJob(job,viewModel,context);
                } else {

                    UserEvent freshEvent = freshEvents.get(index);
                    boolean infoChanged = false;
                    boolean jobChanged = false;

                    if(freshEvent.getStartDate() != null &&
                            !freshEvent.getStartDate().equals(""+ job.getStartTime()) ){
                        job.setStartTime(Long.parseLong(freshEvent.getStartDate()));
                        jobChanged = true;
                        infoChanged = true;
                    }
                    if (freshEvent.getEndDate() != null &&
                            !freshEvent.getEndDate().equals(""+ job.getEndTime())){
                        job.setEndTime(Long.parseLong(freshEvent.getEndDate()));
                        jobChanged = true;
                        infoChanged = true;
                    }
                    if(freshEvent.getLocation() != null &&
                            !freshEvent.getLocation().equals(job.getEventLocation())){
                        job.setEventLocation(freshEvent.getLocation());
                        infoChanged = true;
                    }
                    if(freshEvent.getTitle() != null &&
                            !freshEvent.getTitle().equals(job.getEventTitle())) {
                        job.setEventTitle(freshEvent.getTitle());
                        infoChanged = true;
                    }
                    if(infoChanged) {
                        if(jobChanged) {
                            MuteJobsModel.updateJob(job, context);
                        } else {
                            viewModel.update(job);
                        }
                    }
                }
            }

            //Check if there is a new event
            for(UserEvent event : freshEvents) {

                boolean found = false;

                for(MuteJob job : activeBusinessJobs) {
                    if(job.getId().equals(getEventId(event))){
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    MuteJob newJob = new MuteJob(getEventId(event),Long.parseLong(event.getStartDate()),
                            Long.parseLong(event.getEndDate()),MuteJob.MODE_ONE_TIME,null);

                    if(event.getTitle() != null)
                        newJob.setEventTitle(event.getTitle());

                    if(event.getLocation()!=null)
                        newJob.setEventLocation(event.getLocation());

                    MuteJobsModel.addJob(newJob,context);
                }
            }
            return true;
        }
    }

    private static String getEventId(UserEvent event) {
        return event.getId()+"-"+event.getCalendarId();
    }

    private static int getIndexOfEvent(ArrayList<UserEvent> events, String eventId) {
        for(int i = 0; i < events.size(); i++) {
            String id = getEventId(events.get(i));
            if(eventId.equals(id)){
                return i;
            }
        }
        return EVENT_NOT_FOUND;
    }

//    public static ArrayList<UserEvent> getEventsByIds

    private static String toCommaSeparatedString(List<Long> list) {
        if (list.size() > 0) {
            StringBuilder nameBuilder = new StringBuilder();
            for (Long item : list) {
                nameBuilder.append(item).append(", ");
            }
            nameBuilder.deleteCharAt(nameBuilder.length() - 1);
            nameBuilder.deleteCharAt(nameBuilder.length() - 1);
            return "(" + nameBuilder.toString() + ")";
        } else {
            return "";
        }
    }

    public static List<Long> fromCommaSeperatedString (String str) {
        ArrayList<Long> list= new ArrayList<>();
        if(str.equals("()") || str.isEmpty()){
            return list;
        } else {
            str = str.substring(1,str.length()-1);
            List<String> items = Arrays.asList(str.split("\\s*,\\s*"));
            for(String item : items)
                list.add(Long.parseLong(item));
        }
        return list;
    }

    public static long[] listToArray(List<Long> list) {
        long[] arr = new long[list.size()];

        for(int i = 0; i < list.size();i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

}
