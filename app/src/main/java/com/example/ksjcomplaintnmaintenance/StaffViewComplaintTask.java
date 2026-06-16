package com.example.ksjcomplaintnmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class StaffViewComplaintTask extends AppCompatActivity {

    private String complaintId;
    private EditText etName, etRoom, etDate, etPhone, etOtherDesc, etComplaintDesc;
    private CheckBox cbLeaking, cbPlumbing, cbOther;
    private ImageView ivPhoto;
    private TextView photoAttached;
    private LinearLayout btnInProgress, btnComplete;
    private ProgressDialog progressDialog;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_view_complaint_task);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.returnStudentDashboard);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_staff_dashboard) {
                startActivity(new Intent(this, StaffDashboard.class));
            } else if (id == R.id.nav_staff_assigned_tasks) {
                startActivity(new Intent(this, StaffAssignedTask.class));
            } else if (id == R.id.nav_staff_feedback) {
                startActivity(new Intent(this, StaffFeedbackBoard.class));
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        complaintId = getIntent().getStringExtra("COMPLAINT_ID");
        if (complaintId == null) {
            Toast.makeText(this, "Error: No complaint ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        fetchTaskDetails();

        btnInProgress.setOnClickListener(v -> updateTaskStatus("in_progress"));
        btnComplete.setOnClickListener(v -> updateTaskStatus("resolved"));
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
        
        btnInProgress = findViewById(R.id.staff_inprogress_btn);
        btnComplete = findViewById(R.id.staff_complete_task);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Make fields non-editable for staff
        disableEditing();
    }

    private void disableEditing() {
        etName.setEnabled(false);
        etRoom.setEnabled(false);
        etDate.setEnabled(false);
        etPhone.setEnabled(false);
        etOtherDesc.setEnabled(false);
        etComplaintDesc.setEnabled(false);
        cbLeaking.setEnabled(false);
        cbPlumbing.setEnabled(false);
        cbOther.setEnabled(false);
    }

    private void fetchTaskDetails() {
        progressDialog.setMessage("Fetching task details...");
        progressDialog.show();

        String url = Config.getUrl("get_complaint_details.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d("STAFF_TASK", "Details Response: " + response);
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
                            }

                            String imgPath = data.optString("image_url", "");
                            if (!imgPath.isEmpty()) {
                                ivPhoto.setVisibility(View.VISIBLE);
                                photoAttached.setText("Photo Reference Attached");
                                loadTaskImage(Config.BASE_URL + imgPath);
                            }

                        }
                    } catch (JSONException e) {
                        Log.e("STAFF_VIEW", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("STAFF_VIEW", "Volley Error: " + error.toString());
                    Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
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

    private void loadTaskImage(String urlStr) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);
                runOnUiThread(() -> ivPhoto.setImageBitmap(bmp));
            } catch (Exception e) {
                Log.e("STAFF_VIEW", "Image load error: " + e.getMessage());
            }
        }).start();
    }

    private void updateTaskStatus(String status) {
        progressDialog.setMessage("Updating status to " + status + "...");
        progressDialog.show();

        String url = Config.getUrl("update_task_status.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d("STAFF_TASK", "Update Response: " + response);
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(this, "Status updated to " + status, Toast.LENGTH_SHORT).show();
                        // This will trigger global change by updating DB and notifying student
                        finish(); 
                    } else {
                        Toast.makeText(this, "Failed to update: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("STAFF_VIEW", "Update Volley Error: " + error.toString());
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("complaint_id", complaintId);
                params.put("status", status);
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
}
