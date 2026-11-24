package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.EventEntity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<EventEntity> eventList;
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(EventEntity event);
        void onDeleteClick(EventEntity event);
        void onAddSessionClick(EventEntity event);
    }

    public EventAdapter(List<EventEntity> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventEntity event = eventList.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvEventType.setText(event.getType());
        holder.tvEventStatus.setText(event.getStatus());
        holder.tvEventDate.setText(event.getDate());
        holder.tvEventLocation.setText(event.getLocation());
        holder.tvEventOrganiser.setText(event.getOrganiser());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(event);
            }
        });

        holder.btnAddSessionItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddSessionClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventType, tvEventStatus, tvEventDate, tvEventLocation, tvEventOrganiser;
        ImageButton btnDelete;
        MaterialButton btnAddSessionItem;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventType = itemView.findViewById(R.id.tvEventType);
            tvEventStatus = itemView.findViewById(R.id.tvEventStatus);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
            tvEventOrganiser = itemView.findViewById(R.id.tvEventOrganiser);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddSessionItem = itemView.findViewById(R.id.btnAddSessionItem);
        }
    }
}
