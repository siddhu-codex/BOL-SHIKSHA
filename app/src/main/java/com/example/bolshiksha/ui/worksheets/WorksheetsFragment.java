package com.example.bolshiksha.ui.worksheets;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bolshiksha.data.BolShikshaRepository;
import com.example.bolshiksha.data.generator.WorksheetGenerator;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.model.Worksheet;
import com.example.bolshiksha.databinding.FragmentWorksheetsBinding;

import java.util.ArrayList;
import java.util.List;

public class WorksheetsFragment extends Fragment {

    private FragmentWorksheetsBinding binding;
    private BolShikshaRepository repository;
    private WorksheetGenerator generator;
    private WorksheetAdapter adapter;
    private final List<Worksheet> worksheetList = new ArrayList<>();

    private String selectedClass = "Class 1";
    private String selectedSubject = "हिन्दी";
    private Language targetLang = Language.SANTHALI;
    private LiveData<List<Worksheet>> worksheetsLiveData;
    private Observer<List<Worksheet>> worksheetsObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorksheetsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = BolShikshaRepository.getInstance(requireActivity().getApplication());
        generator = new WorksheetGenerator(requireContext());

        setupFilters();
        setupRecyclerView();
        setupButtons();
        loadWorksheets();
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
                loadWorksheets();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
                loadWorksheets();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new WorksheetAdapter(worksheetList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        binding.btnGenerate.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            repository.getExecutor().execute(() -> {
                WorksheetGenerator.WorksheetConfig cfg = new WorksheetGenerator.WorksheetConfig();
                cfg.classLevel = selectedClass;
                cfg.subject = selectedSubject;
                cfg.targetLanguage = targetLang;
                cfg.totalQuestions = 10;
                cfg.questionType = "Mixed";
                Worksheet ws = generator.generateWorksheet(cfg);
                repository.saveWorksheet(ws);
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "✅ नया कार्यपत्र बन गया",
                                Toast.LENGTH_SHORT).show();
                        loadWorksheets();
                    });
                }
            });
        });
    }

    private void loadWorksheets() {
        if (worksheetsLiveData != null && worksheetsObserver != null) {
            worksheetsLiveData.removeObserver(worksheetsObserver);
        }
        worksheetsObserver = worksheets -> {
            worksheetList.clear();
            if (worksheets != null) {
                worksheetList.addAll(worksheets);
            }
            adapter.notifyDataSetChanged();
            binding.tvCount.setText("कुल: " + worksheetList.size() + " कार्यपत्र");
        };
        worksheetsLiveData = repository.getWorksheetsByClassSubject(selectedClass, selectedSubject);
        worksheetsLiveData.observe(getViewLifecycleOwner(), worksheetsObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
