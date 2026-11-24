package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.SessionEntity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private final List<SessionEntity> sessionList;
    private final OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onEditClick(SessionEntity session);
        void onDeleteClick(SessionEntity session);
    }

    public SessionAdapter(List<SessionEntity> sessionList, OnSessionClickListener listener) {
        this.sessionList = sessionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionEntity session = sessionList.get(position);
        holder.tvSessionName.setText(session.getName());
        holder.tvSessionType.setText(session.getType());
        holder.tvSessionTime.setText(session.getTime());
        holder.tvSessionSpeaker.setText("Speaker: " + (session.getSpeaker() != null ? session.getSpeaker() : "TBD"));
        holder.tvSessionRoom.setText("Room: " + (session.getRoom() != null ? session.getRoom() : "TBD"));
        holder.tvSessionDescription.setText(session.getDescription() != null ? session.getDescription() : "");

        holder.btnEditSession.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(session);
            }
        });

        holder.btnDeleteSession.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionName, tvSessionType, tvSessionTime, tvSessionSpeaker, tvSessionRoom, tvSessionDescription;
        MaterialButton btnEditSession, btnDeleteSession;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionName = itemView.findViewById(R.id.tvSessionName);
            tvSessionType = itemView.findViewById(R.id.tvSessionType);
            tvSessionTime = itemView.findViewById(R.id.tvSessionTime);
            tvSessionSpeaker = itemView.findViewById(R.id.tvSessionSpeaker);
            tvSessionRoom = itemView.findViewById(R.id.tvSessionRoom);
            tvSessionDescription = itemView.findViewById(R.id.tvSessionDescription);
            btnEditSession = itemView.findViewById(R.id.btnEditSession);
            btnDeleteSession = itemView.findViewById(R.id.btnDeleteSession);
        }
    }
}
