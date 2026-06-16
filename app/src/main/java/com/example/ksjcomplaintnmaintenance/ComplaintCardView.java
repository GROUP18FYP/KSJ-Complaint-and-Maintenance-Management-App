package com.example.ksjcomplaintnmaintenance;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class ComplaintCardView {

    public static class ComplaintData {
        private String complaintId;
        private String studentName; // NEW
        private String studentPhone; // NEW
        private String complaintType;
        private String priority;
        private String dateSubmitted;
        private String progress;
        private String roomNumber;
        private String description;
        private String staffName;
        private String staffPhone;

        public ComplaintData(String complaintId, String studentName, String studentPhone, 
                             String complaintType, String priority, String dateSubmitted, 
                             String progress, String roomNumber, String description, 
                             String staffName, String staffPhone) {
            this.complaintId = complaintId;
            this.studentName = studentName;
            this.studentPhone = studentPhone;
            this.complaintType = complaintType;
            this.priority = priority;
            this.dateSubmitted = dateSubmitted;
            this.progress = progress;
            this.roomNumber = roomNumber;
            this.description = description;
            this.staffName = staffName;
            this.staffPhone = staffPhone;
        }

        public String getComplaintId() { return complaintId; }
        public String getStudentName() { return studentName; }
        public String getStudentPhone() { return studentPhone; }
        public String getComplaintType() { return complaintType; }
        public String getPriority() { return priority; }
        public String getDateSubmitted() { return dateSubmitted; }
        public String getProgress() { return progress; }
        public String getRoomNumber() { return roomNumber; }
        public String getDescription() { return description; }
        public String getStaffName() { return staffName; }
        public String getStaffPhone() { return staffPhone; }
    }

    public static CardView createComplaintCard(Context context, ComplaintData data) {
        if (data == null) return null;

        CardView cardView = new CardView(context);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(dpToPx(context, 14), 0, dpToPx(context, 14), dpToPx(context, 20));
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(dpToPx(context, 4));
        cardView.setRadius(dpToPx(context, 15)); // Slightly rounder like your image
        cardView.setUseCompatPadding(true);

        cardView.setBackgroundResource(R.drawable.s7d5260sw1cr10bf3e6eb);

        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setOrientation(LinearLayout.VERTICAL);

        // Row 1: Title and Priority
        LinearLayout firstRow = new LinearLayout(context);
        firstRow.setOrientation(LinearLayout.HORIZONTAL);
        firstRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams firstRowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        firstRowParams.setMargins(dpToPx(context, 15), dpToPx(context, 12), dpToPx(context, 15), dpToPx(context, 2));
        firstRow.setLayoutParams(firstRowParams);

        TextView titleText = new TextView(context);
        titleText.setText(data.getComplaintType());
        titleText.setTextColor(Color.parseColor("#1E1E1E"));
        titleText.setTextSize(17);
        titleText.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        titleText.setLayoutParams(titleParams);
        firstRow.addView(titleText);

        LinearLayout priorityColumn = new LinearLayout(context);
        priorityColumn.setPadding(dpToPx(context, 10), dpToPx(context, 2), dpToPx(context, 10), dpToPx(context, 3));
        priorityColumn.setBackgroundColor(getPriorityColor(data.getPriority()));

        TextView priorityText = new TextView(context);
        priorityText.setText(data.getPriority().toUpperCase());
        priorityText.setTextColor(Color.WHITE);
        priorityText.setTextSize(11);
        priorityText.setTypeface(null, Typeface.BOLD);
        priorityColumn.addView(priorityText);
        firstRow.addView(priorityColumn);

        mainContainer.addView(firstRow);

        // Row 2: Date and Status
        LinearLayout secondRow = new LinearLayout(context);
        secondRow.setOrientation(LinearLayout.HORIZONTAL);
        secondRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams secondRowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondRowParams.setMargins(dpToPx(context, 15), dpToPx(context, 5), dpToPx(context, 15), dpToPx(context, 5));
        secondRow.setLayoutParams(secondRowParams);

        // Date Column (Left)
        LinearLayout dateLayout = new LinearLayout(context);
        dateLayout.setOrientation(LinearLayout.HORIZONTAL);
        dateLayout.setGravity(Gravity.CENTER_VERTICAL);
        dateLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        ImageView calendarIcon = new ImageView(context);
        calendarIcon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(context, 16), dpToPx(context, 16)));
        calendarIcon.setImageResource(R.drawable.outline_calendar_month_24);
        dateLayout.addView(calendarIcon);

        TextView dateText = new TextView(context);
        dateText.setText(data.getDateSubmitted());
        dateText.setPadding(dpToPx(context, 5), 0, 0, 0);
        dateText.setTextSize(12);
        dateLayout.addView(dateText);
        secondRow.addView(dateLayout);

        // Status Box (Right) - Positioned next to date
        LinearLayout statusBox = new LinearLayout(context);
        statusBox.setPadding(dpToPx(context, 8), dpToPx(context, 2), dpToPx(context, 8), dpToPx(context, 2));
        
        String status = data.getProgress();
        if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase("null")) {
            status = "Pending"; // Default to Pending if empty
        }
        
        statusBox.setBackground(createStatusBackground(status));
        
        TextView progressText = new TextView(context);
        String displayText = status;
        if (status.equalsIgnoreCase("in_progress")) displayText = "In Progress";
        if (status.equalsIgnoreCase("resolved")) displayText = "Resolved";
        
        progressText.setText(displayText.toUpperCase());
        progressText.setTextSize(10);
        progressText.setTextColor(Color.WHITE);
        progressText.setTypeface(null, Typeface.BOLD);
        statusBox.addView(progressText);
        secondRow.addView(statusBox);

        mainContainer.addView(secondRow);
        
        // Row 3: Staff Assignment Comment (Below everything else)
        if (data.getStaffName() != null && !data.getStaffName().isEmpty() && !data.getStaffName().equals("null")) {
            TextView staffNote = new TextView(context);
            String staffInfo = "Staff assigned: " + data.getStaffName();
            if (data.getStaffPhone() != null && !data.getStaffPhone().isEmpty() && !data.getStaffPhone().equals("null")) {
                staffInfo += " (" + data.getStaffPhone() + ")";
            }
            staffNote.setText(staffInfo);
            staffNote.setTextColor(Color.parseColor("#444444")); 
            staffNote.setTextSize(12);
            staffNote.setPadding(dpToPx(context, 15), dpToPx(context, 8), dpToPx(context, 15), dpToPx(context, 10));
            mainContainer.addView(staffNote);
        }

        cardView.addView(mainContainer);
        return cardView;
    }

    private static android.graphics.drawable.Drawable createStatusBackground(String progress) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(getProgressColor(progress));
        gd.setCornerRadius(10);
        return gd;
    }

    private static int getPriorityColor(String priority) {
        if (priority == null) return Color.LTGRAY;
        switch (priority.toLowerCase()) {
            case "high": return Color.parseColor("#FF6B6B");
            case "medium": return Color.BLACK;
            case "low": return Color.parseColor("#6BCF7F");
            default: return Color.LTGRAY;
        }
    }

    private static int getProgressColor(String progress) {
        if (progress == null) return Color.GRAY;
        String p = progress.toLowerCase();
        if (p.contains("pending")) return Color.parseColor("#FF9800");
        if (p.contains("progress")) return Color.parseColor("#2196F3");
        if (p.contains("resolved") || p.contains("completed")) return Color.parseColor("#4CAF50");
        return Color.GRAY;
    }

    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}