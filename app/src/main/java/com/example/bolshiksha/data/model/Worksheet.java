package com.example.bolshiksha.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "worksheets")
public class Worksheet {
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
    private String contentHindi;
    private String contentTribal;

    private String questionsHindi;
    private String questionsTribal;

    private String answerKeyHindi;
    private String answerKeyTribal;

    private String questionType;
    private int totalQuestions;
    private int durationMinutes;
    private long timestamp;

    public Worksheet() {
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
    public String getContentHindi() { return contentHindi; }
    public void setContentHindi(@NonNull String contentHindi) { this.contentHindi = contentHindi; }

    public String getContentTribal() { return contentTribal; }
    public void setContentTribal(String contentTribal) { this.contentTribal = contentTribal; }

    public String getQuestionsHindi() { return questionsHindi; }
    public void setQuestionsHindi(String questionsHindi) { this.questionsHindi = questionsHindi; }

    public String getQuestionsTribal() { return questionsTribal; }
    public void setQuestionsTribal(String questionsTribal) { this.questionsTribal = questionsTribal; }

    public String getAnswerKeyHindi() { return answerKeyHindi; }
    public void setAnswerKeyHindi(String answerKeyHindi) { this.answerKeyHindi = answerKeyHindi; }

    public String getAnswerKeyTribal() { return answerKeyTribal; }
    public void setAnswerKeyTribal(String answerKeyTribal) { this.answerKeyTribal = answerKeyTribal; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
