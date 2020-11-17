package com.davjgardner.buttonjournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView eventTypesView = findViewById(R.id.eventListView);
        final EventTypeListAdapter etAdapter = new EventTypeListAdapter(new EventTypeListAdapter.EventTypeDiff());
        eventTypesView.setAdapter(etAdapter);
        eventTypesView.setLayoutManager(new LinearLayoutManager(this));
    }
}