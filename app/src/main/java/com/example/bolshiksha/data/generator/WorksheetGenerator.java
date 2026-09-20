package com.example.bolshiksha.data.generator;

import android.content.Context;

import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.model.Worksheet;
import com.example.bolshiksha.data.nlp.TranslationEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorksheetGenerator {

    private final Context context;
    private final TranslationEngine translationEngine;
    private final Random random = new Random();

    private static final String[] NIPUN_CODES_LITERACY = {
            "NIPUN-FLN-L-1.1", "NIPUN-FLN-L-1.2", "NIPUN-FLN-L-2.1",
            "NIPUN-FLN-L-2.2", "NIPUN-FLN-L-3.1", "NIPUN-FLN-L-3.2"
    };

    private static final String[] NIPUN_CODES_NUMERACY = {
            "NIPUN-FLN-N-1.1", "NIPUN-FLN-N-1.2", "NIPUN-FLN-N-2.1",
            "NIPUN-FLN-N-2.2", "NIPUN-FLN-N-3.1"
    };

    public static class WorksheetConfig {
        public String classLevel = "Class 1";
        public String subject = "हिन्दी";
        public int totalQuestions = 10;
        public String questionType = "Mixed";
        public Language targetLanguage = Language.SANTHALI;
    }

    public WorksheetGenerator(Context context) {
        this.context = context.getApplicationContext();
        this.translationEngine = TranslationEngine.getInstance(context);
    }

    public Worksheet generateWorksheet(WorksheetConfig config) {
        Worksheet worksheet = new Worksheet();
        worksheet.setSubject(config.subject);
        worksheet.setClassLevel(config.classLevel);
        worksheet.setTotalQuestions(config.totalQuestions);
        worksheet.setQuestionType(config.questionType);
        worksheet.setDurationMinutes(30);

        boolean isMath = config.subject.contains("गणित") || config.subject.equalsIgnoreCase("Maths");
        String[] nipunCodes = isMath ? NIPUN_CODES_NUMERACY : NIPUN_CODES_LITERACY;
        worksheet.setLearningOutcomeCode(nipunCodes[random.nextInt(nipunCodes.length)]);

        String titleHindi = buildTitleHindi(config, isMath);
        worksheet.setTitleHindi(titleHindi);
        worksheet.setTitleTribal(translationEngine.translate(
                titleHindi, Language.HINDI, config.targetLanguage));

        String contentHindi = buildContentHindi(config, isMath);
        worksheet.setContentHindi(contentHindi);
        worksheet.setContentTribal(translationEngine.translate(
                contentHindi, Language.HINDI, config.targetLanguage));

        StringBuilder qHindi = new StringBuilder();
        StringBuilder qTribal = new StringBuilder();
        StringBuilder aHindi = new StringBuilder();
        StringBuilder aTribal = new StringBuilder();

        for (int i = 1; i <= config.totalQuestions; i++) {
            String questionHindi;
            String answerHindi;

            if (isMath) {
                String[] qa = generateMathQuestion(i, config.classLevel);
                questionHindi = qa[0];
                answerHindi = qa[1];
            } else {
                String[] qa = generateLiteracyQuestion(i, config.classLevel);
                questionHindi = qa[0];
                answerHindi = qa[1];
            }

            qHindi.append(i).append(". ").append(questionHindi).append("\n\n");
            qTribal.append(i).append(". ")
                    .append(translationEngine.translate(questionHindi,
                            Language.HINDI, config.targetLanguage))
                    .append("\n\n");
            aHindi.append(i).append(". ").append(answerHindi).append("\n");
            aTribal.append(i).append(". ")
                    .append(translationEngine.translate(answerHindi,
                            Language.HINDI, config.targetLanguage))
                    .append("\n");
        }

        worksheet.setQuestionsHindi(qHindi.toString().trim());
        worksheet.setQuestionsTribal(qTribal.toString().trim());
        worksheet.setAnswerKeyHindi(aHindi.toString().trim());
        worksheet.setAnswerKeyTribal(aTribal.toString().trim());

        return worksheet;
    }

    private String buildTitleHindi(WorksheetConfig config, boolean isMath) {
        String typeLabel;
        switch (config.questionType) {
            case "MCQ": typeLabel = "बहुविकल्पीय प्रश्न"; break;
            case "FillBlank": typeLabel = "रिक्त स्थान भरिए"; break;
            case "Matching": typeLabel = "सुमेलन प्रश्न"; break;
            case "ShortAnswer": typeLabel = "लघु उत्तरीय प्रश्न"; break;
            default: typeLabel = "अभ्यास कार्य";
        }
        String topic = isMath ? "संख्या और गणना" : "पठन और शब्दावली";
        return config.classLevel + " - " + config.subject + ": " + topic + " (" + typeLabel + ")";
    }

    private String buildContentHindi(WorksheetConfig config, boolean isMath) {
        String instruction = "निर्देश: सभी प्रश्नों को ध्यान से पढ़ें और उत्तर लिखें। ";
        String help = "यदि किसी प्रश्न का अर्थ समझ न आए तो शिक्षक से पूछें। कक्षा में शांतिपूर्वक कार्य करें।";
        String topic = isMath
                ? "इस कार्यपत्र में संख्या ज्ञान, गिनती, जोड़ और घटाव से संबंधित प्रश्न हैं।"
                : "इस कार्यपत्र में अक्षर पहचान, शब्द निर्माण, पठन और समझ से संबंधित प्रश्न हैं।";
        return instruction + topic + "\n" + help;
    }

    private String[] generateLiteracyQuestion(int qNum, String classLevel) {
        String q;
        String a;
        int pick = (qNum + random.nextInt(4)) % 6;
        switch (pick) {
            case 0:
                q = "निम्नलिखित अक्षरों को पहचानकर उनके नाम लिखिए: क, ख, ग, घ, ङ";
                a = "क (कक), ख (खख), ग (गग), घ (घघ), ङ (ङङ)";
                break;
            case 1:
                q = "दिए गए शब्दों को पढ़कर उनका हिन्दी में अर्थ लिखिए: पाठ, किताब, शिक्षक, छात्र";
                a = "पाठ = lesson (पाठ), किताब = book (किताब), शिक्षक = teacher (गुरु), छात्र = student (विद्यार्थी)";
                break;
            case 2:
                q = "रिक्त स्थान भरिए: \"बच्चे पाठशाला में ______ पढ़ते हैं।\" (शब्द: किताब, पानी)"
;
                a = "किताब";
                break;
            case 3:
                q = "निम्नलिखित में से संज्ञा शब्द छाँटकर लिखिए: राम, तेज, दौड़ा, पाठशाला, खाना";
                a = "संज्ञा शब्द: राम, पाठशाला, खाना";
                break;
            case 4:
                q = "दिए गए वाक्य को पढ़कर उसका सारांश अपने शब्दों में लिखिए: \"सुबह सूरज उगता है। पक्षी गाना गाते हैं। बच्चे पाठशाला जाते हैं।\"";
                a = "सुबह के समय सूरज निकलता है और पक्षी गाने गाते हैं। इस समय बच्चे पाठशाला के लिए निकलते हैं।";
                break;
            default:
                q = "दिए गए वर्णमाला का क्रम सही कीजिए: घ, ख, क, ङ, ग";
                a = "क, ख, ग, घ, ङ";
        }
        return new String[]{q, a};
    }

    private String[] generateMathQuestion(int qNum, String classLevel) {
        String q;
        String a;
        int pick = (qNum + random.nextInt(4)) % 6;
        int aNum = random.nextInt(20) + 1;
        int bNum = random.nextInt(aNum) + 1;

        switch (pick) {
            case 0:
                q = "गिनती लिखिए: 1 से 20 तक (अंक और शब्द दोनों में)।";
                a = "1-एक, 2-दो, 3-तीन, 4-चार, 5-पाँच, 6-छह, 7-सात, 8-आठ, 9-नौ, 10-दस, 11-ग्यारह, 12-बारह, 13-तेरह, 14-चौदह, 15-पंद्रह, 16-सोलह, 17-सत्रह, 18-अठारह, 19-उन्नीस, 20-बीस";
                break;
            case 1:
                q = "संख्या " + aNum + " और " + bNum + " का योग ज्ञात कीजिए और शब्दों में भी लिखिए।";
                int sum = aNum + bNum;
                a = aNum + " + " + bNum + " = " + sum + " (शब्दों में: " + numberToHindi(sum) + ")";
                break;
            case 2:
                int sub = Math.max(aNum, bNum);
                int subB = Math.min(aNum, bNum);
                q = "संख्या " + sub + " में से " + subB + " घटाइए और उत्तर शब्दों में लिखिए।";
                int diff = sub - subB;
                a = sub + " - " + subB + " = " + diff + " (शब्दों में: " + numberToHindi(diff) + ")";
                break;
            case 3:
                q = "रिक्त स्थान भरिए: " + aNum + " + ____ = " + (aNum + bNum);
                a = String.valueOf(bNum);
                break;
            case 4:
                q = "संख्याओं को आरोही क्रम में व्यवस्थित कीजिए: 8, 3, 15, 1, 20, 7";
                a = "1, 3, 7, 8, 15, 20";
                break;
            default:
                q = "राम के पास " + aNum + " सेब हैं। उसने " + bNum + " सेब अपनी बहन को दिए। अब राम के पास कितने सेब बचे हैं?";
                int left = aNum - bNum;
                a = aNum + " - " + bNum + " = " + left + " सेब";
        }
        return new String[]{q, a};
    }

    public List<Worksheet> generateMultiple(int count, WorksheetConfig config) {
        List<Worksheet> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(generateWorksheet(config));
        }
        return list;
    }

    private static String numberToHindi(int n) {
        String[] ones = {"", "एक", "दो", "तीन", "चार", "पाँच", "छह", "सात", "आठ", "नौ"};
        String[] tens = {"", "दस", "ग्यारह", "बारह", "तेरह", "चौदह", "पंद्रह", "सोलह", "सत्रह", "अठारह", "उन्नीस"};
        String[] tens2 = {"", "", "बीस", "तीस", "चालीस", "पचास", "साठ", "सत्तर", "अस्सी", "नब्बे", "सौ"};
        if (n <= 0) return "शून्य";
        if (n < 10) return ones[n];
        if (n < 20) return tens[n - 9];
        if (n == 100) return tens2[10];
        int t = n / 10;
        int o = n % 10;
        return (t < tens2.length ? tens2[t] : "") + (o > 0 ? " " + ones[o] : "");
    }
}
