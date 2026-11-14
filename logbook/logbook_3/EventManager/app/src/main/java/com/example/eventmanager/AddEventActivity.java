package com.example.eventmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.data.AppDatabase;
import com.example.eventmanager.data.EventEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "extra_event";

    private TextInputLayout tilEventName, tilEventDate, tilEventLocation, tilEventOrganiser, tilEventType, tilEventStatus;
    private TextInputEditText etEventName, etEventDate, etEventLocation, etEventOrganiser;
    private AutoCompleteTextView actEventType, actEventStatus;
    private MaterialButton btnSave;

    private EventEntity existingEvent;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddEvent);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        tilEventName = findViewById(R.id.tilEventName);
        tilEventDate = findViewById(R.id.tilEventDate);
        tilEventLocation = findViewById(R.id.tilEventLocation);
        tilEventOrganiser = findViewById(R.id.tilEventOrganiser);
        tilEventType = findViewById(R.id.tilEventType);
        tilEventStatus = findViewById(R.id.tilEventStatus);

        etEventName = findViewById(R.id.etEventName);
        etEventDate = findViewById(R.id.etEventDate);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventOrganiser = findViewById(R.id.etEventOrganiser);
        actEventType = findViewById(R.id.actEventType);
        actEventStatus = findViewById(R.id.actEventStatus);
        btnSave = findViewById(R.id.btnSave);

        // Setup Dropdown Adapters
        String[] eventTypes = {
                getString(R.string.type_conference),
                getString(R.string.type_workshop),
                getString(R.string.type_seminar),
                getString(R.string.type_social),
                getString(R.string.type_other)
        };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, eventTypes);
        actEventType.setAdapter(typeAdapter);

        String[] eventStatuses = {
                getString(R.string.status_upcoming),
                getString(R.string.status_completed),
                getString(R.string.status_cancelled)
        };
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, eventStatuses);
        actEventStatus.setAdapter(statusAdapter);

        // Check if editing existing event
        if (getIntent() != null && getIntent().hasExtra(EXTRA_EVENT)) {
            existingEvent = (EventEntity) getIntent().getSerializableExtra(EXTRA_EVENT);
            if (existingEvent != null) {
                isEditMode = true;
                toolbar.setTitle(R.string.title_edit_event);
                btnSave.setText(R.string.btn_update);

                etEventName.setText(existingEvent.getName());
                etEventDate.setText(existingEvent.getDate());
                etEventLocation.setText(existingEvent.getLocation());
                etEventOrganiser.setText(existingEvent.getOrganiser());
                actEventType.setText(existingEvent.getType(), false);
                actEventStatus.setText(existingEvent.getStatus(), false);
            }
        }

        btnSave.setOnClickListener(v -> validateAndSaveEvent());
    }

    private void validateAndSaveEvent() {
        tilEventName.setError(null);
        tilEventDate.setError(null);
        tilEventLocation.setError(null);
        tilEventOrganiser.setError(null);
        tilEventType.setError(null);
        tilEventStatus.setError(null);

        String name = etEventName.getText() != null ? etEventName.getText().toString().trim() : "";
        String date = etEventDate.getText() != null ? etEventDate.getText().toString().trim() : "";
        String location = etEventLocation.getText() != null ? etEventLocation.getText().toString().trim() : "";
        String organiser = etEventOrganiser.getText() != null ? etEventOrganiser.getText().toString().trim() : "";
        String type = actEventType.getText() != null ? actEventType.getText().toString().trim() : "";
        String status = actEventStatus.getText() != null ? actEventStatus.getText().toString().trim() : "";

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            tilEventName.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(date)) {
            tilEventDate.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!isValidDate(date)) {
            tilEventDate.setError(getString(R.string.error_invalid_date));
            isValid = false;
        }

        if (TextUtils.isEmpty(location)) {
            tilEventLocation.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(organiser)) {
            tilEventOrganiser.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(type)) {
            tilEventType.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(status)) {
            tilEventStatus.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (isValid) {
            if (isEditMode && existingEvent != null) {
                existingEvent.setName(name);
                existingEvent.setDate(date);
                existingEvent.setLocation(location);
                existingEvent.setOrganiser(organiser);
                existingEvent.setType(type);
                existingEvent.setStatus(status);

                AppDatabase.getInstance(this).eventDao().update(existingEvent);
            } else {
                EventEntity newEvent = new EventEntity(name, date, location, organiser, type, status);
                AppDatabase.getInstance(this).eventDao().insert(newEvent);
            }
            finish();
        }
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
