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
}