package com.example.android.silentmyphone.muteService;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.AUDIO_SERVICE;

public class UnmuteWorker extends Worker {

    private static final String TAG = UnmuteWorker.class.getSimpleName();

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

        Log.i(TAG,"start the unmute work");

        Context applicationContext = getApplicationContext();

        try {
            AudioManager audioManager = (AudioManager) applicationContext.getSystemService(AUDIO_SERVICE);

            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            //audioManager.setStreamVolume(AudioManager.STREAM_RING,maxVolume,AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_VIBRATE);
            return Result.SUCCESS;

        } catch (Throwable throwable) {
            Log.i(TAG,throwable.getMessage());
            return Result.FAILURE;
        }

    }


}
