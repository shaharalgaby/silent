package com.example.android.silentmyphone.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.silentmyphone.MuteJob;

@Database(entities = {MuteJob.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class MuteJobDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "muteJobs";

    public abstract JobsDao jobsDao();

    private static volatile MuteJobDatabase INSTANCE;

    static MuteJobDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (MuteJobDatabase.class) {
                if (INSTANCE == null){
                    //create the database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MuteJobDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

