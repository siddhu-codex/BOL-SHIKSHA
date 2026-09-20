package com.example.bolshiksha.ui.translation;

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

import com.example.bolshiksha.data.BolShikshaRepository;
import com.example.bolshiksha.data.model.Language;
import com.example.bolshiksha.data.speech.TextToSpeechService;
import com.example.bolshiksha.databinding.FragmentTranslationBinding;

import java.util.ArrayList;
import java.util.List;

public class TranslationFragment extends Fragment {

    private FragmentTranslationBinding binding;
    private BolShikshaRepository repository;
    private TextToSpeechService tts;
    private Language sourceLang = Language.HINDI;
    private Language targetLang = Language.SANTHALI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTranslationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = BolShikshaRepository.getInstance(requireActivity().getApplication());
        tts = new TextToSpeechService(requireContext());

        setupSpinners();
        setupButtons();
    }

    private void setupSpinners() {
        List<String> sourceOptions = new ArrayList<>();
        sourceOptions.add(Language.HINDI.getEnglishName() + " (" + Language.HINDI.getHindiName() + ")");
        ArrayAdapter<String> srcAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, sourceOptions);
        srcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSource.setAdapter(srcAdapter);
        binding.spinnerSource.setSelection(0);

        List<String> targetOptions = new ArrayList<>();
        targetOptions.add(Language.SANTHALI.getEnglishName() + " (" + Language.SANTHALI.getHindiName() + ")");
        targetOptions.add(Language.HO.getEnglishName() + " (" + Language.HO.getHindiName() + ")");
        targetOptions.add(Language.MUNDARI.getEnglishName() + " (" + Language.MUNDARI.getHindiName() + ")");
        ArrayAdapter<String> tgtAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, targetOptions);
        tgtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTarget.setAdapter(tgtAdapter);
        binding.spinnerTarget.setSelection(0);

        binding.spinnerTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: targetLang = Language.SANTHALI; break;
                    case 1: targetLang = Language.HO; break;
                    case 2: targetLang = Language.MUNDARI; break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupButtons() {
        binding.btnTranslate.setOnClickListener(v -> {
            String input = binding.etSource.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(getContext(), "कृपया टेक्स्ट लिखें / Please enter text",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            binding.progressBar.setVisibility(View.VISIBLE);
            long start = System.currentTimeMillis();

            repository.getTranslationEngine().translateAsync(
                    input, sourceLang, targetLang,
                    new com.example.bolshiksha.data.nlp.TranslationEngine.TranslationCallback() {
                        @Override
                        public void onTranslationComplete(String src, String translated, long latencyMs) {
                            if (getActivity() == null) return;
                            requireActivity().runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.tvTranslated.setText(translated);
                                long total = System.currentTimeMillis() - start;
                                binding.tvLatency.setText("⟳ " + total + " ms (Offline)");
                            });
                        }

                        @Override
                        public void onError(String error) {
                            if (getActivity() == null) return;
                            requireActivity().runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "त्रुटि: " + error,
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
        });

        binding.btnClear.setOnClickListener(v -> {
            binding.etSource.setText("");
            binding.tvTranslated.setText("");
            binding.tvLatency.setText("");
        });

        binding.btnSpeakSrc.setOnClickListener(v -> {
            String text = binding.etSource.getText().toString().trim();
            if (!text.isEmpty()) {
                tts.speak(text, sourceLang.getCode(), false);
            }
        });

        binding.btnSpeakTgt.setOnClickListener(v -> {
            String text = binding.tvTranslated.getText().toString().trim();
            if (!text.isEmpty()) {
                tts.speak(text, targetLang.getCode(), false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tts != null) tts.shutdown();
        binding = null;
    }
}
