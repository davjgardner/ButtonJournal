package com.davjgardner.buttonjournal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davjgardner.buttonjournal.eventdb.EventItem;

public class EventViewHolder extends RecyclerView.ViewHolder {
    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(EventItem e) {
        // TODO
    }

    static EventViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
        return new EventViewHolder(view);
    }
}
