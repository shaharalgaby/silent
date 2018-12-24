package com.example.android.silentmyphone;

public class UserEvent {

    String startDate;
    String endDate;
    String id;
    String title;
    String location;
    String calendarId;

    public UserEvent(String startDate, String endDate, String id, String title, String location, String calendarId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
        this.title = title;
        this.location = location;
        this.calendarId = calendarId;
    }


    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
