package com.example.bolshiksha.ui.voice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bolshiksha.data.BolShikshaRepository;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.nlp.TranslationEngine;
import com.example.bolshiksha.data.speech.SpeechToTextService;
import com.example.bolshiksha.data.speech.TextToSpeechService;
import com.example.bolshiksha.databinding.ActivityVoiceTranslationBinding;

import java.util.ArrayList;
import java.util.List;

public class VoiceTranslationActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private ActivityVoiceTranslationBinding binding;
    private BolShikshaRepository repository;
    private SpeechToTextService stt;
    private TextToSpeechService tts;

    private Language teacherLang = Language.HINDI;
    private Language studentLang = Language.SANTHALI;

    private boolean isTeacherMode = true;
    private boolean isAutoSpeak = true;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long sessionStart;
    private boolean isConversationActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoiceTranslationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = BolShikshaRepository.getInstance(getApplication());
        stt = new SpeechToTextService(this);
        tts = new TextToSpeechService(this);
        sessionStart = System.currentTimeMillis();

        setupToolbar();
        setupLanguageSpinners();
        setupCallbacks();
        setupControls();
        checkPermissions();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Voice Translation / वाक् अनुवाद");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupLanguageSpinners() {
        List<String> teacherOptions = new ArrayList<>();
        teacherOptions.add(Language.HINDI.getEnglishName() + " (शिक्षक भाषा)");
        ArrayAdapter<String> tAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, teacherOptions);
        tAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTeacher.setAdapter(tAdapter);

        List<String> studentOptions = new ArrayList<>();
        studentOptions.add(Language.SANTHALI.getEnglishName() + " (" + Language.SANTHALI.getHindiName() + ")");
        studentOptions.add(Language.HO.getEnglishName() + " (" + Language.HO.getHindiName() + ")");
        studentOptions.add(Language.MUNDARI.getEnglishName() + " (" + Language.MUNDARI.getHindiName() + ")");
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, studentOptions);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStudent.setAdapter(sAdapter);
        binding.spinnerStudent.setSelection(0);

        binding.spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: studentLang = Language.SANTHALI; break;
                    case 1: studentLang = Language.HO; break;
                    case 2: studentLang = Language.MUNDARI; break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupCallbacks() {
        stt.setCallback(new SpeechToTextService.STTCallback() {
            @Override
            public void onTextRecognized(String text) {
                runOnUiThread(() -> {
                    Language src = isTeacherMode ? teacherLang : studentLang;
                    Language tgt = isTeacherMode ? studentLang : teacherLang;
                    if (isTeacherMode) {
                        binding.tvTeacherText.setText(text);
                    } else {
                        binding.tvStudentText.setText(text);
                    }
                    doTranslate(text, src, tgt);
                });
            }

            @Override
            public void onPartialText(String partialText) {
                runOnUiThread(() -> {
                    if (isTeacherMode) {
                        binding.tvTeacherText.setText(partialText + "…");
                    } else {
                        binding.tvStudentText.setText(partialText + "…");
                    }
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                runOnUiThread(() -> {
                    binding.tvStatus.setText("⚠ " + errorMessage);
                    binding.btnMic.setActivated(false);
                    if (isConversationActive) {
                        scheduleNextListening();
                    }
                });
            }

            @Override
            public void onReadyForSpeech() {
                runOnUiThread(() -> {
                    binding.tvStatus.setText(isTeacherMode
                            ? "🎙 शिक्षक: बोलिए..." : "🎙 छात्र: बोलिए...");
                    binding.progressBar.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onEndOfSpeech() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void setupControls() {
        binding.switchSpeakerMode.setOnCheckedChangeListener((btn, checked) -> {
            isAutoSpeak = checked;
        });

        binding.btnModeTeacher.setOnClickListener(v -> {
            isTeacherMode = true;
            binding.btnModeTeacher.setActivated(true);
            binding.btnModeStudent.setActivated(false);
            binding.tvStatus.setText("शिक्षक मोड सक्रिय");
        });
        binding.btnModeStudent.setOnClickListener(v -> {
            isTeacherMode = false;
            binding.btnModeTeacher.setActivated(false);
            binding.btnModeStudent.setActivated(true);
            binding.tvStatus.setText("छात्र मोड सक्रिय");
        });
        binding.btnModeTeacher.setActivated(true);

        binding.btnMic.setOnClickListener(v -> {
            if (stt.isListening()) {
                stt.stopListening();
                binding.btnMic.setActivated(false);
                binding.tvStatus.setText("रुक गया");
            } else {
                String code = isTeacherMode ? teacherLang.getCode() : studentLang.getCode();
                boolean started = stt.startListening(code);
                if (started) {
                    binding.btnMic.setActivated(true);
                } else {
                    Toast.makeText(this, "स्पीच रिकग्निशन उपलब्ध नहीं", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnConversation.setOnClickListener(v -> {
            isConversationActive = !isConversationActive;
            binding.btnConversation.setActivated(isConversationActive);
            if (isConversationActive) {
                binding.tvStatus.setText("🔄 वार्तालाप मोड चालू");
                startAlternatingListening();
            } else {
                stt.cancel();
                binding.tvStatus.setText("वार्तालाप बंद");
            }
        });

        binding.btnClear.setOnClickListener(v -> {
            binding.tvTeacherText.setText("");
            binding.tvTranslatedTeacher.setText("");
            binding.tvStudentText.setText("");
            binding.tvTranslatedStudent.setText("");
            binding.tvLatency.setText("");
        });

        binding.btnPlayTeacher.setOnClickListener(v -> {
            String t = binding.tvTranslatedTeacher.getText().toString().trim();
            if (!t.isEmpty()) tts.speak(t, studentLang.getCode(), false);
        });
        binding.btnPlayStudent.setOnClickListener(v -> {
            String t = binding.tvTranslatedStudent.getText().toString().trim();
            if (!t.isEmpty()) tts.speak(t, teacherLang.getCode(), false);
        });
    }

    private void startAlternatingListening() {
        String code = isTeacherMode ? teacherLang.getCode() : studentLang.getCode();
        stt.startListening(code);
        binding.btnMic.setActivated(true);
    }

    private void scheduleNextListening() {
        mainHandler.postDelayed(() -> {
            if (isConversationActive) {
                isTeacherMode = !isTeacherMode;
                binding.btnModeTeacher.setActivated(isTeacherMode);
                binding.btnModeStudent.setActivated(!isTeacherMode);
                String code = isTeacherMode ? teacherLang.getCode() : studentLang.getCode();
                stt.startListening(code);
            }
        }, 1500);
    }

    private void doTranslate(String text, Language src, Language tgt) {
        // #region agent log
        com.example.bolshiksha.AgentDebugLog.log("E",
                "VoiceTranslationActivity.java:doTranslate",
                "voice translate langs",
                "{\"teacherMode\":" + isTeacherMode
                        + ",\"src\":\"" + src.getCode() + "\",\"tgt\":\"" + tgt.getCode()
                        + "\",\"textLen\":" + (text == null ? 0 : text.length()) + "}");
        // #endregion
        long start = System.currentTimeMillis();
        repository.getTranslationEngine().translateAsync(
                text, src, tgt, new TranslationEngine.TranslationCallback() {
                    @Override
                    public void onTranslationComplete(String s, String translated, long engineLatency) {
                        runOnUiThread(() -> {
                            long total = System.currentTimeMillis() - start;
                            if (isTeacherMode) {
                                binding.tvTranslatedTeacher.setText(translated);
                                if (isAutoSpeak) {
                                    tts.speak(translated, tgt.getCode(), false);
                                }
                            } else {
                                binding.tvTranslatedStudent.setText(translated);
                                if (isAutoSpeak) {
                                    tts.speak(translated, tgt.getCode(), false);
                                }
                            }
                            binding.tvLatency.setText("⟳ विलंबता: " + total + " ms (लक्ष्य < 3000 ms)");
                            binding.tvStatus.setText("✅ अनुवाद पूर्ण");
                            if (isConversationActive) {
                                scheduleNextListening();
                            } else {
                                binding.btnMic.setActivated(false);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(VoiceTranslationActivity.this,
                                        "अनुवाद त्रुटि: " + error, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "माइक्रोफ़ोन अनुमति आवश्यक है", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isConversationActive = false;
        if (stt != null) stt.destroy();
        if (tts != null) tts.shutdown();
        binding = null;
    }
}
