package com.example.ksjcomplaintnmaintenance;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.graphics.Insets;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentDashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuIcon;
    private TextView notificationBadge, tvWelcome;
    private String userId, fullName;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int lastNotificationCount = -1;
    private static final String CHANNEL_ID = "KSJ_NOTIF_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);

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

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.logout_btn);
        notificationBadge = findViewById(R.id.notification_badge);
        tvWelcome = findViewById(R.id.r68vqrxtzgzm);

        if (tvWelcome != null && fullName != null && !fullName.isEmpty()) {
            if (fullName.length() > 15) {
                tvWelcome.setText("Welcome Back,\n" + fullName + "!");
            } else {
                tvWelcome.setText("Welcome Back, " + fullName + "!");
            }
        }

        createNotificationChannel();

        if (menuIcon != null && drawerLayout != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_student_dashboard) {
                    // Already here
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
                if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        LinearLayout historyClick = findViewById(R.id.history_click);
        if (historyClick != null) historyClick.setOnClickListener(v -> startActivity(new Intent(this, StudentHistory.class).putExtra("USER_ID", userId)));

        LinearLayout pendingClick = findViewById(R.id.pending_click);
        if (pendingClick != null) pendingClick.setOnClickListener(v -> startActivity(new Intent(this, StudentPending.class).putExtra("USER_ID", userId)));

        LinearLayout notificationClick = findViewById(R.id.notification_click);
        if (notificationClick != null) notificationClick.setOnClickListener(v -> startActivity(new Intent(this, StudentNotification.class).putExtra("USER_ID", userId)));

        LinearLayout complaintClick = findViewById(R.id.complaint_click);
        if (complaintClick != null) complaintClick.setOnClickListener(v -> startActivity(new Intent(this, StudentComplaintForm.class).putExtra("USER_ID", userId)));

        LinearLayout feedbackClick = findViewById(R.id.feedback_click);
        if (feedbackClick != null) feedbackClick.setOnClickListener(v -> startActivity(new Intent(this, StudentFeedbackForm.class).putExtra("USER_ID", userId)));

        startNotificationPolling();
    }

    private void startNotificationPolling() {
        runnable = new Runnable() {
            @Override
            public void run() {
                checkNewNotifications();
                handler.postDelayed(this, 15000); // Check every 15 seconds
            }
        };
        handler.post(runnable);
    }

    private void checkNewNotifications() {
        if (userId == null) return;
        String url = Config.getUrl("get_notifications.php");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        int unreadCount = 0;
                        for (int i = 0; i < array.length(); i++) {
                            if (array.getJSONObject(i).getInt("is_read") == 0) {
                                unreadCount++;
                            }
                        }

                        // Update Badge
                        if (unreadCount > 0) {
                            notificationBadge.setVisibility(View.VISIBLE);
                            notificationBadge.setText(String.valueOf(unreadCount));
                        } else {
                            notificationBadge.setVisibility(View.GONE);
                        }

                        // Check if we should trigger a system notification
                        if (unreadCount > lastNotificationCount && lastNotificationCount != -1) {
                            JSONObject latest = array.getJSONObject(0); // Assuming sorted by date DESC
                            showSystemNotification("KSJ Maintenance", latest.getString("message"));
                        }
                        
                        lastNotificationCount = unreadCount;

                    } catch (Exception e) {
                        Log.e("NOTIF_POLL", e.getMessage());
                    }
                },
                error -> Log.e("NOTIF_POLL", "Error polling")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showSystemNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        Intent intent = new Intent(this, StudentNotification.class);
        intent.putExtra("USER_ID", userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Maintenance Notifications";
            String description = "Notifications for complaint updates";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop polling when app closed
    }

    private void performLogout() {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}