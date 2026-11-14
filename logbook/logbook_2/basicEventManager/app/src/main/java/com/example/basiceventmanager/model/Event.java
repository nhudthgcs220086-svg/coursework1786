package com.example.basiceventmanager.model;

public class Event {
    private String name;
    private String date;
    private String location;
    private String organiser;
    private String type;

    public Event(String name, String date, String location, String organiser, String type) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.organiser = organiser;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
