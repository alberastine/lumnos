package com.example.lumnos.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lumnos.R;
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

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        prefsManager.clearData();
                        Intent intent = new Intent(this, com.example.lumnos.auth.LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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