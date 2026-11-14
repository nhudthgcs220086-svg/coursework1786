package com.example.basiceventmanager.data;

import com.example.basiceventmanager.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static EventRepository instance;
    private final List<Event> eventList;

    private EventRepository() {
        eventList = new ArrayList<>();
        // Add sample initial events with DD/MM/YYYY format
        eventList.add(new Event("Android Dev Summit", "15/06/2025", "San Francisco, CA", "Google", "Conference"));
        eventList.add(new Event("Kotlin Coroutines Workshop", "20/07/2025", "Online", "JetBrains", "Workshop"));
        eventList.add(new Event("AI in Mobile Apps Seminar", "10/08/2025", "New York, NY", "Tech Academy", "Seminar"));
    }

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    public List<Event> getEvents() {
        return eventList;
    }

    public void addEvent(Event event) {
        eventList.add(0, event);
    }
}
