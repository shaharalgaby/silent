package com.example.android.silentmyphone;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class EventChangeReceiver extends BroadcastReceiver {
    private final static String TAG = EventChangeReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent1 = new Intent(context.getApplicationContext(), SyncService.class);
            context.startForegroundService(intent1);
        }

        Log.i(TAG,"starteddd");
//        Intent intent0 = new Intent( context, ActivityRecognitionService.class );
//        PendingIntent pendingIntent = PendingIntent.getService(context, 111, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
//        ActivityRecognitionClient activityRecognitionClient = ActivityRecognition.getClient(context);
//        activityRecognitionClient.requestActivityUpdates(5000, pendingIntent);
    }
}
