package com.example.lumnos.classroom;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lumnos.R;
import com.google.gson.reflect.TypeToken;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityAddClassroomBinding;
import com.example.lumnos.models.ClassroomModel;
import com.example.lumnos.utils.JsonUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddClassroomActivity extends AppCompatActivity {

    private ActivityAddClassroomBinding binding;
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddClassroomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set back button color to black
        binding.toolbar.getNavigationIcon()
                .setTint(getResources().getColor(R.color.black));

        prefsManager = new SharedPrefsManager(this);

        binding.btnSave.setOnClickListener(v -> saveClassroom());
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void saveClassroom() {
        String classroomName = binding.etClassroomName.getText().toString().trim();
        if (TextUtils.isEmpty(classroomName)) {
            Toast.makeText(this, "Classroom name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String json = prefsManager.getData("classrooms");
        Type type = new TypeToken<ArrayList<ClassroomModel>>(){}.getType();
        List<ClassroomModel> classrooms = JsonUtils.fromJson(json, type);

        if (classrooms == null) {
            classrooms = new ArrayList<>();
        }

        String classroomId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        ClassroomModel newClassroom = new ClassroomModel(classroomId, classroomName, timestamp);

        classrooms.add(newClassroom);

        String updatedJson = JsonUtils.toJson(classrooms);
        prefsManager.saveData("classrooms", updatedJson);

        Toast.makeText(this, "Classroom Saved", Toast.LENGTH_SHORT).show();
        finish(); // Go back to the Dashboard
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}