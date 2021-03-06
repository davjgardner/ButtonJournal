package com.davjgardner.buttonjournal.eventdb;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EventRepository {

    private EventDao eventDao;
    private LiveData<List<EventItem>> events;
    private LiveData<List<EventType>> eventTypes;

    public EventRepository(Context app) {
        EventDB db = EventDB.getInstance(app);
        eventDao = db.eventDao();
        events = eventDao.getAllEvents();
        eventTypes = eventDao.getEventTypes();
    }

    public LiveData<List<EventItem>> getEventsOf(EventType t) {
        return eventDao.getEvents(t.id);
    }

    public LiveData<List<EventItem>> getAllEvents() {
        return events;
    }

    public LiveData<List<EventType>> getEventTypes() {
        return eventTypes;
    }

    public void createEventOf(EventType t) {
        EventItem e = new EventItem(t);
        EventDB.databaseWriter.execute(() -> eventDao.createEvent(e));
    }

    public void createEvent(EventItem e) {
        EventDB.databaseWriter.execute(() -> eventDao.createEvent(e));
    }

    public void createEventType(String name) {
        EventDB.databaseWriter.execute(() -> eventDao.createEventType(new EventType(name)));
    }

    public void deleteEvent(EventItem e) {
        EventDB.databaseWriter.execute(() -> eventDao.deleteEvent(e));
    }

    public void deleteTypeAndEvents(EventType t) {
        EventDB.databaseWriter.execute(() -> {
            eventDao.deleteEventsOf(t.id);
            eventDao.deleteType(t);
        });
    }

    public void renameType(EventType t, String to) {
        EventDB.databaseWriter.execute(() -> eventDao.renameType(t.id, to));
    }
}
