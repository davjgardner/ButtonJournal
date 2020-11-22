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

    private EventType type;
    private EventRepository repo;
    private LiveData<List<EventItem>> events;

    public EventViewModel(@NonNull Application application, EventType type) {
        super(application);
        this.repo = new EventRepository(application);
        this.type = type;
        this.events = repo.getEventsOf(type);
    }

    LiveData<List<EventItem>> getEvents() { return events; }

    public void createEvent() { repo.createEventOf(type); }

    public void deleteEvent(EventItem e) { repo.deleteEvent(e); }

}
