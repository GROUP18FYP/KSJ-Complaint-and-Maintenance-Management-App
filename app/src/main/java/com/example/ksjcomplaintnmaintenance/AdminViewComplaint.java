package com.example.ksjcomplaintnmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AdminViewComplaint extends AppCompatActivity {

    private String complaintId;
    private EditText etName, etRoom, etDate, etPhone, etOtherDesc, etComplaintDesc;
    private CheckBox cbLeaking, cbPlumbing, cbOther;
    private ImageView ivPhoto;
    private TextView photoAttached;
    private Spinner spinnerRole;
    private RadioGroup rgStaffList, rgPriority, rgStatus;
    private LinearLayout btnUpdateAll, btnAssignStaff, btnSetPriority, btnSetStatus;
    private ProgressDialog progressDialog;

    private RadioButton rbHigh, rbMedium, rbLow, rbPending, rbInProgress, rbCompleted;
    private boolean isInitialLoad = true;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private String userId;

    private final String BASE_URL = Config.BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_view_complaint);

        complaintId = getIntent().getStringExtra("COMPLAINT_ID");
        userId = getIntent().getStringExtra("USER_ID");

        if (complaintId == null || complaintId.isEmpty()) {
            Toast.makeText(this, "Error: Missing Complaint ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.returnStudentDashboard);

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

        initViews();
        setupRoleSpinner();
        fetchComplaintDetails();

        // --- BUTTON CLICK UPDATES ---

        btnAssignStaff.setOnClickListener(v -> {
            int selectedStaffId = rgStaffList.getCheckedRadioButtonId();
            if (selectedStaffId == -1) {
                Toast.makeText(this, "Please select a staff member!", Toast.LENGTH_SHORT).show();
            } else {
                updateComplaint(); 
            }
        });

        btnSetPriority.setOnClickListener(v -> updateComplaint());
        btnSetStatus.setOnClickListener(v -> updateComplaint());
        btnUpdateAll.setOnClickListener(v -> updateComplaint());
    }

    private void initViews() {
        etName = findViewById(R.id.student_name);
        etRoom = findViewById(R.id.student_room_number);
        etDate = findViewById(R.id.date_prefer_service);
        etPhone = findViewById(R.id.student_number);
        etOtherDesc = findViewById(R.id.other_desc);
        etComplaintDesc = findViewById(R.id.complaint_desc);
        cbLeaking = findViewById(R.id.leaking_pipe_cbx);
        cbPlumbing = findViewById(R.id.plumbing_cbx);
        cbOther = findViewById(R.id.other_cbx);
        ivPhoto = findViewById(R.id.photo_preview);
        photoAttached = findViewById(R.id.photo_attached);
        spinnerRole = findViewById(R.id.assigning_staff_role);
        rgStaffList = findViewById(R.id.staff_name_list);
        rgPriority = findViewById(R.id.priority_list);
        rgStatus = findViewById(R.id.pending_list);

        btnAssignStaff = findViewById(R.id.assigning_staffBtn);
        btnSetPriority = findViewById(R.id.setting_priorityBtn);
        btnSetStatus = findViewById(R.id.setting_statusBtn);
        btnUpdateAll = findViewById(R.id.submit_btn);

        rbHigh = findViewById(R.id.high_priority_radio);
        rbMedium = findViewById(R.id.medium_priority_radio);
        rbLow = findViewById(R.id.low_priority_radio);
        rbPending = findViewById(R.id.pending_status_radio);
        rbInProgress = findViewById(R.id.inprogress_status_radio);
        rbCompleted = findViewById(R.id.completed_status_radio);

        fixTextVisibility();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void fixTextVisibility() {
        int darkColor = Color.BLACK;
        if (rbHigh != null) rbHigh.setTextColor(darkColor);
        if (rbMedium != null) rbMedium.setTextColor(darkColor);
        if (rbLow != null) rbLow.setTextColor(darkColor);
        if (rbPending != null) rbPending.setTextColor(darkColor);
        if (rbInProgress != null) rbInProgress.setTextColor(darkColor);
        if (rbCompleted != null) rbCompleted.setTextColor(darkColor);
    }

    private void fetchComplaintDetails() {
        progressDialog.setMessage("Loading details...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "get_complaint_details.php",
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response.trim());
                        if (obj.getString("status").equals("success")) {
                            JSONObject data = obj.getJSONObject("data");

                            etName.setText(data.optString("student_name", data.optString("name", "N/A")));
                            etRoom.setText(data.optString("room_number", "N/A"));
                            etDate.setText(data.optString("date_prefer_service", "N/A"));
                            etPhone.setText(data.optString("phone_number", "N/A"));
                            etComplaintDesc.setText(data.optString("description", ""));

                            String type = data.optString("complaint_type", "");
                            cbLeaking.setChecked(type.equalsIgnoreCase("Leaking Pipe"));
                            cbPlumbing.setChecked(type.equalsIgnoreCase("Plumbing"));
                            
                            if (!type.equalsIgnoreCase("Leaking Pipe") && !type.equalsIgnoreCase("Plumbing") && !type.isEmpty()) {
                                cbOther.setChecked(true);
                                etOtherDesc.setVisibility(View.VISIBLE);
                                etOtherDesc.setText(type);
                            } else {
                                cbOther.setChecked(false);
                                etOtherDesc.setVisibility(View.GONE);
                            }

                            String prio = data.optString("priority", "low").toLowerCase();
                            if (prio.contains("high")) rgPriority.check(R.id.high_priority_radio);
                            else if (prio.contains("medium")) rgPriority.check(R.id.medium_priority_radio);
                            else rgPriority.check(R.id.low_priority_radio);

                            String stat = data.optString("status", "pending").toLowerCase();
                            if (stat.contains("progress")) rgStatus.check(R.id.inprogress_status_radio);
                            else if (stat.contains("resolved")) rgStatus.check(R.id.completed_status_radio);
                            else rgStatus.check(R.id.pending_status_radio);

                            String staffId = data.optString("assigned_staff_id", "0");
                            String staffRole = data.optString("staff_role", "");

                            if (!staffId.equals("0") && !staffId.isEmpty() && !staffId.equalsIgnoreCase("null")) {
                                setSpinnerToRoleAndLoadStaff(staffRole, staffId);
                            } else {
                                isInitialLoad = false; 
                            }

                            String imgPath = data.optString("image_url", "");
                            if (!imgPath.isEmpty()) {
                                ivPhoto.setVisibility(View.VISIBLE);
                                photoAttached.setText("Photo Attached ✅");
                                loadNativeImage(BASE_URL + imgPath);
                            }

                            disableStudentFields();
                        }
                    } catch (Exception e) { 
                        Log.e("ADMIN_DETAIL_ERR", e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMsg = "Connection Error";
                    if (error.networkResponse != null) {
                        errorMsg += ": " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("complaint_id", complaintId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void setSpinnerToRoleAndLoadStaff(String role, String targetId) {
        ArrayAdapter adapter = (ArrayAdapter) spinnerRole.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(role)) {
                    spinnerRole.setSelection(i);
                    fetchStaffByRoleAndCheck(role, targetId);
                    break;
                }
            }
        }
    }

    private void loadNativeImage(String urlStr) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);
                runOnUiThread(() -> ivPhoto.setImageBitmap(bmp));
            } catch (Exception e) {
                Log.e("IMAGE_LOAD", "Error: " + e.getMessage());
            }
        }).start();
    }

    private void setupRoleSpinner() {
        String[] roles = {"Select Role", "Technician", "Janitor", "Security", "Plumber"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (!isInitialLoad) {
                        fetchStaffByRole(roles[position]);
                    }
                } else {
                    if (!isInitialLoad) rgStaffList.removeAllViews();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchStaffByRole(String role) {
        fetchStaffByRoleAndCheck(role, null);
    }

    private void fetchStaffByRoleAndCheck(String role, String checkId) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "get_staff_by_role.php",
                response -> {
                    rgStaffList.removeAllViews();
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int staffIdInt = obj.getInt("user_id");
                            
                            RadioButton rb = new RadioButton(this);
                            rb.setText(obj.getString("fullname"));
                            rb.setTextColor(Color.BLACK);
                            rb.setId(staffIdInt);
                            rb.setTag(String.valueOf(staffIdInt));
                            
                            rgStaffList.addView(rb);

                            if (checkId != null && checkId.equals(String.valueOf(staffIdInt))) {
                                rgStaffList.check(staffIdInt);
                            }
                        }
                    } catch (JSONException e) { Log.e("STAFF_ERR", e.getMessage()); }
                    isInitialLoad = false;
                },
                error -> { Log.e("STAFF_ERR", "Network Error"); isInitialLoad = false; }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("role", role);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void updateComplaint() {
        int pId = rgPriority.getCheckedRadioButtonId();
        String priority = "low";
        if (pId == R.id.high_priority_radio) priority = "high";
        else if (pId == R.id.medium_priority_radio) priority = "medium";

        int sId = rgStatus.getCheckedRadioButtonId();
        String status = "pending";
        if (sId == R.id.inprogress_status_radio) status = "in_progress";
        else if (sId == R.id.completed_status_radio) status = "resolved";

        int staffViewId = rgStaffList.getCheckedRadioButtonId();
        String tempStaffId = "0";
        RadioButton selectedRb = rgStaffList.findViewById(staffViewId);
        if (selectedRb != null && selectedRb.getTag() != null) {
            tempStaffId = selectedRb.getTag().toString();
        }

        final String finalPriority = priority;
        final String finalStatus = status;
        final String finalStaffId = tempStaffId;

        // DEBUG: Log parameters as ERROR so they always show up in Logcat
        Log.e("UPDATE_DEBUG", "URL: " + Config.getUrl("update_complaint_admin.php"));
        Log.e("UPDATE_DEBUG", "Params: ID=" + complaintId + ", Prio=" + finalPriority + ", Status=" + finalStatus + ", Staff=" + finalStaffId);

        progressDialog.setMessage("Updating...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Config.getUrl("update_complaint_admin.php"),
                response -> {
                    progressDialog.dismiss();
                    Log.d("UPDATE_DEBUG", "Server Response: " + response);
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                        fetchComplaintDetails();
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMsg = "Network Error";
                    if (error.networkResponse != null) {
                        errorMsg += ": " + error.networkResponse.statusCode;
                    } else {
                        errorMsg += ": " + error.toString();
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("complaint_id", complaintId);
                params.put("priority", finalPriority);
                params.put("status", finalStatus);
                params.put("assigned_staff_id", finalStaffId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void disableStudentFields() {
        etName.setEnabled(false);
        etRoom.setEnabled(false);
        etPhone.setEnabled(false);
        etDate.setEnabled(false);
        etComplaintDesc.setEnabled(false);
        etOtherDesc.setEnabled(false);
        cbLeaking.setEnabled(false);
        cbPlumbing.setEnabled(false);
        cbOther.setEnabled(false);
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
