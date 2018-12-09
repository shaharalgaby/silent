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

public class MuteWorker extends Worker {

    private final static String TAG = MuteWorker.class.getSimpleName();
    private MuteJob job;

    public MuteWorker(@NonNull Context context,
                      @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Override this method to do your actual background processing.
     */
    @NonNull
    @Override
    public Result doWork() {

        Log.i(TAG,"start the mute work at " + CalendarUtils.getPrettyHour(System.currentTimeMillis()));

        Context applicationContext = getApplicationContext();

        long jobId = getInputData().getLong(MuteJobsModel.JOBID,0);
        JobsViewModel viewModel = new JobsViewModel((Application)applicationContext);
        job = viewModel.getJobById(jobId);

        Log.i(TAG,"The job id is: " + job.getId() +", repeat mode = " + job.getRepeatMode() +
        ", should prepare for repeat = " +  job.getIsFirstTimeEnd() + job.getIsFirstTimeStart());


        try {
            if(MuteJobsModel.shouldWorkToday(job,"mute")) {
                Log.i(TAG,"Mute should work today");
                AudioManager audioManager = (AudioManager) applicationContext.getSystemService(AUDIO_SERVICE);
                if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    Log.i(TAG,"Silenting phone");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    NotificationsUtils.vibratePhone(getApplicationContext());
                } else {
                    Log.i(TAG,"Phone is already silent.");
                }
            }

            //Prepare next Jobs if needed
            if(job.getRepeatMode() == MuteJob.MODE_REPEAT && job.getIsFirstTimeStart() == 0) {
                Log.i(TAG,"Preparing next mutes");
                job.setIsFirstTimeStart(1);
                viewModel.update(job);
                MuteJobsModel.prepareDailyJobs(MuteJobsModel.WORK_MUTE, job);
            } else {
                Log.i(TAG,"Repeated request is already on.");
            }

            NotificationsUtils.sendMuteNotification(getApplicationContext(),job,"mute");
            Log.i(TAG, "Success with mute");
            return Result.SUCCESS;

        } catch (Throwable e) {
            Log.i(TAG,e.getMessage());
            return  Result.FAILURE;
        }
    }
}
