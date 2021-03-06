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

    @Query("SELECT * FROM eventitem WHERE type=:type ORDER BY timestamp DESC")
    LiveData<List<EventItem>> getEvents(int type);

    @Query("SELECT * FROM eventitem ORDER BY timestamp DESC")
    LiveData<List<EventItem>> getAllEvents();

    @Insert
    void createEvent(EventItem e);

    @Insert
    void createEventType(EventType t);

    @Query("UPDATE eventtype SET name=:to WHERE id=:id")
    void renameType(int id, String to);

    @Delete
    void deleteType(EventType t);

    @Delete
    void deleteEvent(EventItem e);

    @Query("DELETE FROM eventtype WHERE id=:id")
    void deleteType(int id);

    @Query("DELETE FROM eventitem WHERE id=:id")
    void deleteEvent(int id);

    @Query("DELETE FROM eventitem WHERE type=:type")
    void deleteEventsOf(int type);

    @Query("DELETE FROM eventitem")
    void deleteAllEvents();

    @Query("DELETE FROM eventtype")
    void deleteAllTypes();
}
