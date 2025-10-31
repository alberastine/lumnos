package com.example.lumnos.models;

public class StudentModel {
    private String id;
    private String name;
    private long enrolledAt;

    public StudentModel(String id, String name, long enrolledAt) {
        this.id = id;
        this.name = name;
        this.enrolledAt = enrolledAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
}
