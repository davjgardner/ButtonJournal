package com.davjgardner.buttonjournal;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davjgardner.buttonjournal.eventdb.EventRepository;
import com.davjgardner.buttonjournal.eventdb.EventType;

public class EventTypeViewHolder extends RecyclerView.ViewHolder {

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
            EventRepository repo = new EventRepository(l.getContext().getApplicationContext());
            repo.createEventOf(t);
        });
        Button viewEventsButton = itemRoot.findViewById(R.id.viewTimelineButton);
        viewEventsButton.setOnClickListener(l -> {
            Intent intent = new Intent(l.getContext(), ViewEventActivity.class);
            intent.putExtra(ViewEventActivity.EVENT_TYPE, t.name);
            l.getContext().startActivity(intent);
        });
    }

    static EventTypeViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_button_view, parent, false);
        return new EventTypeViewHolder(view);
    }
}
