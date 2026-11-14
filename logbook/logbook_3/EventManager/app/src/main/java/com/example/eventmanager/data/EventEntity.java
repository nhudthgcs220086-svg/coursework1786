package com.example.eventmanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "events")
public class EventEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String date;
    private String location;
    private String organiser;
    private String type;
    private String status;

    public EventEntity(String name, String date, String location, String organiser, String type, String status) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.organiser = organiser;
        this.type = type;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
