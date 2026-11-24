package com.example.eventmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.adapter.ContactAdapter;
import com.example.eventmanager.adapter.SessionAdapter;
import com.example.eventmanager.data.AppDatabase;
import com.example.eventmanager.data.ContactRequestEntity;
import com.example.eventmanager.data.EventEntity;
import com.example.eventmanager.data.SessionEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity implements SessionAdapter.OnSessionClickListener {

    public static final String EXTRA_EVENT = "extra_event";

    private TextView tvDetailEventName, tvDetailEventType, tvDetailEventDate, tvDetailEventLocation,
            tvDetailEventOrganiser, tvDetailEventDescription, tvDetailContactEmail, tvDetailContactPhone,
            tvTotalSessions, tvEmptySessions, tvEmptyContacts;
    private RecyclerView recyclerViewSessions, recyclerViewContacts;
    private SearchView searchViewSessions;
    private SessionAdapter sessionAdapter;
    private ContactAdapter contactAdapter;
    private List<SessionEntity> sessionList;
    private List<ContactRequestEntity> contactList;

    private EventEntity event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbarEventDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent() != null && getIntent().hasExtra(EXTRA_EVENT)) {
            event = (EventEntity) getIntent().getSerializableExtra(EXTRA_EVENT);
        }

        if (event == null) {
            finish();
            return;
        }

        tvDetailEventName = findViewById(R.id.tvDetailEventName);
        tvDetailEventType = findViewById(R.id.tvDetailEventType);
        tvDetailEventDate = findViewById(R.id.tvDetailEventDate);
        tvDetailEventLocation = findViewById(R.id.tvDetailEventLocation);
        tvDetailEventOrganiser = findViewById(R.id.tvDetailEventOrganiser);
        tvDetailEventDescription = findViewById(R.id.tvDetailEventDescription);
        tvDetailContactEmail = findViewById(R.id.tvDetailContactEmail);
        tvDetailContactPhone = findViewById(R.id.tvDetailContactPhone);
        tvTotalSessions = findViewById(R.id.tvTotalSessions);
        tvEmptySessions = findViewById(R.id.tvEmptySessions);
        tvEmptyContacts = findViewById(R.id.tvEmptyContacts);
        recyclerViewSessions = findViewById(R.id.recyclerViewSessions);
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        searchViewSessions = findViewById(R.id.searchViewSessions);
        FloatingActionButton fabAddSession = findViewById(R.id.fabAddSession);
        MaterialButton btnEditEventDetail = findViewById(R.id.btnEditEventDetail);
        MaterialButton btnEventQrCode = findViewById(R.id.btnEventQrCode);

        sessionList = new ArrayList<>();
        sessionAdapter = new SessionAdapter(sessionList, this);
        recyclerViewSessions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSessions.setAdapter(sessionAdapter);

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactList);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.setAdapter(contactAdapter);

        searchViewSessions.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterSessions(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSessions(newText);
                return true;
            }
        });

        fabAddSession.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, AddSessionActivity.class);
            intent.putExtra(AddSessionActivity.EXTRA_EVENT_ID, event.getId());
            startActivity(intent);
        });

        btnEditEventDetail.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.EXTRA_EVENT, event);
            startActivity(intent);
        });

        btnEventQrCode.setOnClickListener(v -> showEventQrCodeDialog());

        displayEventDetails();
    }

    private void showEventQrCodeDialog() {
        String qrContent = String.format(Locale.getDefault(),
                "EVENT QR CODE\n\nName: %s\nDate: %s\nLocation: %s\nOrganiser: %s\nType: %s\nStatus: %s\nDescription: %s\nEmail: %s\nPhone: %s",
                event.getName(), event.getDate(), event.getLocation(), event.getOrganiser(),
                event.getType(), event.getStatus(), event.getDescription(), event.getContactEmail(), event.getContactPhone());

        ImageView imageView = new ImageView(this);
        Bitmap bitmap = generateQRCodeBitmap(qrContent, 500, 500);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        imageView.setPadding(32, 32, 32, 32);

        new AlertDialog.Builder(this)
                .setTitle("Event QR Code")
                .setView(imageView)
                .setPositiveButton("Close", null)
                .show();
    }

    private Bitmap generateQRCodeBitmap(String content, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            int bmpWidth = bitMatrix.getWidth();
            int bmpHeight = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.RGB_565);
            for (int x = 0; x < bmpWidth; x++) {
                for (int y = 0; y < bmpHeight; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (event != null) {
            event = AppDatabase.getInstance(this).eventDao().getEventById(event.getId());
            if (event == null) {
                finish();
                return;
            }
            displayEventDetails();
            startRealtimeSessionsSync();
            syncContactsFromFirebase();
        }
    }

    private void startRealtimeSessionsSync() {
        DatabaseReference sessionsRef = FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("sessions");
        sessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppDatabase db = AppDatabase.getInstance(EventDetailActivity.this);
                boolean updated = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        SessionEntity fbSession = child.getValue(SessionEntity.class);
                        if (fbSession != null && fbSession.getEventId() == event.getId()) {
                            fbSession.setFirebaseKey(child.getKey());
                            List<SessionEntity> localSessions = db.sessionDao().getSessionsForEvent(event.getId());
                            SessionEntity existingMatch = null;
                            for (SessionEntity local : localSessions) {
                                if ((local.getFirebaseKey() != null && local.getFirebaseKey().equals(fbSession.getFirebaseKey())) ||
                                    local.getName().equalsIgnoreCase(fbSession.getName())) {
                                    existingMatch = local;
                                    break;
                                }
                            }
                            if (existingMatch != null) {
                                existingMatch.setTime(fbSession.getTime());
                                existingMatch.setSpeaker(fbSession.getSpeaker());
                                existingMatch.setRoom(fbSession.getRoom());
                                existingMatch.setType(fbSession.getType());
                                existingMatch.setDescription(fbSession.getDescription());
                                existingMatch.setFirebaseKey(fbSession.getFirebaseKey());
                                db.sessionDao().update(existingMatch);
                                updated = true;
                            } else {
                                db.sessionDao().insert(fbSession);
                                updated = true;
                            }
                        }
                    } catch (Exception e) {
                        // Skip malformed session nodes
                    }
                }
                if (updated) {
                    loadSessions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadSessions();
            }
        });
        loadSessions();
    }

    private void syncContactsFromFirebase() {
        DatabaseReference contactsRef = FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("contactRequests");
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        ContactRequestEntity contact = child.getValue(ContactRequestEntity.class);
                        if (contact != null) {
                            boolean matchName = contact.getEventName() != null && contact.getEventName().equalsIgnoreCase(event.getName());
                            boolean matchId = contact.getEventId() != null && contact.getEventId().equals(String.valueOf(event.getId()));
                            if (matchName || matchId) {
                                contactList.add(contact);
                            }
                        }
                    } catch (Exception e) {
                        // Skip malformed contact nodes
                    }
                }
                contactAdapter.notifyDataSetChanged();
                if (contactList.isEmpty()) {
                    recyclerViewContacts.setVisibility(View.GONE);
                    tvEmptyContacts.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewContacts.setVisibility(View.VISIBLE);
                    tvEmptyContacts.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                recyclerViewContacts.setVisibility(View.GONE);
                tvEmptyContacts.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayEventDetails() {
        tvDetailEventName.setText(event.getName());
        tvDetailEventType.setText(event.getType());
        tvDetailEventDate.setText("Date: " + event.getDate());
        tvDetailEventLocation.setText("Location: " + event.getLocation());
        tvDetailEventOrganiser.setText("Organiser: " + event.getOrganiser());

        if (!TextUtils.isEmpty(event.getDescription())) {
            tvDetailEventDescription.setText(event.getDescription());
            tvDetailEventDescription.setVisibility(View.VISIBLE);
        } else {
            tvDetailEventDescription.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(event.getContactEmail())) {
            tvDetailContactEmail.setText("Email: " + event.getContactEmail());
            tvDetailContactEmail.setVisibility(View.VISIBLE);
        } else {
            tvDetailContactEmail.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(event.getContactPhone())) {
            tvDetailContactPhone.setText("Phone: " + event.getContactPhone());
            tvDetailContactPhone.setVisibility(View.VISIBLE);
        } else {
            tvDetailContactPhone.setVisibility(View.GONE);
        }
    }

    private void loadSessions() {
        String query = searchViewSessions != null ? searchViewSessions.getQuery().toString() : "";
        filterSessions(query);
    }

    private void filterSessions(String query) {
        List<SessionEntity> dbSessions = AppDatabase.getInstance(this).sessionDao().getSessionsForEvent(event.getId());
        sessionList.clear();
        if (dbSessions != null) {
            if (TextUtils.isEmpty(query)) {
                sessionList.addAll(dbSessions);
            } else {
                String lowerQuery = query.toLowerCase(Locale.getDefault());
                for (SessionEntity session : dbSessions) {
                    if ((session.getName() != null && session.getName().toLowerCase(Locale.getDefault()).contains(lowerQuery)) ||
                        (session.getDescription() != null && session.getDescription().toLowerCase(Locale.getDefault()).contains(lowerQuery)) ||
                        (session.getSpeaker() != null && session.getSpeaker().toLowerCase(Locale.getDefault()).contains(lowerQuery)) ||
                        (session.getRoom() != null && session.getRoom().toLowerCase(Locale.getDefault()).contains(lowerQuery))) {
                        sessionList.add(session);
                    }
                }
            }
        }
        sessionAdapter.notifyDataSetChanged();

        if (sessionList.isEmpty()) {
            recyclerViewSessions.setVisibility(View.GONE);
            tvEmptySessions.setVisibility(View.VISIBLE);
        } else {
            recyclerViewSessions.setVisibility(View.VISIBLE);
            tvEmptySessions.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_event) {
            Intent intent = new Intent(EventDetailActivity.this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.EXTRA_EVENT, event);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete_event) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_confirm_title)
                    .setMessage(R.string.delete_confirm_message)
                    .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                        AppDatabase.getInstance(EventDetailActivity.this).eventDao().delete(event);
                        FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("events")
                                .orderByChild("name").equalTo(event.getName())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            child.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                        finish();
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(SessionEntity session) {
        Intent intent = new Intent(EventDetailActivity.this, AddSessionActivity.class);
        intent.putExtra(AddSessionActivity.EXTRA_SESSION, session);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(SessionEntity session) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_session_title)
                .setMessage(R.string.delete_session_message)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    AppDatabase.getInstance(EventDetailActivity.this).sessionDao().delete(session);
                    FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("sessions")
                            .orderByChild("name").equalTo(session.getName())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        SessionEntity s = child.getValue(SessionEntity.class);
                                        if (s != null && s.getEventId() == session.getEventId()) {
                                            child.getRef().removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                    loadSessions();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
