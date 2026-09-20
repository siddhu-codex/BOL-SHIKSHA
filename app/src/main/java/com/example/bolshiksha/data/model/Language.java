package com.example.bolshiksha.data.model;

public enum Language {
    HINDI("hi", "Hindi", "हिन्दी"),
    SANTHALI("sat", "Santhali", "संताली"),
    HO("hoc", "Ho", "हो"),
    MUNDARI("unr", "Mundari", "मुंडारी");

    private final String code;
    private final String englishName;
    private final String hindiName;

    Language(String code, String englishName, String hindiName) {
        this.code = code;
        this.englishName = englishName;
        this.hindiName = hindiName;
    }

    public String getCode() { return code; }
    public String getEnglishName() { return englishName; }
    public String getHindiName() { return hindiName; }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) return lang;
        }
        return SANTHALI;
    }
}
