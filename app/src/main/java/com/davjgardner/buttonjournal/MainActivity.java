package com.davjgardner.buttonjournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.davjgardner.buttonjournal.eventdb.EventType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_TYPE_ACTIVITY_REQUEST_CODE = 1;

    private TypeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView eventTypesView = findViewById(R.id.eventListView);
        final EventTypeListAdapter etAdapter = new EventTypeListAdapter(new EventTypeListAdapter.EventTypeDiff());
        eventTypesView.setAdapter(etAdapter);
        eventTypesView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(TypeViewModel.class);
        viewModel.getTypes().observe(this, etAdapter::submitList);

        FloatingActionButton addButton = findViewById(R.id.addEventButton);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEventTypeActivity.class);
            startActivityForResult(intent, NEW_TYPE_ACTIVITY_REQUEST_CODE);
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TYPE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.createType(data.getStringExtra(AddEventTypeActivity.EXTRA_REPLY));
        }
    }
}