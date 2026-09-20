package com.example.bolshiksha.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bolshiksha.BolShikshaApp;
import com.example.bolshiksha.data.db.BolShikshaDatabase;
import com.example.bolshiksha.data.db.FlashcardDao;
import com.example.bolshiksha.data.db.LessonDao;
import com.example.bolshiksha.data.db.TranslationDao;
import com.example.bolshiksha.data.db.WorksheetDao;
import com.example.bolshiksha.data.model.Flashcard;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.model.Lesson;
import com.example.bolshiksha.data.model.TranslationEntry;
import com.example.bolshiksha.data.model.Worksheet;
import com.example.bolshiksha.data.nlp.TranslationEngine;
import com.example.bolshiksha.data.speech.TextToSpeechService;
import com.example.bolshiksha.data.speech.SpeechToTextService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BolShikshaRepository {

    private static BolShikshaRepository instance;
    private final BolShikshaDatabase db;
    private final TranslationEngine translationEngine;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private final TranslationDao translationDao;
    private final LessonDao lessonDao;
    private final WorksheetDao worksheetDao;
    private final FlashcardDao flashcardDao;

    private BolShikshaRepository(Application app) {
        this.db = BolShikshaDatabase.getInstance(app);
        this.translationEngine = TranslationEngine.getInstance(app);
        this.translationDao = db.translationDao();
        this.lessonDao = db.lessonDao();
        this.worksheetDao = db.worksheetDao();
        this.flashcardDao = db.flashcardDao();
    }

    public static synchronized BolShikshaRepository getInstance(Application app) {
        if (instance == null) {
            instance = new BolShikshaRepository(app);
        }
        return instance;
    }

    public TranslationEngine getTranslationEngine() {
        return translationEngine;
    }

    public String translate(String text, Language src, Language tgt) {
        return translationEngine.translate(text, src, tgt);
    }

    public LiveData<List<TranslationEntry>> getRecentTranslations() {
        return translationDao.getRecent();
    }

    public void saveTranslation(TranslationEntry entry) {
        executor.execute(() -> translationDao.insert(entry));
    }

    public LiveData<List<Lesson>> getAllLessons() {
        return lessonDao.getAll();
    }

    public LiveData<List<Lesson>> getLessonsByClass(String classLevel) {
        return lessonDao.getByClass(classLevel);
    }

    public LiveData<List<Lesson>> getLessonsByClassSubject(String classLevel, String subject) {
        return lessonDao.getByClassAndSubject(classLevel, subject);
    }

    public LiveData<List<Lesson>> getFavoriteLessons() {
        return lessonDao.getFavorites();
    }

    public void saveLesson(Lesson lesson) {
        executor.execute(() -> lessonDao.insert(lesson));
    }

    public void toggleFavorite(Lesson lesson) {
        executor.execute(() -> {
            lesson.setFavorite(!lesson.isFavorite());
            lessonDao.update(lesson);
        });
    }

    public LiveData<List<Worksheet>> getAllWorksheets() {
        return worksheetDao.getAll();
    }

    public LiveData<List<Worksheet>> getWorksheetsByClassSubject(String classLevel, String subject) {
        return worksheetDao.getByClassAndSubject(classLevel, subject);
    }

    public void saveWorksheet(Worksheet worksheet) {
        executor.execute(() -> worksheetDao.insert(worksheet));
    }

    public LiveData<List<Flashcard>> getAllFlashcards() {
        return flashcardDao.getAll();
    }

    public LiveData<List<Flashcard>> getFlashcardsByClassSubject(String classLevel, String subject) {
        return flashcardDao.getByClassAndSubject(classLevel, subject);
    }

    public LiveData<List<Flashcard>> getFlashcardsByCategory(String category) {
        return flashcardDao.getByCategory(category);
    }

    public void saveFlashcard(Flashcard flashcard) {
        executor.execute(() -> flashcardDao.insert(flashcard));
    }

    public void insertFlashcardsBlocking(List<Flashcard> flashcards) {
        flashcardDao.insertAll(flashcards);
    }

    public void updateFlashcardProgress(Flashcard card, boolean correct) {
        executor.execute(() -> {
            card.setReviewCount(card.getReviewCount() + 1);
            if (correct) {
                card.setCorrectCount(card.getCorrectCount() + 1);
            }
            flashcardDao.update(card);
        });
    }

    public BolShikshaDatabase getDatabase() {
        return db;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
