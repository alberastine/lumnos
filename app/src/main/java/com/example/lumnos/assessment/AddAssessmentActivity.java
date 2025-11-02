package com.example.lumnos.assessment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lumnos.R;
import com.example.lumnos.adapter.ClassroomCheckboxAdapter;
import com.example.lumnos.models.ClassroomModel;
import com.google.gson.reflect.TypeToken;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityAddAssessmentBinding;
import com.example.lumnos.models.AssessmentModel;
import com.example.lumnos.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddAssessmentActivity extends AppCompatActivity {

    private ActivityAddAssessmentBinding binding;
    private SharedPrefsManager prefsManager;
    private String classroomId;
    private ClassroomCheckboxAdapter classroomAdapter;
    private List<ClassroomModel> allClassrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        classroomId = getIntent().getStringExtra("CLASSROOM_ID");
        prefsManager = new SharedPrefsManager(this);

        setupSpinner();
        loadOtherClassrooms();

        binding.btnSave.setOnClickListener(v -> saveAssessment());
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        ArrayAdapter<AssessmentModel.AssessmentType> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, AssessmentModel.AssessmentType.values());
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.spinnerType.setAdapter(adapter);
    }

    private void loadOtherClassrooms() {
        // Load classrooms from SharedPrefs or your source
        String json = prefsManager.getData("classrooms");
        Type listType = new TypeToken<ArrayList<ClassroomModel>>(){}.getType();
        allClassrooms = JsonUtils.fromJson(json, listType);

        if (allClassrooms == null) allClassrooms = new ArrayList<>();

        // Remove current classroom from the list
        List<ClassroomModel> otherClassrooms = new ArrayList<>();
        for (ClassroomModel c : allClassrooms) {
            if (!c.getId().equals(classroomId)) {
                otherClassrooms.add(c);
            }
        }

        // Create the adapter
        classroomAdapter = new ClassroomCheckboxAdapter(otherClassrooms);

        // Set adapter and layout manager
        binding.rvOtherClassrooms.setAdapter(classroomAdapter);
        binding.rvOtherClassrooms.setLayoutManager(new LinearLayoutManager(this));
    }


    private void saveAssessment() {
        String name = binding.etAssessmentName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Assessment name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        AssessmentModel.AssessmentType type = (AssessmentModel.AssessmentType) binding.spinnerType.getSelectedItem();
        AssessmentModel.Format format = getSelectedFormat();
        if (format == null) {
            Toast.makeText(this, "Please select a format", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> targetClassrooms = new ArrayList<>();
        targetClassrooms.add(classroomId); // always add the current classroom
        targetClassrooms.addAll(classroomAdapter.getSelectedClassroomIds());

        long timestamp = System.currentTimeMillis();

        for (String cId : targetClassrooms) {
            String key = "assessments_" + cId;
            String json = prefsManager.getData(key);
            Type listType = new TypeToken<ArrayList<AssessmentModel>>(){}.getType();
            List<AssessmentModel> assessments = JsonUtils.fromJson(json, listType);
            if (assessments == null) assessments = new ArrayList<>();

            String id = UUID.randomUUID().toString();
            assessments.add(new AssessmentModel(id, cId, type, name, format, timestamp));
            prefsManager.saveData(key, JsonUtils.toJson(assessments));
        }

        Toast.makeText(this, "Assessment saved!", Toast.LENGTH_SHORT).show();
        finish();
    }


    private AssessmentModel.Format getSelectedFormat() {
        int selectedId = binding.rgFormat.getCheckedRadioButtonId();
        if (selectedId == binding.rbCheckbox.getId()) {
            return AssessmentModel.Format.CHECKBOX;
        } else if (selectedId == binding.rbScore.getId()) {
            return AssessmentModel.Format.SCORE;
        } else if (selectedId == binding.rbBoth.getId()) {
            return AssessmentModel.Format.BOTH;
        }
        return null;
    }
}