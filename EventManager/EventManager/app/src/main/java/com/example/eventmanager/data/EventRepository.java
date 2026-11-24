package com.example.eventmanager.data;

import com.example.eventmanager.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static EventRepository instance;
    private final List<Event> eventList;

    private EventRepository() {
        eventList = new ArrayList<>();
        // Add sample initial events
        eventList.add(new Event("Android Developer Conference", "25/11/2024", "Tech Center, Hanoi", "Google Developers", "Conference"));
        eventList.add(new Event("Jetpack Compose Workshop", "10/12/2024", "Innovation Hub, HCMC", "Droid Vietnam", "Workshop"));
        eventList.add(new Event("AI in Mobile Apps Seminar", "15/01/2025", "University of Science", "AI Research Group", "Seminar"));
        eventList.add(new Event("Developer Community Social", "20/01/2025", "Sky Lounge, Da Nang", "DevCommunity", "Social"));
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
        eventList.add(0, event); // Add to the top of the list
    }
}
