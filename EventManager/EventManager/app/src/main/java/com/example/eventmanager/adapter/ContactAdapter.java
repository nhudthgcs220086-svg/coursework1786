package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.ContactRequestEntity;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<ContactRequestEntity> contactList;

    public ContactAdapter(List<ContactRequestEntity> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactRequestEntity contact = contactList.get(position);
        holder.tvContactName.setText(contact.getName() != null ? contact.getName() : "Anonymous");
        
        String details = "";
        if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
            details += "Email: " + contact.getEmail();
        }
        if (contact.getPhone() != null && !contact.getPhone().isEmpty()) {
            if (!details.isEmpty()) details += " | ";
            details += "Phone: " + contact.getPhone();
        }
        holder.tvContactEmailPhone.setText(details);

        if (contact.getMessage() != null && !contact.getMessage().isEmpty()) {
            holder.tvContactMessage.setText("Message: " + contact.getMessage());
            holder.tvContactMessage.setVisibility(View.VISIBLE);
        } else {
            holder.tvContactMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvContactName, tvContactEmailPhone, tvContactMessage;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactEmailPhone = itemView.findViewById(R.id.tvContactEmailPhone);
            tvContactMessage = itemView.findViewById(R.id.tvContactMessage);
        }
    }
}
