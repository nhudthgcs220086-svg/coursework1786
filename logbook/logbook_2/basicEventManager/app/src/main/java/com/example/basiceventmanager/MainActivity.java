package com.example.basiceventmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basiceventmanager.adapter.EventAdapter;
import com.example.basiceventmanager.data.EventRepository;
import com.example.basiceventmanager.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EventAdapter eventAdapter;
    private RecyclerView rvEvents;
    private TextView tvEmpty;
    private ActivityResultLauncher<Intent> addEventLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvEvents = findViewById(R.id.rvEvents);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAddEvent = findViewById(R.id.fabAddEvent);

        List<Event> eventList = EventRepository.getInstance().getEvents();

        eventAdapter = new EventAdapter(eventList);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);

        updateEmptyState();

        addEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        eventAdapter.setEventList(EventRepository.getInstance().getEvents());
                        updateEmptyState();
                        rvEvents.scrollToPosition(0);
                    }
                }
        );

        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
            addEventLauncher.launch(intent);
        });
    }

    private void updateEmptyState() {
        if (EventRepository.getInstance().getEvents().isEmpty()) {
            rvEvents.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvEvents.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}
