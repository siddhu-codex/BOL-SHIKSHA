package com.example.bolshiksha.ui.worksheets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bolshiksha.R;
import com.example.bolshiksha.data.model.Worksheet;

import java.util.List;

public class WorksheetAdapter extends RecyclerView.Adapter<WorksheetAdapter.VH> {

    private final List<Worksheet> items;

    public WorksheetAdapter(List<Worksheet> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_worksheet, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Worksheet ws = items.get(position);
        h.tvTitle.setText(ws.getTitleHindi());
        h.tvTitleTribal.setText(ws.getTitleTribal() != null ? ws.getTitleTribal() : "");
        h.tvMeta.setText(ws.getClassLevel() + " • " + ws.getSubject() + " • "
                + ws.getTotalQuestions() + " प्रश्न • " + ws.getDurationMinutes() + " मिनट");
        h.tvContent.setText(ws.getQuestionsHindi());
        if (ws.getLearningOutcomeCode() != null) {
            h.tvNipun.setText("📋 " + ws.getLearningOutcomeCode());
        } else {
            h.tvNipun.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTitleTribal, tvMeta, tvContent, tvNipun;
        VH(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvTitleTribal = v.findViewById(R.id.tvTitleTribal);
            tvMeta = v.findViewById(R.id.tvMeta);
            tvContent = v.findViewById(R.id.tvContent);
            tvNipun = v.findViewById(R.id.tvNipun);
        }
    }
}
