package com.davjgardner.buttonjournal.eventdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {EventType.class, EventItem.class}, version = 1)
public abstract class EventDB extends RoomDatabase {

    private static final String DB_NAME = "eventdb";

    private static volatile EventDB INSTANCE;

    private static final int DB_THREADS = 4;
    static final ExecutorService databaseWriter = Executors.newFixedThreadPool(DB_THREADS);

    public abstract EventDao eventDao();

    public static EventDB getInstance(Context c) {
        if (INSTANCE == null) {
            synchronized (EventDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(c.getApplicationContext(), EventDB.class, DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
