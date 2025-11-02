package com.example.lumnos.models;

public class ResultModel {
    private String assessmentId;
    private String studentId;
    private boolean passed;
    private float score;
    private long updatedAt;
    public ResultModel() {}

    public ResultModel(String assessmentId, String studentId, boolean passed, float score, long updatedAt) {
        this.assessmentId = assessmentId;
        this.studentId = studentId;
        this.passed = passed;
        this.score = score;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }
    public String getStudentId() { return studentId; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}