package com.example.ksjcomplaintnmaintenance;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AdminMonthlySummary extends AppCompatActivity {

    private LineChart lineChart;
    private PieChart pieChart;
    private CardView cardBreakdown;
    private TextView tvTotalCases, tvResolvedRate, tvOpenCases, tvOverdueCases;
    private TextView tvHighCount, tvMediumCount, tvLowCount;
    private TextView tvChartTitle, tvTrendInsights, tvAvgComplaints, tvPeakMonth;
    private LinearLayout staffPerformanceContainer, layoutInsights, typeBreakdownContainer;
    private String selectedMonth, selectedYear;
    private View summaryView;
    private Button btnExportPdf, btnAnalyzeTrend, btnPreviousMonth, btnNextMonth, btnGenerate;
    private Spinner spinnerMonth, spinnerYear;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_monthly_summary);

        userId = getIntent().getStringExtra("USER_ID");

        // Initialize Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.btnMenu2);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_dashboard) {
                startActivity(new Intent(this, AdminDashboard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_user_management) {
                startActivity(new Intent(this, UserManagement.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_complaint_list) {
                startActivity(new Intent(this, AdminComplaintList.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_create_user) {
                startActivity(new Intent(this, AdminCreateUser.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_feedback_board) {
                startActivity(new Intent(this, AdminFeedbackBoard.class).putExtra("USER_ID", userId));
            } else if (id == R.id.nav_monthly_summary) {
                // Already here
            } else if (id == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // UI Initialization
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        cardBreakdown = findViewById(R.id.cardBreakdown);
        tvTotalCases = findViewById(R.id.tvTotalCases);
        tvResolvedRate = findViewById(R.id.tvResolvedRate);
        tvOpenCases = findViewById(R.id.tvOpenCases);
        tvOverdueCases = findViewById(R.id.tvOverdueCases);
        tvHighCount = findViewById(R.id.tvHighCount);
        tvMediumCount = findViewById(R.id.tvMediumCount);
        tvLowCount = findViewById(R.id.tvLowCount);
        staffPerformanceContainer = findViewById(R.id.staffPerformanceContainer);
        summaryView = findViewById(R.id.summaryContent);
        
        tvChartTitle = findViewById(R.id.tvChartTitle);
        tvTrendInsights = findViewById(R.id.tvTrendInsights);
        tvAvgComplaints = findViewById(R.id.tvAvgComplaints);
        tvPeakMonth = findViewById(R.id.tvPeakMonth);
        layoutInsights = findViewById(R.id.layoutInsights);
        typeBreakdownContainer = findViewById(R.id.typeBreakdownContainer);
        
        btnExportPdf = findViewById(R.id.btnExportPdf);
        btnAnalyzeTrend = findViewById(R.id.btnAnalyzeTrend);
        btnPreviousMonth = findViewById(R.id.btnPreviousMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        btnGenerate = findViewById(R.id.btnGenerate);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);

        // Set default to current Month/Year
        Calendar c = Calendar.getInstance();
        selectedMonth = String.format("%02d", c.get(Calendar.MONTH) + 1);
        selectedYear = String.valueOf(c.get(Calendar.YEAR));

        setupSpinners();
        setupChart();
        setupPieChart();
        
        // Removed fetchMonthlyData() from here to wait for Generate button

        // Click Listeners
        btnGenerate.setOnClickListener(v -> fetchMonthlyData());
        btnAnalyzeTrend.setOnClickListener(v -> fetchYearlyAnalysis());
        btnExportPdf.setOnClickListener(v -> exportToPdf());
        
        btnPreviousMonth.setOnClickListener(v -> {
            goToPreviousMonth();
            fetchMonthlyData();
        });
        
        btnNextMonth.setOnClickListener(v -> {
            goToNextMonth();
            fetchMonthlyData();
        });

    }

    private void setupSpinners() {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextSize(10); 
                tv.setPadding(10, 0, 0, 0); // Reduced padding to ensure visibility
                tv.setGravity(Gravity.CENTER_VERTICAL);
                return tv;
            }
        };
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(Integer.parseInt(selectedMonth) - 1);

        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 3; i <= currentYear + 3; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextSize(10);
                tv.setPadding(10, 0, 0, 0); // Reduced padding to ensure visibility
                tv.setGravity(Gravity.CENTER_VERTICAL);
                return tv;
            }
        };
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(years.indexOf(selectedYear));

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = String.format("%02d", spinnerMonth.getSelectedItemPosition() + 1);
                selectedYear = spinnerYear.getSelectedItem().toString();
                // No automatic fetch here
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerMonth.setOnItemSelectedListener(listener);
        spinnerYear.setOnItemSelectedListener(listener);
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#40FFFFFF")); // Subtle grid

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGranularity(1f); // Only whole numbers
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGridColor(Color.parseColor("#40FFFFFF"));

        lineChart.getAxisRight().setEnabled(false);
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.CIRCLE);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Monthly Status");
        pieChart.setCenterTextSize(14f);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void fetchMonthlyData() {
        // Sync Spinners with current selected month/year
        spinnerMonth.setSelection(Integer.parseInt(selectedMonth) - 1);
        for (int i = 0; i < spinnerYear.getCount(); i++) {
            if (spinnerYear.getItemAtPosition(i).toString().equals(selectedYear)) {
                spinnerYear.setSelection(i);
                break;
            }
        }

        tvChartTitle.setText("Daily Trend (" + getMonthName(Integer.parseInt(selectedMonth)) + ")");
        layoutInsights.setVisibility(View.GONE);

        String url = Config.getUrl("get_monthly_summary.php") + "?month=" + selectedMonth + "&year=" + selectedYear;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("MONTHLY_DEBUG", "Response: " + response);
            try {
                JSONObject json = new JSONObject(response);
                
                // We will update these cards locally for consistency
                // tvTotalCases.setText(json.optString("total", "0"));
                // tvResolvedRate.setText(json.optString("resolved_rate", "0%"));
                // tvOpenCases.setText(json.optString("open", "0"));
                // tvOverdueCases.setText(json.optString("overdue", "0"));

                // Update Charts remains the same
                JSONArray trendTotal = json.optJSONArray("trend_total");
                JSONArray trendOpen = json.optJSONArray("trend_open");
                JSONArray trendPending = json.optJSONArray("trend_pending");
                JSONArray trendResolved = json.optJSONArray("trend_resolved");

                if (trendTotal != null) {
                    updateChartData(trendTotal, 
                                   trendOpen != null ? trendOpen : new JSONArray(),
                                   trendPending != null ? trendPending : new JSONArray(),
                                   trendResolved, null, true);
                } else {
                    lineChart.clear();
                    lineChart.setNoDataText("No trend data available for this month");
                    lineChart.invalidate();
                }

                // Fallback: Calculate Priority and Staff Performance locally from all complaints
                fetchDetailedStatsLocally();

            } catch (Exception e) {
                Toast.makeText(this, "Data Error", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void fetchDetailedStatsLocally() {
        String urlFeedback = Config.getUrl("get_all_feedback.php");
        StringRequest feedbackRequest = new StringRequest(Request.Method.POST, urlFeedback, feedbackResponse -> {
            Map<String, List<Integer>> ratingsMap = new HashMap<>();
            try {
                JSONArray feedbackArray = new JSONArray(feedbackResponse.trim());
                for (int i = 0; i < feedbackArray.length(); i++) {
                    JSONObject fb = feedbackArray.getJSONObject(i);
                    String staffName = fb.optString("staff_name", "");
                    int rating = fb.optInt("rating", 0);
                    if (!staffName.isEmpty() && !staffName.equalsIgnoreCase("null")) {
                        List<Integer> staffRatings = ratingsMap.get(staffName);
                        if (staffRatings == null) {
                            staffRatings = new ArrayList<>();
                            ratingsMap.put(staffName, staffRatings);
                        }
                        staffRatings.add(rating);
                    }
                }
            } catch (Exception e) {
                Log.e("LOCAL_STATS", "Feedback JSON Error", e);
            }

            // After getting feedback, get complaints
            fetchComplaintsAndCombine(ratingsMap);

        }, error -> fetchComplaintsAndCombine(new HashMap<>())); // Proceed even if feedback fails

        Volley.newRequestQueue(this).add(feedbackRequest);
    }

    private void fetchComplaintsAndCombine(Map<String, List<Integer>> ratingsMap) {
        String url = Config.getUrl("get_all_complaints.php");
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray all = new JSONArray(response.trim());
                int high = 0, medium = 0, low = 0;
                int totalGlobal = 0, resolvedGlobal = 0, inProgressGlobal = 0, pendingGlobal = 0;
                Map<String, int[]> staffMap = new HashMap<>();
                Map<String, Integer> typeMap = new HashMap<>();

                for (int i = 0; i < all.length(); i++) {
                    JSONObject obj = all.getJSONObject(i);
                    String date = obj.optString("created_at", "");
                    
                    if (date.startsWith(selectedYear + "-" + selectedMonth)) {
                        totalGlobal++;
                        
                        String type = obj.optString("complaint_type", "General");
                        typeMap.put(type, typeMap.getOrDefault(type, 0) + 1);

                        String priority = obj.optString("priority", "low").toLowerCase();
                        if (priority.contains("high")) high++;
                        else if (priority.contains("medium")) medium++;
                        else low++;

                        String status = obj.optString("status", "pending").toLowerCase();
                        
                        if (status.equals("resolved") || status.equals("completed")) {
                            resolvedGlobal++;
                        } else if (status.equals("in progress") || status.equals("in_progress")) {
                            inProgressGlobal++;
                        } else {
                            pendingGlobal++;
                        }

                        String staffName = obj.optString("staff_name", "Unassigned");

                        if (staffName != null && !staffName.isEmpty() && 
                            !staffName.equalsIgnoreCase("null") && 
                            !staffName.equalsIgnoreCase("Unassigned")) {

                            int[] stats = staffMap.get(staffName);
                            if (stats == null) {
                                stats = new int[]{0, 0, 0};
                                staffMap.put(staffName, stats);
                            }
                            if (status.equals("resolved") || status.equals("completed")) stats[0]++;
                            else if (status.equals("in progress") || status.equals("in_progress")) stats[1]++;
                            else stats[2]++;
                        }
                    }
                }

                // Update Top Stats Card (Unified)
                tvTotalCases.setText(String.valueOf(totalGlobal));
                tvOpenCases.setText(String.valueOf(inProgressGlobal));
                tvOverdueCases.setText(String.valueOf(pendingGlobal));
                
                updatePieChart(inProgressGlobal, pendingGlobal, resolvedGlobal);

                if (totalGlobal > 0) {
                    int rate = (resolvedGlobal * 100) / totalGlobal;
                    tvResolvedRate.setText(rate + "%");
                } else {
                    tvResolvedRate.setText("0%");
                }

                tvHighCount.setText(String.valueOf(high));
                tvMediumCount.setText(String.valueOf(medium));
                tvLowCount.setText(String.valueOf(low));

                updateTypeBreakdownUI(typeMap);

                JSONArray staffArray = new JSONArray();
                for (Map.Entry<String, int[]> entry : staffMap.entrySet()) {
                    String name = entry.getKey();
                    int[] vals = entry.getValue();
                    JSONObject sObj = new JSONObject();
                    sObj.put("name", name);
                    sObj.put("completed", vals[0]);
                    sObj.put("open", vals[1]);
                    sObj.put("overdue", vals[2]);
                    
                    // Calculate Avg Rating
                    double avg = 0;
                    List<Integer> rList = ratingsMap.get(name);
                    if (rList != null && !rList.isEmpty()) {
                        double sum = 0;
                        for (int r : rList) sum += r;
                        avg = sum / rList.size();
                    }
                    sObj.put("avg_rating", String.format("%.1f", avg));
                    
                    staffArray.put(sObj);
                }
                updateStaffPerformanceUI(staffArray);

            } catch (Exception e) {
                Log.e("LOCAL_STATS", "Calculation Error", e);
            }
        }, error -> Log.e("LOCAL_STATS", "Network Error"));
        Volley.newRequestQueue(this).add(request);
    }


    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return months[month - 1];
    }

    private void updateStaffPerformanceUI(JSONArray staffArray) {
        staffPerformanceContainer.removeAllViews();
        if (staffArray == null || staffArray.length() == 0) {
            TextView empty = new TextView(this);
            empty.setText("No staff data available");
            empty.setTextSize(11);
            empty.setPadding(0, 10, 0, 0);
            staffPerformanceContainer.addView(empty);
            return;
        }

        try {
            for (int i = 0; i < staffArray.length(); i++) {
                JSONObject staff = staffArray.getJSONObject(i);
                
                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setOrientation(LinearLayout.VERTICAL);
                itemLayout.setPadding(0, 12, 0, 12);

                String name = staff.getString("name");
                String avgRating = staff.optString("avg_rating", "0.0");
                int completed = staff.getInt("completed");
                int open = staff.getInt("open");
                int overdue = staff.getInt("overdue");
                int total = completed + open + overdue;

                // Header: Name (Avg Rating)
                TextView tvHeader = new TextView(this);
                tvHeader.setText(name + " (" + avgRating + " ★)");
                tvHeader.setTextSize(13);
                tvHeader.setTypeface(null, Typeface.BOLD);
                tvHeader.setTextColor(Color.BLACK);
                itemLayout.addView(tvHeader);

                // Stats rows
                itemLayout.addView(createStatRow("In Progress", String.valueOf(open), "#F5A623")); // Yellow/Orange
                itemLayout.addView(createStatRow("Overdue", String.valueOf(overdue), "#D0021B")); // Red
                itemLayout.addView(createStatRow("Resolved", String.valueOf(completed), "#7ED321")); // Green
                itemLayout.addView(createStatRow("Total Task", String.valueOf(total), "#4A90E2")); // Blue

                staffPerformanceContainer.addView(itemLayout);

                if (i < staffArray.length() - 1) {
                    View divider = new View(this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    staffPerformanceContainer.addView(divider);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LinearLayout createStatRow(String label, String value, String colorHex) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 4, 0, 4);
        row.setGravity(Gravity.CENTER_VERTICAL);

        // Color indicator (small bar)
        View colorIndicator = new View(this);
        int color = Color.parseColor(colorHex);
        colorIndicator.setBackgroundColor(color);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(this, 12), dpToPx(this, 4));
        barParams.setMargins(0, 0, dpToPx(this, 8), 0);
        colorIndicator.setLayoutParams(barParams);
        row.addView(colorIndicator);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label + ": ");
        tvLabel.setTextSize(10);
        tvLabel.setTextColor(Color.parseColor("#555555"));
        tvLabel.setTypeface(null, Typeface.NORMAL);
        
        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextSize(10);
        tvValue.setTextColor(Color.BLACK);
        tvValue.setTypeface(null, Typeface.BOLD);

        row.addView(tvLabel);
        row.addView(tvValue);
        
        return row;
    }

    private void updateTypeBreakdownUI(Map<String, Integer> typeMap) {
        typeBreakdownContainer.removeAllViews();
        if (typeMap == null || typeMap.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No data available");
            empty.setTextSize(11);
            typeBreakdownContainer.addView(empty);
            return;
        }

        // Color palette for types
        String[] palette = {"#4A90E2", "#F5A623", "#7ED321", "#D0021B", "#9013FE", "#50E3C2"};
        int colorIdx = 0;

        for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
            String color = palette[colorIdx % palette.length];
            typeBreakdownContainer.addView(createStatRow(entry.getKey(), String.valueOf(entry.getValue()), color));
            colorIdx++;
        }
    }

    private void updatePieChart(int open, int pending, int resolved) {
        if (open == 0 && pending == 0 && resolved == 0) {
            cardBreakdown.setVisibility(View.GONE);
            return;
        }

        cardBreakdown.setVisibility(View.VISIBLE);
        ArrayList<com.github.mikephil.charting.data.PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        
        if (open > 0) {
            entries.add(new com.github.mikephil.charting.data.PieEntry(open, "In Progress"));
            colors.add(Color.parseColor("#F5A623")); // Yellow
        }
        if (pending > 0) {
            entries.add(new com.github.mikephil.charting.data.PieEntry(pending, "Pending"));
            colors.add(Color.parseColor("#D0021B")); // Red
        }
        if (resolved > 0) {
            entries.add(new com.github.mikephil.charting.data.PieEntry(resolved, "Resolved"));
            colors.add(Color.parseColor("#7ED321")); // Green
        }

        com.github.mikephil.charting.data.PieDataSet dataSet = new com.github.mikephil.charting.data.PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        com.github.mikephil.charting.data.PieData data = new com.github.mikephil.charting.data.PieData(dataSet);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.DefaultValueFormatter(0));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        pieChart.setData(data);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private int dpToPx(android.content.Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    private void fetchYearlyAnalysis() {
        tvChartTitle.setText("Long-term Pattern Analysis");
        layoutInsights.setVisibility(View.VISIBLE);

        String url = Config.getUrl("get_monthly_summary.php") + "?mode=analyze";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("MONTHLY_DEBUG", "Analysis Response: " + response);
            try {
                JSONObject json = new JSONObject(response);
                JSONArray total = json.getJSONArray("trend_total");
                JSONArray open = json.getJSONArray("trend_open");
                JSONArray pending = json.getJSONArray("trend_pending");
                JSONArray resolved = json.optJSONArray("trend_resolved");

                // Calculate Trends & Insights
                int ytdTotal = 0, ytdResolved = 0, maxCount = -1;
                String peakMonth = "N/A";
                
                for (int i = 0; i < total.length(); i++) {
                    int count = total.getJSONObject(i).getInt("count");
                    ytdTotal += count;
                    if (count > maxCount) {
                        maxCount = count;
                        peakMonth = total.getJSONObject(i).getString("label").replace("\n", " ");
                    }
                    if (resolved != null) ytdResolved += resolved.getJSONObject(i).getInt("count");
                }
                
                double avg = (double) ytdTotal / total.length();
                tvAvgComplaints.setText(String.format("Avg: %.1f/mo", avg));
                tvPeakMonth.setText("Peak: " + peakMonth);

                // Auto-generated insight text
                if (total.length() >= 2) {
                    int last = total.getJSONObject(total.length()-1).getInt("count");
                    int prev = total.getJSONObject(total.length()-2).getInt("count");
                    if (last > prev) tvTrendInsights.setText("Complaints increased recently. Peak month identified as " + peakMonth + ".");
                    else if (last < prev) tvTrendInsights.setText("Complaint volume is trending downwards. Good progress!");
                    else tvTrendInsights.setText("Complaint volume is stable across recent months.");
                }

                String[] labels = new String[total.length()];
                for (int i = 0; i < total.length(); i++) {
                    labels[i] = total.getJSONObject(i).getString("label");
                }

                updateChartData(total, open, pending, resolved, labels, false);
                Toast.makeText(this, "Patterns Analyzed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) { 
                Log.e("MONTHLY_DEBUG", "Analysis Error: " + e.getMessage());
            }
        }, error -> Toast.makeText(this, "Analysis Failed", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateChartData(JSONArray total, JSONArray open, JSONArray pending, JSONArray resolved, String[] labels, boolean isDaily) throws Exception {
        lineChart.clear();
        XAxis xAxis = lineChart.getXAxis();

        if (isDaily) {
            lineChart.fitScreen();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override public String getFormattedValue(float value) { return String.valueOf((int) value); }
            });
            xAxis.setLabelRotationAngle(0f);
            xAxis.setAxisMinimum(0.5f);
            xAxis.setAxisMaximum(31.5f);
            lineChart.setExtraBottomOffset(10f);
        } else {
            lineChart.fitScreen();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setLabelRotationAngle(-20f);
            xAxis.setAxisMinimum(-0.5f);
            xAxis.setAxisMaximum(labels.length - 0.5f);
            lineChart.setExtraBottomOffset(30f);
        }

        LineData data = new LineData();
        data.addDataSet(createLineSet(total, "Total", "#4A90E2", isDaily));
        
        if (!isDaily) { // Only show details in "Analyze" mode
            data.addDataSet(createLineSet(open, "In Progress", "#F5A623", isDaily));
            data.addDataSet(createLineSet(pending, "Pending", "#D0021B", isDaily));
            if (resolved != null) {
                data.addDataSet(createLineSet(resolved, "Resolved", "#7ED321", isDaily));
            }
        }

        lineChart.setData(data);

        if (isDaily) {
            lineChart.setVisibleXRangeMaximum(7);
            lineChart.moveViewToX(1);
        }
        lineChart.animateX(800);
        lineChart.invalidate();
    }

    private LineDataSet createLineSet(JSONArray array, String label, String colorStr, boolean isDaily) throws Exception {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            float x = isDaily ? (float) obj.getInt("day") : (float) i;
            entries.add(new Entry(x, (float) obj.getInt("count")));
        }
        LineDataSet set = new LineDataSet(entries, label);
        int color = Color.parseColor(colorStr);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(2.5f);
        set.setCircleRadius(3.5f);
        set.setDrawCircleHole(true);
        set.setCircleHoleColor(Color.parseColor("#E5989B"));
        set.setDrawValues(true);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setValueFormatter(new com.github.mikephil.charting.formatter.DefaultValueFormatter(0));
        set.setMode(LineDataSet.Mode.LINEAR);
        return set;
    }

    private void exportToPdf() {
        lineChart.fitScreen();

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(summaryView.getWidth(), summaryView.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        summaryView.draw(page.getCanvas());
        document.finishPage(page);

        lineChart.setVisibleXRangeMaximum(7);

        String fileName = "KSJ_Report_" + selectedMonth + "_" + selectedYear + ".pdf";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream out = getContentResolver().openOutputStream(uri);
                    if (out != null) {
                        document.writeTo(out);
                        out.close();
                    }
                }
            } else {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                document.writeTo(new FileOutputStream(file));
            }
            Toast.makeText(this, "PDF Downloaded", Toast.LENGTH_LONG).show();
        } catch (Exception e) { e.printStackTrace(); }
        document.close();
    }

    private void goToPreviousMonth() {
        int m = Integer.parseInt(selectedMonth);
        int y = Integer.parseInt(selectedYear);
        if (m == 1) { m = 12; y--; } else { m--; }
        selectedMonth = String.format("%02d", m);
        selectedYear = String.valueOf(y);
        fetchMonthlyData();
    }

    private void goToNextMonth() {
        int m = Integer.parseInt(selectedMonth);
        int y = Integer.parseInt(selectedYear);
        if (m == 12) { m = 1; y++; } else { m++; }
        selectedMonth = String.format("%02d", m);
        selectedYear = String.valueOf(y);
        fetchMonthlyData();
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
