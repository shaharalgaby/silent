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
import com.example.android.silentmyphone.utils.ClientConfig;
import com.example.android.silentmyphone.utils.NotificationsUtils;

public class UnmuteWorker extends Service {

    private static final String TAG = UnmuteWorker.class.getSimpleName();
    private MuteJob job;
    String jobid;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG,"Start the unmute work");

        jobid = intent.getStringExtra(MuteJobsModel.JOBID);

        MyAsyncTask task = new MyAsyncTask();
        task.execute();

        return super.onStartCommand(intent, flags, startId);
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
                job = viewModel.getJobById(jobid);
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
                if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    NotificationsUtils.vibratePhone(getApplicationContext());
                } else {
                    Log.i(TAG,"Phone is already unmuted");
                }

                NotificationsUtils.sendMuteNotification(getApplicationContext(),job,"unmute");
                Log.i(TAG, "Success with unmute");
                if(job.getRepeatMode() == MuteJob.MODE_ONE_TIME || job.isBusiness()) {
                    viewModel.delete(job);
                }

            } catch (Throwable throwable) {
                Log.i(TAG,throwable.getMessage());
            }

            return null;
        }
    }
}
