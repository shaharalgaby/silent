package com.example.android.silentmyphone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.android.silentmyphone.utils.ServiceUtils;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SyncService extends JobService {

    private final static String TAG = SyncService.class.getSimpleName();
    // The root URI of the media provider, to monitor for generic changes to its content.
    static final Uri CALENDAR_URI = CalendarContract.Calendars.CONTENT_URI;

    // A pre-built JobInfo we use for scheduling our job.
    static final JobInfo JOB_INFO;

    public static final int JOB_ID = 1234;
    public static final int NOTIFY_ID = 4321;

    Context context;

    static {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,
                new ComponentName("com.example.android.silentmyphone", SyncService.class.getName()));
        // Also look for general reports of changes in the overall provider.
        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(CALENDAR_URI, 0));
        JOB_INFO = builder.build();
    }

    // Fake job work.  A real implementation would do some work on a separate thread.
    final Handler mHandler = new Handler();
    final Runnable mWorker = new Runnable() {
        @Override public void run() {
            scheduleJob(SyncService.this);
            jobFinished(mRunningParams, false);
        }
    };

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "JOB STARTED!");
        mRunningParams = params;
        context = getApplicationContext();

        ServiceUtils serviceUtils = new ServiceUtils();
        serviceUtils.updateContentChanges(getApplicationContext());

        // We will emulate taking some time to do this work, so we can see batching happen.
        mHandler.postDelayed(mWorker, 10*1000);
        return true;
    }

    JobParameters mRunningParams;

    // Schedule this job, replace any existing one.
    public static void scheduleJob(Context context) {
        JobScheduler js = context.getSystemService(JobScheduler.class);
        js.schedule(JOB_INFO);
        Log.i(TAG, "JOB SCHEDULED!");
    }

    // Check whether this job is currently scheduled.
    public static boolean isScheduled(Context context) {
        JobScheduler js = context.getSystemService(JobScheduler.class);
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs == null) {
            return false;
        }
        for (int i=0; i<jobs.size(); i++) {
            if (jobs.get(i).getId() == JOB_ID) {
                return true;
            }
        }
        return false;
    }

    // Cancel this job, if currently scheduled.
    public static void cancelJob(Context context) {
        JobScheduler js = context.getSystemService(JobScheduler.class);
        js.cancel(JOB_ID);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacks(mWorker);
        return false;
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this,context.getString(R.string.channel_name));

        b.setOngoing(true)
                .setContentTitle("TITLE")
                .setContentText("TEXT")
                .setSmallIcon(android.R.drawable.star_off)
                .setTicker("STICKER");

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = b.build();
        } else {
            notification = b.getNotification();
        }

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        return(notification);
    }
}
