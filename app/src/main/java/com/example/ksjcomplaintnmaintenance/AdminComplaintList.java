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

import java.util.HashMap;
import java.util.Map;

public class AdminComplaintList extends AppCompatActivity {

    private LinearLayout complaintsContainer;
    private EditText etSearch;
    private JSONArray fullComplaintsList;
    private String currentStatusFilter = "all";
    private String currentPriorityFilter = "all";
    private String currentAssignmentFilter = "all";
    private String filterUserId;

    private LinearLayout btnAll, btnPending, btnInProgress, btnCompleted;
    private TextView txtAll, txtPending, txtInProgress, txtCompleted;
    private LinearLayout btnLow, btnMedium, btnHigh;
    private TextView txtLow, txtMedium, txtHigh;
    private LinearLayout btnAssigned, btnUnassigned;
    private TextView txtAssigned, txtUnassigned;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_complaint_list);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.header_back_btn); // Using this as menu icon

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_dashboard) {
                startActivity(new Intent(this, AdminDashboard.class).putExtra("USER_ID", filterUserId));
            } else if (id == R.id.nav_user_management) {
                startActivity(new Intent(this, UserManagement.class).putExtra("USER_ID", filterUserId));
            } else if (id == R.id.nav_complaint_list) {
                // Already here
            } else if (id == R.id.nav_create_user) {
                startActivity(new Intent(this, AdminCreateUser.class).putExtra("USER_ID", filterUserId));
            } else if (id == R.id.nav_feedback_board) {
                startActivity(new Intent(this, AdminFeedbackBoard.class).putExtra("USER_ID", filterUserId));
            } else if (id == R.id.nav_monthly_summary) {
                startActivity(new Intent(this, AdminMonthlySummary.class).putExtra("USER_ID", filterUserId));
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        filterUserId = getIntent().getStringExtra("USER_ID"); // Standardized to USER_ID
        complaintsContainer = findViewById(R.id.complaints_container);
        etSearch = findViewById(R.id.admin_search_input);

        // Initialize Status Filter Buttons
        btnAll = findViewById(R.id.admin_filter_all);
        btnPending = findViewById(R.id.admin_filter_pending);
        btnInProgress = findViewById(R.id.admin_filter_inprogress);
        btnCompleted = findViewById(R.id.admin_filter_completed);
        txtAll = findViewById(R.id.txt_all);
        txtPending = findViewById(R.id.txt_pending);
        txtInProgress = findViewById(R.id.txt_inprogress);
        txtCompleted = findViewById(R.id.txt_completed);

        // Initialize Priority Filter Buttons
        btnLow = findViewById(R.id.admin_filter_low);
        btnMedium = findViewById(R.id.admin_filter_medium);
        btnHigh = findViewById(R.id.admin_filter_high);
        txtLow = findViewById(R.id.txt_low);
        txtMedium = findViewById(R.id.txt_medium);
        txtHigh = findViewById(R.id.txt_high);

        // Initialize Assignment Filter Buttons
        btnAssigned = findViewById(R.id.admin_filter_assigned);
        btnUnassigned = findViewById(R.id.admin_filter_unassigned);
        txtAssigned = findViewById(R.id.txt_assigned);
        txtUnassigned = findViewById(R.id.txt_unassigned);

        // Click Listeners
        if (btnAll != null) btnAll.setOnClickListener(v -> { currentStatusFilter = "all"; applyFilterAndSearch(); });
        if (btnPending != null) btnPending.setOnClickListener(v -> { 
            currentStatusFilter = currentStatusFilter.equals("pending") ? "all" : "pending"; 
            applyFilterAndSearch(); 
        });
        if (btnInProgress != null) btnInProgress.setOnClickListener(v -> { 
            currentStatusFilter = currentStatusFilter.equals("in progress") ? "all" : "in progress"; 
            applyFilterAndSearch(); 
        });
        if (btnCompleted != null) btnCompleted.setOnClickListener(v -> { 
            currentStatusFilter = currentStatusFilter.equals("resolved") ? "all" : "resolved"; 
            applyFilterAndSearch(); 
        });

        if (btnLow != null) btnLow.setOnClickListener(v -> { 
            currentPriorityFilter = currentPriorityFilter.equals("low") ? "all" : "low"; 
            applyFilterAndSearch(); 
        });
        if (btnMedium != null) btnMedium.setOnClickListener(v -> { 
            currentPriorityFilter = currentPriorityFilter.equals("medium") ? "all" : "medium"; 
            applyFilterAndSearch(); 
        });
        if (btnHigh != null) btnHigh.setOnClickListener(v -> { 
            currentPriorityFilter = currentPriorityFilter.equals("high") ? "all" : "high"; 
            applyFilterAndSearch(); 
        });

        if (btnAssigned != null) btnAssigned.setOnClickListener(v -> { 
            currentAssignmentFilter = currentAssignmentFilter.equals("assigned") ? "all" : "assigned"; 
            applyFilterAndSearch(); 
        });
        if (btnUnassigned != null) btnUnassigned.setOnClickListener(v -> { 
            currentAssignmentFilter = currentAssignmentFilter.equals("unassigned") ? "all" : "unassigned"; 
            applyFilterAndSearch(); 
        });

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilterAndSearch(); }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
        fetchComplaintsFromServer();
    }

    private void fetchComplaintsFromServer() {
        String url = Config.getUrl("get_all_complaints.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        fullComplaintsList = new JSONArray(response.trim());
                        applyFilterAndSearch();
                    } catch (JSONException e) { Log.e("JSON_ERR", e.getMessage()); }
                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void applyFilterAndSearch() {
        if (fullComplaintsList == null || complaintsContainer == null) return;
        complaintsContainer.removeAllViews();
        updateButtonUI();

        String query = etSearch.getText().toString().toLowerCase().trim();

        try {
            for (int i = 0; i < fullComplaintsList.length(); i++) {
                JSONObject obj = fullComplaintsList.getJSONObject(i);

                String dbStatus = obj.optString("status", "pending").toLowerCase();
                String dbPriority = obj.optString("priority", "low").toLowerCase();
                String staffId = obj.optString("assigned_staff_id", "0");
                boolean isAssigned = !staffId.equals("0") && !staffId.isEmpty() && !staffId.equals("null");

                // Unified Status Filter Logic
                boolean statusMatch = currentStatusFilter.equals("all");
                if (!statusMatch) {
                    if (currentStatusFilter.equals("resolved")) {
                        statusMatch = dbStatus.contains("resolved") || dbStatus.contains("completed");
                    } else if (currentStatusFilter.equals("in progress")) {
                        statusMatch = dbStatus.contains("progress");
                    } else {
                        statusMatch = dbStatus.equals(currentStatusFilter);
                    }
                }
                boolean priorityMatch = currentPriorityFilter.equals("all") || dbPriority.equals(currentPriorityFilter);
                boolean assignMatch = currentAssignmentFilter.equals("all") ||
                        (currentAssignmentFilter.equals("assigned") && isAssigned) ||
                        (currentAssignmentFilter.equals("unassigned") && !isAssigned);

                boolean searchMatch = query.isEmpty() || obj.optString("complaint_type").toLowerCase().contains(query)
                        || obj.optString("name").toLowerCase().contains(query);

                if (statusMatch && priorityMatch && assignMatch && searchMatch) {
                    addCardToUI(obj);
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void addCardToUI(JSONObject obj) throws JSONException {
        ComplaintData data = new ComplaintData(
                obj.getString("complaint_id"),
                obj.getString("name"),
                obj.optString("phone_number"),
                obj.getString("complaint_type"),
                obj.optString("priority", "medium"),
                obj.getString("created_at"),
                obj.getString("status"),
                obj.getString("room_number"),
                obj.getString("description"),
                obj.optString("staff_name", "Unassigned"),
                obj.optString("staff_phone", "")
        );

        CardView card = ComplaintCardView.createComplaintCard(this, data);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewComplaint.class);
            intent.putExtra("COMPLAINT_ID", data.getComplaintId());
            startActivity(intent);
        });
        complaintsContainer.addView(card);
    }

    private void updateButtonUI() {
        resetStyle(btnAll, txtAll); resetStyle(btnPending, txtPending);
        resetStyle(btnInProgress, txtInProgress); resetStyle(btnCompleted, txtCompleted);
        resetStyle(btnLow, txtLow); resetStyle(btnMedium, txtMedium); resetStyle(btnHigh, txtHigh);
        resetStyle(btnAssigned, txtAssigned); resetStyle(btnUnassigned, txtUnassigned);

        if (currentStatusFilter.equals("all")) setActive(btnAll, txtAll);
        else if (currentStatusFilter.equals("pending")) setActive(btnPending, txtPending);
        else if (currentStatusFilter.equals("in progress")) setActive(btnInProgress, txtInProgress);
        else if (currentStatusFilter.equals("resolved")) setActive(btnCompleted, txtCompleted);

        if (currentPriorityFilter.equals("low")) setActive(btnLow, txtLow);
        else if (currentPriorityFilter.equals("medium")) setActive(btnMedium, txtMedium);
        else if (currentPriorityFilter.equals("high")) setActive(btnHigh, txtHigh);

        if (currentAssignmentFilter.equals("assigned")) setActive(btnAssigned, txtAssigned);
        else if (currentAssignmentFilter.equals("unassigned")) setActive(btnUnassigned, txtUnassigned);
    }

    private void resetStyle(LinearLayout l, TextView t) {
        if (l != null) l.setBackgroundResource(R.drawable.se6e6e6sw1cr6);
        if (t != null) t.setTextColor(Color.BLACK);
    }

    private void setActive(LinearLayout l, TextView t) {
        if (l != null) l.setBackgroundColor(Color.parseColor("#670E10"));
        if (t != null) t.setTextColor(Color.WHITE);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
