package com.example.android.silentmyphone.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.android.silentmyphone.MuteJob;

import java.util.List;

public class JobsRepository {

    private JobsDao mJobsDao;
    private LiveData<List<MuteJob>> mAllJobs;

    JobsRepository(Application application){
        MuteJobDatabase db = MuteJobDatabase.getDatabase(application);
        mJobsDao = db.jobsDao();
        mAllJobs = mJobsDao.getAllJobs();
    }

    LiveData<List<MuteJob>> getmAllJobs(){
        return mAllJobs;
    }

    List<MuteJob> getJobs() {
        return mJobsDao.getJobs();
    }

    void insert (MuteJob myJob){
        new insertAsyncTask(mJobsDao).execute(myJob);
    }

    public void delete (MuteJob myJob){
        new deleteAsyncTask(mJobsDao).execute(myJob);
    }

    void update(MuteJob myJob) {
        new updateAsyncTask(mJobsDao).execute(myJob);
    }

    MuteJob getJobById(String id){
        return mJobsDao.getJobById(id);
    }

    //AsyncTask for inserting the new object
    private static class insertAsyncTask extends AsyncTask<MuteJob, Void, Void> {

        private JobsDao mAsyncTaskDao;

        insertAsyncTask(JobsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MuteJob... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //AsyncTask for inserting delete video object
    private static class deleteAsyncTask extends AsyncTask<MuteJob, Void, Void> {

        private JobsDao mAsyncTaskDao;

        deleteAsyncTask(JobsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MuteJob... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    //AsyncTask for updating a object
    private static class updateAsyncTask extends AsyncTask<MuteJob, Void, Void> {

        private JobsDao mAsyncTaskDao;

        updateAsyncTask(JobsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MuteJob... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

}

