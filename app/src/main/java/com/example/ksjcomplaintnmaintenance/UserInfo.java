package com.example.ksjcomplaintnmaintenance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfo extends AppCompatActivity {

    private String userId, intentEmail;
    private EditText etName, etEmail, etPassword, etPhone;
    private RadioGroup rgRole;
    private RadioButton rbUser, rbStaff, rbAdmin;
    private Spinner spinnerJobRole;
    private ListView listComplaints;
    private LinearLayout editBtn, deleteBtn, updateBtn, cancelBtn, viewModeButtons, editModeButtons, tasksSection, jobRoleSection;
    private TextView tasksCount, labelComplaintList;
    private ProgressDialog progressDialog;
    
    private List<String> complaintIds = new ArrayList<>();
    
    private List<String> complaintDisplays = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    private final String BASE_URL = Config.BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.user_info_menu);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_dashboard) {
                startActivity(new Intent(this, AdminDashboard.class));
            } else if (id == R.id.nav_user_management) {
                startActivity(new Intent(this, UserManagement.class));
            } else if (id == R.id.nav_complaint_list) {
                startActivity(new Intent(this, AdminComplaintList.class));
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

        userId = getIntent().getStringExtra("USER_ID");
        intentEmail = getIntent().getStringExtra("USER_EMAIL");

        initViews();
        toggleEdit(false); 
        loadUserInfo();

        editBtn.setOnClickListener(v -> toggleEdit(true));
        updateBtn.setOnClickListener(v -> updateUserInfo());
        cancelBtn.setOnClickListener(v -> {
            toggleEdit(false);
            loadUserInfo(); 
        });
        deleteBtn.setOnClickListener(v -> confirmDelete());

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_staff) {
                jobRoleSection.setVisibility(View.VISIBLE);
            } else {
                jobRoleSection.setVisibility(View.GONE);
            }
            updateEmailField();
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (rbUser.isChecked()) {
                    updateEmailField();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        listComplaints.setOnItemClickListener((parent, view, position, id) -> {
            String complaintId = complaintIds.get(position);
            Intent intent = new Intent(UserInfo.this, AdminViewComplaint.class);
            intent.putExtra("COMPLAINT_ID", complaintId);
            startActivity(intent);
        });
    }

    private void initViews() {
        etName = findViewById(R.id.student_name);
        etEmail = findViewById(R.id.student_email);
        etPassword = findViewById(R.id.student_password);
        etPhone = findViewById(R.id.student_number);
        
        rgRole = findViewById(R.id.role_radio_group);
        rbUser = findViewById(R.id.rb_user);
        rbStaff = findViewById(R.id.rb_staff);
        rbAdmin = findViewById(R.id.rb_admin);

        spinnerJobRole = findViewById(R.id.staff_job_role_spinner);
        jobRoleSection = findViewById(R.id.staff_job_role_section);

        listComplaints = findViewById(R.id.list_complaint);
        
        editBtn = findViewById(R.id.edit_btn);
        deleteBtn = findViewById(R.id.delete_acc_btn);
        updateBtn = findViewById(R.id.update_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        
        viewModeButtons = findViewById(R.id.view_mode_buttons);
        editModeButtons = findViewById(R.id.edit_mode_buttons);
        
        tasksSection = findViewById(R.id.tasks_done_section);
        tasksCount = findViewById(R.id.tasks_done_count);
        labelComplaintList = findViewById(R.id.label_complaint_list);

        adapter = new ArrayAdapter<>(this, R.layout.complaint_list_item, complaintDisplays);
        listComplaints.setAdapter(adapter);

        String[] jobs = {"Technician", "Janitor", "Security", "Plumber"};
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_black, jobs);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobRole.setAdapter(jobAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void updateEmailField() {
        // Only trigger automatic logic if we are actually in Edit Mode
        if (editModeButtons != null && editModeButtons.getVisibility() != View.VISIBLE) return;

        if (rbUser.isChecked()) {
            String name = etName.getText().toString().trim().toLowerCase().replace(" ", "");
            if (!name.isEmpty()) {
                etEmail.setText(name + "@graduate.utm.my");
            } else {
                etEmail.setText("@graduate.utm.my");
            }
            etEmail.setEnabled(false);
        } else {
            // For Staff/Admin, allow editing if in edit mode
            etEmail.setEnabled(true);
        }
    }

    private void toggleEdit(boolean editable) {
        int textColor = android.graphics.Color.BLACK;

        etName.setEnabled(editable);
        etName.setTextColor(textColor);

        // Email is disabled for students regardless of edit mode
        if (rbUser.isChecked()) {
            etEmail.setEnabled(false);
        } else {
            etEmail.setEnabled(editable);
        }
        etEmail.setTextColor(textColor);

        etPassword.setEnabled(editable);
        etPassword.setTextColor(textColor);

        etPhone.setEnabled(editable);
        etPhone.setTextColor(textColor);

        rbUser.setEnabled(editable);
        rbStaff.setEnabled(editable);
        rbAdmin.setEnabled(editable);

        spinnerJobRole.setEnabled(editable);

        viewModeButtons.setVisibility(editable ? View.GONE : View.VISIBLE);
        editModeButtons.setVisibility(editable ? View.VISIBLE : View.GONE);
    }

    private void loadUserInfo() {
        progressDialog.setMessage("Loading user info...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "get_user_details.php",
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response.trim());
                        if (obj.getString("status").equals("success")) {
                            JSONObject data = obj.getJSONObject("data");
                            
                            etName.setText(data.optString("fullname", data.optString("name", "N/A")));
                            
                            // Use database email if available, otherwise use email from intent
                            String dbEmail = data.optString("email", data.optString("user_email", ""));
                            if (dbEmail.isEmpty() && intentEmail != null) {
                                etEmail.setText(intentEmail);
                            } else {
                                etEmail.setText(dbEmail.isEmpty() ? "N/A" : dbEmail);
                            }

                            etPassword.setText(""); // Keep password empty for security, user can fill to change
                            etPhone.setText(data.optString("phone_number", "N/A"));

                            String role = data.optString("role", "user").toLowerCase();
                            if (role.equals("admin")) rbAdmin.setChecked(true);
                            else if (role.equals("staff")) rbStaff.setChecked(true);
                            else rbUser.setChecked(true);

                            loadUserComplaints(role); // Load complaints based on role

                            if (role.equals("staff")) {
                                tasksSection.setVisibility(View.VISIBLE);
                                // We will calculate tasks_done in loadUserComplaints
                                
                                String currentJob = data.optString("staff_role", "");
                                ArrayAdapter<String> myAdapter = (ArrayAdapter<String>) spinnerJobRole.getAdapter();
                                if (myAdapter != null) {
                                    for (int i = 0; i < myAdapter.getCount(); i++) {
                                        if (myAdapter.getItem(i).equalsIgnoreCase(currentJob)) {
                                            spinnerJobRole.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                tasksSection.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("USER_INFO", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void loadUserComplaints(String role) {
        String url;
        final String paramKey;

        if ("staff".equalsIgnoreCase(role)) {
            url = BASE_URL + "get_assigned_tasks.php";
            paramKey = "staff_id";
            if (labelComplaintList != null) labelComplaintList.setText("Assigned Complaints");
        } else {
            url = BASE_URL + "get_user_complaints.php";
            paramKey = "user_id";
            if (labelComplaintList != null) labelComplaintList.setText("List of Complaint");
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        complaintIds.clear();
                        complaintDisplays.clear();
                        int completedCount = 0;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            complaintIds.add(obj.getString("complaint_id"));
                            complaintDisplays.add("Complaint #" + obj.getString("complaint_id") + ": " + obj.getString("complaint_type"));

                            if ("staff".equalsIgnoreCase(role)) {
                                String status = obj.optString("status", "").toLowerCase();
                                // Count resolved/completed tasks
                                if (status.contains("resolved") || status.contains("completed")) {
                                    completedCount++;
                                }
                            }
                        }

                        if ("staff".equalsIgnoreCase(role)) {
                            tasksCount.setText(String.valueOf(completedCount));
                        }

                        adapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(listComplaints);
                    } catch (JSONException e) {
                        Log.e("USER_INFO", "Complaints JSON Error: " + e.getMessage());
                        // If it's not a JSON array (maybe error message), clear the list
                        complaintIds.clear();
                        complaintDisplays.clear();
                        adapter.notifyDataSetChanged();
                    }
                },
                error -> Log.e("USER_INFO", "Complaints fetch error")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(paramKey, userId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void updateUserInfo() {
        int selectedId = rgRole.getCheckedRadioButtonId();
        String roleValue = "user";
        if (selectedId == R.id.rb_staff) roleValue = "staff";
        else if (selectedId == R.id.rb_admin) roleValue = "admin";

        final String updatedRole = roleValue;
        final String jobRole = (roleValue.equals("staff")) ? spinnerJobRole.getSelectedItem().toString() : "";
        
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "update_user_admin.php",
                response -> {
                    progressDialog.dismiss();
                    if (response.trim().equals("success")) {
                        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                        toggleEdit(false);
                        loadUserInfo(); 
                    } else {
                        Toast.makeText(this, "Update failed: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("fullname", etName.getText().toString().trim());
                params.put("email", etEmail.getText().toString().trim());
                params.put("password", etPassword.getText().toString().trim());
                params.put("phone_number", etPhone.getText().toString().trim());
                params.put("role", updatedRole);
                params.put("staff_role", jobRole);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Delete this user and all their data? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser() {
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "delete_user.php",
                response -> {
                    progressDialog.dismiss();
                    if (response.trim().equals("success")) {
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Delete failed: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
