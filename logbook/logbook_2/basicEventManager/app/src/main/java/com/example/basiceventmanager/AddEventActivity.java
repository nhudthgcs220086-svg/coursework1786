package com.example.basiceventmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.basiceventmanager.data.EventRepository;
import com.example.basiceventmanager.model.Event;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private TextInputLayout tilEventName, tilEventDate, tilEventLocation, tilEventOrganizer;
    private TextInputEditText etEventName, etEventDate, etEventLocation, etEventOrganizer;
    private Spinner spinnerEventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppBarAdd);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tilEventName = findViewById(R.id.tilEventName);
        etEventName = findViewById(R.id.etEventName);

        tilEventDate = findViewById(R.id.tilEventDate);
        etEventDate = findViewById(R.id.etEventDate);

        tilEventLocation = findViewById(R.id.tilEventLocation);
        etEventLocation = findViewById(R.id.etEventLocation);

        tilEventOrganizer = findViewById(R.id.tilEventOrganizer);
        etEventOrganizer = findViewById(R.id.etEventOrganizer);

        spinnerEventType = findViewById(R.id.spinnerEventType);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        // Setup Event Type Spinner
        String[] eventTypes = {
                getString(R.string.type_conference),
                getString(R.string.type_workshop),
                getString(R.string.type_seminar),
                getString(R.string.type_social),
                getString(R.string.type_other)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(adapter);

        // Date Picker setup (DD/MM/YYYY)
        etEventDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                etEventDate.setText(formattedDate);
                tilEventDate.setError(null);
            }, year, month, day);
            datePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> validateAndSaveEvent());
    }

    private void validateAndSaveEvent() {
        String name = etEventName.getText() != null ? etEventName.getText().toString().trim() : "";
        String date = etEventDate.getText() != null ? etEventDate.getText().toString().trim() : "";
        String location = etEventLocation.getText() != null ? etEventLocation.getText().toString().trim() : "";
        String organizer = etEventOrganizer.getText() != null ? etEventOrganizer.getText().toString().trim() : "";
        String type = spinnerEventType.getSelectedItem() != null ? spinnerEventType.getSelectedItem().toString() : "";

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            tilEventName.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            tilEventName.setError(null);
        }

        if (TextUtils.isEmpty(date)) {
            tilEventDate.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!isValidDate(date)) {
            tilEventDate.setError(getString(R.string.error_invalid_date));
            isValid = false;
        } else {
            tilEventDate.setError(null);
        }

        if (TextUtils.isEmpty(location)) {
            tilEventLocation.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            tilEventLocation.setError(null);
        }

        if (TextUtils.isEmpty(organizer)) {
            tilEventOrganizer.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            tilEventOrganizer.setError(null);
        }

        if (isValid) {
            Event newEvent = new Event(name, date, location, organizer, type);
            EventRepository.getInstance().addEvent(newEvent);
            Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
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
