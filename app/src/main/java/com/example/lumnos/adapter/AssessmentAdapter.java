package com.example.lumnos.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumnos.databinding.ItemAssessmentBinding;
import com.example.lumnos.models.AssessmentModel;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private final List<AssessmentModel> assessments;
    private final OnAssessmentClickListener listener;

    public interface OnAssessmentClickListener {
        void onAssessmentClick(AssessmentModel assessment);
    }

    public AssessmentAdapter(List<AssessmentModel> assessments, OnAssessmentClickListener listener) {
        this.assessments = assessments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAssessmentBinding binding = ItemAssessmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AssessmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        holder.bind(assessments.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAssessmentBinding binding;

        public AssessmentViewHolder(ItemAssessmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AssessmentModel assessment, OnAssessmentClickListener listener) {
            binding.tvAssessmentName.setText(assessment.getName());
            binding.tvAssessmentType.setText(assessment.getType().toString());
            itemView.setOnClickListener(v -> listener.onAssessmentClick(assessment));
        }
    }
}
