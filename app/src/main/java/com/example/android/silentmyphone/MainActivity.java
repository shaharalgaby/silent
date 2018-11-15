package com.example.android.silentmyphone;

import android.Manifest;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;
import com.example.android.silentmyphone.adapters.JobsAdapter;
import com.example.android.silentmyphone.db.JobsViewModel;
import com.example.android.silentmyphone.fragments.NewJobDialog;
import com.example.android.silentmyphone.muteService.MuteJobsModel;
import com.example.android.silentmyphone.utils.NotificationsUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JobsAdapter.OnJobClickListener, NewJobDialog.OnSetJobBtnClicked {

    public final static String TAG = MainActivity.class.getSimpleName();
    public static final int PERMISSION_CALENDAR_READ = 221;

    private RecyclerView mRecyclerView;
    private JobsAdapter mAdapter;
    private JobsViewModel mViewModel;
    private FloatingActionButton mAddBtn;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddBtn = findViewById(R.id.floating_btn);

        initViewModel();

        mRecyclerView = findViewById(R.id.jobs_recyclerview);
        mAdapter = new JobsAdapter(this, new ArrayList<>(), this);
        initializeRecyclerView();

        fragmentManager = getFragmentManager();

        checkForPermissions();
        NotificationsUtils.createNotificationChannel(this);
    }

    void initializeRecyclerView(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        mRecyclerView.setItemAnimator(itemAnimator);
        mRecyclerView.setAdapter(mAdapter);

        if(mAdapter.getItemCount() > 0){
            findViewById(R.id.no_jobs_message).setVisibility(View.GONE);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new
            ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            ArrayList<MuteJob> list = mAdapter.getmJobsList();
            MuteJob job = list.get(viewHolder.getAdapterPosition());
            mViewModel.delete(job);
        }
    };

    void initViewModel(){
        mViewModel = ViewModelProviders.of(this).get(JobsViewModel.class);
        mViewModel.getAllJobs().observe(this, jobs -> mAdapter.setJobs(jobs));
    }

    void checkForPermissions(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    @Override
    public void onJobClick() {

    }

    public void addJobs(View view) {
        showAddJobDialog();
    }

    void showAddJobDialog(){
        NewJobDialog newFragment = new NewJobDialog(this);
        newFragment.show(fragmentManager, "datePicker");
    }

    @Override
    public void onSetJobBtnClicked(TimePicker start, TimePicker end, boolean isChecked,
                                   CheckBox[] checkBoxes, NewJobDialog fragment) {

        MuteJobsModel.addMuteJob(start,end, isChecked, checkBoxes, mViewModel);
        fragment.dismiss();
    }

    public void syncWithCalendar(){
        Intent mServiceIntent = new Intent();

        SyncWithCalendarService.enqueueWork(
                this,
                SyncWithCalendarService.class,
                200,
                mServiceIntent);
    }

    public void log(String s) {
        Log.i(TAG,s);
    }
}
