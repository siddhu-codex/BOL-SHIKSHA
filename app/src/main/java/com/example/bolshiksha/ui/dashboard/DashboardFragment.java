package com.example.bolshiksha.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bolshiksha.R;
import com.example.bolshiksha.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupCards();
    }

    private void setupCards() {
        binding.cardTranslation.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboard_to_translation));

        binding.cardVoiceTranslation.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboard_to_voiceTranslation));

        binding.cardCurriculum.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboard_to_curriculum));

        binding.cardWorksheets.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboard_to_worksheets));

        binding.cardFlashcards.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboard_to_flashcards));

        binding.cardSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboard_to_settings));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
