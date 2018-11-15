package com.example.android.silentmyphone.muteService;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TimePicker;

import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.db.JobsViewModel;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MuteJobsModel {

    public static final String TAG = MuteJobsModel.class.getSimpleName();

    //helper method to add a job.
    public static void addMuteJob(TimePicker start, TimePicker end, boolean isChecked,
                                  CheckBox[] checkBoxes, JobsViewModel mViewModel){

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        int startHour, endHour, startMinute, endMinute;

        if(Build.VERSION.SDK_INT < 23){
            startHour = start.getCurrentHour();
            startMinute = start.getCurrentMinute();
            endHour = end.getCurrentHour();
            endMinute = end.getCurrentMinute();
        } else{
            startHour = start.getHour();
            startMinute = start.getMinute();
            endHour = end.getHour();
            endMinute = end.getMinute();
        }

        startDate.set(Calendar.HOUR_OF_DAY, startHour);
        startDate.set(Calendar.MINUTE, startMinute);
        endDate.set(Calendar.HOUR_OF_DAY, endHour);
        endDate.set(Calendar.MINUTE,endMinute);

        boolean[] repeatDays = new boolean[7];
        for(int i = 0; i< repeatDays.length; i++) {
            repeatDays[i] = checkBoxes[i].isChecked();
        }

        MuteJob job = new MuteJob(
                startDate.getTimeInMillis(),
                endDate.getTimeInMillis(),
                MuteJob.REPEAT_MODE_DAILY,
                repeatDays);

        mViewModel.insert(job);

        addJob(startDate.getTimeInMillis(), endDate.getTimeInMillis(),
                job);
    }

    //This is the actual job to add a job.
    public static void addJob(long startDateInMillis, long endDateInMillis, MuteJob job) {

        long startDelay = startDateInMillis - System.currentTimeMillis();
        long endDelay = endDateInMillis - System.currentTimeMillis();

        Log.i(TAG,"Adding the job with delay "+startDelay+", "+endDelay);

        @SuppressLint("RestrictedApi") //Temoporary solution, apparently a bug
        Data data = new Data.Builder().putLong("jobid",job.getStartTime()).build();

        //Assign the mute and unmute works.
        OneTimeWorkRequest muteWorkRequest =
                new OneTimeWorkRequest.Builder(MuteWorker.class)
                .addTag("mute"+job.getStartTime())
                .setInitialDelay(startDelay,TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();
        WorkManager.getInstance().enqueue(muteWorkRequest);

        OneTimeWorkRequest unmuteWorkRequest =
                new OneTimeWorkRequest.Builder(UnmuteWorker.class)
                .addTag("unmute"+job.getStartTime())
                .setInitialDelay(endDelay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();
        WorkManager.getInstance().enqueue(unmuteWorkRequest);
    }

}
