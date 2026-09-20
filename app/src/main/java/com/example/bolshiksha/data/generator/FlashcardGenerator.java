package com.example.bolshiksha.data.generator;

import android.content.Context;

import com.example.bolshiksha.data.model.Flashcard;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.nlp.TranslationEngine;

import java.util.ArrayList;
import java.util.List;

public class FlashcardGenerator {

    private final Context context;
    private final TranslationEngine translationEngine;

    public static class FlashcardConfig {
        public String classLevel = "Class 1";
        public String subject = "हिन्दी";
        public String category = "अक्षर";
        public Language targetLanguage = Language.SANTHALI;
        public int count = 20;
    }

    private static final String[][] HINDI_VARNAKSHARA = {
            {"क", "कक (कंकर)"}, {"ख", "खख (खरगोश)"}, {"ग", "गग (गाय)"},
            {"घ", "घघ (घड़ी)"}, {"ङ", "ङङ (ङकार)"}, {"च", "चच (चम्मच)"},
            {"छ", "छछ (छतरी)"}, {"ज", "जज (जड़ी)"}, {"झ", "झझ (झंडा)"},
            {"ञ", "ञञ (ञकार)"}, {"ट", "टट (टमाटर)"}, {"ठ", "ठठ (ठेकेदार)"},
            {"ड", "डड (डमरू)"}, {"ढ", "ढढ (ढोल)"}, {"ण", "णण (णकार)"},
            {"त", "तत (तम्बू)"}, {"थ", "थथ (थाल)"}, {"द", "दद (दवा)"},
            {"ध", "धध (धनुष)"}, {"न", "नन (नकली)"}, {"प", "पप (पतंग)"},
            {"फ", "फफ (फल)"}, {"ब", "बब (बकरी)"}, {"भ", "भभ (भालू)"},
            {"म", "मम (मछली)"}, {"य", "यय (यमुना)"}, {"र", "रर (रथ)"},
            {"ल", "लल (लड्डू)"}, {"व", "वव (वन)"}, {"श", "शश (शेर)"},
            {"ष", "षष (षटकोण)"}, {"स", "सस (सूरज)"}, {"ह", "हह (हाथ)"}
    };

    private static final String[][] NUMBERS = {
            {"1", "एक (One)"}, {"2", "दो (Two)"}, {"3", "तीन (Three)"},
            {"4", "चार (Four)"}, {"5", "पाँच (Five)"}, {"6", "छह (Six)"},
            {"7", "सात (Seven)"}, {"8", "आठ (Eight)"}, {"9", "नौ (Nine)"},
            {"10", "दस (Ten)"}
    };

    private static final String[][] EVERYDAY_WORDS = {
            {"आम", "Mango (Aam)"}, {"सेब", "Apple (Seb)"}, {"केला", "Banana (Kela)"},
            {"पानी", "Water (Pani)"}, {"दूध", "Milk (Doodh)"}, {"खाना", "Food (Khana)"},
            {"घर", "House (Ghar)"}, {"पाठशाला", "School (Pathshala)"},
            {"बाग़", "Garden (Bagh)"}, {"पेड़", "Tree (Ped)"}, {"फूल", "Flower (Phool)"},
            {"पक्षी", "Bird (Pakshi)"}, {"कुत्ता", "Dog (Kutta)"}, {"बिल्ली", "Cat (Billi)"},
            {"गाय", "Cow (Gai)"}, {"बछड़ा", "Calf (Bachhda)"}, {"बच्चा", "Child (Bachcha)"},
            {"माता", "Mother (Mata)"}, {"पिता", "Father (Pita)"}, {"दोस्त", "Friend (Dost)"}
    };

    private static final String[][] ANIMALS = {
            {"शेर", "Lion (Sher)"}, {"बाघ", "Tiger (Bagh)"}, {"हाथी", "Elephant (Hathi)"},
            {"जिराफ", "Giraffe (Jiraf)"}, {"बंदर", "Monkey (Bandar)"},
            {"खरगोश", "Rabbit (Khargosh)"}, {"कछुआ", "Turtle (Kachhua)"},
            {"मछली", "Fish (Machhli)"}, {"तोता", "Parrot (Tota)"}, {"मोर", "Peacock (Mor)"}
    };

    private static final String[][] COLORS = {
            {"लाल", "Red (Laal)"}, {"नीला", "Blue (Neela)"}, {"हरा", "Green (Hara)"},
            {"पीला", "Yellow (Peela)"}, {"सफ़ेद", "White (Safed)"}, {"काला", "Black (Kala)"},
            {"नारंगी", "Orange (Narangi)"}, {"गुलाबी", "Pink (Gulabi)"}
    };

    private static final String[][] FLN_WORDS = {
            {"पाठ", "Lesson (Paath)"}, {"किताब", "Book (Kitab)"},
            {"शिक्षक", "Teacher (Shikshak)"}, {"छात्र", "Student (Chhatra)"},
            {"अभ्यास", "Practice (Abhyas)"}, {"अक्षर", "Letter (Akshar)"},
            {"शब्द", "Word (Shabd)"}, {"वाक्य", "Sentence (Vakya)"},
            {"संख्या", "Number (Sankhya)"}, {"गणना", "Counting (Ganana)"},
            {"जोड़", "Addition (Jod)"}, {"घटाव", "Subtraction (Ghatav)"},
            {"प्रश्न", "Question (Prashn)"}, {"उत्तर", "Answer (Uttar)"},
            {"समझ", "Understanding (Samajh)"}, {"बोली", "Dialect (Boli)"},
            {"बोलना", "Speak (Bolna)"}, {"पढ़ना", "Read (Padhna)"},
            {"लिखना", "Write (Likhna)"}, {"सुनना", "Listen (Sunna)"}
    };

    public FlashcardGenerator(Context context) {
        this.context = context.getApplicationContext();
        this.translationEngine = TranslationEngine.getInstance(context);
    }

    public List<Flashcard> generateFlashcards(FlashcardConfig config) {
        List<Flashcard> cards = new ArrayList<>();
        String[][] source = resolveSource(config.category);

        int count = Math.min(config.count, source.length);
        for (int i = 0; i < count; i++) {
            Flashcard card = new Flashcard();
            card.setSubject(config.subject);
            card.setClassLevel(config.classLevel);
            card.setCategory(config.category);

            String frontHindi = source[i][0];
            String backHindi = source[i][1];

            card.setFrontHindi(frontHindi);
            card.setBackHindi(backHindi);

            card.setFrontTribal(translationEngine.translate(
                    frontHindi, Language.HINDI, config.targetLanguage));
            card.setBackTribal(translationEngine.translate(
                    backHindi, Language.HINDI, config.targetLanguage));

            card.setPhoneticHindi(frontHindi);
            cards.add(card);
        }

        return cards;
    }

    private String[][] resolveSource(String category) {
        if (category == null) return FLN_WORDS;
        // #region agent log
        boolean matchedDaily = "रोजमर्रा के शब्द".equals(category)
                || "Daily Words".equals(category);
        com.example.bolshiksha.AgentDebugLog.log("B", "FlashcardGenerator.java:resolveSource",
                "category resolve",
                "{\"categoryLen\":" + category.length()
                        + ",\"matchedDailyExact\":" + matchedDaily
                        + ",\"startsRoj\":" + category.startsWith("रोजमर्रा") + "}");
        // #endregion
        switch (category) {
            case "अक्षर":
            case "वर्णमाला":
            case "Letters":
                return HINDI_VARNAKSHARA;
            case "संख्या":
            case "गिनती":
            case "Numbers":
                return NUMBERS;
            case "रोजमर्रा के शब्द":
            case "Daily Words":
                return EVERYDAY_WORDS;
            case "जानवर":
            case "Animals":
                return ANIMALS;
            case "रंग":
            case "Colors":
                return COLORS;
            case "FLN शब्द":
            case "शिक्षा शब्द":
            default:
                return FLN_WORDS;
        }
    }

    public List<String> getAvailableCategories() {
        List<String> cats = new ArrayList<>();
        cats.add("अक्षर (Letters)");
        cats.add("संख्या (Numbers)");
        cats.add("रोजमर्रा के शब्द (Daily Words)");
        cats.add("जानवर (Animals)");
        cats.add("रंग (Colors)");
        cats.add("FLN शब्द (Education Words)");
        return cats;
    }
}
