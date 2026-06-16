package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);

        //working like a button -- HISTORY
        LinearLayout student_history = findViewById(R.id.history_click);
        student_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This works exactly like a button click
                Log.d("TAG", "Card clicked!");

                // Navigate to another activity
                Intent intent = new Intent(StudentDashboard.this, StudentHistory.class);
                startActivity(intent);
            }
        });

        //working like a button -- PENDING
        LinearLayout student_pending = findViewById(R.id.pending_click);
        student_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This works exactly like a button click
                Log.d("TAG", "Card clicked!");

                // Navigate to another activity
                Intent intent = new Intent(StudentDashboard.this, StudentPending.class);
                startActivity(intent);
            }
        });

        //working like a button -- NOTIFICATION
        LinearLayout student_notification = findViewById(R.id.notification_click);
        student_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This works exactly like a button click
                Log.d("TAG", "Card clicked!");

                // Navigate to another activity
                Intent intent = new Intent(StudentDashboard.this, StudentNotification.class);
                startActivity(intent);
            }
        });

        //working like a button -- COMPLAINT
        LinearLayout student_complaint_click = findViewById(R.id.complaint_click);
        student_complaint_click .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This works exactly like a button click
                Log.d("TAG", "Card clicked!");

                // Navigate to another activity
                Intent intent = new Intent(StudentDashboard.this, StudentComplaintForm.class);
                startActivity(intent);
            }
        });

        //working like a button -- FEEDBACK
        LinearLayout student_feedback_click = findViewById(R.id.feedback_click);
        student_complaint_click .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This works exactly like a button click
                Log.d("TAG", "Card clicked!");

                // Navigate to another activity
                Intent intent = new Intent(StudentDashboard.this, StudentFeedbackForm.class);
                startActivity(intent);
            }
        });
    }
}