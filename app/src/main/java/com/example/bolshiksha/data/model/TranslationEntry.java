package com.example.bolshiksha.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "translation_entries",
        indices = {@Index(value = {"sourceLang", "targetLang", "sourceText"})})
public class TranslationEntry {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String sourceLang;

    @NonNull
    private String targetLang;

    @NonNull
    private String sourceText;

    @NonNull
    private String translatedText;

    private String domain;
    private long timestamp;
    private int usageCount;
    private boolean isVerified;

    public TranslationEntry() {
        this.timestamp = System.currentTimeMillis();
        this.usageCount = 0;
        this.isVerified = false;
    }

    @Ignore
    public TranslationEntry(@NonNull String sourceLang, @NonNull String targetLang,
                            @NonNull String sourceText, @NonNull String translatedText) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.sourceText = sourceText;
        this.translatedText = translatedText;
        this.timestamp = System.currentTimeMillis();
        this.usageCount = 0;
        this.isVerified = false;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getSourceLang() { return sourceLang; }
    public void setSourceLang(@NonNull String sourceLang) { this.sourceLang = sourceLang; }

    @NonNull
    public String getTargetLang() { return targetLang; }
    public void setTargetLang(@NonNull String targetLang) { this.targetLang = targetLang; }

    @NonNull
    public String getSourceText() { return sourceText; }
    public void setSourceText(@NonNull String sourceText) { this.sourceText = sourceText; }

    @NonNull
    public String getTranslatedText() { return translatedText; }
    public void setTranslatedText(@NonNull String translatedText) { this.translatedText = translatedText; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
