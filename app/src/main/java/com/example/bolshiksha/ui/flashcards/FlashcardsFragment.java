package com.example.bolshiksha.ui.flashcards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.bolshiksha.data.BolShikshaRepository;
import com.example.bolshiksha.data.generator.FlashcardGenerator;
import com.example.bolshiksha.data.model.Flashcard;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.speech.TextToSpeechService;
import com.example.bolshiksha.databinding.FragmentFlashcardsBinding;

import java.util.ArrayList;
import java.util.List;

public class FlashcardsFragment extends Fragment {

    private FragmentFlashcardsBinding binding;
    private BolShikshaRepository repository;
    private FlashcardGenerator generator;
    private FlashcardAdapter adapter;
    private TextToSpeechService tts;
    private final List<Flashcard> cardList = new ArrayList<>();

    private String selectedClass = "Class 1";
    private String selectedSubject = "हिन्दी";
    private Language targetLang = Language.SANTHALI;
    private String selectedCategory = "FLN शब्द";
    private LiveData<List<Flashcard>> flashcardsLiveData;
    private Observer<List<Flashcard>> flashcardsObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFlashcardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = BolShikshaRepository.getInstance(requireActivity().getApplication());
        generator = new FlashcardGenerator(requireContext());
        tts = new TextToSpeechService(requireContext());

        setupFilters();
        setupRecyclerView();
        setupButtons();
        loadFlashcards();
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
                loadFlashcards();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        List<String> categories = generator.getAvailableCategories();
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(catAdapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                String fullLabel = categories.get(pos);
                selectedCategory = fullLabel.split(" \\(")[0];
                // #region agent log
                com.example.bolshiksha.AgentDebugLog.log("B",
                        "FlashcardsFragment.java:category",
                        "category truncated",
                        "{\"pos\":" + pos
                                + ",\"fullLen\":" + fullLabel.length()
                                + ",\"truncatedLen\":" + selectedCategory.length()
                                + ",\"truncatedEqFull\":" + selectedCategory.equals(fullLabel) + "}");
                // #endregion
                loadFlashcards();
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
        adapter = new FlashcardAdapter(cardList, new FlashcardAdapter.CardCallback() {
            @Override
            public void onSpeak(Flashcard card, boolean tribal) {
                String text = tribal ? card.getFrontTribal() : card.getFrontHindi();
                String code = tribal ? targetLang.getCode() : Language.HINDI.getCode();
                tts.speak(text, code, false);
            }

            @Override
            public void onFlip(Flashcard card) {}
        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        binding.btnGenerate.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            repository.getExecutor().execute(() -> {
                FlashcardGenerator.FlashcardConfig cfg =
                        new FlashcardGenerator.FlashcardConfig();
                cfg.classLevel = selectedClass;
                cfg.subject = selectedSubject;
                cfg.category = selectedCategory;
                cfg.targetLanguage = targetLang;
                cfg.count = 15;
                List<Flashcard> cards = generator.generateFlashcards(cfg);
                repository.insertFlashcardsBlocking(cards);
                // #region agent log
                com.example.bolshiksha.AgentDebugLog.log("D",
                        "FlashcardsFragment.java:generate",
                        "generated without loadFlashcards",
                        "{\"count\":" + cards.size()
                                + ",\"categoryLen\":" + selectedCategory.length()
                                + ",\"firstFrontHindiLen\":"
                                + (cards.isEmpty() ? 0 : cards.get(0).getFrontHindi().length())
                                + "}");
                // #endregion
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "✅ " + cards.size()
                                + " फ्लैशकार्ड बन गए", Toast.LENGTH_SHORT).show();
                        loadFlashcards();
                    });
                }
            });
        });
    }

    private void loadFlashcards() {
        if (flashcardsLiveData != null && flashcardsObserver != null) {
            flashcardsLiveData.removeObserver(flashcardsObserver);
        }
        flashcardsObserver = cards -> {
            cardList.clear();
            if (cards != null) {
                cardList.addAll(cards);
            }
            adapter.notifyDataSetChanged();
            binding.tvCount.setText("कुल: " + cardList.size() + " कार्ड");
        };
        flashcardsLiveData = repository.getFlashcardsByClassSubject(selectedClass, selectedSubject);
        flashcardsLiveData.observe(getViewLifecycleOwner(), flashcardsObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tts != null) tts.shutdown();
        binding = null;
    }
}
