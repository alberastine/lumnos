package com.example.lumnos.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.example.lumnos.assessment.AddAssessmentActivity;
import com.example.lumnos.adapter.AssessmentAdapter;
import com.example.lumnos.adapter.StudentAdapter;
import com.example.lumnos.assessment.AssessmentDetailActivity;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityClassroomDashboardBinding;
import com.example.lumnos.models.AssessmentModel;
import com.example.lumnos.models.StudentModel;
import com.example.lumnos.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDashboardActivity extends AppCompatActivity {

    private ActivityClassroomDashboardBinding binding;
    private SharedPrefsManager prefsManager;
    private StudentManager studentManager;

    private String classroomId;
    private String classroomName;

    private StudentAdapter studentAdapter;
    private AssessmentAdapter assessmentAdapter;
    private List<StudentModel> studentList;
    private List<AssessmentModel> assessmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassroomDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        classroomId = getIntent().getStringExtra("CLASSROOM_ID");
        classroomName = getIntent().getStringExtra("CLASSROOM_NAME");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(classroomName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefsManager = new SharedPrefsManager(this);
        studentManager = new StudentManager(this, classroomId);

        setupRecyclerViews();

        binding.fabAddStudent.setOnClickListener(v -> showAddStudentDialog());
        binding.fabAddAssessment.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAssessmentActivity.class);
            intent.putExtra("CLASSROOM_ID", classroomId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
        loadAssessments();
    }

    private void setupRecyclerViews() {
        // Students
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList);
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudents.setAdapter(studentAdapter);

        // Assessments
        assessmentList = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(assessmentList, this::onAssessmentClicked);
        binding.rvAssessments.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAssessments.setAdapter(assessmentAdapter);
    }

    private void loadStudents() {
        studentList.clear();
        studentList.addAll(studentManager.getStudents());
        studentAdapter.notifyDataSetChanged();
    }

    private void loadAssessments() {
        String key = "assessments_" + classroomId;
        String json = prefsManager.getData(key);
        Type type = new TypeToken<ArrayList<AssessmentModel>>(){}.getType();
        List<AssessmentModel> loadedAssessments = JsonUtils.fromJson(json, type);

        if (loadedAssessments != null) {
            assessmentList.clear();
            assessmentList.addAll(loadedAssessments);
            assessmentAdapter.notifyDataSetChanged();
        }
    }

    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Student");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint("Student Name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String studentName = input.getText().toString().trim();
            if (!studentName.isEmpty()) {
                studentManager.addStudent(studentName);
                loadStudents(); // Refresh the list
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void onAssessmentClicked(AssessmentModel assessment) {
        Intent intent = new Intent(this, AssessmentDetailActivity.class);
        intent.putExtra("ASSESSMENT_OBJECT", (Parcelable) assessment);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}