package com.example.lumnos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityRegisterBinding;
import com.example.lumnos.models.UserModel;
import com.example.lumnos.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        prefsManager = new SharedPrefsManager(this);

        binding.btnRegister.setOnClickListener(v -> handleRegistration());
        binding.tvGoToLogin.setOnClickListener(v -> finish()); // Go back to LoginActivity
    }

    private void handleRegistration() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // 1. Validate input
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Username and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Load existing users
        String usersJson = prefsManager.getData("users_list");
        Type type = new TypeToken<ArrayList<UserModel>>(){}.getType();
        List<UserModel> users = JsonUtils.fromJson(usersJson, type);
        if (users == null) {
            users = new ArrayList<>();
        }

        // 3. Check if username already exists
        for (UserModel user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 4. Add new user and save
        users.add(new UserModel(username, password));
        prefsManager.saveData("users_list", JsonUtils.toJson(users));

        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
        finish(); // Go back to login screen
    }
}