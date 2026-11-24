package com.example.eventmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.data.AppDatabase;
import com.example.eventmanager.data.EventEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "extra_event";

    private TextInputLayout tilEventName, tilEventDate, tilEventLocation, tilEventOrganiser,
            tilContactEmail, tilEventType, tilEventStatus;
    private TextInputEditText etEventName, etEventDate, etEventLocation, etEventOrganiser,
            etEventDescription, etContactEmail, etContactPhone;
    private AutoCompleteTextView actEventType, actEventStatus;

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
        tilContactEmail = findViewById(R.id.tilContactEmail);
        tilEventType = findViewById(R.id.tilEventType);
        tilEventStatus = findViewById(R.id.tilEventStatus);

        etEventName = findViewById(R.id.etEventName);
        etEventDate = findViewById(R.id.etEventDate);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventOrganiser = findViewById(R.id.etEventOrganiser);
        etEventDescription = findViewById(R.id.etEventDescription);
        etContactEmail = findViewById(R.id.etContactEmail);
        etContactPhone = findViewById(R.id.etContactPhone);
        actEventType = findViewById(R.id.actEventType);
        actEventStatus = findViewById(R.id.actEventStatus);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        // Setup Date Picker Dialog on click / touch
        etEventDate.setFocusable(false);
        etEventDate.setClickable(true);
        etEventDate.setOnClickListener(v -> showDatePickerDialog());
        tilEventDate.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        tilEventDate.setEndIconDrawable(android.R.drawable.ic_menu_my_calendar);
        tilEventDate.setEndIconOnClickListener(v -> showDatePickerDialog());

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
                etEventDescription.setText(existingEvent.getDescription());
                etContactEmail.setText(existingEvent.getContactEmail());
                etContactPhone.setText(existingEvent.getContactPhone());
                actEventType.setText(existingEvent.getType(), false);
                actEventStatus.setText(existingEvent.getStatus(), false);
            }
        }

        btnSave.setOnClickListener(v -> validateAndConfirmEvent());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
            etEventDate.setText(formattedDate);
            tilEventDate.setError(null);
        }, year, month, day);

        // Disable past dates (minDate is today)
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void validateAndConfirmEvent() {
        tilEventName.setError(null);
        tilEventDate.setError(null);
        tilEventLocation.setError(null);
        tilEventOrganiser.setError(null);
        tilContactEmail.setError(null);
        tilEventType.setError(null);
        tilEventStatus.setError(null);

        String name = etEventName.getText() != null ? etEventName.getText().toString().trim() : "";
        String date = etEventDate.getText() != null ? etEventDate.getText().toString().trim() : "";
        String location = etEventLocation.getText() != null ? etEventLocation.getText().toString().trim() : "";
        String organiser = etEventOrganiser.getText() != null ? etEventOrganiser.getText().toString().trim() : "";
        String description = etEventDescription.getText() != null ? etEventDescription.getText().toString().trim() : "";
        String email = etContactEmail.getText() != null ? etContactEmail.getText().toString().trim() : "";
        String phone = etContactPhone.getText() != null ? etContactPhone.getText().toString().trim() : "";
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

        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilContactEmail.setError(getString(R.string.error_invalid_email));
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

        if (!isValid) {
            Toast.makeText(this, "Please check form errors.", Toast.LENGTH_LONG).show();
            return;
        }

        // Duplicate check (same name and date)
        EventEntity duplicate = AppDatabase.getInstance(this).eventDao().getEventByNameAndDate(name, date);
        if (duplicate != null) {
            if (!isEditMode || duplicate.getId() != existingEvent.getId()) {
                tilEventName.setError(getString(R.string.error_duplicate_event));
                tilEventDate.setError(getString(R.string.error_duplicate_event));
                Toast.makeText(this, "An event with this name and date already exists!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Show Confirmation Dialog before saving
        String summary = String.format(Locale.getDefault(),
                "Name: %s\nDate: %s\nLocation: %s\nOrganiser: %s\nType: %s\nStatus: %s",
                name, date, location, organiser, type, status);

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(getString(R.string.confirm_dialog_message) + "\n\n" + summary)
                .setPositiveButton(R.string.btn_confirm, (dialog, which) -> saveEventToDb(name, date, location, organiser, description, email, phone, type, status))
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void saveEventToDb(String name, String date, String location, String organiser,
                               String description, String email, String phone, String type, String status) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("events");

        if (isEditMode && existingEvent != null) {
            existingEvent.setName(name);
            existingEvent.setDate(date);
            existingEvent.setLocation(location);
            existingEvent.setOrganiser(organiser);
            existingEvent.setDescription(description);
            existingEvent.setContactEmail(email);
            existingEvent.setContactPhone(phone);
            existingEvent.setType(type);
            existingEvent.setStatus(status);

            AppDatabase.getInstance(this).eventDao().update(existingEvent);

            if (SyncActivity.isNetworkAvailable(this)) {
                if (existingEvent.getFirebaseKey() != null && !existingEvent.getFirebaseKey().isEmpty()) {
                    eventsRef.child(existingEvent.getFirebaseKey()).setValue(existingEvent);
                } else {
                    String key = eventsRef.push().getKey();
                    if (key != null) {
                        existingEvent.setFirebaseKey(key);
                        eventsRef.child(key).setValue(existingEvent);
                        AppDatabase.getInstance(this).eventDao().update(existingEvent);
                    }
                }
            }
        } else {
            EventEntity newEvent = new EventEntity(name, date, location, organiser, type, status, description, email, phone);
            AppDatabase.getInstance(this).eventDao().insert(newEvent);

            if (SyncActivity.isNetworkAvailable(this)) {
                String key = eventsRef.push().getKey();
                if (key != null) {
                    newEvent.setFirebaseKey(key);
                    eventsRef.child(key).setValue(newEvent);
                }
            }
        }
        finish();
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
