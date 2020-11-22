package com.davjgardner.buttonjournal;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.davjgardner.buttonjournal.eventdb.EventItem;
import com.davjgardner.buttonjournal.eventdb.EventType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewEventActivity extends AppCompatActivity {

    public static final String TAG = "ViewEventActivity";

    public static final String EVENT_TYPE_ID = "com.davjgardner.buttonjournal.EVENT_TYPE_ID";
    public static final String EVENT_TYPE_NAME = "com.davjgardner.buttonjournal.EVENT_TYPE_NAME";
    public static final int NEW_EVENT_ACTIVITY_REQUEST_CODE = 1;

    private static final long DAYS_IN_MONTH = 30;
    private static final long DAYS_IN_YEAR = 365;

    LiveData<List<EventItem>> events;
    private EventType eventType;
    private EventViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        eventType = new EventType(getIntent().getStringExtra(EVENT_TYPE_NAME));
        eventType.id = getIntent().getIntExtra(EVENT_TYPE_ID, 0);

        RecyclerView eventsView = findViewById(R.id.event_list);
        final EventListAdapter listAdapter = new EventListAdapter(new EventDiff());
        eventsView.setAdapter(listAdapter);
        eventsView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        viewModel.setEventType(eventType);
        events = viewModel.getEvents();
        viewModel.getEvents().observe(this, listAdapter::submitList);

        TextView title = findViewById(R.id.event_view_title);
        title.setText(eventType.name);

        FloatingActionButton addButton = findViewById(R.id.event_view_add_event);
        addButton.setOnClickListener(l -> {
            Intent intent = new Intent(ViewEventActivity.this, AddEventActivity.class);
            intent.putExtra(EVENT_TYPE_NAME, eventType.name);
            startActivityForResult(intent, NEW_EVENT_ACTIVITY_REQUEST_CODE);
        });

        events.observe(this, events -> {
            long now = System.currentTimeMillis();
            Calendar since = Calendar.getInstance();
            since.set(Calendar.HOUR, 0);
            since.set(Calendar.MINUTE, 0);
            since.set(Calendar.SECOND, 0);
            since.set(Calendar.MILLISECOND, 0);

            TextView dayCount = findViewById(R.id.tv_day_count);
            dayCount.setText(lts(countSince(since.getTimeInMillis())));

            since.set(Calendar.DAY_OF_MONTH, 1);

            TextView monthCount = findViewById(R.id.tv_month_count);
            monthCount.setText(lts(countSince(since.getTimeInMillis())));

            since.set(Calendar.DAY_OF_YEAR, 1);
            TextView yearCount = findViewById(R.id.tv_year_count);
            yearCount.setText(lts(countSince(since.getTimeInMillis())));

            since = Calendar.getInstance();
            since.set(Calendar.HOUR, 0);
            since.set(Calendar.MINUTE, 0);
            since.set(Calendar.SECOND, 0);
            since.set(Calendar.MILLISECOND, 0);
            since.set(Calendar.DAY_OF_WEEK, 1);
            TextView weekCount = findViewById(R.id.tv_week_count);
            weekCount.setText(lts(countSince(now - DateUtils.WEEK_IN_MILLIS)));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_EVENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            long t = data.getLongExtra(AddEventActivity.REPLY_TIMESTAMP, 0);
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(t);
            Log.d(TAG, "Create new event at " + date);
            viewModel.createEvent(t);
        }
    }

    private List<EventItem> getEvents() {
        List<EventItem> events = this.events.getValue();
        return (events == null)? new ArrayList<>() : events;
    }

    private String lts(long l) {
        return String.format(getResources().getConfiguration().getLocales().get(0), "%d", l);
    }

    private long countSince(long time) {
        return countBetween(time, System.currentTimeMillis());
    }

    private long countBetween(long start, long end) {
        return getEvents().stream().filter(e -> e.timestamp > start && e.timestamp < end).count();
    }

    private EventItem getEarliest() {
        return getEvents().stream().min((e1, e2) -> Long.compare(e1.timestamp, e2.timestamp)).orElse(null);
    }

    private EventItem getLatest() {
        return getEvents().stream().max((e1, e2) -> Long.compare(e1.timestamp, e2.timestamp)).orElse(null);
    }


    private class EventListAdapter extends ListAdapter<EventItem, EventViewHolder> {
        protected EventListAdapter(@NonNull DiffUtil.ItemCallback<EventItem> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return createEventViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            EventItem currentItem = getItem(position);
            holder.bind(currentItem);
        }
    }

    private static class EventDiff extends DiffUtil.ItemCallback<EventItem> {
        @Override
        public boolean areItemsTheSame(@NonNull EventItem oldItem, @NonNull EventItem newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventItem oldItem, @NonNull EventItem newItem) {
            return oldItem.equals(newItem);
        }
    }

    EventViewHolder createEventViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_view, parent, false);
        return new EventViewHolder(view);
    }

    private class EventViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "EventTypeViewHolder";

        View itemRoot;

        private EventViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemRoot = itemView;
        }

        public void bind(EventItem event) {
            AppCompatImageButton deleteButton = itemRoot.findViewById(R.id.event_view_delete_event);
            deleteButton.setOnClickListener(l -> {
                viewModel.deleteEvent(event);
            });

            TextView timestampText = itemRoot.findViewById(R.id.event_view_time);
            // FIXME format date
            timestampText.setText(new Date(event.timestamp).toString());
        }
    }


}