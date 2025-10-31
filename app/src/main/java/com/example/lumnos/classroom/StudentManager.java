package com.example.lumnos.classroom;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.models.StudentModel;
import com.example.lumnos.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;


public class StudentManager {
    private final SharedPrefsManager prefsManager;
    private final String classroomId;
    private final String STUDENTS_KEY;

    public StudentManager(Context context, String classroomId) {
        this.prefsManager = new SharedPrefsManager(context);
        this.classroomId = classroomId;
        this.STUDENTS_KEY = "students_" + classroomId;
    }

    public List<StudentModel> getStudents() {
        String json = prefsManager.getData(STUDENTS_KEY);
        Type type = new TypeToken<ArrayList<StudentModel>>(){}.getType();
        List<StudentModel> students = JsonUtils.fromJson(json, type);
        return students != null ? students : new ArrayList<>();
    }

    public void addStudent(String studentName) {
        List<StudentModel> students = getStudents();
        String id = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        students.add(new StudentModel(id, studentName, timestamp));
        prefsManager.saveData(STUDENTS_KEY, JsonUtils.toJson(students));
    }

    // Additional methods for updating/deleting students can be added here
}