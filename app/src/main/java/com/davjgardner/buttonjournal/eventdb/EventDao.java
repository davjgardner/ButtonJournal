package com.davjgardner.buttonjournal.eventdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM eventtype")
    LiveData<List<EventType>> getEventTypes();

    @Query("SELECT * FROM eventitem WHERE type=:type")
    LiveData<List<EventItem>> getEvents(int type);

    @Query("SELECT * FROM eventitem")
    LiveData<List<EventItem>> getAllEvents();

    @Insert
    void createEvent(EventItem e);

    @Insert
    void createEventType(EventType t);

    @Delete
    void deleteType(EventType t);

    @Delete
    void deleteEvent(EventItem e);

    @Query("DELETE FROM eventtype WHERE id=:id")
    void deleteType(int id);

    @Query("DELETE FROM eventitem WHERE id=:id")
    void deleteEvent(int id);
}
