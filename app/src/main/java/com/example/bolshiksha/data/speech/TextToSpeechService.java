package com.example.bolshiksha.data.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechService implements TextToSpeech.OnInitListener {

    private static final String TAG = "TextToSpeechService";
    private final Context context;
    private TextToSpeech tts;
    private boolean initialized = false;
    private Locale currentLocale;
    private TTSStatusCallback statusCallback;

    public interface TTSStatusCallback {
        void onInitialized(boolean success);
        void onSpeakStart();
        void onSpeakDone();
        void onError(String message);
    }

    public TextToSpeechService(Context context) {
        this.context = context.getApplicationContext();
        tts = new TextToSpeech(this.context, this);
    }

    public void setStatusCallback(TTSStatusCallback callback) {
        this.statusCallback = callback;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            setLanguage("hi-IN");
            tts.setPitch(1.0f);
            tts.setSpeechRate(0.95f);
            if (statusCallback != null) statusCallback.onInitialized(true);
        } else {
            initialized = false;
            Log.e(TAG, "TTS initialization failed with status: " + status);
            if (statusCallback != null) statusCallback.onInitialized(false);
        }
    }

    public boolean setLanguage(String languageCode) {
        if (!initialized || tts == null) return false;

        Locale locale = resolveLocale(languageCode);
        currentLocale = locale;

        int result = tts.setLanguage(locale);
        if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "Language not supported: " + languageCode
                    + ", falling back to Hindi");
            result = tts.setLanguage(new Locale("hi", "IN"));
            return result == TextToSpeech.SUCCESS;
        }
        return true;
    }

    public boolean speak(String text, String languageCode, boolean queueMode) {
        return speak(text, languageCode, queueMode, null);
    }

    public boolean speak(String text, String languageCode, boolean queueMode,
                         final TTSUtteranceListener listener) {
        if (!initialized || tts == null) {
            Log.w(TAG, "TTS not initialized");
            return false;
        }
        if (text == null || text.isEmpty()) {
            return false;
        }

        setLanguage(languageCode);

        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                String.valueOf(System.currentTimeMillis()));
        params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0");

        tts.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                if (listener != null) listener.onStart();
                if (statusCallback != null) statusCallback.onSpeakStart();
            }

            @Override
            public void onDone(String utteranceId) {
                if (listener != null) listener.onDone();
                if (statusCallback != null) statusCallback.onSpeakDone();
            }

            @Override
            public void onError(String utteranceId) {
                if (listener != null) listener.onError("TTS error");
                if (statusCallback != null) statusCallback.onError("TTS playback error");
            }
        });

        int mode = queueMode ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH;
        int result = tts.speak(text, mode, params);
        return result == TextToSpeech.SUCCESS;
    }

    public void stop() {
        if (tts != null && initialized) {
            tts.stop();
        }
    }

    public boolean isSpeaking() {
        return tts != null && tts.isSpeaking();
    }

    public void setPitch(float pitch) {
        if (tts != null && initialized) {
            tts.setPitch(pitch);
        }
    }

    public void setSpeechRate(float rate) {
        if (tts != null && initialized) {
            tts.setSpeechRate(rate);
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private Locale resolveLocale(String code) {
        if (code == null) return new Locale("hi", "IN");
        switch (code) {
            case "hi":
            case "hi-IN":
                return new Locale("hi", "IN");
            case "en":
            case "en-IN":
                return new Locale("en", "IN");
            case "sat":
            case "hoc":
            case "unr":
                return new Locale("hi", "IN");
            default:
                return new Locale(code);
        }
    }

    public interface TTSUtteranceListener {
        void onStart();
        void onDone();
        void onError(String message);
    }
}
