package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.WindowInsetsControllerCompat;
import android.graphics.Color;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminDashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private String userId, fullName;
    private TextView tvTotalComplaints, tvAvgRating, tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        
        // Ensure status bar icons are light (white) since background is dark maroon
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getStringExtra("USER_ID");
        fullName = getIntent().getStringExtra("FULL_NAME");

        if (fullName == null || fullName.isEmpty()) {
            fullName = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("FULL_NAME", "");
        }

        tvTotalComplaints = findViewById(R.id.tv_total_complaints_count);
        tvAvgRating = findViewById(R.id.tv_avg_rating);
        tvWelcome = findViewById(R.id.r68vqrxtzgzm);

        if (tvWelcome != null) {
            tvWelcome.setText("Welcome Back, Admin!");
        }
        
        findViewById(R.id.card_feedback_stat).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminFeedbackBoard.class).putExtra("USER_ID", userId));
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.rksubsmsym5a);

        // Drawer Setup
        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_dashboard) {
                // Already here
            } else if (id == R.id.nav_user_management) {
                startActivity(new Intent(this, UserManagement.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_complaint_list) {
                startActivity(new Intent(this, AdminComplaintList.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_create_user) {
                startActivity(new Intent(this, AdminCreateUser.class).putExtra("USER_ID", userId));
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

        fetchStats();

        LinearLayout student_pending = findViewById(R.id.user_managementbtn);
        student_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Heading to User Management");

                Intent intent = new Intent(AdminDashboard.this, UserManagement.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        LinearLayout admin_complaint = findViewById(R.id.admin_complaint);
        admin_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Heading to Complaint List");

                Intent intent = new Intent(AdminDashboard.this, AdminComplaintList.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        LinearLayout admin_create_user = findViewById(R.id.admin_create_user);
        admin_create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Heading to Create New User");

                Intent intent = new Intent(AdminDashboard.this, AdminCreateUser.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        LinearLayout admin_monthly_summary = findViewById(R.id.admin_monthly_summary);
        admin_monthly_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Heading to Monthly Summary");

                Intent intent = new Intent(AdminDashboard.this, AdminMonthlySummary.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        LinearLayout admin_feedback_board = findViewById(R.id.admin_feedback_board);
        admin_feedback_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Heading to Feedback Board");

                Intent intent = new Intent(AdminDashboard.this, AdminFeedbackBoard.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    private void fetchStats() {
        // Fetch Complaints Count (Filter for Pending)
        String urlComplaints = Config.getUrl("get_all_complaints.php");
        StringRequest complaintsRequest = new StringRequest(Request.Method.POST, urlComplaints,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        int pendingCount = 0;
                        for (int i = 0; i < array.length(); i++) {
                            String status = array.getJSONObject(i).optString("status", "").toLowerCase();
                            if (status.contains("pending")) {
                                pendingCount++;
                            }
                        }
                        tvTotalComplaints.setText(String.valueOf(pendingCount));
                    } catch (Exception e) { tvTotalComplaints.setText("0"); }
                }, error -> tvTotalComplaints.setText("ERR"));

        // Fetch Feedback Avg Rating
        String urlFeedback = Config.getUrl("get_all_feedback.php");
        StringRequest feedbackRequest = new StringRequest(Request.Method.POST, urlFeedback,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        double total = 0;
                        for (int i = 0; i < array.length(); i++) {
                            total += array.getJSONObject(i).optInt("rating", 0);
                        }
                        if (array.length() > 0) {
                            double avg = total / array.length();
                            tvAvgRating.setText(String.format("%.1f ★", avg));
                        } else {
                            tvAvgRating.setText("0.0 ★");
                        }
                    } catch (Exception e) { tvAvgRating.setText("0.0 ★"); }
                }, error -> tvAvgRating.setText("ERR"));

        Volley.newRequestQueue(this).add(complaintsRequest);
        Volley.newRequestQueue(this).add(feedbackRequest);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}