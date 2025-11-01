package com.example.lumnos.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lumnos.R;
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

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        classroomId = getIntent().getStringExtra("CLASSROOM_ID");
        classroomName = getIntent().getStringExtra("CLASSROOM_NAME");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.toolbar.getNavigationIcon()
                .setTint(getResources().getColor(R.color.black));

        binding.tvToolbarTitle.setText(classroomName);

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

        if (studentList.isEmpty()) {
            binding.tvNoStudentsImg.setVisibility(View.VISIBLE);
            binding.tvNoStudents.setVisibility(View.VISIBLE);
            binding.rvStudents.setVisibility(View.GONE);
        } else {
            binding.tvNoStudentsImg.setVisibility(View.GONE);
            binding.tvNoStudents.setVisibility(View.GONE);
            binding.rvStudents.setVisibility(View.VISIBLE);
        }
        updateToolbarSubtitle();
    }

    private void loadAssessments() {
        String key = "assessments_" + classroomId;
        String json = prefsManager.getData(key);
        Type type = new TypeToken<ArrayList<AssessmentModel>>(){}.getType();
        List<AssessmentModel> loadedAssessments = JsonUtils.fromJson(json, type);

        assessmentList.clear();
        if (loadedAssessments != null) {
            assessmentList.addAll(loadedAssessments);
        }
        assessmentAdapter.notifyDataSetChanged();

        if (assessmentList.isEmpty()) {
            binding.tvNoAssessmentsImg.setVisibility(View.VISIBLE);
            binding.tvNoAssessments.setVisibility(View.VISIBLE);
            binding.rvAssessments.setVisibility(View.GONE);
        } else {
            binding.tvNoAssessmentsImg.setVisibility(View.GONE);
            binding.tvNoAssessments.setVisibility(View.GONE);
            binding.rvAssessments.setVisibility(View.VISIBLE);
        }
        updateToolbarSubtitle();
    }

    private void updateToolbarSubtitle() {
        int studentCount = studentList.size();
        int assessmentCount = assessmentList.size();

        binding.tvStudentsCount.setText(studentCount + (studentCount == 1 ? " student" : " students"));
        binding.tvAssessmentsCount.setText(assessmentCount + (assessmentCount == 1 ? " assessment" : " assessments"));
    }


    private void showAddStudentDialog() {
        // Inflate custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        final EditText input = dialogView.findViewById(R.id.etStudentName);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.WhiteAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            // Customize buttons
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_600));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String studentName = input.getText().toString().trim();
                if (!studentName.isEmpty()) {
                    studentManager.addStudent(studentName);
                    loadStudents(); // Refresh the list
                    dialog.dismiss();
                } else {
                    input.setError("Student name is required");
                }
            });
        });

        dialog.show();
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