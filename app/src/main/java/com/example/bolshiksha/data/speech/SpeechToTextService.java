package com.example.bolshiksha.data.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextService {

    private static final String TAG = "SpeechToText";
    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private STTCallback callback;
    private boolean isListening = false;

    public interface STTCallback {
        void onTextRecognized(String text);
        void onPartialText(String partialText);
        void onError(int errorCode, String errorMessage);
        void onReadyForSpeech();
        void onEndOfSpeech();
    }

    public SpeechToTextService(Context context) {
        this.context = context.getApplicationContext();
        initRecognizer();
    }

    private void initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        } else {
            Log.w(TAG, "Speech recognition not available on this device");
        }
    }

    public void setCallback(STTCallback callback) {
        this.callback = callback;
    }

    public boolean startListening(String languageCode) {
        if (speechRecognizer == null) {
            if (callback != null) {
                callback.onError(-1, "Speech recognition not available");
            }
            return false;
        }

        if (isListening) {
            stopListening();
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, resolveLocale(languageCode));
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                isListening = true;
                if (callback != null) callback.onReadyForSpeech();
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                isListening = false;
                if (callback != null) callback.onEndOfSpeech();
            }

            @Override
            public void onError(int error) {
                isListening = false;
                if (callback != null) {
                    callback.onError(error, getErrorText(error));
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty() && callback != null) {
                    callback.onTextRecognized(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty() && callback != null) {
                    callback.onPartialText(matches.get(0));
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        try {
            speechRecognizer.startListening(intent);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to start listening", e);
            isListening = false;
            return false;
        }
    }

    public void stopListening() {
        if (speechRecognizer != null && isListening) {
            try {
                speechRecognizer.stopListening();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping listening", e);
            }
        }
        isListening = false;
    }

    public void cancel() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.cancel();
            } catch (Exception e) {
                Log.e(TAG, "Error canceling", e);
            }
        }
        isListening = false;
    }

    public boolean isListening() {
        return isListening;
    }

    public void destroy() {
        cancel();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    private String resolveLocale(String code) {
        if (code == null) return "hi-IN";
        switch (code) {
            case "hi":
            case "hi-IN":
                return "hi-IN";
            case "en":
            case "en-IN":
                return "en-IN";
            case "sat":
            case "hoc":
            case "unr":
                return "hi-IN";
            default:
                return "hi-IN";
        }
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error (offline mode may not be available)";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No speech match. Please try speaking again.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognizer busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input detected";
            default:
                return "Unknown error: " + errorCode;
        }
    }
}
