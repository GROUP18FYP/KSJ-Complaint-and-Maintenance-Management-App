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

    // Data model class
    public static class ComplaintData {
        private String complaintType;
        private String priority;
        private String dateSubmitted;
        private String progress;

        public ComplaintData(String complaintType, String priority,
                             String dateSubmitted, String progress) {
            this.complaintType = complaintType;
            this.priority = priority;
            this.dateSubmitted = dateSubmitted;
            this.progress = progress;
        }

        public String getComplaintType() { return complaintType; }
        public String getPriority() { return priority; }
        public String getDateSubmitted() { return dateSubmitted; }
        public String getProgress() { return progress; }
    }

    public static CardView createComplaintCard(Context context, ComplaintData data) {
        if (data == null) {
            return null;
        }

        // Create CardView as container
        CardView cardView = new CardView(context);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(
                dpToPx(context, 14),
                0,
                dpToPx(context, 14),
                dpToPx(context, 48)
        );
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(dpToPx(context, 4));
        cardView.setRadius(dpToPx(context, 8));
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(
                dpToPx(context, 0),
                dpToPx(context, 8),
                dpToPx(context, 0),
                dpToPx(context, 8)
        );

        // Set background from drawable
        cardView.setBackgroundResource(R.drawable.s7d5260sw1cr10bf3e6eb);

        // Main container (vertical)
        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setClipChildren(false);
        mainContainer.setClipToPadding(false);

        // First row container (horizontal)
        LinearLayout firstRow = new LinearLayout(context);
        firstRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams firstRowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        firstRowParams.setMargins(
                dpToPx(context, 15),
                0,
                dpToPx(context, 15),
                dpToPx(context, 2)
        );
        firstRow.setLayoutParams(firstRowParams);

        // Left column (complaint info)
        LinearLayout leftColumn = new LinearLayout(context);
        leftColumn.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        leftParams.setMarginEnd(dpToPx(context, 13));
        leftColumn.setLayoutParams(leftParams);

        // Complaint title
        TextView titleText = new TextView(context);
        titleText.setText("Complaint");
        titleText.setTextColor(Color.parseColor("#1E1E1E"));
        titleText.setTextSize(16);
        titleText.setTypeface(null, Typeface.BOLD);
        leftColumn.addView(titleText);

        // Complaint type container
        LinearLayout typeContainer = new LinearLayout(context);
        typeContainer.setOrientation(LinearLayout.HORIZONTAL);
        typeContainer.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams typeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        typeParams.setMarginEnd(dpToPx(context, 46));
        typeContainer.setLayoutParams(typeParams);

        // Icon placeholder (16x16 dp)
        LinearLayout iconPlaceholder = new LinearLayout(context);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(context, 16),
                dpToPx(context, 16)
        );
        iconParams.setMarginEnd(dpToPx(context, 12));
        iconPlaceholder.setLayoutParams(iconParams);
        iconPlaceholder.setBackgroundColor(Color.TRANSPARENT);
        typeContainer.addView(iconPlaceholder);

        // Complaint type text
        TextView complaintTypeText = new TextView(context);
        LinearLayout.LayoutParams complaintParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        complaintTypeText.setLayoutParams(complaintParams);
        complaintTypeText.setText(data.getComplaintType());
        complaintTypeText.setTextColor(Color.BLACK);
        complaintTypeText.setPadding(0, 0, 0, dpToPx(context, 2));
        typeContainer.addView(complaintTypeText);

        leftColumn.addView(typeContainer);
        firstRow.addView(leftColumn);

        // Priority column
        LinearLayout priorityColumn = new LinearLayout(context);
        priorityColumn.setOrientation(LinearLayout.VERTICAL);
        priorityColumn.setPadding(
                dpToPx(context, 10),
                0,
                dpToPx(context, 10),
                dpToPx(context, 3)
        );
        priorityColumn.setBackgroundColor(getPriorityColor(context, data.getPriority()));

        TextView priorityText = new TextView(context);
        priorityText.setText(data.getPriority());
        priorityText.setTextColor(Color.parseColor("#090909"));
        priorityText.setTextSize(13);
        priorityText.setTypeface(null, Typeface.BOLD);
        priorityColumn.addView(priorityText);

        firstRow.addView(priorityColumn);
        mainContainer.addView(firstRow);

        // Date row
        LinearLayout dateRow = new LinearLayout(context);
        dateRow.setOrientation(LinearLayout.HORIZONTAL);
        dateRow.setGravity(Gravity.CENTER_VERTICAL);
        dateRow.setClipChildren(false);
        dateRow.setClipToPadding(false);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dateParams.setMargins(
                dpToPx(context, 42),
                0,
                0,
                dpToPx(context, 16)
        );
        dateRow.setLayoutParams(dateParams);

        // Calendar icon
        ImageView calendarIcon = new ImageView(context);
        LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(
                dpToPx(context, 20),
                dpToPx(context, 20)
        );
        iconLayoutParams.setMarginEnd(dpToPx(context, 1));
        calendarIcon.setLayoutParams(iconLayoutParams);
        calendarIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        // Set your calendar icon - make sure you have this drawable
        calendarIcon.setImageResource(R.drawable.outline_calendar_month_24);

        dateRow.addView(calendarIcon);

        // Date text
        TextView dateText = new TextView(context);
        dateText.setText(data.getDateSubmitted());
        dateText.setTextColor(Color.BLACK);
        dateText.setTextSize(12);
        dateRow.addView(dateText);

        mainContainer.addView(dateRow);

        // Progress row
        LinearLayout progressRow = new LinearLayout(context);
        progressRow.setOrientation(LinearLayout.VERTICAL);
        progressRow.setGravity(Gravity.END);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        progressParams.setMargins(
                dpToPx(context, 12),
                0,
                dpToPx(context, 12),
                0
        );
        progressRow.setLayoutParams(progressParams);

        TextView progressText = new TextView(context);
        progressText.setText(data.getProgress());
        progressText.setTextSize(12);
        progressText.setTextColor(getProgressColor(context, data.getProgress()));
        progressRow.addView(progressText);

        mainContainer.addView(progressRow);
        cardView.addView(mainContainer);

        return cardView;
    }

    // Helper method to get priority background color
    private static int getPriorityColor(Context context, String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return Color.parseColor("#FF6B6B"); // Red
            case "medium":
                return Color.parseColor("#FFD93D"); // Yellow
            case "low":
                return Color.parseColor("#6BCF7F"); // Green/Blue
            default:
                return Color.parseColor("#E0E0E0"); // Gray
        }
    }

    // Helper method to get progress text color
    private static int getProgressColor(Context context, String progress) {
        switch (progress.toLowerCase()) {
            case "pending":
                return Color.parseColor("#FF9800"); // Orange
            case "in progress":
                return Color.parseColor("#2196F3"); // Blue
            case "completed":
                return Color.parseColor("#4CAF50"); // Green
            default:
                return Color.parseColor("#9E9E9E"); // Gray
        }
    }

    // Helper method to convert dp to pixels
    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}