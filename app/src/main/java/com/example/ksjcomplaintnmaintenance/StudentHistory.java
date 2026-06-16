package com.example.ksjcomplaintnmaintenance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
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

public class StudentHistory extends AppCompatActivity {

    private LinearLayout complaintsContainer;
    private String userId;
    private JSONArray fullComplaintsList; // Holds all data from server
    private String currentStatusFilter = "all"; // To remember which button is clicked
    private String currentPriorityFilter = "all";
    private EditText etSearch;

    // Filter Buttons
    private LinearLayout btnAll, btnPending, btnInProgress, btnCompleted;
    private TextView txtAll, txtPending, txtInProgress, txtCompleted;

    private LinearLayout btnLow, btnMedium, btnHigh;
    private TextView txtLow, txtMedium, txtHigh;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_history);

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
                // Already here
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
        etSearch = findViewById(R.id.search_input); // The new EditText

        // 1. Initialize Layouts
        btnAll = findViewById(R.id.all_filter);
        btnPending = findViewById(R.id.pending_filter);
        btnInProgress = findViewById(R.id.inprogress_filter);
        btnCompleted = findViewById(R.id.completed_filter);

        btnLow = findViewById(R.id.low_filter);
        btnMedium = findViewById(R.id.medium_filter);
        btnHigh = findViewById(R.id.high_filter);

        // 2. Initialize TextViews
        txtAll = findViewById(R.id.r8bjuwvs2d1d);
        txtPending = findViewById(R.id.r8bjuwvs2d1dj);
        txtInProgress = findViewById(R.id.r8bjuwvs2d1djs);
        txtCompleted = findViewById(R.id.r8bjuwvs2d1djsr);

        txtLow = findViewById(R.id.txt_low);
        txtMedium = findViewById(R.id.txt_medium);
        txtHigh = findViewById(R.id.txt_high);

        // 3. Set Click Listeners for buttons (Togglable)
        btnAll.setOnClickListener(v -> {
            currentStatusFilter = "all";
            currentPriorityFilter = "all";
            applyFilterAndSearch();
        });

        btnPending.setOnClickListener(v -> {
            currentStatusFilter = currentStatusFilter.equals("pending") ? "all" : "pending";
            applyFilterAndSearch();
        });

        btnInProgress.setOnClickListener(v -> {
            currentStatusFilter = currentStatusFilter.equals("in_progress") ? "all" : "in_progress";
            applyFilterAndSearch();
        });

        btnCompleted.setOnClickListener(v -> {
            currentStatusFilter = currentStatusFilter.equals("resolved") ? "all" : "resolved";
            applyFilterAndSearch();
        });

        btnLow.setOnClickListener(v -> {
            currentPriorityFilter = currentPriorityFilter.equals("low") ? "all" : "low";
            applyFilterAndSearch();
        });

        btnMedium.setOnClickListener(v -> {
            currentPriorityFilter = currentPriorityFilter.equals("medium") ? "all" : "medium";
            applyFilterAndSearch();
        });

        btnHigh.setOnClickListener(v -> {
            currentPriorityFilter = currentPriorityFilter.equals("high") ? "all" : "high";
            applyFilterAndSearch();
        });

        // 4. Add Search Listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch(); // Filter list as you type
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchComplaintsFromServer();
    }

    private void fetchComplaintsFromServer() {
        // Correct IP for Physical Phone
        String url = Config.getUrl("get_history.php");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        fullComplaintsList = new JSONArray(response.trim());
                        applyFilterAndSearch(); // Initial load
                    } catch (JSONException e) {
                        showErrorMessage("Data Format Error");
                    }
                },
                error -> showErrorMessage("Connection Failed. Check XAMPP.")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId != null ? userId : "0");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void applyFilterAndSearch() {
        if (fullComplaintsList == null) return;

        complaintsContainer.removeAllViews();
        updateButtonUI();

        String query = etSearch.getText().toString().toLowerCase().trim();

        try {
            boolean found = false;
            for (int i = 0; i < fullComplaintsList.length(); i++) {
                JSONObject obj = fullComplaintsList.getJSONObject(i);

                String dbStatus = obj.optString("status", "pending").toLowerCase();
                String dbPriority = obj.optString("priority", "low").toLowerCase();
                String dbType = obj.optString("complaint_type", "No Title").toLowerCase();

                boolean statusMatches = currentStatusFilter.equals("all") ||
                        dbStatus.equals(currentStatusFilter) ||
                        dbStatus.replace("_", " ").equals(currentStatusFilter.replace("_", " "));

                boolean priorityMatches = currentPriorityFilter.equals("all") || 
                        dbPriority.equals(currentPriorityFilter);

                boolean searchMatches = dbType.contains(query);

                if (statusMatches && priorityMatches && searchMatches) {
                    found = true;
                    addCardToUI(obj);
                }
            }
            if (!found) {
                String msg = query.isEmpty() ? "No matching complaints."
                        : "No results for '" + query + "'";
                showNoComplaintsMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addCardToUI(JSONObject obj) throws JSONException {
        String id = obj.optString("complaint_id");
        String name = obj.optString("name");
        String phone = obj.optString("phone_number");
        String type = obj.optString("complaint_type");
        String priority = obj.optString("priority", "medium");
        String date = obj.optString("created_at");
        String status = obj.optString("status");
        String room = obj.optString("room_number");
        String desc = obj.optString("description");
        String staffName = obj.optString("staff_name", "");
        String staffPhone = obj.optString("staff_phone", "");

        // UPDATE: Using the 11-parameter constructor
        ComplaintData data = new ComplaintData(id, name, phone, type, priority, date, status, room, desc, staffName, staffPhone);

        CardView card = ComplaintCardView.createComplaintCard(this, data);

        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(StudentHistory.this, StudentViewComplaintHistory.class);
                intent.putExtra("COMPLAINT_ID", id);
                startActivity(intent);
            });
            complaintsContainer.addView(card);
        }
    }

    private void updateButtonUI() {
        // Reset Status Buttons
        resetButtonStyle(btnAll, txtAll);
        resetButtonStyle(btnPending, txtPending);
        resetButtonStyle(btnInProgress, txtInProgress);
        resetButtonStyle(btnCompleted, txtCompleted);

        // Reset Priority Buttons
        resetButtonStyle(btnLow, txtLow);
        resetButtonStyle(btnMedium, txtMedium);
        resetButtonStyle(btnHigh, txtHigh);

        // Highlight Active Status
        if (currentStatusFilter.equalsIgnoreCase("all")) setActiveButtonStyle(btnAll, txtAll);
        else if (currentStatusFilter.equalsIgnoreCase("pending")) setActiveButtonStyle(btnPending, txtPending);
        else if (currentStatusFilter.equalsIgnoreCase("in_progress")) setActiveButtonStyle(btnInProgress, txtInProgress);
        else if (currentStatusFilter.equalsIgnoreCase("resolved")) setActiveButtonStyle(btnCompleted, txtCompleted);

        // Highlight Active Priority
        if (currentPriorityFilter.equalsIgnoreCase("low")) setActiveButtonStyle(btnLow, txtLow);
        else if (currentPriorityFilter.equalsIgnoreCase("medium")) setActiveButtonStyle(btnMedium, txtMedium);
        else if (currentPriorityFilter.equalsIgnoreCase("high")) setActiveButtonStyle(btnHigh, txtHigh);
    }

    private void resetButtonStyle(LinearLayout layout, TextView text) {
        if (layout != null) layout.setBackgroundResource(R.drawable.se6e6e6sw1cr6);
        if (text != null) text.setTextColor(Color.parseColor("#1A1A1A"));
    }

    private void setActiveButtonStyle(LinearLayout layout, TextView text) {
        if (layout != null) layout.setBackgroundColor(Color.parseColor("#670E10"));
        if (text != null) text.setTextColor(Color.WHITE);
    }

    private void showNoComplaintsMessage(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 100, 0, 0);
        complaintsContainer.addView(tv);
    }

    private void showErrorMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
