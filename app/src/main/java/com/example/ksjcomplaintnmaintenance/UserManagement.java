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
import com.example.ksjcomplaintnmaintenance.UserCardView.UserData;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserManagement extends AppCompatActivity {

    private LinearLayout userContainer;
    private EditText etSearch;
    private JSONArray fullUserList; // Stores all users from database
    private String currentRoleFilter = "all"; // Remembers selected role

    // Filter Buttons (Layouts)
    private LinearLayout btnAll, btnUser, btnStaff, btnAdmin;
    // Filter TextViews (to change text color)
    private TextView txtAll, txtUser, txtStaff, txtAdmin;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    private final String URL_GET_USERS = Config.getUrl("get_users.php");

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);

        userId = getIntent().getStringExtra("USER_ID");

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.ry2nn68ppi9);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_dashboard) {
                startActivity(new Intent(this, AdminDashboard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_user_management) {
                // Already here
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

        // Initialize Container and Search
        userContainer = findViewById(R.id.user_container);
        etSearch = findViewById(R.id.user_search_input);

        View btnAddUser = findViewById(R.id.btn_add_user_form);
        if (btnAddUser != null) {
            btnAddUser.setOnClickListener(v -> {
                startActivity(new Intent(UserManagement.this, AdminCreateUser.class));
            });
        }

        // 1. Initialize Filter UI components
        btnAll = findViewById(R.id.filter_all);
        btnUser = findViewById(R.id.filter_user);
        btnStaff = findViewById(R.id.filter_staff);
        btnAdmin = findViewById(R.id.filter_admin);

        txtAll = findViewById(R.id.txt_all);
        txtUser = findViewById(R.id.txt_user);
        txtStaff = findViewById(R.id.txt_staff);
        txtAdmin = findViewById(R.id.txt_admin);

        // 2. Set Click Listeners for Role Filters
        btnAll.setOnClickListener(v -> { currentRoleFilter = "all"; applyFilterAndSearch(); });
        btnUser.setOnClickListener(v -> { currentRoleFilter = "user"; applyFilterAndSearch(); });
        btnStaff.setOnClickListener(v -> { currentRoleFilter = "staff"; applyFilterAndSearch(); });
        btnAdmin.setOnClickListener(v -> { currentRoleFilter = "admin"; applyFilterAndSearch(); });

        // 3. Set Search Listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch(); // Filter as you type
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchUsersFromServer();
    }

    private void fetchUsersFromServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_USERS,
                response -> {
                    try {
                        fullUserList = new JSONArray(response.trim());
                        applyFilterAndSearch(); // Initial display
                    } catch (JSONException e) {
                        Log.e("USER_MGMT", "JSON Error: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    // This method handles BOTH role filtering and name searching
    private void applyFilterAndSearch() {
        if (fullUserList == null) return;

        userContainer.removeAllViews();
        updateFilterButtonUI(); // Highlight the correct button

        String query = etSearch.getText().toString().toLowerCase().trim();

        try {
            boolean found = false;
            for (int i = 0; i < fullUserList.length(); i++) {
                JSONObject obj = fullUserList.getJSONObject(i);

                String dbRole = obj.optString("role", "user").toLowerCase();
                String dbName = obj.optString("name", "").toLowerCase();

                // Logic: (Role matches OR filter is 'all') AND (Name contains search query)
                boolean roleMatches = currentRoleFilter.equals("all") || dbRole.equals(currentRoleFilter);
                boolean searchMatches = dbName.contains(query);

                if (roleMatches && searchMatches) {
                    found = true;
                    addUserCardToUI(obj);
                }
            }

            if (!found) {
                String displayFilter = currentRoleFilter.equals("user") ? "student" : currentRoleFilter;
                showNoResultsMessage(query.isEmpty() ? "No users found for " + displayFilter : "No results for '" + query + "'");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addUserCardToUI(JSONObject obj) throws JSONException {
        UserData data = new UserData(
                obj.getString("user_id"),
                obj.getString("name"),
                obj.getString("email"),
                obj.getString("role")
        );

        CardView card = UserCardView.createUserCard(this, data);
        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(UserManagement.this, UserInfo.class);
                intent.putExtra("USER_ID", data.getUserId());
                intent.putExtra("USER_EMAIL", data.getEmail());
                startActivity(intent);
            });
            userContainer.addView(card);
        }
    }

    private void updateFilterButtonUI() {
        // Reset all buttons to default grey
        resetButtonStyle(btnAll, txtAll);
        resetButtonStyle(btnUser, txtUser);
        resetButtonStyle(btnStaff, txtStaff);
        resetButtonStyle(btnAdmin, txtAdmin);

        // Highlight selected
        if (currentRoleFilter.equals("all")) setActiveButtonStyle(btnAll, txtAll);
        else if (currentRoleFilter.equals("user")) setActiveButtonStyle(btnUser, txtUser);
        else if (currentRoleFilter.equals("staff")) setActiveButtonStyle(btnStaff, txtStaff);
        else if (currentRoleFilter.equals("admin")) setActiveButtonStyle(btnAdmin, txtAdmin);
    }

    private void resetButtonStyle(LinearLayout layout, TextView text) {
        layout.setBackgroundResource(R.drawable.se6e6e6sw1cr6); // Grey drawable
        text.setTextColor(Color.parseColor("#1A1A1A")); // Dark text
    }

    private void setActiveButtonStyle(LinearLayout layout, TextView text) {
        layout.setBackgroundColor(Color.parseColor("#670E10")); // Dark Red
        text.setTextColor(Color.WHITE); // White text
    }

    private void showNoResultsMessage(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 100, 0, 0);
        userContainer.addView(tv);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
