package com.example.lumnos.models;

public class ClassroomModel {
    private String id;
    private String name;
    private long createdAt;
    private int studentCount;
    private int assessmentCount;

    public ClassroomModel(String id, String name, long createdAt, int studentCount, int assessmentCount) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.studentCount = studentCount;
        this.assessmentCount = assessmentCount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public long getCreatedAt() { return createdAt; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public int getAssessmentCount() { return assessmentCount; }
    public void setAssessmentCount(int assessmentCount) { this.assessmentCount = assessmentCount; }
}
