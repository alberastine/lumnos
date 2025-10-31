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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssessmentResultsAdapter extends RecyclerView.Adapter<AssessmentResultsAdapter.ResultViewHolder> {

    private final List<StudentModel> students;
    private final Map<String, ResultModel> resultsMap;
    private final AssessmentModel.Format format;

    public AssessmentResultsAdapter(List<StudentModel> students, List<ResultModel> existingResults, AssessmentModel.Format format) {
        this.students = students;
        this.format = format;
        this.resultsMap = existingResults.stream()
                .collect(Collectors.toMap(ResultModel::getStudentId, result -> result));
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
        holder.bind(student, result, format);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public List<ResultModel> getResults(String assessmentId) {
        // This is simplified. In a real app, you would get the updated data from the ViewHolder's listeners.
        // For this example, we assume the map is updated directly by the holder.
        students.forEach(student -> {
            if (!resultsMap.containsKey(student.getId())) {
                // Create a default empty result if none exists
                resultsMap.put(student.getId(), new ResultModel(assessmentId, student.getId(), false, 0, 0));
            }
            resultsMap.get(student.getId()).setUpdatedAt(System.currentTimeMillis());
        });
        return new ArrayList<>(resultsMap.values());
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {
        private final ItemStudentResultBinding binding;

        public ResultViewHolder(ItemStudentResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StudentModel student, ResultModel result, AssessmentModel.Format format) {
            binding.tvStudentName.setText(student.getName());

            // Get or create result model
            final ResultModel currentResult = resultsMap.computeIfAbsent(student.getId(),
                    k -> new ResultModel("", student.getId(), false, 0, 0));


            // Configure visibility based on format
            binding.cbPassed.setVisibility(format == AssessmentModel.Format.CHECKBOX || format == AssessmentModel.Format.BOTH ? View.VISIBLE : View.GONE);
            binding.etScore.setVisibility(format == AssessmentModel.Format.SCORE || format == AssessmentModel.Format.BOTH ? View.VISIBLE : View.GONE);

            // Set initial values
            binding.cbPassed.setChecked(currentResult.isPassed());
            binding.etScore.setText(String.valueOf(currentResult.getScore()));

            // Remove existing listeners to prevent loops
            binding.etScore.removeTextChangedListener((TextWatcher) binding.etScore.getTag());

            // Set listeners to update the map
            binding.cbPassed.setOnCheckedChangeListener((buttonView, isChecked) -> {
                currentResult.setPassed(isChecked);
            });

            TextWatcher textWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    try {
                        currentResult.setScore(Float.parseFloat(s.toString()));
                    } catch (NumberFormatException e) {
                        currentResult.setScore(0);
                    }
                }
            };
            binding.etScore.addTextChangedListener(textWatcher);
            binding.etScore.setTag(textWatcher);
        }
    }
}
