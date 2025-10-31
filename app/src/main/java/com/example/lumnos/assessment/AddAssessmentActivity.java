package com.example.lumnos.assessment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        classroomId = getIntent().getStringExtra("CLASSROOM_ID");
        prefsManager = new SharedPrefsManager(this);

        setupSpinner();

        binding.btnSave.setOnClickListener(v -> saveAssessment());
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        ArrayAdapter<AssessmentModel.AssessmentType> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, AssessmentModel.AssessmentType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerType.setAdapter(adapter);
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

        String key = "assessments_" + classroomId;
        String json = prefsManager.getData(key);
        Type listType = new TypeToken<ArrayList<AssessmentModel>>(){}.getType();
        List<AssessmentModel> assessments = JsonUtils.fromJson(json, listType);
        if (assessments == null) {
            assessments = new ArrayList<>();
        }

        String id = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        assessments.add(new AssessmentModel(id, classroomId, type, name, format, timestamp));

        prefsManager.saveData(key, JsonUtils.toJson(assessments));
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