package com.davjgardner.buttonjournal.eventdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EventItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int type;

    public long timestamp;

    public EventItem(int type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public EventItem(EventType type) {
        this.type = type.id;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != EventItem.class) return false;
        EventItem e = (EventItem) o;
        return e.id == id;
    }
}
