package com.example.lumnos.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumnos.databinding.ItemStudentResultBinding;
import com.example.lumnos.models.AssessmentModel;
import com.example.lumnos.models.ResultModel;
import com.example.lumnos.models.StudentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssessmentResultsAdapter extends RecyclerView.Adapter<AssessmentResultsAdapter.ResultViewHolder> {

    private final List<StudentModel> students;
    private final Map<String, ResultModel> resultsMap;
    private final AssessmentModel.Format format;

    public AssessmentResultsAdapter(List<StudentModel> students, List<ResultModel> existingResults, AssessmentModel.Format format) {
        this.students = students;
        this.format = format;

        // Safely initialize the results map
        this.resultsMap = new HashMap<>();
        if (existingResults != null) {
            for (ResultModel result : existingResults) {
                this.resultsMap.put(result.getStudentId(), result);
            }
        }
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentResultBinding binding = ItemStudentResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        StudentModel student = students.get(position);
        ResultModel result = resultsMap.get(student.getId());

        // Always bind using saved data
        if (result == null) {
            result = new ResultModel("", student.getId(), false, 0, System.currentTimeMillis());
            resultsMap.put(student.getId(), result);
        }

        holder.bind(student, result, format);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public List<ResultModel> getResults(String assessmentId) {
        for (ResultModel result : resultsMap.values()) {
            result.setAssessmentId(assessmentId);
            result.setUpdatedAt(System.currentTimeMillis());
        }
        return new ArrayList<>(resultsMap.values());
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {
        private final ItemStudentResultBinding binding;
        private TextWatcher scoreWatcher; // keep reference to remove it properly

        public ResultViewHolder(ItemStudentResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StudentModel student, ResultModel result, AssessmentModel.Format format) {
            binding.tvStudentName.setText(student.getName());

            // Setup visibility
            binding.cbPassed.setVisibility(
                    format == AssessmentModel.Format.CHECKBOX || format == AssessmentModel.Format.BOTH
                            ? View.VISIBLE : View.GONE);
            binding.etScore.setVisibility(
                    format == AssessmentModel.Format.SCORE || format == AssessmentModel.Format.BOTH
                            ? View.VISIBLE : View.GONE);

            // --- Remove old listener before rebinding ---
            if (scoreWatcher != null) {
                binding.etScore.removeTextChangedListener(scoreWatcher);
            }

            // --- Restore saved values ---
            binding.cbPassed.setOnCheckedChangeListener(null); // prevent unwanted triggers
            binding.cbPassed.setChecked(result.isPassed());
            binding.etScore.setText(String.valueOf(result.getScore()));

            // --- Set new listeners ---
            binding.cbPassed.setOnCheckedChangeListener((buttonView, isChecked) -> {
                result.setPassed(isChecked);
                resultsMap.put(student.getId(), result);
            });

            scoreWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    float score = 0;
                    try {
                        if (!s.toString().isEmpty()) {
                            score = Float.parseFloat(s.toString());
                        }
                    } catch (NumberFormatException ignored) {}
                    result.setScore(score);
                    resultsMap.put(student.getId(), result);
                }
            };
            binding.etScore.addTextChangedListener(scoreWatcher);
        }
    }
}
