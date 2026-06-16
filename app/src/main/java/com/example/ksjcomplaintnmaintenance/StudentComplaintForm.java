package com.example.ksjcomplaintnmaintenance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StudentComplaintForm extends AppCompatActivity {

    private Bitmap selectedBitmap;
    private EditText etName, etPhone, etRoom, etDesc, etOtherDesc, etDatePrefer;
    private ProgressDialog progressDialog;
    private CheckBox cbLeaking, cbPlumbing, cbOther;
    private LinearLayout submitBtn, uploadBtn;
    private TextView photoAttachedText;
    private ImageView menuIcon;
    private String userId;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        try {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            String fileName = getFileName(selectedImage);
                            if (photoAttachedText != null) photoAttachedText.setText(fileName);
                            Toast.makeText(this, "Photo Selected!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e("UPLOAD_ERR", "Error loading image", e);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complaint_form);

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.returnStudentDashboard);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_student_dashboard) {
                startActivity(new Intent(this, StudentDashboard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_student_complaint) {
                // Already here
            } else if (id == R.id.nav_student_history) {
                startActivity(new Intent(this, StudentHistory.class).putExtra("USER_ID", userId));
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

        // Initialize Views
        etName = findViewById(R.id.student_name);
        etPhone = findViewById(R.id.student_number);
        etRoom = findViewById(R.id.student_room_number);
        etDesc = findViewById(R.id.complaint_desc);
        etOtherDesc = findViewById(R.id.other_desc);
        etDatePrefer = findViewById(R.id.date_prefer_service);

        cbLeaking = findViewById(R.id.leaking_pipe_cbx);
        cbPlumbing = findViewById(R.id.plumbing_cbx);
        cbOther = findViewById(R.id.other_cbx);

        submitBtn = findViewById(R.id.submit_btn);
        uploadBtn = findViewById(R.id.upload_btn);
        photoAttachedText = findViewById(R.id.photo_attached);

        etOtherDesc.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting complaint...");
        progressDialog.setCancelable(false);

        // CheckBox Logic
        cbOther.setOnCheckedChangeListener((v, isChecked) -> {
            etOtherDesc.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if(isChecked) { cbLeaking.setChecked(false); cbPlumbing.setChecked(false); }
        });
        cbLeaking.setOnCheckedChangeListener((v, isChecked) -> {
            if(isChecked) { cbPlumbing.setChecked(false); cbOther.setChecked(false); }
        });
        cbPlumbing.setOnCheckedChangeListener((v, isChecked) -> {
            if(isChecked) { cbLeaking.setChecked(false); cbOther.setChecked(false); }
        });

        uploadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        submitBtn.setOnClickListener(v -> validateAndUpload());
    }

    private String getSelectedType() {
        if (cbLeaking.isChecked()) return "Leaking Pipe";
        if (cbPlumbing.isChecked()) return "Plumbing";
        if (cbOther.isChecked()) {
            String other = etOtherDesc.getText().toString().trim();
            return other.isEmpty() ? "Other" : other;
        }
        return "";
    }

    private void validateAndUpload() {
        String name = etName.getText().toString().trim();
        String room = etRoom.getText().toString().trim();
        String type = getSelectedType();
        String desc = etDesc.getText().toString().trim();
        String datePref = etDatePrefer.getText().toString().trim();

        if (name.isEmpty() || room.isEmpty() || type.isEmpty() || desc.isEmpty() || datePref.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadComplaint(name, room, type, desc, datePref);
    }

    private void uploadComplaint(String name, String room, String type, String desc, String datePref) {
        progressDialog.show();
        String url = Config.getUrl("upload_complaint.php");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(this, "Submitted!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e("SERVER_ERR", response);
                        Toast.makeText(this, "Server Error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Connection Error!", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("name", name);
                params.put("phone_number", etPhone.getText().toString().trim());
                params.put("date_prefer_service", datePref);
                params.put("room_number", room);
                params.put("complaint_type", type);
                params.put("description", desc);
                params.put("priority", "medium");

                if (selectedBitmap != null) {
                    params.put("image", getStringImage(selectedBitmap));
                }
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
