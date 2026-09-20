package com.example.bolshiksha.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bolshiksha.data.model.Flashcard;

import java.util.List;

@Dao
public interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Flashcard flashcard);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Flashcard> flashcards);

    @Update
    void update(Flashcard flashcard);

    @Delete
    void delete(Flashcard flashcard);

    @Query("SELECT * FROM flashcards ORDER BY timestamp DESC")
    LiveData<List<Flashcard>> getAll();

    @Query("SELECT * FROM flashcards WHERE classLevel = :classLevel AND subject = :subject ORDER BY timestamp DESC")
    LiveData<List<Flashcard>> getByClassAndSubject(String classLevel, String subject);

    @Query("SELECT * FROM flashcards WHERE category = :category")
    LiveData<List<Flashcard>> getByCategory(String category);

    @Query("SELECT * FROM flashcards WHERE id = :id")
    Flashcard getById(long id);

    @Query("DELETE FROM flashcards")
    void deleteAll();
}
