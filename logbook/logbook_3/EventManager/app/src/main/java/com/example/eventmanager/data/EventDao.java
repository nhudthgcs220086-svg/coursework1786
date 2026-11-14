package com.example.eventmanager.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events ORDER BY id DESC")
    List<EventEntity> getAllEvents();

    @Insert
    void insert(EventEntity event);

    @Update
    void update(EventEntity event);

    @Delete
    void delete(EventEntity event);

    @Query("DELETE FROM events WHERE id = :id")
    void deleteById(long id);
}
