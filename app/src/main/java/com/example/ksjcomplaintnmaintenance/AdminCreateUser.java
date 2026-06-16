package com.example.ksjcomplaintnmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

public class AdminCreateUser extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPhone;
    private RadioGroup rgRole;
    private LinearLayout staffFields, btnSubmit;
    private Spinner spinnerStaffRole;
    private ProgressDialog progressDialog;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private String userId;

    private final String URL_CREATE_USER = Config.getUrl("create_user_admin.php");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create_user);

        userId = getIntent().getStringExtra("USER_ID");

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_dashboard) {
                startActivity(new Intent(this, AdminDashboard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_user_management) {
                startActivity(new Intent(this, UserManagement.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_complaint_list) {
                startActivity(new Intent(this, AdminComplaintList.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_create_user) {
                // Already here
            } else if (id == R.id.nav_feedback_board) {
                startActivity(new Intent(this, AdminFeedbackBoard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_monthly_summary) {
                startActivity(new Intent(this, AdminMonthlySummary.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        initViews();
        setupRoleToggle();

        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void initViews() {
        etName = findViewById(R.id.new_user_fullname);
        etEmail = findViewById(R.id.new_user_email);
        etPassword = findViewById(R.id.new_user_password);
        etPhone = findViewById(R.id.new_user_phone);

        rgRole = findViewById(R.id.new_user_role_group);
        staffFields = findViewById(R.id.staff_fields);
        spinnerStaffRole = findViewById(R.id.new_user_staff_role);
        btnSubmit = findViewById(R.id.btn_create_user_submit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Setup Staff Role Spinner
        String[] roles = {"Technician", "Janitor", "Security", "Plumber"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStaffRole.setAdapter(adapter);
    }

    private void setupRoleToggle() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_new_staff) {
                staffFields.setVisibility(View.VISIBLE);
            } else { 
                staffFields.setVisibility(View.GONE);
            }
        });
    }

    private void validateAndSubmit() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all core fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            etEmail.setError("Invalid email format");
            return;
        }

        if (!isAcceptablePassword(pass)) {
            etPassword.setError("Password must be 8-30 characters with uppercase, lowercase, digits and special characters.");
            return;
        }

        submitToServer(name, email, pass, phone);
    }

    public static final String SPECIAL_CHARACTERS = "!@#$%^&*()~`-=_+[]{}|:\";',./<>?";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 30;

    public static boolean isAcceptablePassword(String password) {
        if (TextUtils.isEmpty(password)) return false;
        password = password.trim();
        int len = password.length();
        if (len < MIN_PASSWORD_LENGTH || len > MAX_PASSWORD_LENGTH) return false;

        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (SPECIAL_CHARACTERS.indexOf(String.valueOf(c)) >= 0) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void submitToServer(String name, String email, String pass, String phone) {
        progressDialog.setMessage("Creating user...");
        progressDialog.show();

        // 1. Get the checked RadioButton ID
        int selectedId = rgRole.getCheckedRadioButtonId();
        String role = "user"; // Default to Student

        // 2. Standardize roles based on IDs
        if (selectedId == R.id.rb_new_staff) {
            role = "staff";
        } else if (selectedId == R.id.rb_new_admin) {
            role = "admin";
        } else if (selectedId == R.id.rb_new_student) {
            role = "user";
        }

        String staffJob = (role.equals("staff")) ? spinnerStaffRole.getSelectedItem().toString() : "";

        // DEBUG LOGS
        Log.d("ADMIN_CREATE", "Selected ID: " + selectedId + " | Sent Role: " + role);
        Toast.makeText(this, "Creating " + role + " account...", Toast.LENGTH_SHORT).show();

        // Final variables for the request
        final String finalRole = role;
        final String finalStaffJob = staffJob;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CREATE_USER,
                response -> {
                    progressDialog.dismiss();
                    Log.d("ADMIN_CREATE", "Response: " + response);
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(this, "User created successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", name);
                params.put("email", email.toLowerCase().trim()); // Clean email
                params.put("password", pass);
                params.put("phone_number", phone);
                params.put("role", finalRole);
                params.put("staff_role", finalStaffJob);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
