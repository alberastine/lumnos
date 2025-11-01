package com.example.lumnos.assessment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lumnos.R;
import com.example.lumnos.adapter.AssessmentResultsAdapter;
import com.google.gson.reflect.TypeToken;
import com.example.lumnos.classroom.StudentManager;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityAssessmentDetailBinding;
import com.example.lumnos.models.AssessmentModel;
import com.example.lumnos.models.ResultModel;
import com.example.lumnos.models.StudentModel;
import com.example.lumnos.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssessmentDetailActivity extends AppCompatActivity {

    private ActivityAssessmentDetailBinding binding;
    private AssessmentModel assessment;
    private List<StudentModel> studentList;
    private List<ResultModel> existingResults;
    private AssessmentResultsAdapter adapter;
    private SharedPrefsManager prefsManager;
    private StudentManager studentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssessmentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        assessment = (AssessmentModel) getIntent().getSerializableExtra("ASSESSMENT_OBJECT");
        prefsManager = new SharedPrefsManager(this);
        studentManager = new StudentManager(this, assessment.getClassroomId());

        if (assessment == null) {
            Toast.makeText(this, "Error: Assessment not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.toolbar.getNavigationIcon()
                .setTint(getResources().getColor(R.color.black));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(assessment.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
        setupRecyclerView();

        binding.btnSaveResults.setOnClickListener(v -> saveResults());
    }

    private void loadData() {
        studentList = studentManager.getStudents();

        String key = "results_" + assessment.getId();
        String json = prefsManager.getData(key);
        Type type = new TypeToken<ArrayList<ResultModel>>(){}.getType();
        existingResults = JsonUtils.fromJson(json, type);
        if (existingResults == null) {
            existingResults = new ArrayList<>();
        }
    }

    private void setupRecyclerView() {
        adapter = new AssessmentResultsAdapter(studentList, existingResults, assessment.getFormat());
        binding.rvStudentResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudentResults.setAdapter(adapter);
    }

    private void saveResults() {
        List<ResultModel> finalResults = adapter.getResults(assessment.getId());

        String key = "results_" + assessment.getId();
        prefsManager.saveData(key, JsonUtils.toJson(finalResults));
        Toast.makeText(this, "Results Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}