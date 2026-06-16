package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffFeedbackBoard extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<FeedbackModel> list;
    private FeedbackAdapter adapter;
    private String staffId;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feedback_board);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.btnMenu2);

        // Set Staff Menu
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.staff_menu);

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
                Intent intent = new Intent(this, StaffAssignedTask.class);
                intent.putExtra("USER_ID", staffId);
                startActivity(intent);
            } else if (id == R.id.nav_staff_feedback) {
                // Already here
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        staffId = getIntent().getStringExtra("STAFF_ID");

        recyclerView = findViewById(R.id.rvFeedbackBoard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new FeedbackAdapter(list);
        recyclerView.setAdapter(adapter);

        loadMyFeedback();
    }

    private void loadMyFeedback() {
        String url = Config.getUrl("get_all_feedback.php");

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("STAFF_FEEDBACK", "Response: " + response);
            try {
                JSONArray array = new JSONArray(response.trim());
                list.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    
                    // 1. Try student_name (from SQL alias), then name, then user_id
                    String rawName = obj.optString("student_name", obj.optString("name", ""));
                    String displayName = "Anonymous";

                    if (rawName != null && !rawName.isEmpty() && !rawName.equalsIgnoreCase("null")) {
                        String[] parts = rawName.trim().split("\\s+");
                        if (parts.length > 1) {
                            String first = parts[0].toLowerCase();
                            // If first name is a prefix like Nur, Mohd, etc., use the second name
                            if (first.equals("nur") || first.equals("mohd") || first.equals("ahmad") || first.equals("wan") || first.equals("siti") || first.length() <= 3) {
                                displayName = parts[1] + " Anonymous";
                            } else {
                                displayName = parts[0] + " Anonymous";
                            }
                        } else {
                            displayName = parts[0] + " Anonymous";
                        }
                    } else {
                        String sid = obj.optString("user_id", "");
                        displayName = sid.isEmpty() ? "Anonymous" : "Student " + sid + " Anonymous";
                    }

                    // 2. Try staff_name (from SQL alias), then staff_id
                    String staffName = obj.optString("staff_name", obj.optString("staffName", ""));
                    if (staffName.isEmpty() || staffName.equalsIgnoreCase("null")) {
                        String stid = obj.optString("staff_id", obj.optString("assigned_staff_id", ""));
                        if (!stid.isEmpty() && !stid.equalsIgnoreCase("null")) {
                            staffName = stid;
                        } else {
                            staffName = "General";
                        }
                    }

                    list.add(new FeedbackModel(
                            displayName,
                            obj.getString("message"),
                            obj.getInt("rating"),
                            staffName
                    ));
                }
                adapter.notifyDataSetChanged();

                if (list.isEmpty()) {
                    Toast.makeText(this, "No feedback received yet.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Data Error: Check PHP output", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e("VOLLEY_ERROR", error.toString());
            if (error.networkResponse != null) {
                Log.e("VOLLEY_ERROR", "Status: " + error.networkResponse.statusCode);
            }
            Toast.makeText(this, "Network Error: Check Server/IP", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("staff_id", staffId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static class FeedbackModel {
        private String name, message, staffName;
        private int rating;

        public FeedbackModel(String name, String message, int rating, String staffName) {
            this.name = name;
            this.message = message;
            this.rating = rating;
            this.staffName = staffName;
        }

        public String getName() { return name; }
        public String getMessage() { return message; }
        public int getRating() { return rating; }
        public String getStaffName() { return staffName; }
    }

    public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
        private List<FeedbackModel> feedbackList;

        public FeedbackAdapter(List<FeedbackModel> feedbackList) {
            this.feedbackList = feedbackList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FeedbackModel model = feedbackList.get(position);
            
            // Set Staff Name as the main title
            String staffLabel = model.getStaffName();
            if (staffLabel.equalsIgnoreCase("General")) {
                holder.tvName.setText("Staff: General");
            } else {
                if (staffLabel.matches("\\d+")) {
                    holder.tvName.setText("Staff ID: " + staffLabel);
                } else {
                    holder.tvName.setText("Staff: " + staffLabel);
                }
            }

            holder.tvMessage.setText(model.getMessage());
            holder.ratingBar.setRating(model.getRating());

            // Hide the redundant student name/anonymous and the old staff refer label
            holder.tvStaffRefer.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return feedbackList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvMessage, tvStaffRefer;
            RatingBar ratingBar;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvBoardName);
                tvMessage = itemView.findViewById(R.id.tvBoardMessage);
                tvStaffRefer = itemView.findViewById(R.id.tvStaffRefer);
                ratingBar = itemView.findViewById(R.id.boardRatingBar);
            }
        }
    }
}
