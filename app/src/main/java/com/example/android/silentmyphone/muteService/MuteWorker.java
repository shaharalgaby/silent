package com.example.android.silentmyphone.muteService;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.utils.NotificationsUtils;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

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

        Log.i(TAG,"start the mute work");

        Context applicationContext = getApplicationContext();

//        JobsViewModel viewModel = new JobsViewModel((Application)applicationContext);
//        LiveData<MuteJob> muteWorker = viewModel.getJobById(getInputData().getLong("jobid",0));
//        job = muteWorker.getValue();

        try {
            AudioManager audioManager = (AudioManager) applicationContext.getSystemService(AUDIO_SERVICE);
            //maxVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            vibratePhone();

//            NotificationsUtils.sendMuteNotification(applicationContext,job);
            return Result.SUCCESS;
        } catch (Throwable e) {
            Log.i(TAG,e.getMessage());
            return  Result.FAILURE;
        }

    }


    void vibratePhone(){
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }
    }
}
