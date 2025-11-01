package com.example.lumnos.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumnos.R;
import com.example.lumnos.databinding.ItemStudentBinding;
import com.example.lumnos.models.StudentModel;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private final List<StudentModel> students;
    private final OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onStudentEdit(StudentModel student);
        void onStudentDelete(StudentModel student);
    }

    public StudentAdapter(List<StudentModel> students, OnStudentClickListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentBinding binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(students.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private final ItemStudentBinding binding;

        public StudentViewHolder(ItemStudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StudentModel student, OnStudentClickListener listener) {
            binding.tvStudentName.setText(student.getName());

            binding.getRoot().findViewById(R.id.editStudent).setOnClickListener(v -> StudentAdapter.this.listener.onStudentEdit(student));
            binding.getRoot().findViewById(R.id.deleteStudent).setOnClickListener(v -> StudentAdapter.this.listener.onStudentDelete(student));
        }
    }
}
