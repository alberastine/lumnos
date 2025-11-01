package com.example.lumnos.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lumnos.R;
import com.example.lumnos.assessment.AddAssessmentActivity;
import com.example.lumnos.assessment.AssessmentDetailActivity;
import com.example.lumnos.adapter.AssessmentAdapter;
import com.example.lumnos.adapter.StudentAdapter;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityClassroomDashboardBinding;
import com.example.lumnos.models.AssessmentModel;
import com.example.lumnos.models.StudentModel;
import com.example.lumnos.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;

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
        binding.toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.black));
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
        studentAdapter = new StudentAdapter(studentList, new StudentAdapter.OnStudentClickListener() {
            @Override
            public void onStudentEdit(StudentModel student) {
                showEditStudentDialog(student);
            }

            @Override
            public void onStudentDelete(StudentModel student) {
                showDeleteStudentDialog(student);
            }
        });
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudents.setAdapter(studentAdapter);

        // Assessments
        assessmentList = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(assessmentList, new AssessmentAdapter.OnAssessmentClickListener() {
            @Override
            public void onAssessmentClick(AssessmentModel assessment) {
                Intent intent = new Intent(ClassroomDashboardActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("ASSESSMENT_OBJECT", assessment);
                startActivity(intent);
            }

            @Override
            public void onAssessmentEdit(AssessmentModel assessment) {
                showEditAssessmentDialog(assessment);
            }

            @Override
            public void onAssessmentDelete(AssessmentModel assessment) {
                showDeleteAssessmentDialog(assessment);
            }
        });
        binding.rvAssessments.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAssessments.setAdapter(assessmentAdapter);
    }

    // ------------------- Dialogs -------------------

    private void showAddStudentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
        EditText input = dialogView.findViewById(R.id.etItemName);
        TextView title = dialogView.findViewById(R.id.tvDialogTitle);
        title.setText("Add Student");

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.WhiteAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_600));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String inputText = input.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    String[] names = inputText.split(",");
                    boolean hasValidName = false;
                    for (String name : names) {
                        String cleanName = name.trim();
                        if (!cleanName.isEmpty()) {
                            studentManager.addStudent(cleanName);
                            hasValidName = true;
                        }
                    }

                    if (hasValidName) {
                        loadStudents();
                        dialog.dismiss();
                    } else {
                        input.setError("Please enter at least one valid student name");
                    }
                } else {
                    input.setError("Student name is required");
                }
            });
        });

        dialog.show();
    }

    private void showEditStudentDialog(StudentModel student) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
        EditText input = dialogView.findViewById(R.id.etItemName);
        TextView title = dialogView.findViewById(R.id.tvDialogTitle);
        title.setText("Edit Student");

        input.setText(student.getName());

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.WhiteAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_600));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    studentManager.updateStudent(student.getId(), newName);
                    loadStudents();
                    dialog.dismiss();
                } else {
                    input.setError("Student name cannot be empty");
                }
            });
        });

        dialog.show();
    }

    private void showDeleteStudentDialog(StudentModel student) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_item, null);
        TextView title = dialogView.findViewById(R.id.tvDialogTitle);
        TextView message = dialogView.findViewById(R.id.tvDialogMessage);
        title.setText("Delete Student");
        message.setText("Are you sure you want to delete this student?");

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.WhiteAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Delete", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_600));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                studentManager.deleteStudent(student.getId());
                loadStudents();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showEditAssessmentDialog(AssessmentModel assessment) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
        EditText input = dialogView.findViewById(R.id.etItemName);
        TextView title = dialogView.findViewById(R.id.tvDialogTitle);
        title.setText("Edit Assessment");
        input.setText(assessment.getName());

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.WhiteAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_600));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    assessment.setName(newName);
                    saveAssessments();
                    loadAssessments();
                    dialog.dismiss();
                } else {
                    input.setError("Assessment name cannot be empty");
                }
            });
        });

        dialog.show();
    }

    private void showDeleteAssessmentDialog(AssessmentModel assessment) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_item, null);
        TextView title = dialogView.findViewById(R.id.tvDialogTitle);
        TextView message = dialogView.findViewById(R.id.tvDialogMessage);
        title.setText("Delete Assessment");
        message.setText("Are you sure you want to delete this assessment?");

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.WhiteAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Delete", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_600));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                assessmentList.remove(assessment);
                saveAssessments();
                loadAssessments();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // ------------------- Load & Save -------------------

    private void loadStudents() {
        studentList.clear();
        studentList.addAll(studentManager.getStudents());
        studentAdapter.notifyDataSetChanged();

        binding.rvStudents.setVisibility(studentList.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvNoStudents.setVisibility(studentList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.tvNoStudentsImg.setVisibility(studentList.isEmpty() ? View.VISIBLE : View.GONE);

        updateToolbarSubtitle();
    }

    private void loadAssessments() {
        String key = "assessments_" + classroomId;
        String json = prefsManager.getData(key);
        Type type = new TypeToken<ArrayList<AssessmentModel>>(){}.getType();
        List<AssessmentModel> loadedAssessments = JsonUtils.fromJson(json, type);

        assessmentList.clear();
        if (loadedAssessments != null) assessmentList.addAll(loadedAssessments);
        assessmentAdapter.notifyDataSetChanged();

        binding.rvAssessments.setVisibility(assessmentList.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvNoAssessments.setVisibility(assessmentList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.tvNoAssessmentsImg.setVisibility(assessmentList.isEmpty() ? View.VISIBLE : View.GONE);

        updateToolbarSubtitle();
    }

    private void saveAssessments() {
        String key = "assessments_" + classroomId;
        prefsManager.saveData(key, JsonUtils.toJson(assessmentList));
    }

    private void updateToolbarSubtitle() {
        binding.tvStudentsCount.setText(studentList.size() + (studentList.size() == 1 ? " student" : " students"));
        binding.tvAssessmentsCount.setText(assessmentList.size() + (assessmentList.size() == 1 ? " assessment" : " assessments"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
