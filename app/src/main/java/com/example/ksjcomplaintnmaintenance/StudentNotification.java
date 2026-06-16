package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
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
import com.example.ksjcomplaintnmaintenance.NotificationCardView.NotificationData;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentNotification extends AppCompatActivity {

    private LinearLayout notificationsContainer;
    private String userId;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_notification);

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
            if (id == R.id.nav_student_dashboard) {
                startActivity(new Intent(this, StudentDashboard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_complaint) {
                startActivity(new Intent(this, StudentComplaintForm.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_history) {
                startActivity(new Intent(this, StudentHistory.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_notifications) {
                // Already here
            } else if (id == R.id.nav_student_feedback) {
                startActivity(new Intent(this, StudentFeedbackForm.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        notificationsContainer = findViewById(R.id.complaints_container);

        fetchNotifications();
    }

    private void fetchNotifications() {
        String url = Config.getUrl("get_notifications.php");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    notificationsContainer.removeAllViews();
                    try {
                        JSONArray jsonArray = new JSONArray(response.trim());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                NotificationData data = new NotificationData(
                                        obj.getString("notification_id"),
                                        obj.getString("message"),
                                        obj.getString("created_at"),
                                        obj.getInt("is_read")
                                );

                                CardView card = NotificationCardView.createNotificationCard(this, data);
                                if (card != null) {
                                    card.setOnClickListener(v -> markAsRead(data.getId()));
                                    notificationsContainer.addView(card);
                                }
                            }
                        } else {
                            showEmptyMessage("No notifications yet");
                        }
                    } catch (JSONException e) {
                        Log.e("NOTIF_ERR", e.getMessage());
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

    private void markAsRead(String notifId) {
        String url = Config.getUrl("mark_as_read.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        fetchNotifications(); // Refresh list
                    }
                },
                error -> Log.e("NOTIF_ERR", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("notification_id", notifId);
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
        notificationsContainer.addView(tv);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
