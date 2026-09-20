package com.example.bolshiksha.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bolshiksha.data.model.Flashcard;
import com.example.bolshiksha.data.model.Lesson;
import com.example.bolshiksha.data.model.TranslationEntry;
import com.example.bolshiksha.data.model.Worksheet;

@Database(entities = {
        TranslationEntry.class,
        Lesson.class,
        Worksheet.class,
        Flashcard.class
}, version = 1, exportSchema = false)
public abstract class BolShikshaDatabase extends RoomDatabase {

    public abstract TranslationDao translationDao();
    public abstract LessonDao lessonDao();
    public abstract WorksheetDao worksheetDao();
    public abstract FlashcardDao flashcardDao();

    private static volatile BolShikshaDatabase INSTANCE;

    public static BolShikshaDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BolShikshaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            BolShikshaDatabase.class,
                            "bolshiksha_db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
