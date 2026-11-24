package com.example.eventmanager.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(
        tableName = "sessions",
        foreignKeys = @ForeignKey(
                entity = EventEntity.class,
                parentColumns = "id",
                childColumns = "eventId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("eventId")}
)
public class SessionEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Long eventId;
    private String name;
    private String time;
    private String speaker;
    private String room;
    private String type;
    private String description;
    private String firebaseKey;

    public SessionEntity() {
    }

    public SessionEntity(Long eventId, String name, String time, String speaker, String room, String type, String description) {
        this.eventId = eventId;
        this.name = name;
        this.time = time;
        this.speaker = speaker;
        this.room = room;
        this.type = type;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }
}
