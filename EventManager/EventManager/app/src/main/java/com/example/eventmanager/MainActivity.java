package com.example.eventmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerViewEvents;
    private TextView tvEmptyEvents;
    private EventAdapter eventAdapter;
    private List<EventEntity> eventList;

    private String currentQuery = "";
    private String filterDate = "";
    private String filterOrganiser = "";
    private static boolean isSyncStarted = false;

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
        if (!isSyncStarted) {
            startRealtimeFirebaseSync();
            isSyncStarted = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void startRealtimeFirebaseSync() {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance("https://event-manager-e0e3d-default-rtdb.firebaseio.com").getReference("events");
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppDatabase db = AppDatabase.getInstance(MainActivity.this);
                boolean updated = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        EventEntity fbEvent = child.getValue(EventEntity.class);
                        if (fbEvent != null && fbEvent.getName() != null) {
                            EventEntity existing = db.eventDao().getEventByNameAndDate(fbEvent.getName(), fbEvent.getDate());
                            if (existing == null) {
                                db.eventDao().insert(fbEvent);
                                updated = true;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Firebase event", e);
                    }
                }
                if (updated) {
                    loadEvents();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase sync cancelled", error.toException());
            }
        });
    }

    private void loadEvents() {
        List<EventEntity> dbEvents;
        AppDatabase db = AppDatabase.getInstance(this);

        if (!TextUtils.isEmpty(filterDate)) {
            dbEvents = db.eventDao().getEventsByDate(filterDate);
        } else if (!TextUtils.isEmpty(filterOrganiser)) {
            dbEvents = db.eventDao().getEventsByOrganiser("%" + filterOrganiser + "%");
        } else if (!TextUtils.isEmpty(currentQuery)) {
            dbEvents = db.eventDao().searchEvents("%" + currentQuery + "%");
        } else {
            dbEvents = db.eventDao().getAllEvents();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    currentQuery = query;
                    loadEvents();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentQuery = newText;
                    loadEvents();
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_advanced_search) {
            showAdvancedSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAdvancedSearchDialog() {
        AlertDialog.Builder database = new AlertDialog.Builder(this);
        database.setTitle(R.string.title_advanced_search);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 32);

        final EditText etFilterDate = new EditText(this);
        etFilterDate.setHint(R.string.filter_by_date);
        etFilterDate.setText(filterDate);
        layout.addView(etFilterDate);

        final EditText etFilterOrganiser = new EditText(this);
        etFilterOrganiser.setHint(R.string.filter_by_organiser);
        etFilterOrganiser.setText(filterOrganiser);
        layout.addView(etFilterOrganiser);

        database.setView(layout);

        database.setPositiveButton(R.string.btn_apply, (dialog, which) -> {
            filterDate = etFilterDate.getText().toString().trim();
            filterOrganiser = etFilterOrganiser.getText().toString().trim();
            loadEvents();
        });

        database.setNeutralButton(R.string.btn_clear, (dialog, which) -> {
            filterDate = "";
            filterOrganiser = "";
            loadEvents();
        });

        database.setNegativeButton(R.string.btn_cancel, null);
        database.show();
    }

    @Override
    public void onEventClick(EventEntity eventEntity) {
        Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
        intent.putExtra(EventDetailActivity.EXTRA_EVENT, eventEntity);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(EventEntity event) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    AppDatabase.getInstance(MainActivity.this).eventDao().delete(event);
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
                    loadEvents();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @Override
    public void onAddSessionClick(EventEntity event) {
        Intent intent = new Intent(MainActivity.this, AddSessionActivity.class);
        intent.putExtra(AddSessionActivity.EXTRA_EVENT_ID, event.getId());
        startActivity(intent);
    }
}
