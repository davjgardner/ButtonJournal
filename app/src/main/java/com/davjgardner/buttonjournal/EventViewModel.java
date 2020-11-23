package com.davjgardner.buttonjournal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.davjgardner.buttonjournal.eventdb.EventItem;
import com.davjgardner.buttonjournal.eventdb.EventRepository;
import com.davjgardner.buttonjournal.eventdb.EventType;

import java.util.List;

public class EventViewModel extends AndroidViewModel {

    private final EventRepository repo;
    private LiveData<List<EventItem>> events;
    private EventType eventType;

    public EventViewModel(@NonNull Application application) {
        super(application);
        repo = new EventRepository(application.getApplicationContext());
    }

    public void setEventType(EventType t) {
        this.eventType = t;
    }

    LiveData<List<EventItem>> getEvents() {
        if (events == null && eventType != null) {
            events = repo.getEventsOf(eventType);
        }
        return events;
    }

    public void createEvent(long timestamp) {
        repo.createEvent(new EventItem(eventType, timestamp));
    }

    public void deleteEvent(EventItem e) {
        repo.deleteEvent(e);
    }

    public void renameType(EventType t, String to) { repo.renameType(t, to); }

}
