package com.davjgardner.buttonjournal.eventdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM eventtype")
    List<EventType> getEventTypes();

    @Query("SELECT * FROM eventitem WHERE type=:type")
    List<EventItem> getEvents(int type);

    @Query("SELECT * FROM eventitem")
    List<EventItem> getAllEvents();

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
