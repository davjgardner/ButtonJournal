package com.davjgardner.buttonjournal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.davjgardner.buttonjournal.eventdb.EventRepository;
import com.davjgardner.buttonjournal.eventdb.EventType;

import java.util.List;

public class EventTypeViewModel extends AndroidViewModel {

    private final EventRepository repo;
    private final LiveData<List<EventType>> types;

    public EventTypeViewModel(@NonNull Application application) {
        super(application);
        repo = new EventRepository(application);
        types = repo.getEventTypes();
    }

    LiveData<List<EventType>> getTypes() { return types; }

    public void createType(String name) { repo.createEventType(name); }

    public void createEventOf(EventType t) { repo.createEventOf(t); }
}
