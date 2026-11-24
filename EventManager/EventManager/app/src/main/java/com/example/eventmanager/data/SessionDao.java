package com.example.eventmanager.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SessionDao {
    @Query("SELECT * FROM sessions WHERE eventId = :eventId ORDER BY id DESC")
    List<SessionEntity> getSessionsForEvent(long eventId);

    @Query("SELECT * FROM sessions WHERE name LIKE :query OR description LIKE :query OR speaker LIKE :query ORDER BY id DESC")
    List<SessionEntity> searchSessions(String query);

    @Insert
    void insert(SessionEntity session);

    @Update
    void update(SessionEntity session);

    @Delete
    void delete(SessionEntity session);

    @Query("DELETE FROM sessions WHERE eventId = :eventId")
    void deleteSessionsForEvent(long eventId);
}
