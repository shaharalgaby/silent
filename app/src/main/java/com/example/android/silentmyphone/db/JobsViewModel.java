package com.example.android.silentmyphone.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.silentmyphone.MuteJob;

import java.util.List;

public class JobsViewModel extends AndroidViewModel {

    private JobsRepository mRepository;

    private LiveData<List<MuteJob>> mAllJobs;

    public JobsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new JobsRepository(application);
        mAllJobs = mRepository.getmAllJobs();
    }

    public LiveData<List<MuteJob>> getAllJobs(){
        return mAllJobs;
    }

    public MuteJob getJobById(String id){
        return mRepository.getJobById(id);
    }

    public void insert(MuteJob job){
        mRepository.insert(job);
    }

    public void delete(MuteJob job){
        mRepository.delete(job);
    }

    public void update(MuteJob job) {
        mRepository.update(job);
    }

    public List<MuteJob> getJobs(){return mRepository.getJobs();}

}

