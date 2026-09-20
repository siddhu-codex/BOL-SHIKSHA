package com.example.bolshiksha.data.model;

public class CurriculumContent {
    public enum Type { SCRIPT, ACTIVITY, ASSESSMENT }

    private Type type;
    private String nipunOutcomeCode;
    private String nipunOutcomeDescription;
    private String contentHindi;
    private String contentTribal;

    public CurriculumContent() {}

    public CurriculumContent(Type type, String nipunOutcomeCode,
                             String nipunOutcomeDescription,
                             String contentHindi, String contentTribal) {
        this.type = type;
        this.nipunOutcomeCode = nipunOutcomeCode;
        this.nipunOutcomeDescription = nipunOutcomeDescription;
        this.contentHindi = contentHindi;
        this.contentTribal = contentTribal;
    }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getNipunOutcomeCode() { return nipunOutcomeCode; }
    public void setNipunOutcomeCode(String nipunOutcomeCode) { this.nipunOutcomeCode = nipunOutcomeCode; }

    public String getNipunOutcomeDescription() { return nipunOutcomeDescription; }
    public void setNipunOutcomeDescription(String nipunOutcomeDescription) { this.nipunOutcomeDescription = nipunOutcomeDescription; }

    public String getContentHindi() { return contentHindi; }
    public void setContentHindi(String contentHindi) { this.contentHindi = contentHindi; }

    public String getContentTribal() { return contentTribal; }
    public void setContentTribal(String contentTribal) { this.contentTribal = contentTribal; }
}
