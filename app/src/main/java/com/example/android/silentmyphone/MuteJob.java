package com.example.android.silentmyphone;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


@Entity(tableName = MuteJob.ROOM_TABLE_NAME)
public class MuteJob implements Parcelable {

    public final static int MODE_ONE_TIME = 0;
    public final static int MODE_REPEAT = 1;
    public final static String ROOM_TABLE_NAME = "job";


    @PrimaryKey
    @NonNull
    private long id;
    private long startTime;
    private long endTime;
    private int isFirstTimeStart;
    private int isFirstTimeEnd;
    private int repeatMode;
    private boolean[] repeatDays;

    public MuteJob(){}

    public MuteJob(long start, long end,int repeartMode, boolean[] repeatDays, int isFirstTimeStart,
                   int isFirstTimeEnd) {
        id = System.currentTimeMillis();
        this.startTime = start;
        this.endTime = end;
        this.repeatMode = repeartMode;
        this.repeatDays = repeatDays;
        this.isFirstTimeStart = isFirstTimeStart;
        this.isFirstTimeEnd = isFirstTimeEnd;
    }

    protected MuteJob(Parcel in) {
        id = in.readLong();
        startTime = in.readLong();
        endTime = in.readLong();
        isFirstTimeStart = in.readInt();
        isFirstTimeEnd = in.readInt();
        repeatMode = in.readInt();
        repeatDays = in.createBooleanArray();
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

    public int getIsFirstTimeStart() {
        return isFirstTimeStart;
    }

    public void setIsFirstTimeStart(int firstTimeStart) {
        isFirstTimeStart = firstTimeStart;
    }

    public int getIsFirstTimeEnd() {
        return isFirstTimeEnd;
    }

    public void setIsFirstTimeEnd(int firstTimeEnd) {
        isFirstTimeEnd = firstTimeEnd;
    }

    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setId(long id){
        this.id = id;
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

    public long getId(){return id;}

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
        parcel.writeLong(id);
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
        parcel.writeInt(isFirstTimeStart);
        parcel.writeInt(isFirstTimeEnd);
        parcel.writeInt(repeatMode);
        parcel.writeBooleanArray(repeatDays);
    }
}
