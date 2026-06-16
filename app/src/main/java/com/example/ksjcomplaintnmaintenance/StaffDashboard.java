package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.graphics.Color;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class StaffDashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private String staffId, fullName;
    private TextView tvPendingCount, tvAvgRating, tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_dashboard);

        // Ensure status bar icons are light (white) since background is dark maroon
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        staffId = getIntent().getStringExtra("USER_ID");
        fullName = getIntent().getStringExtra("FULL_NAME");

        if (fullName == null || fullName.isEmpty()) {
            fullName = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("FULL_NAME", "");
        }

        tvPendingCount = findViewById(R.id.tv_pending_tasks_count);
        tvAvgRating = findViewById(R.id.tv_staff_avg_rating);
        tvWelcome = findViewById(R.id.r68vqrxtzgzm);

        if (fullName != null && !fullName.isEmpty()) {
            if (fullName.length() > 15) {
                tvWelcome.setText("Welcome Back,\n" + fullName + "!");
                if (menuIcon != null) menuIcon.setVisibility(View.INVISIBLE);
            } else {
                tvWelcome.setText("Welcome Back, " + fullName + "!");
            }
        }

        findViewById(R.id.card_staff_feedback_stat).setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffFeedbackBoard.class);
            intent.putExtra("STAFF_ID", staffId);
            startActivity(intent);
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.rksubsmsym5a);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_staff_dashboard) {
                // Already here
            } else if (id == R.id.nav_staff_assigned_tasks) {
                Intent intent = new Intent(this, StaffAssignedTask.class);
                intent.putExtra("USER_ID", staffId);
                startActivity(intent);
            } else if (id == R.id.nav_staff_feedback) {
                Intent intent = new Intent(this, StaffFeedbackBoard.class);
                intent.putExtra("STAFF_ID", staffId);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        fetchStaffStats();

        LinearLayout complaintClick = findViewById(R.id.complaint_click);
        complaintClick.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffAssignedTask.class);
            intent.putExtra("USER_ID", staffId);
            startActivity(intent);
        });

        LinearLayout feedbackClick = findViewById(R.id.feedback_click);
        feedbackClick.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffFeedbackBoard.class);
            intent.putExtra("STAFF_ID", staffId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStaffStats();
    }

    private void fetchStaffStats() {
        // Fetch Pending Tasks count specifically for this staff
        String urlTasks = Config.getUrl("get_assigned_tasks.php");
        StringRequest tasksRequest = new StringRequest(Request.Method.POST, urlTasks,
                response -> {
                    Log.d("STAFF_STATS", "Tasks Response: " + response);
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        int pendingCount = 0;
                        for (int i = 0; i < array.length(); i++) {
                            String status = array.getJSONObject(i).optString("status", "").toLowerCase();
                            if (status.contains("pending")) {
                                pendingCount++;
                            }
                        }
                        tvPendingCount.setText(String.valueOf(pendingCount));
                    } catch (Exception e) { 
                        Log.e("STAFF_STATS", "JSON Error: " + e.getMessage());
                        tvPendingCount.setText("0"); 
                    }
                }, error -> {
                    Log.e("STAFF_STATS", "Volley Error: " + error.toString());
                    tvPendingCount.setText("ERR");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("staff_id", staffId);
                return params;
            }
        };

        // Fetch Feedback Avg Rating specifically for this staff
        String urlFeedback = Config.getUrl("get_all_feedback.php");
        StringRequest feedbackRequest = new StringRequest(Request.Method.POST, urlFeedback,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        double total = 0;
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                total += array.getJSONObject(i).optInt("rating", 0);
                            }
                            double avg = total / array.length();
                            tvAvgRating.setText(String.format("%.1f ★", avg));
                        } else {
                            tvAvgRating.setText("0.0 ★");
                        }
                    } catch (Exception e) { tvAvgRating.setText("0.0 ★"); }
                }, error -> tvAvgRating.setText("ERR")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("staff_id", staffId); // Filter by staff_id on server
                return params;
            }
        };

        Volley.newRequestQueue(this).add(tasksRequest);
        Volley.newRequestQueue(this).add(feedbackRequest);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}