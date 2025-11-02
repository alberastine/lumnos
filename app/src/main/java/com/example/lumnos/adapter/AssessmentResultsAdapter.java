package com.example.lumnos.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ItemStudentResultBinding;
import com.example.lumnos.models.AssessmentModel;
import com.example.lumnos.models.ResultModel;
import com.example.lumnos.models.StudentModel;
import com.example.lumnos.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssessmentResultsAdapter extends RecyclerView.Adapter<AssessmentResultsAdapter.ResultViewHolder> {

    private final List<StudentModel> students;
    private final Map<String, ResultModel> resultsMap;
    private final AssessmentModel.Format format;
    private final SharedPrefsManager prefsManager;
    private final String assessmentId;

    public AssessmentResultsAdapter(
            Context context,
            List<StudentModel> students,
            List<ResultModel> existingResults,
            AssessmentModel.Format format,
            String assessmentId
    ) {
        this.students = students;
        this.format = format;
        this.assessmentId = assessmentId;
        this.prefsManager = new SharedPrefsManager(context);

        // Map existing results by student ID
        this.resultsMap = existingResults.stream()
                .collect(Collectors.toMap(ResultModel::getStudentId, result -> result));
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentResultBinding binding = ItemStudentResultBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        StudentModel student = students.get(position);
        ResultModel result = resultsMap.get(student.getId());
        holder.bind(student, result, format);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public List<ResultModel> getResults() {
        // Return all results as a list
        return new ArrayList<>(resultsMap.values());
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {
        private final ItemStudentResultBinding binding;
        private TextWatcher textWatcher;

        public ResultViewHolder(ItemStudentResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StudentModel student, ResultModel result, AssessmentModel.Format format) {
            binding.tvStudentName.setText(student.getName());

            final ResultModel currentResult = resultsMap.computeIfAbsent(student.getId(),
                    k -> new ResultModel(assessmentId, student.getId(), false, 0, 0));

            // Show/hide views based on assessment format
            binding.cbPassed.setVisibility(
                    format == AssessmentModel.Format.CHECKBOX || format == AssessmentModel.Format.BOTH
                            ? View.VISIBLE : View.GONE
            );
            binding.etScore.setVisibility(
                    format == AssessmentModel.Format.SCORE || format == AssessmentModel.Format.BOTH
                            ? View.VISIBLE : View.GONE
            );

            // Remove old text watcher before adding new
            if (textWatcher != null) binding.etScore.removeTextChangedListener(textWatcher);

            // Set initial values
            binding.cbPassed.setChecked(currentResult.isPassed());
            binding.etScore.setText(currentResult.getScore() == 0 ? "" : String.valueOf(currentResult.getScore()));

            // Checkbox listener
            binding.cbPassed.setOnCheckedChangeListener((buttonView, isChecked) -> {
                currentResult.setPassed(isChecked);
                currentResult.setUpdatedAt(System.currentTimeMillis());
                autoSave();
            });

            // TextWatcher for score input
            textWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    try {
                        float score = Float.parseFloat(s.toString());
                        currentResult.setScore(score);
                    } catch (NumberFormatException e) {
                        currentResult.setScore(0);
                    }
                    currentResult.setUpdatedAt(System.currentTimeMillis());
                    autoSave();
                }
            };
            binding.etScore.addTextChangedListener(textWatcher);
        }

        private void autoSave() {
            prefsManager.saveData("results_" + assessmentId, JsonUtils.toJson(getResults()));
        }
    }
}
