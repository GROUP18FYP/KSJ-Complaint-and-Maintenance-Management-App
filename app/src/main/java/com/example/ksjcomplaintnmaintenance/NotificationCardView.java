package com.example.ksjcomplaintnmaintenance;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class NotificationCardView {

    public static class NotificationData {
        private String id;
        private String message;
        private String date;
        private int isRead;

        public NotificationData(String id, String message, String date, int isRead) {
            this.id = id;
            this.message = message;
            this.date = date;
            this.isRead = isRead;
        }

        public String getId() { return id; }
        public String getMessage() { return message; }
        public String getDate() { return date; }
        public int getIsRead() { return isRead; }
    }

    public static CardView createNotificationCard(Context context, NotificationData data) {
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
        
        // If unread, slightly different background or indicator
        if (data.getIsRead() == 0) {
            cardView.setCardBackgroundColor(Color.parseColor("#FFF9C4")); // Light yellow for unread
        } else {
            cardView.setCardBackgroundColor(Color.WHITE);
        }

        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(dpToPx(context, 15), dpToPx(context, 12), dpToPx(context, 15), dpToPx(context, 12));

        TextView msgText = new TextView(context);
        msgText.setText(data.getMessage());
        msgText.setTextColor(Color.parseColor("#1E1E1E"));
        msgText.setTextSize(16);
        if (data.getIsRead() == 0) {
            msgText.setTypeface(null, Typeface.BOLD);
        }
        mainContainer.addView(msgText);

        TextView dateText = new TextView(context);
        dateText.setText(data.getDate());
        dateText.setTextColor(Color.GRAY);
        dateText.setTextSize(12);
        dateText.setPadding(0, dpToPx(context, 8), 0, 0);
        dateText.setGravity(Gravity.END);
        mainContainer.addView(dateText);

        cardView.addView(mainContainer);
        return cardView;
    }

    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
