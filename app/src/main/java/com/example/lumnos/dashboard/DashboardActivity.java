package com.example.lumnos.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.lumnos.R;
import com.example.lumnos.auth.LoginActivity;
import com.example.lumnos.adapter.ClassroomAdapter;
import com.example.lumnos.classroom.AddClassroomActivity;
import com.example.lumnos.classroom.ClassroomDashboardActivity;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityDashboardBinding;
import com.example.lumnos.models.ClassroomModel;
import com.example.lumnos.utils.JsonUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.reflect.TypeToken;

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

    private void setupRecyclerView() {
        classroomList = new ArrayList<>();
        adapter = new ClassroomAdapter(classroomList, this::onClassroomClicked);
        binding.rvClassrooms.setLayoutManager(new LinearLayoutManager(this));
        binding.rvClassrooms.setAdapter(adapter);
    }

    private void loadClassrooms() {
        String json = prefsManager.getData("classrooms");
        if (json.isEmpty()) {
            prefsManager.saveData("classrooms", "[]");
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

    // ✅ Add logout menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Create the white dialog
            MaterialAlertDialogBuilder builder =
                    new MaterialAlertDialogBuilder(this, R.style.WhiteAlertDialog);

            builder.setTitle("")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        // Remove session only (not clearing all data)
                        prefsManager.removeData("is_logged_in");
                        prefsManager.removeData("logged_in_user");

                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null);

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            // Set message and button text colors after showing
            TextView message = dialog.findViewById(android.R.id.message);
            if (message != null)
                message.setTextColor(ContextCompat.getColor(this, R.color.black));

            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.blue_600));
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.gray_600));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
