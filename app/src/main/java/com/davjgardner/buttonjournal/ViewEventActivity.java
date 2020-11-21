package com.davjgardner.buttonjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.davjgardner.buttonjournal.eventdb.EventType;

public class ViewEventActivity extends AppCompatActivity {

    public static final String EVENT_TYPE = "com.davjgardner.buttonjournal.EVENT_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        EventType eventType = new EventType(getIntent().getStringExtra(EVENT_TYPE));
    }
}