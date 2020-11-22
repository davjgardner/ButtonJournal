package com.davjgardner.buttonjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davjgardner.buttonjournal.eventdb.EventRepository;
import com.davjgardner.buttonjournal.eventdb.EventType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_TYPE_ACTIVITY_REQUEST_CODE = 1;

    private EventTypeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView eventTypesView = findViewById(R.id.eventListView);
        final EventTypeListAdapter etAdapter = new EventTypeListAdapter(new EventTypeDiff());
        eventTypesView.setAdapter(etAdapter);
        eventTypesView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(EventTypeViewModel.class);
        viewModel.getTypes().observe(this, etAdapter::submitList);

        FloatingActionButton addButton = findViewById(R.id.addEventButton);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEventTypeActivity.class);
            startActivityForResult(intent, NEW_TYPE_ACTIVITY_REQUEST_CODE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    boolean deleteMode = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_types) {
            deleteMode = !deleteMode;
            if (deleteMode) {
                item.setTitle(R.string.delete_event_types);
            } else {
                item.setTitle(R.string.done);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TYPE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.createType(data.getStringExtra(AddEventTypeActivity.EXTRA_REPLY));
        }
    }

    private class EventTypeListAdapter extends ListAdapter<EventType, EventTypeViewHolder> {
        protected EventTypeListAdapter(@NonNull DiffUtil.ItemCallback<EventType> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return createEventTypeViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
            EventType currentItem = getItem(position);
            holder.bind(currentItem);
        }
    }

    private static class EventTypeDiff extends DiffUtil.ItemCallback<EventType> {

        @Override
        public boolean areItemsTheSame(@NonNull EventType oldItem, @NonNull EventType newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventType oldItem, @NonNull EventType newItem) {
            return oldItem.equals(newItem);
        }
    }

    private class EventTypeViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "EventTypeViewHolder";

        View itemRoot;

        private EventTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemRoot = itemView;
        }

        public void bind(EventType t) {
            Button triggerButton = itemRoot.findViewById(R.id.eventTriggeredButton);
            triggerButton.setText(t.name);
            triggerButton.setOnClickListener(l -> {
                Log.d(TAG, "Creating event of type " + t.name);
                viewModel.createEventOf(t);
            });
            Button viewEventsButton = itemRoot.findViewById(R.id.viewTimelineButton);
            viewEventsButton.setOnClickListener(l -> {
                Intent intent = new Intent(l.getContext(), ViewEventActivity.class);
                intent.putExtra(ViewEventActivity.EVENT_TYPE_ID, t.id);
                intent.putExtra(ViewEventActivity.EVENT_TYPE_NAME, t.name);
                l.getContext().startActivity(intent);
            });
        }
    }

    EventTypeViewHolder createEventTypeViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_button_view, parent, false);
        return new EventTypeViewHolder(view);
    }
}