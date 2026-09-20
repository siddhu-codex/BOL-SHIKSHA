package com.example.bolshiksha.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "flashcards")
public class Flashcard {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String frontHindi;
    private String frontTribal;

    @NonNull
    private String backHindi;
    private String backTribal;

    private String subject;
    private String classLevel;
    private String category;
    private String imageUri;
    private String phoneticHindi;
    private String phoneticTribal;

    private long timestamp;
    private int reviewCount;
    private int correctCount;

    public Flashcard() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getFrontHindi() { return frontHindi; }
    public void setFrontHindi(@NonNull String frontHindi) { this.frontHindi = frontHindi; }

    public String getFrontTribal() { return frontTribal; }
    public void setFrontTribal(String frontTribal) { this.frontTribal = frontTribal; }

    @NonNull
    public String getBackHindi() { return backHindi; }
    public void setBackHindi(@NonNull String backHindi) { this.backHindi = backHindi; }

    public String getBackTribal() { return backTribal; }
    public void setBackTribal(String backTribal) { this.backTribal = backTribal; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getClassLevel() { return classLevel; }
    public void setClassLevel(String classLevel) { this.classLevel = classLevel; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getPhoneticHindi() { return phoneticHindi; }
    public void setPhoneticHindi(String phoneticHindi) { this.phoneticHindi = phoneticHindi; }

    public String getPhoneticTribal() { return phoneticTribal; }
    public void setPhoneticTribal(String phoneticTribal) { this.phoneticTribal = phoneticTribal; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
}
