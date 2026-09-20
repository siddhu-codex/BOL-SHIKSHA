package com.example.bolshiksha.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bolshiksha.data.model.Lesson;

import java.util.List;

@Dao
public interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Lesson lesson);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Lesson> lessons);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);

    @Query("SELECT * FROM lessons ORDER BY timestamp DESC")
    LiveData<List<Lesson>> getAll();

    @Query("SELECT * FROM lessons WHERE classLevel = :classLevel AND subject = :subject")
    LiveData<List<Lesson>> getByClassAndSubject(String classLevel, String subject);

    @Query("SELECT * FROM lessons WHERE classLevel = :classLevel")
    LiveData<List<Lesson>> getByClass(String classLevel);

    @Query("SELECT * FROM lessons WHERE isFavorite = 1 ORDER BY timestamp DESC")
    LiveData<List<Lesson>> getFavorites();

    @Query("SELECT * FROM lessons WHERE id = :id")
    Lesson getById(long id);

    @Query("DELETE FROM lessons")
    void deleteAll();
}
