package com.davjgardner.buttonjournal.eventdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
                    INSTANCE = Room.databaseBuilder(c.getApplicationContext(), EventDB.class, DB_NAME).addCallback(rdCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    // FIXME DEBUG ONLY
    private static RoomDatabase.Callback rdCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriter.execute(() -> {
                EventDao dao = INSTANCE.eventDao();
                dao.deleteAllTypes();

                EventType e = new EventType("Event 1");
                dao.createEventType(e);
                e = new EventType("Event 2");
                dao.createEventType(e);
            });
        }
    };
}
