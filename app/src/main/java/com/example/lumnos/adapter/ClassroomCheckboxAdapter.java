package com.example.lumnos.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lumnos.databinding.ItemCheckboxBinding;
import com.example.lumnos.models.ClassroomModel;
import java.util.ArrayList;
import java.util.List;

public class ClassroomCheckboxAdapter extends RecyclerView.Adapter<ClassroomCheckboxAdapter.ViewHolder> {

    private final List<ClassroomModel> classrooms;
    private final List<String> selectedClassroomIds = new ArrayList<>();

    public ClassroomCheckboxAdapter(List<ClassroomModel> classrooms) {
        this.classrooms = classrooms;
    }

    public List<String> getSelectedClassroomIds() {
        return selectedClassroomIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCheckboxBinding binding = ItemCheckboxBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassroomModel classroom = classrooms.get(position);
        holder.binding.checkbox.setText(classroom.getName());
        holder.binding.checkbox.setOnCheckedChangeListener(null);
        holder.binding.checkbox.setChecked(selectedClassroomIds.contains(classroom.getId()));

        holder.binding.checkbox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                selectedClassroomIds.add(classroom.getId());
            } else {
                selectedClassroomIds.remove(classroom.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return classrooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCheckboxBinding binding;
        public ViewHolder(ItemCheckboxBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
