package com.davjgardner.buttonjournal;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.davjgardner.buttonjournal.eventdb.EventType;

public class EventTypeListAdapter extends ListAdapter<EventType, EventTypeViewHolder> {
    protected EventTypeListAdapter(@NonNull DiffUtil.ItemCallback<EventType> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return EventTypeViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
        EventType currentItem = getItem(position);
        holder.bind(currentItem);
    }

    static class EventTypeDiff extends DiffUtil.ItemCallback<EventType> {

        @Override
        public boolean areItemsTheSame(@NonNull EventType oldItem, @NonNull EventType newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventType oldItem, @NonNull EventType newItem) {
            return oldItem.equals(newItem);
        }
    }
}
