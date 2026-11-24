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

    @Query("SELECT * FROM events WHERE id = :id")
    EventEntity getEventById(long id);

    @Query("SELECT * FROM events WHERE name = :name AND date = :date")
    EventEntity getEventByNameAndDate(String name, String date);

    @Query("SELECT * FROM events WHERE name LIKE :query OR description LIKE :query OR organiser LIKE :query OR date LIKE :query ORDER BY id DESC")
    List<EventEntity> searchEvents(String query);

    @Query("SELECT * FROM events WHERE date = :date ORDER BY id DESC")
    List<EventEntity> getEventsByDate(String date);

    @Query("SELECT * FROM events WHERE organiser LIKE :organiser ORDER BY id DESC")
    List<EventEntity> getEventsByOrganiser(String organiser);

    @Insert
    void insert(EventEntity event);

    @Update
    void update(EventEntity event);

    @Delete
    void delete(EventEntity event);

    @Query("DELETE FROM events WHERE id = :id")
    void deleteById(long id);
}
