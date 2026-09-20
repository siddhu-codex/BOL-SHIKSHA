package com.example.bolshiksha.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bolshiksha.data.model.Worksheet;

import java.util.List;

@Dao
public interface WorksheetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Worksheet worksheet);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Worksheet> worksheets);

    @Update
    void update(Worksheet worksheet);

    @Delete
    void delete(Worksheet worksheet);

    @Query("SELECT * FROM worksheets ORDER BY timestamp DESC")
    LiveData<List<Worksheet>> getAll();

    @Query("SELECT * FROM worksheets WHERE classLevel = :classLevel AND subject = :subject ORDER BY timestamp DESC")
    LiveData<List<Worksheet>> getByClassAndSubject(String classLevel, String subject);

    @Query("SELECT * FROM worksheets WHERE id = :id")
    Worksheet getById(long id);

    @Query("DELETE FROM worksheets")
    void deleteAll();
}
