package com.example.bolshiksha.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lessons")
public class Lesson {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String titleHindi;
    private String titleTribal;

    @NonNull
    private String subject;

    @NonNull
    private String classLevel;

    private String learningOutcomeCode;

    @NonNull
    private String scriptHindi;
    private String scriptTribal;

    private String activityInstructionsHindi;
    private String activityInstructionsTribal;

    private String assessmentPromptHindi;
    private String assessmentPromptTribal;

    private String keywordsHindi;
    private String keywordsTribal;

    private long timestamp;
    private boolean isFavorite;

    public Lesson() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getTitleHindi() { return titleHindi; }
    public void setTitleHindi(@NonNull String titleHindi) { this.titleHindi = titleHindi; }

    public String getTitleTribal() { return titleTribal; }
    public void setTitleTribal(String titleTribal) { this.titleTribal = titleTribal; }

    @NonNull
    public String getSubject() { return subject; }
    public void setSubject(@NonNull String subject) { this.subject = subject; }

    @NonNull
    public String getClassLevel() { return classLevel; }
    public void setClassLevel(@NonNull String classLevel) { this.classLevel = classLevel; }

    public String getLearningOutcomeCode() { return learningOutcomeCode; }
    public void setLearningOutcomeCode(String learningOutcomeCode) { this.learningOutcomeCode = learningOutcomeCode; }

    @NonNull
    public String getScriptHindi() { return scriptHindi; }
    public void setScriptHindi(@NonNull String scriptHindi) { this.scriptHindi = scriptHindi; }

    public String getScriptTribal() { return scriptTribal; }
    public void setScriptTribal(String scriptTribal) { this.scriptTribal = scriptTribal; }

    public String getActivityInstructionsHindi() { return activityInstructionsHindi; }
    public void setActivityInstructionsHindi(String activityInstructionsHindi) { this.activityInstructionsHindi = activityInstructionsHindi; }

    public String getActivityInstructionsTribal() { return activityInstructionsTribal; }
    public void setActivityInstructionsTribal(String activityInstructionsTribal) { this.activityInstructionsTribal = activityInstructionsTribal; }

    public String getAssessmentPromptHindi() { return assessmentPromptHindi; }
    public void setAssessmentPromptHindi(String assessmentPromptHindi) { this.assessmentPromptHindi = assessmentPromptHindi; }

    public String getAssessmentPromptTribal() { return assessmentPromptTribal; }
    public void setAssessmentPromptTribal(String assessmentPromptTribal) { this.assessmentPromptTribal = assessmentPromptTribal; }

    public String getKeywordsHindi() { return keywordsHindi; }
    public void setKeywordsHindi(String keywordsHindi) { this.keywordsHindi = keywordsHindi; }

    public String getKeywordsTribal() { return keywordsTribal; }
    public void setKeywordsTribal(String keywordsTribal) { this.keywordsTribal = keywordsTribal; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
