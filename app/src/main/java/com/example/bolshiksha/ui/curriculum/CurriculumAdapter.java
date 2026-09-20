package com.example.bolshiksha.ui.curriculum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bolshiksha.R;
import com.example.bolshiksha.data.model.Lesson;

import java.util.List;

public class CurriculumAdapter extends RecyclerView.Adapter<CurriculumAdapter.VH> {

    private final List<Lesson> items;
    private final LessonCallback callback;

    public interface LessonCallback {
        void onSpeakScript(Lesson lesson, boolean tribal);
        void onStartVoiceDialogue(Lesson lesson);
    }

    public CurriculumAdapter(List<Lesson> items, LessonCallback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_curriculum_lesson, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Lesson l = items.get(position);
        h.tvTitleHindi.setText(l.getTitleHindi());
        h.tvTitleTribal.setText(l.getTitleTribal() != null ? l.getTitleTribal() : "");
        h.tvMeta.setText(l.getClassLevel() + " • " + l.getSubject()
                + (l.getLearningOutcomeCode() != null ? " • 📋 " + l.getLearningOutcomeCode() : ""));

        h.tvScriptHindi.setText("📖 " + (l.getScriptHindi() != null ? l.getScriptHindi() : ""));
        h.tvScriptTribal.setText(l.getScriptTribal() != null ? l.getScriptTribal() : "");

        h.tvActivityHindi.setText("🎯 गतिविधियाँ:\n"
                + (l.getActivityInstructionsHindi() != null ? l.getActivityInstructionsHindi() : ""));
        h.tvActivityTribal.setText(l.getActivityInstructionsTribal() != null
                ? l.getActivityInstructionsTribal() : "");

        h.tvAssessmentHindi.setText("✏ आकलन:\n"
                + (l.getAssessmentPromptHindi() != null ? l.getAssessmentPromptHindi() : ""));
        h.tvAssessmentTribal.setText(l.getAssessmentPromptTribal() != null
                ? l.getAssessmentPromptTribal() : "");

        if (l.getKeywordsHindi() != null && !l.getKeywordsHindi().isEmpty()) {
            h.tvKeywords.setVisibility(View.VISIBLE);
            h.tvKeywords.setText("🔑 कीवर्ड: " + l.getKeywordsHindi());
        } else {
            h.tvKeywords.setVisibility(View.GONE);
        }

        h.btnSpeakHindi.setOnClickListener(v -> {
            if (callback != null) callback.onSpeakScript(l, false);
        });
        h.btnSpeakTribal.setOnClickListener(v -> {
            if (callback != null) callback.onSpeakScript(l, true);
        });
        h.btnVoiceDialogue.setOnClickListener(v -> {
            if (callback != null) callback.onStartVoiceDialogue(l);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitleHindi, tvTitleTribal, tvMeta,
                tvScriptHindi, tvScriptTribal,
                tvActivityHindi, tvActivityTribal,
                tvAssessmentHindi, tvAssessmentTribal,
                tvKeywords;
        Button btnSpeakHindi, btnSpeakTribal, btnVoiceDialogue;

        VH(View v) {
            super(v);
            tvTitleHindi = v.findViewById(R.id.tvTitleHindi);
            tvTitleTribal = v.findViewById(R.id.tvTitleTribal);
            tvMeta = v.findViewById(R.id.tvMeta);
            tvScriptHindi = v.findViewById(R.id.tvScriptHindi);
            tvScriptTribal = v.findViewById(R.id.tvScriptTribal);
            tvActivityHindi = v.findViewById(R.id.tvActivityHindi);
            tvActivityTribal = v.findViewById(R.id.tvActivityTribal);
            tvAssessmentHindi = v.findViewById(R.id.tvAssessmentHindi);
            tvAssessmentTribal = v.findViewById(R.id.tvAssessmentTribal);
            tvKeywords = v.findViewById(R.id.tvKeywords);
            btnSpeakHindi = v.findViewById(R.id.btnSpeakHindi);
            btnSpeakTribal = v.findViewById(R.id.btnSpeakTribal);
            btnVoiceDialogue = v.findViewById(R.id.btnVoiceDialogue);
        }
    }
}
