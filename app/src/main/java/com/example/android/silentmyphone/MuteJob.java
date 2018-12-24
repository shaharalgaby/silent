package com.example.android.silentmyphone;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;

import com.example.android.silentmyphone.utils.CalendarUtils;


@Entity(tableName = MuteJob.ROOM_TABLE_NAME)
public class MuteJob implements Parcelable {

    public final static int MODE_ONE_TIME = 0;
    public final static int MODE_REPEAT = 1;
    public final static String ROOM_TABLE_NAME = "job";
    public final static int ID__NON_BUSINESS = -1;


    @PrimaryKey
    @NonNull
    private String id;
    private long startTime;
    private long endTime;
    private int isFirstTimeStart;
    private int isFirstTimeEnd;
    private int repeatMode;
    private boolean[] repeatDays;
    private String eventLocation;
    private String eventTitle;
    private boolean isBusiness;

    public MuteJob(){}

    public MuteJob(String id, long start, long end,int repeartMode, boolean[] repeatDays) {
        if(id == null) {
            this.id = System.currentTimeMillis()+"";
            isBusiness = false;
        } else {
            this.id = id;
            isBusiness = true;
        }
        this.startTime = start;
        this.endTime = end;
        this.repeatMode = repeartMode;
        this.repeatDays = repeatDays;
        this.isFirstTimeStart = 0;
        this.isFirstTimeEnd = 0;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean business) {
        isBusiness = business;
    }

    protected MuteJob(Parcel in) {
        id = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        isFirstTimeStart = in.readInt();
        isFirstTimeEnd = in.readInt();
        repeatMode = in.readInt();
        repeatDays = in.createBooleanArray();
        eventLocation = in.readString();
        eventTitle = in.readString();
        isBusiness = in.readInt() == 0 ? false : true;
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

    public void setEventLocation(String eventLocation){
        this.eventLocation = eventLocation;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventTitle(String title) {
        this.eventTitle = title;
    }

    public String getEventTitle () {
        return eventTitle;
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

    public void setId(String id){
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

    public String getId(){return id;}

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
        parcel.writeString(id);
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
        parcel.writeInt(isFirstTimeStart);
        parcel.writeInt(isFirstTimeEnd);
        parcel.writeInt(repeatMode);
        parcel.writeBooleanArray(repeatDays);
        parcel.writeString(eventLocation);
        parcel.writeString(eventTitle);
        parcel.writeInt(isBusiness ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if(startTime != ((MuteJob)obj).startTime ||
                endTime != ((MuteJob)obj).endTime ||
                repeatDays.equals(((MuteJob)obj).repeatDays)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return CalendarUtils.getPrettyHourAndDate(startTime) + " - " +
                CalendarUtils.getPrettyHourAndDate(endTime) + ", " +
                (repeatMode == MODE_ONE_TIME ? "one time" : "repeat" ) +", " +
                (repeatDays == null ? "" : repeatDays[0]+", " + repeatDays[1]+", " + repeatDays[2]+", " +
                        repeatDays[3]+", " + repeatDays[4]+", " + repeatDays[5]+", " + repeatDays[6]);
    }
}
