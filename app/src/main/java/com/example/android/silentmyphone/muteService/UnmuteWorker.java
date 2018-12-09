package com.example.android.silentmyphone.muteService;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.utils.CalendarUtils;
import com.example.android.silentmyphone.utils.NotificationsUtils;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import static android.content.Context.AUDIO_SERVICE;

public class UnmuteWorker extends Worker {

    private static final String TAG = UnmuteWorker.class.getSimpleName();
    private MuteJob job;

    public UnmuteWorker(@NonNull Context context,
                        @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Override this method to do your actual background processing.
     */
    @NonNull
    @Override
    public Result doWork() {

        Log.i(TAG,"start the unmute work at " + CalendarUtils.getPrettyHour(System.currentTimeMillis()));

        Context applicationContext = getApplicationContext();

        long jobid = getInputData().getLong(MuteJobsModel.JOBID,0);
        JobsViewModel viewModel = new JobsViewModel((Application)applicationContext);
        job = viewModel.getJobById(jobid);

        try {

            if(MuteJobsModel.shouldWorkToday(job,MuteJobsModel.WORK_UNMUTE)) {
                Log.i(TAG,"Unmute should work today");
                AudioManager audioManager = (AudioManager) applicationContext.getSystemService(AUDIO_SERVICE);
                if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    NotificationsUtils.vibratePhone(getApplicationContext());
                } else {
                    Log.i(TAG,"Phone is already unmuted");
                }
            }

            //Prepare next Jobs if needed
            if(job.getRepeatMode() == MuteJob.MODE_REPEAT && job.getIsFirstTimeEnd() == 0) {
                Log.i(TAG,"Preparing next unmutes");
                job.setIsFirstTimeEnd(1);
                viewModel.update(job);
                MuteJobsModel.prepareDailyJobs(MuteJobsModel.WORK_MUTE, job);
            }

            NotificationsUtils.sendMuteNotification(getApplicationContext(),job,"unmute");
            Log.i(TAG, "Success with unmute");
            return Result.SUCCESS;

        } catch (Throwable throwable) {
            Log.i(TAG,throwable.getMessage());
            return Result.FAILURE;
        }
    }
}
