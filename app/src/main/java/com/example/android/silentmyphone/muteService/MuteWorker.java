package com.example.android.silentmyphone.muteService;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.utils.CalendarUtils;
import com.example.android.silentmyphone.utils.NotificationsUtils;

public class MuteWorker extends Service {

    private final static String TAG = MuteWorker.class.getSimpleName();
    private MuteJob job;
    String jobId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG,"start the mute work at " + CalendarUtils.getPrettyHour(System.currentTimeMillis()));

        jobId = intent.getStringExtra(MuteJobsModel.JOBID);

        MyAsyncTask task = new MyAsyncTask();
        task.execute();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                JobsViewModel viewModel = new JobsViewModel(getApplication());
                job = viewModel.getJobById(jobId);

                Log.i(TAG,"Mute should work today");
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
                if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    Log.i(TAG,"Silenting phone");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    NotificationsUtils.vibratePhone(getApplicationContext());
                } else {
                    Log.i(TAG,"Phone is already silent.");
                }

                //Prepare next Jobs if needed
                if(job.getRepeatMode() == MuteJob.MODE_REPEAT) {
                    Log.i(TAG,"Preparing next mutes");
                    MuteJobsModel.updateJobTime(job,getApplicationContext());
                    MuteJobsModel.addAlaramJob(job,getApplicationContext());
                }

                NotificationsUtils.sendMuteNotification(getApplicationContext(),job,"mute");
                Log.i(TAG, "Success with mute");

            } catch (Throwable e) {
                Log.i(TAG,e.getMessage());
            }
            return null;
        }
    }
}


