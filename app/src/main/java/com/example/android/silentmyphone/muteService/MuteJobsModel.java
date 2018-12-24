package com.example.android.silentmyphone.muteService;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.utils.CalendarUtils;
import com.example.android.silentmyphone.utils.NotificationsUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MuteJobsModel {

    private static final String TAG = MuteJobsModel.class.getSimpleName();

    public static final String JOBID = "jobid";
    private final static long DAY_IN_MILLIS = 1000*60*60*24;

    public static final String WORK_MUTE= "mute";
    public static final String WORK_UNMUTE= "unmute";

    public static void addJob(MuteJob job,Context context) {

        boolean isChecked = job.getRepeatDays() != null;
        JobsViewModel mViewModel = new JobsViewModel((Application)context.getApplicationContext());

        mViewModel.insert(job);

        long startDelay = job.getStartTime() - System.currentTimeMillis();
        long endDelay = job.getEndTime() - System.currentTimeMillis();

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(job.getStartTime());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(job.getEndTime());

        if(isChecked && start.get(Calendar.HOUR_OF_DAY) > end.get(Calendar.HOUR_OF_DAY)) {
            //user set one alarm for tomorrow
            endDelay += DAY_IN_MILLIS;
        }

        if(!isChecked && startDelay > endDelay) {
            try {
                NotificationsUtils.showAlertDialog("Can't add it",
                        "Start cannot come after the end !",context);
                mViewModel.delete(job);
                throw new Exception("Start cannot come after the end !");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            addJob(job, startDelay, endDelay);
        }
    }

    //helper method to add a job.
    public static void addMuteJob(String id, int startDayOffset, int endDayOffset,
                                  TimePicker startPicker, TimePicker endPicker, boolean isChecked,
                                  CheckBox[] checkBoxes, Context context){

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        int startHour, endHour, startMinute, endMinute;

        if(Build.VERSION.SDK_INT < 23) {
            startHour = startPicker.getCurrentHour();
            endHour = endPicker.getCurrentHour();
            startMinute = startPicker.getCurrentMinute();
            endMinute = endPicker.getCurrentMinute();
        } else {
            startHour = startPicker.getHour();
            startMinute = startPicker.getMinute();
            endHour = endPicker.getHour();
            endMinute = endPicker.getMinute();
        }

        startDate.set(Calendar.HOUR_OF_DAY, startHour);
        startDate.set(Calendar.MINUTE, startMinute);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.DAY_OF_YEAR,now.get(Calendar.DAY_OF_YEAR)+startDayOffset);
        endDate.set(Calendar.HOUR_OF_DAY, endHour);
        endDate.set(Calendar.MINUTE,endMinute);
        endDate.set(Calendar.SECOND,0);
        endDate.set(Calendar.DAY_OF_YEAR,now.get(Calendar.DAY_OF_YEAR)+endDayOffset);


        boolean[] repeatDays = new boolean[7];
        for(int i = 0; i< repeatDays.length; i++) {
            repeatDays[i] = checkBoxes[i].isChecked();
        }

        MuteJob job = new MuteJob(
                id,
                startDate.getTimeInMillis(),
                endDate.getTimeInMillis(),
                isChecked ? MuteJob.MODE_REPEAT : MuteJob.MODE_ONE_TIME,
                isChecked ? repeatDays : null);

        addJob(job,context);

    }

    //This is the actual method to add a job.
    private static void addJob(MuteJob job, long startDelay, long endDelay) {

        Log.i(TAG,"Adding the job with delay "+startDelay+", "+endDelay);

        @SuppressLint("RestrictedApi") //Temporary solution, apparently a bug
        Data data = new Data.Builder().putString(JOBID,job.getId()).build();

        //Assign the mute work.
        OneTimeWorkRequest muteWorkRequest =
                new OneTimeWorkRequest.Builder(MuteWorker.class)
                .addTag("mute"+job.getId())
                .setInitialDelay(startDelay,TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();
        WorkManager.getInstance().enqueue(muteWorkRequest);

        //Assign the unmute work.
        OneTimeWorkRequest unmuteWorkRequest =
                new OneTimeWorkRequest.Builder(UnmuteWorker.class)
                .addTag("unmute"+job.getId())
                .setInitialDelay(endDelay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();
        WorkManager.getInstance().enqueue(unmuteWorkRequest);
    }

    public static void removeJob(MuteJob job, JobsViewModel viewModel,Context context) {
        viewModel.delete(job);
        WorkManager workManager = WorkManager.getInstance();
        workManager.cancelAllWorkByTag("mute"+job.getId());
        workManager.cancelAllWorkByTag("unmute" + job.getId());
        workManager.cancelUniqueWork("muteperiodic"+job.getId());
        workManager.cancelUniqueWork("unmuteperiodic"+job.getId());
    }

    public static void updateJob(MuteJob job,Context context) {
        JobsViewModel viewModel = new JobsViewModel((Application)context.getApplicationContext());
        viewModel.update(job);
        WorkManager workManager = WorkManager.getInstance();
        workManager.cancelAllWorkByTag("mute"+job.getId());
        workManager.cancelAllWorkByTag("unmute" + job.getId());
        workManager.cancelUniqueWork("muteperiodic"+job.getId());
        workManager.cancelUniqueWork("unmuteperiodic"+job.getId());
        addJob(job,context);
    }

    static void prepareDailyJobs(String jobType, MuteJob job) {
        @SuppressLint("RestrictedApi") //Temporary solution, apparently a bug
                Data data = new Data.Builder().putString(JOBID,job.getId()).build();

        PeriodicWorkRequest.Builder workRequest =
                new PeriodicWorkRequest.Builder(MuteWorker.class,24,TimeUnit.HOURS)
                        .addTag(jobType+job.getId())
                        .setInputData(data);

        PeriodicWorkRequest work = workRequest.build();
        WorkManager.getInstance()
                .enqueueUniquePeriodicWork("muteperiodic"+job.getId(),ExistingPeriodicWorkPolicy.REPLACE, work);
    }

    static boolean shouldWorkToday(MuteJob job, String work) {

        if(job.getRepeatMode() == MuteJob.MODE_ONE_TIME)
            return true;

        Calendar nowCalendar = Calendar.getInstance();
        int today = nowCalendar.get(Calendar.DAY_OF_WEEK);
        Log.i(TAG,"Today is: " + today);

        if(work.equals(WORK_MUTE)) {
            return job.getRepeatDays()[(today + 5) % 7];
        } else {
            //unmute job
            if (CalendarUtils.isBefore(job.getStartTime(), job.getEndTime()))
                return job.getRepeatDays()[(today + 5) % 7];
            else {
                //return yesterday
                return job.getRepeatDays()[(today+4) % 7 ];
            }
        }
    }
}
