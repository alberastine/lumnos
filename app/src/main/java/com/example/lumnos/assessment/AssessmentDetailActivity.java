package com.example.lumnos.assessment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lumnos.R;
import com.example.lumnos.adapter.AssessmentResultsAdapter;
import com.example.lumnos.models.ClassroomModel;
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

        studentManager = new StudentManager(this, assessment.getClassroomId());

        // Store classroom name from assessment if available
        if (assessment.getClassroomName() != null && !assessment.getClassroomName().isEmpty()) {
            studentManager.setClassroomName(assessment.getClassroomName());
        }

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
        binding.btnConvertToPdf.setOnClickListener(v -> downloadAssessmentAsPdf());
        binding.btnConvertToPdfAndSaveResults.setOnClickListener(v -> {
            saveAndDownloadResults();
            Toast.makeText(this, "Results saved and PDF generated successfully!", Toast.LENGTH_SHORT).show();
        });
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
        adapter = new AssessmentResultsAdapter(
                this,
                studentList,
                existingResults,
                assessment.getFormat(),
                assessment.getId()
        );
        binding.rvStudentResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudentResults.setAdapter(adapter);
    }

    private void saveAndDownloadResults() {
        saveResults();
        downloadAssessmentAsPdf();
    }


    private void saveResults() {
        List<ResultModel> finalResults = adapter.getResults();
        prefsManager.saveData("results_" + assessment.getId(), JsonUtils.toJson(finalResults));
        Toast.makeText(this, "Results Saved!", Toast.LENGTH_SHORT).show();
    }

    private void downloadAssessmentAsPdf() {
        if (studentList.isEmpty()) {
            Toast.makeText(this, "No students to export", Toast.LENGTH_SHORT).show();
            return;
        }

        android.graphics.pdf.PdfDocument pdfDocument = new android.graphics.pdf.PdfDocument();
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setTextSize(14);

        int pageNumber = 1;
        android.graphics.pdf.PdfDocument.PageInfo pageInfo =
                new android.graphics.pdf.PdfDocument.PageInfo.Builder(842, 595, pageNumber).create(); // Landscape A4
        android.graphics.pdf.PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        android.graphics.Canvas canvas = page.getCanvas();

        int y = 50;

        // Title
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        canvas.drawText(assessment.getName() + " Results", 40, y, paint);
        y += 20;
        paint.setTextSize(14);

        // ✅ Fetch classroom name with fallback
        String classroomName = studentManager.getClassroomName();
        if (classroomName == null || classroomName.isEmpty()) {
            String classroomsJson = prefsManager.getData("classrooms");
            Type type = new TypeToken<List<ClassroomModel>>(){}.getType();
            List<ClassroomModel> classrooms = JsonUtils.fromJson(classroomsJson, type);

            if (classrooms != null) {
                for (ClassroomModel c : classrooms) {
                    if (c.getId().equals(assessment.getClassroomId())) {
                        classroomName = c.getName();
                        studentManager.setClassroomName(classroomName); // ✅ store for next time
                        break;
                    }
                }
            }

            if (classroomName == null) classroomName = "Unknown Class";
        }

        // ✅ Draw classroom name
        canvas.drawText("Classroom: " + classroomName, 40, y, paint);
        y += 30;

        // Table header
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        canvas.drawText("Student Name", 40, y, paint);
        canvas.drawText("Status", 300, y, paint);
        canvas.drawText("Result", 500, y, paint);
        paint.setFakeBoldText(false);
        y += 25;

        // Draw student rows
        for (StudentModel student : studentList) {
            String status = "Missing";
            String result = "-";

            for (ResultModel r : existingResults) {
                if (r.getStudentId().equals(student.getId())) {
                    status = r.isPassed() ? "Submitted" : "Missing";
                    result = r.getScore() > 0 ? String.valueOf(r.getScore()) : "--";
                    break;
                }
            }

            canvas.drawText(student.getName(), 40, y, paint);
            canvas.drawText(status, 300, y, paint);
            canvas.drawText(result, 500, y, paint);
            y += 25;

            // New page if overflow
            if (y > 550) {
                pdfDocument.finishPage(page);
                pageNumber++;
                pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(842, 595, pageNumber).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }

        pdfDocument.finishPage(page);

        // Save to Downloads folder
        String safeClassroom = classroomName.replaceAll("\\s+", "_");
        String safeAssessment = assessment.getName().replaceAll("\\s+", "_");
        String fileName = safeClassroom + "_" + safeAssessment + "_Results.pdf";

        java.io.File downloadsFolder = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS);
        java.io.File file = new java.io.File(downloadsFolder, fileName);

        try {
            pdfDocument.writeTo(new java.io.FileOutputStream(file));
            Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdfDocument.close();
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}