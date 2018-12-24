package com.example.android.silentmyphone;

public class UserCalendar {

    long id;
    String displayName;
    String accountName;
    String calendarOwner;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCalendarOwner() {
        return calendarOwner;
    }

    public void setCalendarOwner(String calendarOwner) {
        this.calendarOwner = calendarOwner;
    }

    public UserCalendar(long id, String displayName, String accountName, String calendarOwner) {
        this.id = id;
        this.displayName = displayName;
        this.accountName = accountName;
        this.calendarOwner = calendarOwner;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
