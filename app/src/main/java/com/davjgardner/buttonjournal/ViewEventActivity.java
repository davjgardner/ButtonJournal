package com.davjgardner.buttonjournal;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.text.DateFormat;
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

    EventListAdapter listAdapter;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        eventType = new EventType(getIntent().getStringExtra(EVENT_TYPE_NAME));
        eventType.id = getIntent().getIntExtra(EVENT_TYPE_ID, 0);

        RecyclerView eventsView = findViewById(R.id.event_list);
        listAdapter = new EventListAdapter(new EventDiff());
        eventsView.setAdapter(listAdapter);
        eventsView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        viewModel.setEventType(eventType);
        events = viewModel.getEvents();
        viewModel.getEvents().observe(this, listAdapter::submitList);

        title = findViewById(R.id.event_view_title);
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
            setToStart(since, DAY);

            TextView dayCount = findViewById(R.id.tv_day_count);
            dayCount.setText(lts(countSince(since.getTimeInMillis())));

            TextView dayAverage = findViewById(R.id.tv_day_average);
            dayAverage.setText(dts(average(DAY)));

            since.set(Calendar.DAY_OF_MONTH, 1);

            TextView monthCount = findViewById(R.id.tv_month_count);
            monthCount.setText(lts(countSince(since.getTimeInMillis())));

            TextView monthAverage = findViewById(R.id.tv_month_average);
            monthAverage.setText(dts(average(MONTH)));

            since.set(Calendar.DAY_OF_YEAR, 1);
            TextView yearCount = findViewById(R.id.tv_year_count);
            yearCount.setText(lts(countSince(since.getTimeInMillis())));

            TextView yearAverage = findViewById(R.id.tv_year_average);
            yearAverage.setText(dts(average(YEAR)));

            since = Calendar.getInstance();
            setToStart(since, WEEK);
            TextView weekCount = findViewById(R.id.tv_week_count);
            weekCount.setText(lts(countSince(since.getTimeInMillis())));

            TextView weekAverage = findViewById(R.id.tv_week_average);
            weekAverage.setText(dts(average(WEEK)));

            TextView totalCount = findViewById(R.id.tv_total_count);
            totalCount.setText(lts(events.size()));
        });
    }

    private boolean deleteMode = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_view_menu, menu);
        MenuItem deleteItem = menu.findItem(R.id.menu_delete_events);
        deleteItem.setTitle(deleteMode? R.string.done : R.string.delete_events);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_events) {
            deleteMode = !deleteMode;
            item.setTitle(deleteMode? R.string.done : R.string.delete_events);
            listAdapter.notifyDataSetChanged();
            return true;
        } else if (item.getItemId() == R.id.menu_rename_type) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.rename_type, eventType.name));
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
                    | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input)
                    .setPositiveButton(android.R.string.ok, (dialog, which) ->
                            rename(input.getText().toString()))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void rename(String to) {
        viewModel.renameType(eventType, to);
        eventType.name = to;
        title.setText(to);
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

    private String dts(double d) {
        return String.format(getResources().getConfiguration().getLocales().get(0), "%f", d);
    }

    private long countSince(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
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

    private double average(int field) {
        if (getEvents().size() == 0) return 0.0;

        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        setToStart(start, field);
        Calendar first = Calendar.getInstance();
        setToStart(first, field);
        first.setTimeInMillis(getEarliest().timestamp);

        int increment = Calendar.DAY_OF_MONTH;
        switch (field) {
            case DAY:
                increment = Calendar.DAY_OF_MONTH;
                break;
            case WEEK:
                increment = Calendar.WEEK_OF_YEAR;
                break;
            case MONTH:
                increment = Calendar.MONTH;
                break;
            case YEAR:
                increment = Calendar.YEAR;
                break;
        }

        double count = 0;
        double periods = 0;
        while (end.after(first)) {
            count += countBetween(start.getTimeInMillis(), end.getTimeInMillis());
            periods++;
            end.setTimeInMillis(start.getTimeInMillis());
            start.add(increment, -1);
        }
        return count / periods;
    }

    public static final int DAY = 1, WEEK = 2, MONTH = 3, YEAR = 4;

    private void setToStart(Calendar date, int field) {
        switch (field) {
            case YEAR:
                date.set(Calendar.MONTH, 1);
            case MONTH:
                date.set(Calendar.DAY_OF_MONTH, 1);
            case DAY:
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                break;
            case WEEK:
                date.set(Calendar.DAY_OF_WEEK, 1);
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                break;
        }
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
            deleteButton.setEnabled(deleteMode);
            deleteButton.setVisibility(deleteMode? View.VISIBLE : View.INVISIBLE);
            deleteButton.setOnClickListener(l -> viewModel.deleteEvent(event));

            TextView timestampText = itemRoot.findViewById(R.id.event_view_time);
            // FIXME format date
            timestampText.setText(new Date(event.timestamp).toString());
        }
    }


}