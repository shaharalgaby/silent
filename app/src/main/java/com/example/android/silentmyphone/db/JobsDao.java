package com.example.android.silentmyphone.db;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.silentmyphone.MuteJob;

import java.util.List;

@Dao
public interface JobsDao {

    @Insert
    void insert(MuteJob muteJob);

    @Delete
    void delete(MuteJob myVideo);

    @Update
    void update(MuteJob myVideo);

    @Query("SELECT * from "+MuteJob.ROOM_TABLE_NAME+" ORDER BY startTime DESC")
    LiveData<List<MuteJob>> getAllJobs();

    @Query("SELECT * from "+MuteJob.ROOM_TABLE_NAME+" WHERE startTime =:id")
    LiveData<MuteJob>  getJobById(long id);
}
