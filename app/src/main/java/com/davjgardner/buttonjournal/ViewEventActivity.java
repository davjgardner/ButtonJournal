package com.davjgardner.buttonjournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.davjgardner.buttonjournal.eventdb.EventItem;
import com.davjgardner.buttonjournal.eventdb.EventRepository;
import com.davjgardner.buttonjournal.eventdb.EventType;

import java.util.ArrayList;
import java.util.List;

public class ViewEventActivity extends AppCompatActivity {

    public static final String TAG = "ViewEventActivity";

    public static final String EVENT_TYPE = "com.davjgardner.buttonjournal.EVENT_TYPE";

    private static final long MILLISECONDS_IN_DAY = 86400000;
    private static final long DAYS_IN_WEEK = 7;
    private static final long DAYS_IN_MONTH = 30;
    private static final long DAYS_IN_YEAR = 365;

    private EventRepository repo;
    LiveData<List<EventItem>> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        EventType eventType = new EventType(getIntent().getStringExtra(EVENT_TYPE));
        repo = new EventRepository(getApplication());
        events = repo.getEventsOf(eventType);
        if (events.getValue() == null) Log.w(TAG, "Got null event list.");

        TextView title = findViewById(R.id.event_view_title);
        title.setText(eventType.name);

        long now = System.currentTimeMillis();

        TextView dayCount = findViewById(R.id.tv_day_count);
        dayCount.setText(lts(countSince(now - MILLISECONDS_IN_DAY)));

        TextView monthCount = findViewById(R.id.tv_month_count);
        monthCount.setText(lts(countSince(now - MILLISECONDS_IN_DAY * DAYS_IN_MONTH)));

        TextView weekCount = findViewById(R.id.tv_week_count);
        weekCount.setText(lts(countSince(now - MILLISECONDS_IN_DAY * DAYS_IN_WEEK)));

        TextView yearCount = findViewById(R.id.tv_year_count);
        yearCount.setText(lts(countSince(now - MILLISECONDS_IN_DAY * DAYS_IN_YEAR)));

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
}