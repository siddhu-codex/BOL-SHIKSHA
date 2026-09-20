package com.example.bolshiksha.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bolshiksha.data.model.TranslationEntry;

import java.util.List;

@Dao
public interface TranslationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TranslationEntry entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TranslationEntry> entries);

    @Update
    void update(TranslationEntry entry);

    @Delete
    void delete(TranslationEntry entry);

    @Query("SELECT * FROM translation_entries WHERE sourceLang = :src AND targetLang = :tgt AND sourceText = :text LIMIT 1")
    TranslationEntry findExact(String src, String tgt, String text);

    @Query("SELECT * FROM translation_entries WHERE sourceLang = :src AND targetLang = :tgt AND sourceText LIKE '%' || :query || '%' LIMIT 50")
    List<TranslationEntry> search(String src, String tgt, String query);

    @Query("SELECT * FROM translation_entries WHERE sourceLang = :src AND targetLang = :tgt ORDER BY usageCount DESC LIMIT 100")
    LiveData<List<TranslationEntry>> getFrequent(String src, String tgt);

    @Query("SELECT * FROM translation_entries ORDER BY timestamp DESC LIMIT 200")
    LiveData<List<TranslationEntry>> getRecent();

    @Query("UPDATE translation_entries SET usageCount = usageCount + 1 WHERE id = :id")
    void incrementUsage(long id);

    @Query("DELETE FROM translation_entries")
    void deleteAll();
}
