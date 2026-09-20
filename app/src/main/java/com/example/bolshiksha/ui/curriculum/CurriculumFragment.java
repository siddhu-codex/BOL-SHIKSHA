package com.example.bolshiksha.ui.curriculum;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bolshiksha.data.BolShikshaRepository;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.model.Lesson;
import com.example.bolshiksha.data.speech.TextToSpeechService;
import com.example.bolshiksha.databinding.FragmentCurriculumBinding;
import com.example.bolshiksha.ui.voice.VoiceTranslationActivity;

import java.util.ArrayList;
import java.util.List;

public class CurriculumFragment extends Fragment {

    private FragmentCurriculumBinding binding;
    private BolShikshaRepository repository;
    private CurriculumAdapter adapter;
    private TextToSpeechService tts;
    private final List<Lesson> lessonList = new ArrayList<>();

    private String selectedClass = "Class 1";
    private String selectedSubject = "हिन्दी";
    private Language targetLang = Language.SANTHALI;
    private LiveData<List<Lesson>> lessonsLiveData;
    private Observer<List<Lesson>> lessonsObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCurriculumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = BolShikshaRepository.getInstance(requireActivity().getApplication());
        tts = new TextToSpeechService(requireContext());

        setupFilters();
        setupRecyclerView();
        setupButtons();
        loadLessons();
    }

    private void setupFilters() {
        String[] classes = {"Class 1", "Class 2", "Class 3"};
        ArrayAdapter<String> clsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, classes);
        clsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerClass.setAdapter(clsAdapter);
        binding.spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedClass = classes[pos];
                loadLessons();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] subjects = {"हिन्दी", "गणित", "पर्यावरण अध्ययन"};
        ArrayAdapter<String> subjAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, subjects);
        subjAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSubject.setAdapter(subjAdapter);
        binding.spinnerSubject.setSelection(0);
        binding.spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedSubject = subjects[pos];
                loadLessons();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] langs = {
                Language.SANTHALI.getEnglishName(),
                Language.HO.getEnglishName(),
                Language.MUNDARI.getEnglishName()
        };
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, langs);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLang.setAdapter(langAdapter);
        binding.spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                switch (pos) {
                    case 0: targetLang = Language.SANTHALI; break;
                    case 1: targetLang = Language.HO; break;
                    case 2: targetLang = Language.MUNDARI; break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new CurriculumAdapter(lessonList, new CurriculumAdapter.LessonCallback() {
            @Override
            public void onSpeakScript(Lesson lesson, boolean tribal) {
                String text = tribal ? lesson.getScriptTribal() : lesson.getScriptHindi();
                String code = tribal ? targetLang.getCode() : Language.HINDI.getCode();
                tts.speak(text, code, false);
            }

            @Override
            public void onStartVoiceDialogue(Lesson lesson) {
                startActivity(new Intent(getContext(), VoiceTranslationActivity.class));
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        binding.btnVoiceMode.setOnClickListener(v ->
                startActivity(new Intent(getContext(), VoiceTranslationActivity.class)));
    }

    private void loadLessons() {
        // #region agent log
        com.example.bolshiksha.AgentDebugLog.log("C",
                "CurriculumFragment.java:loadLessons",
                "new observe attached",
                "{\"classLen\":" + selectedClass.length()
                        + ",\"subjectLen\":" + selectedSubject.length() + "}");
        // #endregion
        if (lessonsLiveData != null && lessonsObserver != null) {
            lessonsLiveData.removeObserver(lessonsObserver);
        }
        lessonsObserver = lessons -> {
                    // #region agent log
                    com.example.bolshiksha.AgentDebugLog.log("C",
                            "CurriculumFragment.java:observer",
                            "observer fired",
                            "{\"selectedClassLen\":" + selectedClass.length()
                                    + ",\"resultCount\":" + (lessons == null ? -1 : lessons.size())
                                    + "}");
                    // #endregion
            lessonList.clear();
            if (lessons != null) {
                lessonList.addAll(lessons);
            }
            adapter.notifyDataSetChanged();
            binding.tvCount.setText("कुल: " + lessonList.size() + " पाठ");
        };
        lessonsLiveData = repository.getLessonsByClassSubject(selectedClass, selectedSubject);
        lessonsLiveData.observe(getViewLifecycleOwner(), lessonsObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tts != null) tts.shutdown();
        binding = null;
    }
}
