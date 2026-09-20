package com.example.bolshiksha.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bolshiksha.data.BolShikshaRepository;
import com.example.bolshiksha.data.nlp.TranslationEngine;
import com.example.bolshiksha.databinding.FragmentSettingsBinding;
import com.example.bolshiksha.ui.auth.AuthSession;
import com.example.bolshiksha.ui.auth.LoginActivity;
import com.example.bolshiksha.ui.voice.VoiceTranslationActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TranslationEngine engine = TranslationEngine.getInstance(requireContext());
        binding.tvEngineStatus.setText(engine.isOnnxModelLoaded()
                ? "✅ तंत्र चालू (ONNX न्यूरल मॉडल)"
                : "✅ तंत्र चालू (नियम+शब्दकोश आधारित)");
        binding.tvOfflineStatus.setText("✅ ऑफ़लाइन मोड सक्रिय - सारा डेटा डिवाइस पर संग्रहीत");
        binding.tvLanguages.setText("🇮🇳 हिन्दी ⇄ Santhali (संताली), Ho (हो), Mundari (मुंडारी)");
        binding.tvNipun.setText("📋 NIPUN भारत FLN मानदंड से संरेखित पाठ्यक्रम और कार्यपत्र");

        binding.btnClearCache.setOnClickListener(v -> {
            new Thread(() -> {
                BolShikshaRepository repo = BolShikshaRepository.getInstance(
                        requireActivity().getApplication());
                repo.getDatabase().translationDao().deleteAll();
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "कैश साफ़ कर दिया गया",
                                    Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        binding.btnTestVoice.setOnClickListener(v ->
                startActivity(new Intent(getContext(), VoiceTranslationActivity.class)));

        binding.tvAppVersion.setText("BOL SHIKSHA v1.0 • Offline MTB-MLE Assist • PALASH Jharkhand");

        binding.tvSignedInName.setText(AuthSession.displayName());
        binding.tvSignedInEmail.setText(AuthSession.email());
        binding.btnSignOut.setOnClickListener(v -> AuthSession.signOut(requireContext(), () -> {
            if (getActivity() == null) {
                return;
            }
            requireActivity().runOnUiThread(() -> {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
