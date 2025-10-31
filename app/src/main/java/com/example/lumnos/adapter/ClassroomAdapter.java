package com.example.lumnos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumnos.databinding.ItemClassroomBinding;
import com.example.lumnos.models.ClassroomModel;
import java.util.List;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder> {

    private final List<ClassroomModel> classrooms;
    private final OnClassroomClickListener listener;

    public interface OnClassroomClickListener {
        void onClassroomClick(ClassroomModel classroom);
    }

    public ClassroomAdapter(List<ClassroomModel> classrooms, OnClassroomClickListener listener) {
        this.classrooms = classrooms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClassroomBinding binding = ItemClassroomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ClassroomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        ClassroomModel classroom = classrooms.get(position);
        holder.bind(classroom, listener);
    }

    @Override
    public int getItemCount() {
        return classrooms.size();
    }

    static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        private final ItemClassroomBinding binding;

        public ClassroomViewHolder(ItemClassroomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final ClassroomModel classroom, final OnClassroomClickListener listener) {
            binding.tvClassroomName.setText(classroom.getName());
            binding.tvStudentsCount.setText(classroom.getStudentCount() + " students");
            binding.tvAssessmentsCount.setText(classroom.getAssessmentCount() + " assessments");

            itemView.setOnClickListener(v -> listener.onClassroomClick(classroom));
        }

    }
}
