package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ksjcomplaintnmaintenance.ComplaintCardView.ComplaintData;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentPending extends AppCompatActivity {

    private LinearLayout complaintsContainer;
    private String userId;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_pending);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.ry2nn68ppi9);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_student_dashboard) {
                startActivity(new Intent(this, StudentDashboard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_complaint) {
                startActivity(new Intent(this, StudentComplaintForm.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_history) {
                startActivity(new Intent(this, StudentHistory.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_notifications) {
                startActivity(new Intent(this, StudentNotification.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_feedback) {
                startActivity(new Intent(this, StudentFeedbackForm.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        userId = getIntent().getStringExtra("USER_ID");
        complaintsContainer = findViewById(R.id.complaints_container);

        fetchPendingComplaints();
    }

    private void fetchPendingComplaints() {
        String url = Config.getUrl("get_history.php");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    complaintsContainer.removeAllViews();
                    try {
                        JSONArray jsonArray = new JSONArray(response.trim());
                        boolean found = false;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String status = obj.optString("status", "").toLowerCase();

                            // Filter: Only show In Progress
                            if (status.contains("progress")) {
                                found = true;
                                String id = obj.getString("complaint_id");
                                ComplaintData data = new ComplaintData(
                                        id,
                                        obj.optString("name", ""),
                                        obj.optString("phone_number", ""),
                                        obj.optString("complaint_type", ""),
                                        obj.optString("priority", "medium"),
                                        obj.optString("created_at", ""),
                                        obj.getString("status"),
                                        obj.optString("room_number", ""),
                                        obj.optString("description", ""),
                                        obj.optString("staff_name", ""),
                                        obj.optString("staff_phone", "")
                                );

                                CardView card = ComplaintCardView.createComplaintCard(this, data);
                                if (card != null) {
                                    card.setOnClickListener(v -> {
                                        Intent intent = new Intent(StudentPending.this, StudentViewComplaintHistory.class);
                                        intent.putExtra("COMPLAINT_ID", id);
                                        startActivity(intent);
                                    });
                                    complaintsContainer.addView(card);
                                }
                            }
                        }
                        
                        if (!found) {
                            showEmptyMessage("No in-progress complaints");
                        }
                    } catch (JSONException e) {
                        Log.e("PENDING", "JSON Error: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId != null ? userId : "0");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showEmptyMessage(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(32, 100, 32, 32);
        complaintsContainer.addView(tv);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
