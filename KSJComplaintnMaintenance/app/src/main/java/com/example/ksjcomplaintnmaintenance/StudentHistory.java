package com.example.ksjcomplaintnmaintenance;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.ksjcomplaintnmaintenance.ComplaintCardView;
import com.example.ksjcomplaintnmaintenance.ComplaintCardView.ComplaintData;

import com.example.ksjcomplaintnmaintenance.R;

import java.util.ArrayList;
import java.util.List;

public class StudentHistory extends AppCompatActivity {

    private LinearLayout complaintsContainer;
    private List<ComplaintCardView.ComplaintData> complaintsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_history);

        complaintsContainer = findViewById(R.id.complaints_container);
        // Fetch or load your data
        loadComplaints();
    }
    private void loadComplaints() {
        // Example: Get data from API, database, or user input
        complaintsList = getComplaintsFromDataSource();

        // Clear container first
        complaintsContainer.removeAllViews();

        // Conditionally create and add cards based on data
        if (complaintsList != null && !complaintsList.isEmpty()) {
            for (ComplaintCardView.ComplaintData complaint : complaintsList) {
                CardView card = ComplaintCardView.createComplaintCard(this, complaint);
                if (card != null) {
                    complaintsContainer.addView(card);
                }
            }
        } else {
            // Show "No complaints" message
            TextView emptyView = new TextView(this);
            emptyView.setText("No complaints submitted yet");
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setPadding(32, 32, 32, 32);
            complaintsContainer.addView(emptyView);
        }
    }

    private List<ComplaintCardView.ComplaintData> getComplaintsFromDataSource() {
        List<ComplaintCardView.ComplaintData> complaints = new ArrayList<>();

        // Sample data - replace with your actual data source
        complaints.add(new ComplaintCardView.ComplaintData(
                "Broken Air Conditioner",
                "High",
                "2024-01-15",
                "In Progress"
        ));

        complaints.add(new ComplaintCardView.ComplaintData(
                "No Hot Water",
                "Medium",
                "2024-01-14",
                "Pending"
        ));

        complaints.add(new ComplaintCardView.ComplaintData(
                "Light Bulb Replacement",
                "Low",
                "2024-01-13",
                "Completed"
        ));

        return complaints;
    }

    // Method to add a new complaint dynamically
    private void addNewComplaint(ComplaintCardView.ComplaintData newComplaint) {
        if (newComplaint != null) {
            CardView newCard = ComplaintCardView.createComplaintCard(this, newComplaint);
            if (newCard != null) {
                complaintsContainer.addView(newCard, 0); // Add at top
            }
        }
    }

}

