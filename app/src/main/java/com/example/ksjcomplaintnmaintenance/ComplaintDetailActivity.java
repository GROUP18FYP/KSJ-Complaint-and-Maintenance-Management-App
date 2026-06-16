package com.example.ksjcomplaintnmaintenance;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Use Glide for safer image loading

public class ComplaintDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complaint_form);

        // 1. Get Data from Intent
        String type = getIntent().getStringExtra("TYPE");
        String room = getIntent().getStringExtra("ROOM");
        String desc = getIntent().getStringExtra("DESC");
        String name = getIntent().getStringExtra("NAME");
        String phone = getIntent().getStringExtra("PHONE");
        String datePrefer = getIntent().getStringExtra("DATE_PREFER");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        // 2. Find Views
        EditText etName = findViewById(R.id.student_name);
        EditText etRoom = findViewById(R.id.student_room_number);
        EditText etPhone = findViewById(R.id.student_number);
        EditText etDatePrefer = findViewById(R.id.date_prefer_service);
        EditText etDesc = findViewById(R.id.complaint_desc);
        EditText etOtherDesc = findViewById(R.id.other_desc);

        CheckBox cbPipe = findViewById(R.id.leaking_pipe_cbx);
        CheckBox cbPlumb = findViewById(R.id.plumbing_cbx);
        CheckBox cbOther = findViewById(R.id.other_cbx);

        TextView tvTitle = findViewById(R.id.complaints_title);
        View btnBack = findViewById(R.id.submit_btn);
        ImageView ivPhoto = findViewById(R.id.photo_preview);

        if (tvTitle != null) {
            tvTitle.setText("Complaint Details");
        }

        // 3. Ticking Logic (ADDED NULL CHECKS TO PREVENT CRASH)
        if (type != null && !type.isEmpty()) {
            if (cbPipe != null && type.equalsIgnoreCase("Leaking Pipe")) {
                cbPipe.setChecked(true);
            } else if (cbPlumb != null && type.equalsIgnoreCase("Plumbing")) {
                cbPlumb.setChecked(true);
            } else if (cbOther != null) {
                cbOther.setChecked(true);
                if (etOtherDesc != null) {
                    etOtherDesc.setVisibility(View.VISIBLE);
                    etOtherDesc.setText(type);
                }
            }
        }

        // Safely disable checkboxes
        if (cbPipe != null) cbPipe.setEnabled(false);
        if (cbPlumb != null) cbPlumb.setEnabled(false);
        if (cbOther != null) cbOther.setEnabled(false);

        // 4. Fill and Disable Fields Safely
        if (etName != null) { etName.setText(name); etName.setEnabled(false); etName.setTextColor(Color.BLACK); }
        if (etRoom != null) { etRoom.setText(room); etRoom.setEnabled(false); etRoom.setTextColor(Color.BLACK); }
        if (etPhone != null) { etPhone.setText(phone); etPhone.setEnabled(false); etPhone.setTextColor(Color.BLACK); }
        if (etDatePrefer != null) { etDatePrefer.setText(datePrefer); etDatePrefer.setEnabled(false); etDatePrefer.setTextColor(Color.BLACK); }
        if (etDesc != null) { etDesc.setText(desc); etDesc.setEnabled(false); etDesc.setTextColor(Color.BLACK); }

        // 5. Image Loading using Glide (Safer and Faster)
        if (imageUrl != null && !imageUrl.isEmpty() && ivPhoto != null) {
            ivPhoto.setVisibility(View.VISIBLE);
            String fullUrl = Config.getUrl(imageUrl); // Use getUrl to handle BASE_URL automatically

            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(ivPhoto);
        }

        // 6. Setup Back Button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());

            // Check if the submit button text view exists
            TextView btnText = findViewById(R.id.submit_btn_text);
            if (btnText != null) {
                btnText.setText("Back to History");
            }
        }
    }
}
