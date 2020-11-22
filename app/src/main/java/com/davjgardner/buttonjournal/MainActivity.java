package com.davjgardner.buttonjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davjgardner.buttonjournal.eventdb.EventType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final int NEW_TYPE_ACTIVITY_REQUEST_CODE = 1;

    private EventTypeViewModel viewModel;

    EventTypeListAdapter etAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView eventTypesView = findViewById(R.id.eventListView);
        etAdapter = new EventTypeListAdapter(new EventTypeDiff());
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem deleteItem = menu.findItem(R.id.menu_delete_types);
        deleteItem.setTitle(deleteMode? R.string.done : R.string.delete_event_types);
        return true;
    }

    boolean deleteMode = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_types) {
            deleteMode = !deleteMode;
            if (deleteMode) {
                item.setTitle(R.string.done);
            } else {
                item.setTitle(R.string.delete_event_types);
            }
            etAdapter.notifyDataSetChanged();
            Log.d(TAG, "Delete mode is " + (deleteMode? "on" : "off"));
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
            if (deleteMode) {
                viewEventsButton.setText(R.string.delete);
            } else {
                viewEventsButton.setText(R.string.view_timeline_button_text);
            }
            viewEventsButton.setOnClickListener(l -> {
                if (deleteMode) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.confirm_delete_type_title, t.name))
                            .setMessage(R.string.confirm_delete_type_text)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes,
                                    (dialog, button) -> viewModel.deleteTypeAndEvents(t))
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                } else {
                    Intent intent = new Intent(l.getContext(), ViewEventActivity.class);
                    intent.putExtra(ViewEventActivity.EVENT_TYPE_ID, t.id);
                    intent.putExtra(ViewEventActivity.EVENT_TYPE_NAME, t.name);
                    l.getContext().startActivity(intent);
                }
            });
        }
    }

    EventTypeViewHolder createEventTypeViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_button_view, parent, false);
        return new EventTypeViewHolder(view);
    }
}