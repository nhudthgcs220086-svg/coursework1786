package com.example.eventmanager.data;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class ContactRequestEntity implements Serializable {
    private String Name;
    private String Email;
    private String Phone;
    private String Message;
    private String EventName;
    private String EventId;
    private String CreatedAtUtc;

    public ContactRequestEntity() {
    }

    @PropertyName("Name")
    public String getName() { return Name; }
    @PropertyName("Name")
    public void setName(String name) { Name = name; }

    @PropertyName("Email")
    public String getEmail() { return Email; }
    @PropertyName("Email")
    public void setEmail(String email) { Email = email; }

    @PropertyName("Phone")
    public String getPhone() { return Phone; }
    @PropertyName("Phone")
    public void setPhone(String phone) { Phone = phone; }

    @PropertyName("Message")
    public String getMessage() { return Message; }
    @PropertyName("Message")
    public void setMessage(String message) { Message = message; }

    @PropertyName("EventName")
    public String getEventName() { return EventName; }
    @PropertyName("EventName")
    public void setEventName(String eventName) { EventName = eventName; }

    @PropertyName("EventId")
    public String getEventId() { return EventId; }
    @PropertyName("EventId")
    public void setEventId(Object eventId) {
        if (eventId != null) {
            this.EventId = String.valueOf(eventId);
        }
    }

    @PropertyName("CreatedAtUtc")
    public String getCreatedAtUtc() { return CreatedAtUtc; }
    @PropertyName("CreatedAtUtc")
    public void setCreatedAtUtc(String createdAtUtc) { CreatedAtUtc = createdAtUtc; }
}
