package com.example.ksjcomplaintnmaintenance;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class UserCardView {

    public static class UserData {
        private String userId;
        private String fullname;
        private String email;
        private String role;

        public UserData(String userId, String fullname, String email, String role) {
            this.userId = userId;
            this.fullname = fullname;
            this.email = email;
            this.role = role;
        }

        public String getUserId() { return userId; }
        public String getFullname() { return fullname; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }

    public static CardView createUserCard(Context context, UserData data) {
        if (data == null) return null;

        CardView cardView = new CardView(context);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(dpToPx(context, 14), 0, dpToPx(context, 14), dpToPx(context, 12));
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(dpToPx(context, 4));
        cardView.setRadius(dpToPx(context, 8));
        cardView.setUseCompatPadding(true);
        cardView.setCardBackgroundColor(Color.WHITE);

        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(dpToPx(context, 15), dpToPx(context, 12), dpToPx(context, 15), dpToPx(context, 12));

        // Name and Role row
        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView nameText = new TextView(context);
        nameText.setText(data.getFullname());
        nameText.setTextColor(Color.parseColor("#1E1E1E"));
        nameText.setTextSize(18);
        nameText.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        nameText.setLayoutParams(nameParams);
        topRow.addView(nameText);

        TextView roleText = new TextView(context);
        String displayRole = data.getRole().toUpperCase();
        if (displayRole.equals("USER")) displayRole = "STUDENT";
        roleText.setText(displayRole);
        roleText.setTextColor(getRoleColor(data.getRole()));
        roleText.setTextSize(12);
        roleText.setTypeface(null, Typeface.BOLD);
        topRow.addView(roleText);

        mainContainer.addView(topRow);

        // Email row
        TextView emailText = new TextView(context);
        emailText.setText(data.getEmail());
        emailText.setTextColor(Color.GRAY);
        emailText.setTextSize(14);
        emailText.setPadding(0, dpToPx(context, 4), 0, 0);
        mainContainer.addView(emailText);

        cardView.addView(mainContainer);
        return cardView;
    }

    private static int getRoleColor(String role) {
        switch (role.toLowerCase()) {
            case "admin": return Color.parseColor("#76002E"); // Your primary dark red
            case "staff": return Color.parseColor("#1565C0"); // Blue
            case "user": 
            case "student": return Color.parseColor("#2E7D32"); // Green
            default: return Color.GRAY;
        }
    }

    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
