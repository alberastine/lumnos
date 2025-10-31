package com.example.lumnos.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lumnos.R;
import com.example.lumnos.RegisterActivity;
import com.example.lumnos.dashboard.DashboardActivity;
import com.example.lumnos.data.SharedPrefsManager;
import com.example.lumnos.databinding.ActivityLoginBinding;
import com.example.lumnos.models.UserModel;
import com.example.lumnos.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        prefsManager = new SharedPrefsManager(this);

        // Check if user is already logged in
        String isLoggedIn = prefsManager.getData("is_logged_in");
        if ("true".equals(isLoggedIn)) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        // **MODIFIED:** Set up the default admin user within a user list
        setupDefaultUser();

        binding.btnLogin.setOnClickListener(v -> handleLogin());

        // **NEW:** Add click listener to go to RegisterActivity
        binding.tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void setupDefaultUser() {
        // Check for the "users_list" key instead of the old admin flag
        String usersJson = prefsManager.getData("users_list");
        if (usersJson.isEmpty()) {
            // If the list is empty, create the first admin user
            List<UserModel> users = new ArrayList<>();
            users.add(new UserModel("admin", "password"));
            prefsManager.saveData("users_list", JsonUtils.toJson(users));
        }
    }

    private void handleLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String usersJson = prefsManager.getData("users_list");
        Type type = new TypeToken<ArrayList<UserModel>>(){}.getType();
        List<UserModel> users = JsonUtils.fromJson(usersJson, type);

        boolean loggedIn = false;
        if (users != null) {
            for (UserModel user : users) {
                if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                    loggedIn = true;

                    // ✅ Save session info
                    prefsManager.saveData("logged_in_user", user.getUsername());
                    prefsManager.saveData("is_logged_in", "true");

                    break;
                }
            }
        }

        if (loggedIn) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }
}