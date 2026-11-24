package com.example.eventmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.data.AppDatabase;
import com.example.eventmanager.data.SessionEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddSessionActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "extra_event_id";
    public static final String EXTRA_SESSION = "extra_session";

    private TextInputLayout tilSessionName, tilSessionTime, tilSessionType;
    private TextInputEditText etSessionName, etSessionTime, etSessionSpeaker, etSessionRoom, etSessionDescription;
    private AutoCompleteTextView actSessionType;

    private long eventId = -1;
    private SessionEntity existingSession;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddSession);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tilSessionName = findViewById(R.id.tilSessionName);
        tilSessionTime = findViewById(R.id.tilSessionTime);
        tilSessionType = findViewById(R.id.tilSessionType);

        etSessionName = findViewById(R.id.etSessionName);
        etSessionTime = findViewById(R.id.etSessionTime);
        etSessionSpeaker = findViewById(R.id.etSessionSpeaker);
        etSessionRoom = findViewById(R.id.etSessionRoom);
        etSessionDescription = findViewById(R.id.etSessionDescription);
        actSessionType = findViewById(R.id.actSessionType);
        MaterialButton btnSaveSession = findViewById(R.id.btnSaveSession);

        String[] sessionTypes = {
                getString(R.string.session_type_workshop),
                getString(R.string.session_type_presentation),
                getString(R.string.session_type_panel),
                getString(R.string.session_type_networking),
                getString(R.string.session_type_keynote),
                getString(R.string.session_type_others)
        };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sessionTypes);
        actSessionType.setAdapter(typeAdapter);

        if (getIntent() != null) {
            eventId = getIntent().getLongExtra(EXTRA_EVENT_ID, -1);
            if (getIntent().hasExtra(EXTRA_SESSION)) {
                existingSession = (SessionEntity) getIntent().getSerializableExtra(EXTRA_SESSION);
                if (existingSession != null) {
                    isEditMode = true;
                    eventId = existingSession.getEventId();
                    toolbar.setTitle(R.string.title_edit_session);
                    btnSaveSession.setText(R.string.btn_update_session);

                    etSessionName.setText(existingSession.getName());
                    etSessionTime.setText(existingSession.getTime());
                    etSessionSpeaker.setText(existingSession.getSpeaker());
                    etSessionRoom.setText(existingSession.getRoom());
                    etSessionDescription.setText(existingSession.getDescription());
                    actSessionType.setText(existingSession.getType(), false);
                }
            }
        }

        btnSaveSession.setOnClickListener(v -> validateAndSaveSession());
    }

    private void validateAndSaveSession() {
        tilSessionName.setError(null);
        tilSessionTime.setError(null);
        tilSessionType.setError(null);

        String name = etSessionName.getText() != null ? etSessionName.getText().toString().trim() : "";
        String time = etSessionTime.getText() != null ? etSessionTime.getText().toString().trim() : "";
        String speaker = etSessionSpeaker.getText() != null ? etSessionSpeaker.getText().toString().trim() : "";
        String room = etSessionRoom.getText() != null ? etSessionRoom.getText().toString().trim() : "";
        String type = actSessionType.getText() != null ? actSessionType.getText().toString().trim() : "";
        String description = etSessionDescription.getText() != null ? etSessionDescription.getText().toString().trim() : "";

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            tilSessionName.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(time)) {
            tilSessionTime.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(type)) {
            tilSessionType.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (isValid && eventId != -1) {
            DatabaseReference sessionsRef = FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("sessions");

            if (isEditMode && existingSession != null) {
                existingSession.setName(name);
                existingSession.setTime(time);
                existingSession.setSpeaker(speaker);
                existingSession.setRoom(room);
                existingSession.setType(type);
                existingSession.setDescription(description);

                AppDatabase.getInstance(this).sessionDao().update(existingSession);

                if (isNetworkAvailable(this)) {
                    if (existingSession.getFirebaseKey() != null && !existingSession.getFirebaseKey().isEmpty()) {
                        sessionsRef.child(existingSession.getFirebaseKey()).setValue(existingSession);
                    } else {
                        String key = sessionsRef.push().getKey();
                        if (key != null) {
                            existingSession.setFirebaseKey(key);
                            sessionsRef.child(key).setValue(existingSession);
                            AppDatabase.getInstance(this).sessionDao().update(existingSession);
                        }
                    }
                }
            } else {
                SessionEntity newSession = new SessionEntity(eventId, name, time, speaker, room, type, description);
                AppDatabase.getInstance(this).sessionDao().insert(newSession);

                if (isNetworkAvailable(this)) {
                    String key = sessionsRef.push().getKey();
                    if (key != null) {
                        newSession.setFirebaseKey(key);
                        sessionsRef.child(key).setValue(newSession);
                    }
                }
            }
            finish();
        }
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
