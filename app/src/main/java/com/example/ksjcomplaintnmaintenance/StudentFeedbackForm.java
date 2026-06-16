package com.example.ksjcomplaintnmaintenance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentFeedbackForm extends AppCompatActivity {


    private EditText etFeedbackMessage;
    private RatingBar ratingBar;
    private Spinner spinnerStaff;
    private Button btnSubmit;

    private List<String> staffNames = new ArrayList<>();
    private List<String> staffIds = new ArrayList<>();
    private String studentId;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_feedback_form);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_student_dashboard) {
                startActivity(new Intent(this, StudentDashboard.class).putExtra("USER_ID", studentId));
            } else if (id == R.id.nav_student_complaint) {
                startActivity(new Intent(this, StudentComplaintForm.class).putExtra("USER_ID", studentId));
            } else if (id == R.id.nav_student_history) {
                startActivity(new Intent(this, StudentHistory.class).putExtra("USER_ID", studentId));
            } else if (id == R.id.nav_student_notifications) {
                startActivity(new Intent(this, StudentNotification.class).putExtra("USER_ID", studentId));
            } else if (id == R.id.nav_student_feedback) {
                // Already here
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 1. Get student ID passed from Dashboard
        studentId = getIntent().getStringExtra("USER_ID");

        spinnerStaff = findViewById(R.id.spinnerStaff);
        etFeedbackMessage = findViewById(R.id.etFeedbackText);
        ratingBar = findViewById(R.id.feedbackRatingBar);
        btnSubmit = findViewById(R.id.btnSubmitFeedback);

        loadStaffList();
        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void loadStaffList() {
        String url = Config.getUrl("get_completed_staff.php?student_id=" + studentId);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        staffNames.clear();
                        staffIds.clear();

                        String cleanResponse = response.trim();

                        JSONArray array = new JSONArray(cleanResponse);

                        if (array.length() == 0) {
                            staffNames.add("No completed complaints found");
                            staffIds.add("");
                            btnSubmit.setEnabled(false);
                        } else {
                            staffNames.add("Select the staff involved");
                            staffIds.add("");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                staffNames.add(obj.getString("name"));
                                staffIds.add(obj.getString("user_id"));
                            }
                            btnSubmit.setEnabled(true);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, staffNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerStaff.setAdapter(adapter);

                    } catch (JSONException e) {
                        String errorSnippet = response.length() > 100 ? response.substring(0, 100) : response;
                        Toast.makeText(this, "PHP Error: " + errorSnippet.trim(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: Check IP", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void submitFeedback() {
        int index = spinnerStaff.getSelectedItemPosition();
        if (index <= 0) {
            Toast.makeText(this, "Please select a staff member", Toast.LENGTH_SHORT).show();
            return;
        }

        final String staffIdStr = staffIds.get(index);
        final String message = etFeedbackMessage.getText().toString().trim();
        final String rating = String.valueOf((int)ratingBar.getRating());

        if (ratingBar.getRating() == 0 || message.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        hideKeyboard();

        String url = Config.getUrl("feedback.php");
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    btnSubmit.setEnabled(true);
                    if (response.trim().equalsIgnoreCase("Success")) {
                        Toast.makeText(this, "Feedback sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + response.trim(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", studentId);
                params.put("staff_id", staffIdStr);
                params.put("rating", rating);
                params.put("message", message);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
