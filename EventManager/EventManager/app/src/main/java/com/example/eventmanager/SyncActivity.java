package com.example.eventmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.data.AppDatabase;
import com.example.eventmanager.data.EventEntity;
import com.example.eventmanager.data.SessionEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class SyncActivity extends AppCompatActivity {

    private TextView tvNetworkStatus, tvLocalEventsCount, tvLocalSessionsCount, tvSyncResult;
    private MaterialButton btnBatchSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSync);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvNetworkStatus = findViewById(R.id.tvNetworkStatus);
        tvLocalEventsCount = findViewById(R.id.tvLocalEventsCount);
        tvLocalSessionsCount = findViewById(R.id.tvLocalSessionsCount);
        tvSyncResult = findViewById(R.id.tvSyncResult);
        btnBatchSync = findViewById(R.id.btnBatchSync);

        checkNetworkAndStats();

        btnBatchSync.setOnClickListener(v -> performBatchSync());
    }

    private void checkNetworkAndStats() {
        boolean isConnected = isNetworkAvailable(this);
        if (isConnected) {
            tvNetworkStatus.setText("Connected to Internet (Online)");
            tvNetworkStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
            btnBatchSync.setEnabled(true);
        } else {
            tvNetworkStatus.setText("No Internet Connection (Offline)");
            tvNetworkStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark, getTheme()));
            btnBatchSync.setEnabled(false);
        }

        AppDatabase db = AppDatabase.getInstance(this);
        List<EventEntity> events = db.eventDao().getAllEvents();
        int eventCount = events != null ? events.size() : 0;

        int sessionCount = 0;
        if (events != null) {
            for (EventEntity event : events) {
                List<SessionEntity> sessions = db.sessionDao().getSessionsForEvent(event.getId());
                if (sessions != null) {
                    sessionCount += sessions.size();
                }
            }
        }

        tvLocalEventsCount.setText(String.format(Locale.getDefault(), "Local Events: %d", eventCount));
        tvLocalSessionsCount.setText(String.format(Locale.getDefault(), "Local Sessions: %d", sessionCount));
    }

    private void performBatchSync() {
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection! Cannot sync to cloud.", Toast.LENGTH_LONG).show();
            tvSyncResult.setText("Sync failed: No internet connection.");
            return;
        }

        tvSyncResult.setText("Syncing data to Firebase...");
        btnBatchSync.setEnabled(false);

        AppDatabase db = AppDatabase.getInstance(this);
        List<EventEntity> events = db.eventDao().getAllEvents();

        DatabaseReference eventsRef = FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("events");
        DatabaseReference sessionsRef = FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("sessions");

        int uploadedEvents = 0;
        int uploadedSessions = 0;

        if (events != null) {
            for (EventEntity event : events) {
                if (event.getFirebaseKey() == null || event.getFirebaseKey().isEmpty()) {
                    String key = eventsRef.push().getKey();
                    if (key != null) {
                        event.setFirebaseKey(key);
                        db.eventDao().update(event);
                    }
                }
                if (event.getFirebaseKey() != null) {
                    eventsRef.child(event.getFirebaseKey()).setValue(event);
                } else {
                    eventsRef.push().setValue(event);
                }
                uploadedEvents++;

                List<SessionEntity> sessions = db.sessionDao().getSessionsForEvent(event.getId());
                if (sessions != null) {
                    for (SessionEntity session : sessions) {
                        if (session.getFirebaseKey() == null || session.getFirebaseKey().isEmpty()) {
                            String key = sessionsRef.push().getKey();
                            if (key != null) {
                                session.setFirebaseKey(key);
                                db.sessionDao().update(session);
                            }
                        }
                        if (session.getFirebaseKey() != null) {
                            sessionsRef.child(session.getFirebaseKey()).setValue(session);
                        } else {
                            sessionsRef.push().setValue(session);
                        }
                        uploadedSessions++;
                    }
                }
            }
        }

        tvSyncResult.setText(String.format(Locale.getDefault(), "Batch sync successful! Uploaded %d events and %d sessions.", uploadedEvents, uploadedSessions));
        Toast.makeText(this, "Batch upload & sync completed successfully!", Toast.LENGTH_LONG).show();
        btnBatchSync.setEnabled(true);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && (
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                );
            }
        }
        return false;
    }
}
