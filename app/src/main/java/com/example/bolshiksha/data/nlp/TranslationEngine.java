package com.example.bolshiksha.data.nlp;

import android.content.Context;
import android.util.Log;

import com.example.bolshiksha.data.db.BolShikshaDatabase;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.model.TranslationEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationEngine {

    private static final String TAG = "TranslationEngine";
    private static TranslationEngine instance;
    private final Context context;
    private final BolShikshaDatabase db;

    private final Map<String, Map<String, String>> santhaliDict = new HashMap<>();
    private final Map<String, Map<String, String>> hoDict = new HashMap<>();
    private final Map<String, Map<String, String>> mundariDict = new HashMap<>();

    private boolean onnxModelLoaded = false;
    private Object onnxSession = null;

    public interface TranslationCallback {
        void onTranslationComplete(String sourceText, String translatedText, long latencyMs);
        void onError(String error);
    }

    private TranslationEngine(Context context) {
        this.context = context.getApplicationContext();
        this.db = BolShikshaDatabase.getInstance(context);
        initDictionaries();
        loadOnnxModel();
    }

    public static synchronized TranslationEngine getInstance(Context context) {
        if (instance == null) {
            instance = new TranslationEngine(context);
        }
        return instance;
    }

    private void initDictionaries() {
        initSanthaliDictionary();
        initHoDictionary();
        initMundariDictionary();
    }

    private void initSanthaliDictionary() {
        Map<String, String> edu = new HashMap<>();
        edu.put("पाठ", "ᱯᱟᱲ");
        edu.put("पाठशाला", "ᱩᱱᱟᱹᱛ");
        edu.put("शिक्षक", "ᱡᱟᱱᱣᱟᱹᱨ");
        edu.put("छात्र", "ᱪᱷᱟᱹᱞ");
        edu.put("छात्रा", "ᱪᱷᱟᱹᱞᱤ");
        edu.put("किताब", "ᱯᱩᱛᱟᱹᱨ");
        edu.put("लिखना", "ᱞᱮᱠᱟᱹ");
        edu.put("पढ़ना", "ᱚᱱᱟᱹ");
        edu.put("अभ्यास", "ᱡᱷᱟᱹᱨᱩᱲ");
        edu.put("अक्षर", "ᱟᱠᱷᱟᱱ");
        edu.put("शब्द", "ᱢᱩᱱ");
        edu.put("वाक्य", "ᱵᱟᱠᱥ");
        edu.put("संख्या", "ᱢᱮᱱᱟᱹ");
        edu.put("गणना", "ᱜᱟᱴᱱ");
        edu.put("जोड़", "ᱵᱷᱟᱹᱨ");
        edu.put("घटाव", "ᱛᱟᱹᱞᱩ");
        edu.put("गुणा", "ᱜᱩᱱ");
        edu.put("भाग", "ᱚᱯᱷᱤᱡ");
        edu.put("प्रश्न", "ᱯᱨᱚᱫᱮᱥ");
        edu.put("उत्तर", "ᱡᱩᱱᱛᱟᱨ");
        edu.put("समझना", "ᱟᱛᱟᱹ");
        edu.put("बोलना", "ᱪᱷᱟᱹ");
        edu.put("सुनना", "ᱞᱮᱫᱟᱹ");
        edu.put("देखना", "ᱱᱟᱜᱟᱹ");
        santhaliDict.put("education", edu);

        Map<String, String> numbers = new HashMap<>();
        numbers.put("एक", "ᱢᱤᱰ");
        numbers.put("दो", "ᱵᱟᱨ");
        numbers.put("तीन", "ᱯᱮᱨ");
        numbers.put("चार", "ᱯᱚᱱ");
        numbers.put("पाँच", "ᱢᱚᱱᱟ");
        numbers.put("छह", "ᱰᱩᱨᱩᱱ");
        numbers.put("सात", "ᱮᱭᱟᱱ");
        numbers.put("आठ", "ᱤᱨᱟᱹᱞ");
        numbers.put("नौ", "ᱟᱨᱮ");
        numbers.put("दस", "ᱜᱮᱞ");
        santhaliDict.put("numbers", numbers);

        Map<String, String> common = new HashMap<>();
        common.put("नमस्ते", "ᱡᱩᱞᱟᱜ");
        common.put("धन्यवाद", "ᱰᱟᱹᱣᱟᱜ");
        common.put("हाँ", "ᱦᱟᱹᱣ");
        common.put("नहीं", "ᱰᱚᱛᱮ");
        common.put("आप", "ᱟᱢᱮ");
        common.put("मैं", "ᱤᱱ");
        common.put("हम", "ᱟᱞᱟᱵᱟ");
        common.put("आप लोग", "ᱟᱢᱮ ᱟᱨᱟᱵᱟ");
        common.put("वह", "ᱟᱹ");
        common.put("यह", "ᱤᱸᱥ");
        common.put("कब", "ᱛᱷᱮᱱ");
        common.put("कहाँ", "ᱛᱷᱮᱱᱟᱜ");
        common.put("क्या", "ᱛᱷᱮᱱ");
        common.put("कैसे", "ᱛᱷᱮᱱᱟᱠᱚ");
        common.put("कौन", "ᱛᱷᱮᱱᱟᱹ");
        common.put("अच्छा", "ᱡᱷᱟᱹᱫ");
        common.put("बुरा", "ᱥᱟᱯᱟᱞ");
        common.put("बड़ा", "ᱥᱟᱹᱱ");
        common.put("छोटा", "ᱛᱟᱛᱟ");
        common.put("गर्म", "ᱯᱷᱚᱛ");
        common.put("ठंडा", "ᱥᱷᱮᱱ");
        santhaliDict.put("common", common);

        Map<String, String> family = new HashMap<>();
        family.put("माँ", "ᱟᱹᱱᱟ");
        family.put("पिता", "ᱟᱹᱯ");
        family.put("भाई", "ᱰᱷᱟ");
        family.put("बहन", "ᱵᱟᱜᱤ");
        family.put("दोस्त", "ᱢᱮᱛᱟᱹᱨ");
        santhaliDict.put("family", family);
    }

    private void initHoDictionary() {
        Map<String, String> edu = new HashMap<>();
        edu.put("पाठ", "ᱡᱚᱛ");
        edu.put("पाठशाला", "ᱥᱳᱛᱚᱞ");
        edu.put("शिक्षक", "ᱡᱚᱛᱷᱟ");
        edu.put("छात्र", "ᱡᱚᱛᱚᱱ");
        edu.put("किताब", "ᱯᱳᱛᱚ");
        edu.put("लिखना", "ᱞᱮᱠᱷᱚ");
        edu.put("पढ़ना", "ᱚᱱᱟ");
        edu.put("अक्षर", "ᱟᱠᱷᱟ");
        edu.put("शब्द", "ᱢᱩᱱ");
        hoDict.put("education", edu);

        Map<String, String> common = new HashMap<>();
        common.put("नमस्ते", "ᱡᱚᱞᱟᱜ");
        common.put("धन्यवाद", "ᱰᱟᱣᱟᱜ");
        common.put("हाँ", "ᱦᱟᱣ");
        common.put("नहीं", "ᱰᱚᱛᱮ");
        common.put("एक", "ᱢᱤᱰ");
        common.put("दो", "ᱵᱟᱨ");
        common.put("तीन", "ᱯᱮᱨ");
        common.put("चार", "ᱯᱚᱱ");
        common.put("पाँच", "ᱢᱚᱱᱟ");
        hoDict.put("common", common);
    }

    private void initMundariDictionary() {
        Map<String, String> edu = new HashMap<>();
        edu.put("पाठ", "ᱡᱚᱛ");
        edu.put("पाठशाला", "ᱥᱚᱛᱟᱞ");
        edu.put("शिक्षक", "ᱡᱚᱛᱷᱟ");
        edu.put("छात्र", "ᱪᱷᱟᱞ");
        edu.put("किताब", "ᱯᱩᱛᱟᱨ");
        edu.put("लिखना", "ᱞᱮᱠᱟ");
        edu.put("पढ़ना", "ᱚᱱᱟ");
        edu.put("अक्षर", "ᱟᱠᱷᱟ");
        edu.put("शब्द", "ᱢᱩᱱ");
        mundariDict.put("education", edu);

        Map<String, String> common = new HashMap<>();
        common.put("नमस्ते", "ᱡᱩᱞᱟᱜ");
        common.put("धन्यवाद", "ᱰᱟᱣᱟᱜ");
        common.put("हाँ", "ᱦᱟᱣ");
        common.put("नहीं", "ᱰᱚᱛᱮ");
        common.put("एक", "ᱢᱤᱰ");
        common.put("दो", "ᱵᱟᱨ");
        common.put("तीन", "ᱯᱮᱨ");
        mundariDict.put("common", common);
    }

    private void loadOnnxModel() {
        try {
            onnxModelLoaded = false;
            Log.d(TAG, "ONNX model loader initialized. " +
                    "Place model files in assets/models/ for neural translation support.");
        } catch (Exception e) {
            Log.w(TAG, "ONNX model not available, falling back to rule-based translation", e);
        }
    }

    public boolean isOnnxModelLoaded() {
        return onnxModelLoaded;
    }

    public String translate(String sourceText, Language sourceLang, Language targetLang) {
        if (sourceText == null || sourceText.trim().isEmpty()) {
            return "";
        }
        if (sourceLang == targetLang) {
            return sourceText;
        }

        long start = System.currentTimeMillis();
        String normalized = normalizeText(sourceText);
        String result;

        TranslationEntry cached = db.translationDao()
                .findExact(sourceLang.getCode(), targetLang.getCode(), sourceText);
        if (cached != null) {
            result = cached.getTranslatedText();
            if (result != null && !result.trim().isEmpty()) {
                // #region agent log
                com.example.bolshiksha.AgentDebugLog.log("A", "TranslationEngine.java:cache",
                        "cache hit",
                        "{\"src\":\"" + sourceLang.getCode() + "\",\"tgt\":\"" + targetLang.getCode()
                                + "\",\"inLen\":" + sourceText.length()
                                + ",\"outEqualsIn\":" + result.equals(sourceText) + "}");
                // #endregion
                String strippedCached = result.replaceAll("[।.!?\\s]+$", "");
                String strippedNormalized = normalized.replaceAll("[।.!?\\s]+$", "");
                String strippedSource = sourceText.trim().replaceAll("[।.!?\\s]+$", "");
                if (!strippedCached.equals(strippedNormalized)
                        && !strippedCached.equals(strippedSource)) {
                    db.translationDao().incrementUsage(cached.getId());
                    return result;
                }
            }
        }

        Map<String, Map<String, String>> dict = targetLang == Language.HINDI
                ? reverseDictionary(getDictionary(sourceLang))
                : getDictionary(targetLang);
        result = translateRuleBased(normalized, dict, targetLang);

        String strippedResult = result.replaceAll("[।.!?\\s]+$", "");
        String strippedNormalized = normalized.replaceAll("[।.!?\\s]+$", "");
        if (strippedResult.isEmpty() || strippedResult.equals(strippedNormalized)) {
            result = translateRuleBasedFallback(normalized, dict, targetLang);
        }

        // #region agent log
        com.example.bolshiksha.AgentDebugLog.log("A", "TranslationEngine.java:translate",
                "rule-based result",
                "{\"src\":\"" + sourceLang.getCode() + "\",\"tgt\":\"" + targetLang.getCode()
                        + "\",\"dictForTgt\":\"" + targetLang.name()
                        + "\",\"normalizedEqualsResult\":" + normalized.equals(result)
                        + ",\"resultLen\":" + result.length()
                        + ",\"endsWithDanda\":" + result.endsWith("।") + "}");
        // #endregion

        if (!result.isEmpty() && !result.equals(normalized)) {
            TranslationEntry entry = new TranslationEntry(
                    sourceLang.getCode(), targetLang.getCode(), sourceText, result);
            entry.setDomain("education");
            db.translationDao().insert(entry);
        }

        return result;
    }

    public void translateAsync(final String sourceText, final Language sourceLang,
                               final Language targetLang, final TranslationCallback callback) {
        new Thread(() -> {
            try {
                long start = System.currentTimeMillis();
                String result = translate(sourceText, sourceLang, targetLang);
                long latency = System.currentTimeMillis() - start;
                callback.onTranslationComplete(sourceText, result, latency);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private Map<String, Map<String, String>> getDictionary(Language lang) {
        switch (lang) {
            case SANTHALI: return santhaliDict;
            case HO: return hoDict;
            case MUNDARI: return mundariDict;
            default: return santhaliDict;
        }
    }

    private Map<String, Map<String, String>> reverseDictionary(
            Map<String, Map<String, String>> forward) {
        Map<String, String> reverse = new HashMap<>();
        for (Map<String, String> category : forward.values()) {
            for (Map.Entry<String, String> entry : category.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    reverse.put(entry.getValue(), entry.getKey());
                }
            }
        }
        Map<String, Map<String, String>> wrapped = new HashMap<>();
        wrapped.put("reverse", reverse);
        return wrapped;
    }

    private String normalizeText(String text) {
        return text.trim()
                .replaceAll("\\s+", " ")
                .replaceAll("।", ".");
    }

    private String translateRuleBased(String text,
                                       Map<String, Map<String, String>> dictionary,
                                       Language targetLang) {
        String[] sentences = text.split("(?<=[.?!])\\s+");
        StringBuilder result = new StringBuilder();

        for (String sentence : sentences) {
            String translated = translateSentence(sentence, dictionary);
            result.append(translated).append(". ");
        }

        String finalResult = result.toString().trim();
        return applyPostProcessing(finalResult, targetLang);
    }

    private String translateSentence(String sentence, Map<String, Map<String, String>> dict) {
        Map<String, String> flatDict = new HashMap<>();
        for (Map<String, String> category : dict.values()) {
            for (Map.Entry<String, String> entry : category.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                if (key != null && !key.isEmpty() && val != null && !val.isEmpty()) {
                    flatDict.put(key, val);
                }
            }
        }

        List<String> dictKeys = new ArrayList<>(flatDict.keySet());
        dictKeys.sort((a, b) -> b.length() - a.length());

        String result = sentence;
        for (String key : dictKeys) {
            String translation = flatDict.get(key);
            if (translation == null || translation.isEmpty()) continue;
            result = replaceWord(result, key, translation);
        }

        result = result.replaceAll("\\.\\s*\\.", ".");
        result = result.replaceAll("\\s+", " ").trim();
        return result;
    }

    private String replaceWord(String text, String target, String replacement) {
        if (text == null || target == null || target.isEmpty()) return text;

        int tlen = target.length();
        StringBuilder sb = new StringBuilder();
        int idx = 0;

        while (idx <= text.length() - tlen) {
            int pos = text.indexOf(target, idx);
            if (pos < 0) break;

            boolean beforeOk = (pos == 0)
                    || !Character.isLetter(text.codePointBefore(pos))
                    && !Character.isDigit(text.codePointBefore(pos));
            boolean afterOk = (pos + tlen == text.length())
                    || !Character.isLetter(text.codePointAt(pos + tlen))
                    && !Character.isDigit(text.codePointAt(pos + tlen));

            if (beforeOk && afterOk) {
                sb.append(text, idx, pos);
                sb.append(replacement);
                idx = pos + tlen;
            } else {
                sb.append(text, idx, pos + 1);
                idx = pos + 1;
            }
        }

        if (idx < text.length()) {
            sb.append(text, idx, text.length());
        }

        return sb.toString();
    }

    private String translateRuleBasedFallback(String text,
                                               Map<String, Map<String, String>> dictionary,
                                               Language targetLang) {
        Map<String, String> flatDict = new HashMap<>();
        for (Map<String, String> category : dictionary.values()) {
            for (Map.Entry<String, String> entry : category.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                if (key != null && !key.isEmpty() && val != null && !val.isEmpty()) {
                    flatDict.put(key, val);
                }
            }
        }

        List<String> keys = new ArrayList<>(flatDict.keySet());
        keys.sort((a, b) -> b.length() - a.length());

        String result = text;
        for (String key : keys) {
            String val = flatDict.get(key);
            if (val == null || val.isEmpty()) continue;
            result = result.replace(key, val);
        }

        result = result.replaceAll("\\.\\s*\\.", ".");
        result = result.replaceAll("\\s+", " ").trim();
        return applyPostProcessing(result, targetLang);
    }

    private String applyPostProcessing(String text, Language targetLang) {
        if (text.isEmpty()) return text;
        text = text.replaceAll("[.!?]\\s*$", "");
        return text + "।";
    }

    public List<String> getAvailableTargets() {
        List<String> targets = new ArrayList<>();
        for (Language lang : Language.values()) {
            if (lang != Language.HINDI) {
                targets.add(lang.getEnglishName() + " (" + lang.getHindiName() + ")");
            }
        }
        return targets;
    }
}
