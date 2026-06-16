package com.example.ksjcomplaintnmaintenance;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffAssignedTask extends AppCompatActivity {

    private LinearLayout complaintsContainer;
    private EditText etSearch;
    private LinearLayout filterAll, filterPending, filterInProgress, filterCompleted;
    private LinearLayout filterLow, filterMedium, filterHigh;
    private TextView txtAll, txtPending, txtInProgress, txtCompleted, txtLow, txtMedium, txtHigh;
    
    private String staffId;
    private List<ComplaintData> fullTaskList = new ArrayList<>();
    private String currentStatusFilter = "all";
    private String currentPriorityFilter = "all";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_assigned_task);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.ry2nn68ppi9);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_staff_dashboard) {
                Intent intent = new Intent(this, StaffDashboard.class);
                intent.putExtra("USER_ID", staffId);
                startActivity(intent);
            } else if (id == R.id.nav_staff_assigned_tasks) {
                // Already here
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

        staffId = getIntent().getStringExtra("USER_ID");
        
        initViews();
        setupListeners();
        fetchAssignedTasks();
    }

    private void initViews() {
        complaintsContainer = findViewById(R.id.complaints_container);
        etSearch = findViewById(R.id.user_search_input);
        
        filterAll = findViewById(R.id.all_filter);
        filterPending = findViewById(R.id.pending_filter);
        filterInProgress = findViewById(R.id.inprogress_filter);
        filterCompleted = findViewById(R.id.completed_filter);
        
        filterLow = findViewById(R.id.lowprio_filter);
        filterMedium = findViewById(R.id.mediumprio_filter);
        filterHigh = findViewById(R.id.highprio_filter);

        txtAll = findViewById(R.id.txt_all);
        txtPending = findViewById(R.id.txt_pending);
        txtInProgress = findViewById(R.id.txt_inprogress);
        txtCompleted = findViewById(R.id.txt_completed);
        txtLow = findViewById(R.id.txt_low);
        txtMedium = findViewById(R.id.txt_medium);
        txtHigh = findViewById(R.id.txt_high);
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterAll.setOnClickListener(v -> { 
            currentStatusFilter = "all"; 
            currentPriorityFilter = "all"; 
            applyFilters(); 
        });

        filterPending.setOnClickListener(v -> { 
            currentStatusFilter = currentStatusFilter.equals("pending") ? "all" : "pending"; 
            applyFilters(); 
        });

        filterInProgress.setOnClickListener(v -> { 
            currentStatusFilter = currentStatusFilter.equals("in_progress") ? "all" : "in_progress"; 
            applyFilters(); 
        });

        filterCompleted.setOnClickListener(v -> { 
            currentStatusFilter = currentStatusFilter.equals("resolved") ? "all" : "resolved"; 
            applyFilters(); 
        });
        
        filterLow.setOnClickListener(v -> { 
            currentPriorityFilter = currentPriorityFilter.equals("low") ? "all" : "low"; 
            applyFilters(); 
        });

        filterMedium.setOnClickListener(v -> { 
            currentPriorityFilter = currentPriorityFilter.equals("medium") ? "all" : "medium"; 
            applyFilters(); 
        });

        filterHigh.setOnClickListener(v -> { 
            currentPriorityFilter = currentPriorityFilter.equals("high") ? "all" : "high"; 
            applyFilters(); 
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAssignedTasks();
    }

    private void fetchAssignedTasks() {
        String url = Config.getUrl("get_assigned_tasks.php");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    fullTaskList.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(response.trim());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ComplaintData data = new ComplaintData(
                                    obj.optString("complaint_id", ""),
                                    obj.optString("name", "N/A"),
                                    obj.optString("phone_number", ""),
                                    obj.optString("complaint_type", ""),
                                    obj.optString("priority", "medium"),
                                    obj.optString("created_at", ""),
                                    obj.optString("status", "pending"),
                                    obj.optString("room_number", "N/A"),
                                    obj.optString("description", ""),
                                    obj.optString("staff_name", ""),
                                    obj.optString("staff_phone", "")
                            );
                            fullTaskList.add(data);
                        }
                        applyFilters();
                    } catch (JSONException e) {
                        Log.e("STAFF_TASKS", "JSON Error: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("staff_id", staffId != null ? staffId : "0");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void applyFilters() {
        complaintsContainer.removeAllViews();
        updateButtonUI();
        
        String query = etSearch.getText().toString().toLowerCase().trim();
        boolean found = false;

        for (ComplaintData task : fullTaskList) {
            String dbStatus = task.getProgress().toLowerCase();
            String priority = task.getPriority().toLowerCase();

            // Unified Status Filter Logic (Consistent with Admin side)
            boolean statusMatches = currentStatusFilter.equals("all") || 
                                   dbStatus.contains(currentStatusFilter) ||
                                   dbStatus.replace("_", " ").equals(currentStatusFilter);

            // Priority Filter Logic
            boolean priorityMatches = currentPriorityFilter.equals("all") || 
                                     priority.equals(currentPriorityFilter);
            
            // Unified Search Logic (Title, Name, Room, ID, Status)
            boolean searchMatches = query.isEmpty() || 
                                    task.getComplaintType().toLowerCase().contains(query) || 
                                    task.getStudentName().toLowerCase().contains(query) ||
                                    task.getRoomNumber().toLowerCase().contains(query) ||
                                    task.getComplaintId().toLowerCase().contains(query) ||
                                    dbStatus.contains(query);

            if (statusMatches && priorityMatches && searchMatches) {
                CardView card = ComplaintCardView.createComplaintCard(this, task);
                if (card != null) {
                    card.setOnClickListener(v -> {
                        Intent intent = new Intent(this, StaffViewComplaintTask.class);
                        intent.putExtra("COMPLAINT_ID", task.getComplaintId());
                        startActivity(intent);
                    });
                    complaintsContainer.addView(card);
                    found = true;
                }
            }
        }
        
        if (!found) {
            showEmptyMessage(query.isEmpty() ? "No tasks found" : "No results for '" + query + "'");
        }
    }

    private void updateButtonUI() {
        resetStyle(filterAll, txtAll);
        resetStyle(filterPending, txtPending);
        resetStyle(filterInProgress, txtInProgress);
        resetStyle(filterCompleted, txtCompleted);
        resetStyle(filterLow, txtLow);
        resetStyle(filterMedium, txtMedium);
        resetStyle(filterHigh, txtHigh);

        if (currentStatusFilter.equals("all")) setActive(filterAll, txtAll);
        else if (currentStatusFilter.equals("pending")) setActive(filterPending, txtPending);
        else if (currentStatusFilter.equals("in_progress")) setActive(filterInProgress, txtInProgress);
        else if (currentStatusFilter.equals("resolved")) setActive(filterCompleted, txtCompleted);

        if (currentPriorityFilter.equals("low")) setActive(filterLow, txtLow);
        else if (currentPriorityFilter.equals("medium")) setActive(filterMedium, txtMedium);
        else if (currentPriorityFilter.equals("high")) setActive(filterHigh, txtHigh);
    }

    private void resetStyle(LinearLayout l, TextView t) {
        if (l != null) l.setBackgroundResource(R.drawable.se6e6e6sw1cr6);
        if (t != null) t.setTextColor(Color.parseColor("#1A1A1A"));
    }

    private void setActive(LinearLayout l, TextView t) {
        if (l != null) l.setBackgroundColor(Color.parseColor("#670E10"));
        if (t != null) t.setTextColor(Color.WHITE);
    }

    private void showEmptyMessage(String msg) {
        TextView emptyView = new TextView(this);
        emptyView.setText(msg);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setPadding(32, 100, 32, 32);
        complaintsContainer.addView(emptyView);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
