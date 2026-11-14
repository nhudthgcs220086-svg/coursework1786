package com.example.eventmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.adapter.EventAdapter;
import com.example.eventmanager.data.AppDatabase;
import com.example.eventmanager.data.EventEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerViewEvents;
    private TextView tvEmptyEvents;
    private EventAdapter eventAdapter;
    private List<EventEntity> eventList;

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

        MaterialToolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        tvEmptyEvents = findViewById(R.id.tvEmptyEvents);
        FloatingActionButton fabAddEvent = findViewById(R.id.fabAddEvent);

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvents.setAdapter(eventAdapter);

        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
            startActivity(intent);
        });

        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        List<EventEntity> dbEvents = AppDatabase.getInstance(this).eventDao().getAllEvents();
        eventList.clear();
        if (dbEvents != null) {
            eventList.addAll(dbEvents);
        }
        eventAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (eventList.isEmpty()) {
            recyclerViewEvents.setVisibility(View.GONE);
            tvEmptyEvents.setVisibility(View.VISIBLE);
        } else {
            recyclerViewEvents.setVisibility(View.VISIBLE);
            tvEmptyEvents.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEventClick(EventEntity event) {
        Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
        intent.putExtra(AddEventActivity.EXTRA_EVENT, event);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(EventEntity event) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    AppDatabase.getInstance(MainActivity.this).eventDao().delete(event);
                    loadEvents();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
