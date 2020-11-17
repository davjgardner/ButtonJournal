package com.davjgardner.buttonjournal.eventdb;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EventType {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    public EventType(@NonNull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != EventType.class) return false;
        EventType t = (EventType) o;
        return t.id == id;
    }
}
