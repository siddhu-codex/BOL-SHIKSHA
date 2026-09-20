package com.example.bolshiksha.ui.flashcards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bolshiksha.R;
import com.example.bolshiksha.data.model.Flashcard;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.VH> {

    private final List<Flashcard> items;
    private final CardCallback callback;

    public interface CardCallback {
        void onSpeak(Flashcard card, boolean tribal);
        void onFlip(Flashcard card);
    }

    public FlashcardAdapter(List<Flashcard> items, CardCallback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flashcard, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Flashcard c = items.get(position);
        h.tvFrontHindi.setText(c.getFrontHindi());
        h.tvFrontTribal.setText(c.getFrontTribal() != null ? c.getFrontTribal() : "");
        h.tvBackHindi.setText(c.getBackHindi());
        h.tvBackTribal.setText(c.getBackTribal() != null ? c.getBackTribal() : "");
        h.tvCategory.setText(c.getCategory() != null ? c.getCategory() : "");

        h.cardFront.setVisibility(View.VISIBLE);
        h.cardBack.setVisibility(View.GONE);

        View.OnClickListener flipper = v -> {
            boolean showFront = h.cardFront.getVisibility() == View.VISIBLE;
            ObjectAnimator out = ObjectAnimator.ofFloat(
                    showFront ? h.cardFront : h.cardBack, "rotationY", 0f, 90f);
            out.setDuration(150);
            out.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (showFront) {
                        h.cardFront.setVisibility(View.GONE);
                        h.cardBack.setVisibility(View.VISIBLE);
                    } else {
                        h.cardBack.setVisibility(View.GONE);
                        h.cardFront.setVisibility(View.VISIBLE);
                    }
                    ObjectAnimator in = ObjectAnimator.ofFloat(
                            showFront ? h.cardBack : h.cardFront, "rotationY", -90f, 0f);
                    in.setDuration(150);
                    in.start();
                }
            });
            out.start();
            if (callback != null) callback.onFlip(c);
        };

        h.cardFront.setOnClickListener(flipper);
        h.cardBack.setOnClickListener(flipper);

        h.btnSpeakHindi.setOnClickListener(v -> {
            if (callback != null) callback.onSpeak(c, false);
        });
        h.btnSpeakTribal.setOnClickListener(v -> {
            if (callback != null) callback.onSpeak(c, true);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        View cardFront, cardBack;
        TextView tvFrontHindi, tvFrontTribal, tvBackHindi, tvBackTribal, tvCategory;
        ImageView btnSpeakHindi, btnSpeakTribal;

        VH(View v) {
            super(v);
            cardFront = v.findViewById(R.id.cardFront);
            cardBack = v.findViewById(R.id.cardBack);
            tvFrontHindi = v.findViewById(R.id.tvFrontHindi);
            tvFrontTribal = v.findViewById(R.id.tvFrontTribal);
            tvBackHindi = v.findViewById(R.id.tvBackHindi);
            tvBackTribal = v.findViewById(R.id.tvBackTribal);
            tvCategory = v.findViewById(R.id.tvCategory);
            btnSpeakHindi = v.findViewById(R.id.btnSpeakHindi);
            btnSpeakTribal = v.findViewById(R.id.btnSpeakTribal);
        }
    }
}
