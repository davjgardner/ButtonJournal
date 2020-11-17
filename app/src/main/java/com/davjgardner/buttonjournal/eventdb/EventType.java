package com.davjgardner.buttonjournal.eventdb;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EventType {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    String name;

    public EventType(@NonNull String name) {
        this.name = name;
    }
}
