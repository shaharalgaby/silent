package com.example.android.silentmyphone.muteService;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TimePicker;

import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.utils.CalendarUtils;

import java.util.Calendar;

import androidx.work.Data;
import androidx.work.WorkManager;

public class MuteJobsModel {

    private static final String TAG = MuteJobsModel.class.getSimpleName();

    public static final String JOBID = "jobid";
    private final static long DAY_IN_MILLIS = 1000*60*60*24;

    public static final String WORK_MUTE= "mute";
    public static final String WORK_UNMUTE= "unmute";

    public static void addJob(MuteJob job,Context context) {

        JobsViewModel mViewModel = new JobsViewModel((Application)context.getApplicationContext());
        mViewModel.insert(job);

        //TODO check if input is valid
        addAlaramJob(job,context);
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

    public static void addAlaramJob(MuteJob job, Context context) {
        Log.i(TAG,"Adding the alarm");

        Intent muteIntent = new Intent(context, MuteWorker.class);
        muteIntent.putExtra(JOBID,job.getId());
        muteIntent.setAction(getJobMuteAlarmId(job));
        PendingIntent pIntent = PendingIntent.getService(context, 0, muteIntent, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(Build.VERSION.SDK_INT >= 23)
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,job.getStartTime(),pIntent);
        else
            alarm.setExact(AlarmManager.RTC_WAKEUP,job.getStartTime(),pIntent);

        Intent unmuteIntent = new Intent(context,UnmuteWorker.class);
        unmuteIntent.putExtra(JOBID,job.getId());
        unmuteIntent.setAction(getJobUnmuteAlarmId(job));
        PendingIntent pendingIntent = PendingIntent.getService(context,0,unmuteIntent,0);
        if(Build.VERSION.SDK_INT >= 23)
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,job.getEndTime(),pendingIntent);
        else
            alarm.setExact(AlarmManager.RTC_WAKEUP,job.getEndTime(),pendingIntent);
    }

    public static void removeJob(MuteJob job, JobsViewModel viewModel,Context context) {
        viewModel.delete(job);
        removeAlarms(job,context);
    }

    public static void updateJob(MuteJob job,Context context) {
        JobsViewModel viewModel = new JobsViewModel((Application)context.getApplicationContext());
        viewModel.update(job);
        removeAlarms(job,context);
        addAlaramJob(job,context);
    }

    public static void removeAlarms(MuteJob job, Context context) {
        Intent muteIntent = new Intent(context,MuteWorker.class);
        muteIntent.setAction(getJobMuteAlarmId(job));
        Intent unmuteIntent = new Intent(context,UnmuteWorker.class);
        unmuteIntent.setAction(getJobUnmuteAlarmId(job));

        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pIntent = PendingIntent.getService(context, 0, muteIntent, 0);
        PendingIntent pendingIntent = PendingIntent.getService(context,0,unmuteIntent,0);

        alarm.cancel(pIntent);
        alarm.cancel(pendingIntent);
    }

    static void updateJobTime(MuteJob job, Context context) {

        JobsViewModel viewModel = new JobsViewModel((Application)context.getApplicationContext());

        Calendar nowCalendar = Calendar.getInstance();
        int today = nowCalendar.get(Calendar.DAY_OF_WEEK);
        Log.i(TAG,"Today is: " + today);

        int offset = 0;
        boolean[] checked = job.getRepeatDays();

        for(int i= (today + 6) % 7 ;i<8 ;i++) {
            offset ++ ;
            if(checked[i % 7]) {
                break;
            }
        }

        Log.i(TAG,"Offset is: " + offset);

        Calendar calendar = Calendar.getInstance();

        //Change the job start time
        calendar.setTimeInMillis(job.getStartTime());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR + offset));
        job.setStartTime(calendar.getTimeInMillis());

        //change the job end time
        calendar.setTimeInMillis(job.getEndTime());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR + offset));
        job.setEndTime(calendar.getTimeInMillis());

        viewModel.update(job);
    }

    public static String getJobMuteAlarmId(MuteJob job) {
        return job.getId()+"mute";
    }

    public static String getJobUnmuteAlarmId(MuteJob job) {

        return job.getId()+"unmute";
    }
}
