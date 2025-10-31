package com.example.lumnos.models;

public class ClassroomModel {
    private String id;
    private String name;
    private long createdAt;

    public ClassroomModel(String id, String name, long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public long getCreatedAt() { return createdAt; }
}
