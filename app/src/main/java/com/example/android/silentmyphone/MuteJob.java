package com.example.android.silentmyphone;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


@Entity(tableName = MuteJob.ROOM_TABLE_NAME)
public class MuteJob implements Parcelable{

    public final static int REPEAT_MODE_ONE_TIME = 0;
    public final static int REPEAT_MODE_DAILY = 1;
    public final static int REPEAT_MODE_WEEKLY = 2;
    public final static String ROOM_TABLE_NAME = "job";


    @PrimaryKey
    @NonNull
    private long startTime;
    private long endTime;
    private int repeatMode;
    private boolean[] repeatDays;

    public MuteJob(){}

    public MuteJob(long start, long end,int repeartMode, boolean[] repeatDays) {
        this.startTime = start;
        this.endTime = end;
        this.repeatMode = repeartMode;
    }

    protected MuteJob(Parcel in) {
        startTime = in.readLong();
        endTime = in.readLong();
        repeatMode = in.readInt();
    }

    public static final Creator<MuteJob> CREATOR = new Creator<MuteJob>() {
        @Override
        public MuteJob createFromParcel(Parcel in) {
            return new MuteJob(in);
        }

        @Override
        public MuteJob[] newArray(int size) {
            return new MuteJob[size];
        }
    };

    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
        parcel.writeInt(repeatMode);
        parcel.writeBooleanArray(repeatDays);
    }
}
