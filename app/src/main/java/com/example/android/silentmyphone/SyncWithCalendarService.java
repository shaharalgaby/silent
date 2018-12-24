package com.example.android.silentmyphone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.muteService.MuteJobsModel;
import com.example.android.silentmyphone.utils.ServiceUtils;

import java.util.ArrayList;

public class SyncWithCalendarService extends JobIntentService {

    private static final String TAG = SyncWithCalendarService.class.getSimpleName();

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "Start working on the service");

        long[] calendars = intent.getLongArrayExtra("chosenCalendars");
        ArrayList<Long> calendarsList = new ArrayList<>();
        for(long l : calendars) {
            calendarsList.add(l);
        }

        ArrayList<UserEvent> events = ServiceUtils.getEvents(calendarsList,getApplicationContext());

        if(events != null) {
            JobsViewModel viewModel = new JobsViewModel(getApplication());
            //Add al the jobs
            for (UserEvent event : events) {
                MuteJob job = new MuteJob(event.calendarId + "-" + event.getId(), Long.parseLong(event.getStartDate()),
                        Long.parseLong(event.getEndDate()), MuteJob.MODE_ONE_TIME, null);
                job.setEventTitle(event.getTitle());
                job.setEventLocation(event.getLocation());
                job.setBusiness(true);
                if(viewModel.getJobById(job.getId()) == null) {
                    Log.i(TAG,"Inserting new job");
                    MuteJobsModel.addJob(job,getApplicationContext());
                }
            }
        }
    }
}
