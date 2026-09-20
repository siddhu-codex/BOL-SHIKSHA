package com.example.bolshiksha.data.seed;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bolshiksha.data.db.BolShikshaDatabase;
import com.example.bolshiksha.data.model.Flashcard;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.model.Lesson;
import com.example.bolshiksha.data.model.TranslationEntry;
import com.example.bolshiksha.data.model.Worksheet;
import com.example.bolshiksha.data.generator.FlashcardGenerator;
import com.example.bolshiksha.data.generator.WorksheetGenerator;
import com.example.bolshiksha.data.nlp.TranslationEngine;

import java.util.ArrayList;
import java.util.List;

public class SeedDataLoader {

    private static final String TAG = "SeedDataLoader";
    private static final String PREFS_NAME = "bolshiksha_prefs";
    private static final String KEY_SEED_LOADED = "seed_data_loaded_v1";

    public static void loadInitialData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_SEED_LOADED, false)) {
            Log.d(TAG, "Seed data already loaded, skipping.");
            return;
        }

        Log.d(TAG, "Loading initial seed data...");
        BolShikshaDatabase db = BolShikshaDatabase.getInstance(context);
        TranslationEngine engine = TranslationEngine.getInstance(context);

        try {
            loadTranslations(db, engine);
            loadLessons(db, engine);
            loadFlashcards(context, db);
            loadWorksheets(context, db);
            prefs.edit().putBoolean(KEY_SEED_LOADED, true).apply();
            Log.d(TAG, "Seed data loaded successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error loading seed data", e);
        }
    }

    private static void loadTranslations(BolShikshaDatabase db, TranslationEngine engine) {
        List<TranslationEntry> entries = new ArrayList<>();

        String[][] eduPairs = {
                {"पाठ", "Lesson"}, {"पाठशाला", "School"}, {"शिक्षक", "Teacher"},
                {"छात्र", "Student"}, {"छात्रा", "Girl Student"}, {"किताब", "Book"},
                {"लिखना", "to Write"}, {"पढ़ना", "to Read"}, {"अभ्यास", "Practice"},
                {"अक्षर", "Letter"}, {"शब्द", "Word"}, {"वाक्य", "Sentence"},
                {"संख्या", "Number"}, {"गणना", "Counting"}, {"जोड़", "Addition"},
                {"घटाव", "Subtraction"}, {"गुणा", "Multiplication"}, {"भाग", "Division"},
                {"प्रश्न", "Question"}, {"उत्तर", "Answer"}, {"समझना", "to Understand"},
                {"बोलना", "to Speak"}, {"सुनना", "to Listen"}, {"देखना", "to See"},
                {"नमस्ते", "Hello"}, {"धन्यवाद", "Thank You"}, {"हाँ", "Yes"}, {"नहीं", "No"},
                {"माँ", "Mother"}, {"पिता", "Father"}, {"भाई", "Brother"},
                {"बहन", "Sister"}, {"दोस्त", "Friend"}
        };

        for (String[] pair : eduPairs) {
            for (Language target : new Language[]{Language.SANTHALI, Language.HO, Language.MUNDARI}) {
                TranslationEntry e = new TranslationEntry(
                        Language.HINDI.getCode(), target.getCode(), pair[0],
                        engine.translate(pair[0], Language.HINDI, target));
                e.setDomain("education");
                e.setVerified(true);
                entries.add(e);
            }
        }

        String[][] numbers = {
                {"एक", "1"}, {"दो", "2"}, {"तीन", "3"}, {"चार", "4"}, {"पाँच", "5"},
                {"छह", "6"}, {"सात", "7"}, {"आठ", "8"}, {"नौ", "9"}, {"दस", "10"}
        };
        for (String[] pair : numbers) {
            for (Language target : new Language[]{Language.SANTHALI, Language.HO, Language.MUNDARI}) {
                TranslationEntry e = new TranslationEntry(
                        Language.HINDI.getCode(), target.getCode(), pair[0],
                        engine.translate(pair[0], Language.HINDI, target));
                e.setDomain("numbers");
                e.setVerified(true);
                entries.add(e);
            }
        }

        if (!entries.isEmpty()) {
            db.translationDao().insertAll(entries);
        }
    }

    private static void loadLessons(BolShikshaDatabase db, TranslationEngine engine) {
        List<Lesson> lessons = new ArrayList<>();

        String[][] lessonData = {
                {
                    "Class 1", "हिन्दी", "NIPUN-FLN-L-1.1",
                    "अक्षर परिचय (क से ज्ञ तक)",
                    "आज हम हिन्दी वर्णमाला के अक्षर सीखेंगे। " +
                    "पहले हम क, ख, ग, घ, ङ पढ़ेंगे फिर शेष अक्षर। " +
                    "हर अक्षर को बोलते हुए लिखने का अभ्यास करेंगे।",
                    "1. हर बच्चा अक्षर को देखकर पहचान सके।\n" +
                    "2. अक्षर का उच्चारण सही तरीके से कर सके।\n" +
                    "3. कागज पर अक्षर लिख सके।",
                    "1. आज हमने कौन से अक्षर सीखे?\n" +
                    "2. 'क' से कौन से शब्द बनते हैं? (कमल, कबूतर)\n" +
                    "3. कक्षा में किसी एक विद्यार्थी से 'ग' अक्षर लिखवाएँ।",
                    "क, ख, ग, घ, ङ, वर्णमाला, अक्षर, उच्चारण, लिखना"
                },
                {
                    "Class 1", "गणित", "NIPUN-FLN-N-1.1",
                    "संख्या ज्ञान 1 से 20 तक",
                    "आज हम संख्याएँ 1 से 20 तक सीखेंगे। " +
                    "हर संख्या को अंक और शब्द दोनों रूपों में जानेंगे। " +
                    "वस्तुओं को गिनकर संख्या के बारे में समझेंगे।",
                    "1. छात्र 1 से 20 तक गिनती कर सके।\n" +
                    "2. संख्याओं को अंक और शब्द दोनों में पहचान सके।\n" +
                    "3. कक्षा में दी गई वस्तुओं की संख्या गिन सके।",
                    "1. तीन सेब और दो केलों को मिलाकर कुल कितने फल हुए?\n" +
                    "2. दस में से पाँच घटाने पर क्या शेष बचता है?\n" +
                    "3. किन्हीं पाँच वस्तुओं को गिनकर बताइए।",
                    "संख्या, गिनती, अंक, शब्द, एक, दो, दस, बीस, फल, वस्तुएँ"
                },
                {
                    "Class 2", "हिन्दी", "NIPUN-FLN-L-2.1",
                    "शब्द निर्माण और वाक्य रचना",
                    "आज हम सीखेंगे कि अक्षरों से कैसे शब्द बनते हैं " +
                    "और शब्दों से कैसे वाक्य बनते हैं। छोटे-छोटे शब्दों " +
                    "को जोड़कर नए शब्द और वाक्य बनाने का अभ्यास करेंगे।",
                    "1. दो-तीन अक्षरों के शब्द बना सके।\n" +
                    "2. शब्दों को पढ़कर उनका अर्थ समझ सके।\n" +
                    "3. 3-4 शब्दों का सरल वाक्य बना सके।",
                    "1. नीचे दिए अक्षरों से दो-दो शब्द बनाइए: अ, म, ल, क, त\n" +
                    "2. राम, खाता, है, आम — इन शब्दों से एक वाक्य बनाइए।\n" +
                    "3. अपने पसंदीदा फल का नाम लिखकर उस पर एक वाक्य लिखिए।",
                    "शब्द, वाक्य, अक्षर, निर्माण, रचना, फल, आम, राम"
                }
        };

        for (String[] ld : lessonData) {
            for (Language target : new Language[]{Language.SANTHALI, Language.HO, Language.MUNDARI}) {
                Lesson lesson = new Lesson();
                lesson.setClassLevel(ld[0]);
                lesson.setSubject(ld[1]);
                lesson.setLearningOutcomeCode(ld[2]);
                lesson.setTitleHindi(ld[3]);
                lesson.setTitleTribal(engine.translate(ld[3], Language.HINDI, target));
                lesson.setScriptHindi(ld[4]);
                lesson.setScriptTribal(engine.translate(ld[4], Language.HINDI, target));
                lesson.setActivityInstructionsHindi(ld[5]);
                lesson.setActivityInstructionsTribal(engine.translate(ld[5], Language.HINDI, target));
                lesson.setAssessmentPromptHindi(ld[6]);
                lesson.setAssessmentPromptTribal(engine.translate(ld[6], Language.HINDI, target));
                lesson.setKeywordsHindi(ld[7]);
                lesson.setKeywordsTribal(engine.translate(ld[7], Language.HINDI, target));
                lessons.add(lesson);
            }
        }

        if (!lessons.isEmpty()) {
            db.lessonDao().insertAll(lessons);
        }
    }

    private static void loadFlashcards(Context context, BolShikshaDatabase db) {
        FlashcardGenerator gen = new FlashcardGenerator(context);
        FlashcardGenerator.FlashcardConfig cfg = new FlashcardGenerator.FlashcardConfig();

        for (Language lang : new Language[]{Language.SANTHALI, Language.HO, Language.MUNDARI}) {
            cfg.targetLanguage = lang;

            cfg.classLevel = "Class 1";
            cfg.category = "अक्षर";
            cfg.count = 15;
            List<Flashcard> letters = gen.generateFlashcards(cfg);
            db.flashcardDao().insertAll(letters);

            cfg.category = "संख्या";
            cfg.count = 10;
            List<Flashcard> nums = gen.generateFlashcards(cfg);
            db.flashcardDao().insertAll(nums);

            cfg.category = "FLN शब्द";
            cfg.count = 20;
            List<Flashcard> fln = gen.generateFlashcards(cfg);
            db.flashcardDao().insertAll(fln);
        }
    }

    private static void loadWorksheets(Context context, BolShikshaDatabase db) {
        WorksheetGenerator gen = new WorksheetGenerator(context);
        WorksheetGenerator.WorksheetConfig cfg = new WorksheetGenerator.WorksheetConfig();

        for (Language lang : new Language[]{Language.SANTHALI, Language.HO, Language.MUNDARI}) {
            cfg.targetLanguage = lang;

            cfg.classLevel = "Class 1";
            cfg.subject = "हिन्दी";
            cfg.totalQuestions = 8;
            cfg.questionType = "Mixed";
            Worksheet ws1 = gen.generateWorksheet(cfg);
            db.worksheetDao().insert(ws1);

            cfg.subject = "गणित";
            Worksheet ws2 = gen.generateWorksheet(cfg);
            db.worksheetDao().insert(ws2);

            cfg.classLevel = "Class 2";
            cfg.subject = "हिन्दी";
            Worksheet ws3 = gen.generateWorksheet(cfg);
            db.worksheetDao().insert(ws3);
        }
    }
}
