package com.example.lumnos.models;


import java.io.Serializable;

public class AssessmentModel implements Serializable {
    public enum Format { CHECKBOX, SCORE, BOTH }
    public enum AssessmentType { ACTIVITY, ASSIGNMENT, RECITATION, EXAM, PROJECT }

    private String id;
    private String classroomId;
    private AssessmentType type;
    private String name;
    private Format format;
    private long createdAt;

    public AssessmentModel(String id, String classroomId, AssessmentType type, String name, Format format, long createdAt) {
        this.id = id;
        this.classroomId = classroomId;
        this.type = type;
        this.name = name;
        this.format = format;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getClassroomId() { return classroomId; }
    public AssessmentType getType() { return type; }
    public String getName() { return name; }
    public Format getFormat() { return format; }
    public void setName(String name) {
        this.name = name;
    }

}
