package com.example.lumnos.dashboard;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lumnos.adapter.ClassroomAdapter;
import com.google.gson.reflect.TypeToken;
import com.example.lumnos.classroom.AddClassroomActivity;
import com.example.lumnos.classroom.ClassroomDashboardActivity;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityDashboardBinding;
import com.example.lumnos.models.ClassroomModel;
import com.example.lumnos.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private SharedPrefsManager prefsManager;
    private ClassroomAdapter adapter;
    private List<ClassroomModel> classroomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        prefsManager = new SharedPrefsManager(this);

        binding.fabAddClassroom.setOnClickListener(v -> {
            startActivity(new Intent(this, AddClassroomActivity.class));
        });

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClassrooms();
    }

    private void setupRecyclerView() {
        classroomList = new ArrayList<>();
        adapter = new ClassroomAdapter(classroomList, this::onClassroomClicked);
        binding.rvClassrooms.setLayoutManager(new LinearLayoutManager(this));
        binding.rvClassrooms.setAdapter(adapter);
    }

    private void loadClassrooms() {
        String json = prefsManager.getData("classrooms");
        if (json.isEmpty()) {
            prefsManager.saveData("classrooms", "[]"); // Initialize if empty
            json = "[]";
        }
        Type type = new TypeToken<ArrayList<ClassroomModel>>(){}.getType();
        List<ClassroomModel> loadedClassrooms = JsonUtils.fromJson(json, type);

        if (loadedClassrooms != null) {
            classroomList.clear();
            classroomList.addAll(loadedClassrooms);
            adapter.notifyDataSetChanged();
        }
    }

    private void onClassroomClicked(ClassroomModel classroom) {
        Intent intent = new Intent(this, ClassroomDashboardActivity.class);
        intent.putExtra("CLASSROOM_ID", classroom.getId());
        intent.putExtra("CLASSROOM_NAME", classroom.getName());
        startActivity(intent);
    }
}