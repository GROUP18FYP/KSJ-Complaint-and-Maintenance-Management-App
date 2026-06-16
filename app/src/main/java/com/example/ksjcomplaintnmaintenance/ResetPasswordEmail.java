package com.example.ksjcomplaintnmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordEmail extends AppCompatActivity {

    private TextView goToLogin;
    private EditText username;
    private LinearLayout resetPassEmailBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password_email);

        username = findViewById(R.id.username);
        goToLogin = findViewById(R.id.go_to_login);
        resetPassEmailBtn = findViewById(R.id.resetPassEmailBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending OTP...");
        progressDialog.setCancelable(false);

        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ResetPasswordEmail.this, LoginActivity.class));
            finish();
        });

        resetPassEmailBtn.setOnClickListener(v -> {
            String email = username.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.endsWith("@graduate.utm.my")) {
                username.setError("Please use your student email");
                return;
            }

            // 1. ONLY call the network request here.
            // DO NOT start the activity here.
            sendOtpRequest(email);
        });
    }

    private void sendOtpRequest(String email) {
        String url = Config.getUrl("forgot_password.php");

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d("SERVER_DEBUG", "ForgotPass Response: " + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response.trim());
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            Toast.makeText(this, "OTP sent to your email!", Toast.LENGTH_LONG).show();

                            // 2. THIS IS THE KEYPOINT:
                            // Only move to the next screen if the server confirms success.
                            Intent intent = new Intent(ResetPasswordEmail.this, ResetPasswordOTP.class);

                            // 3. Pass the email to the next screen so the Resend button knows who to send to
                            intent.putExtra("EMAIL", email);

                            startActivity(intent);
                            finish(); // Optional: close this screen so they can't go back to it

                        } else {
                            String message = jsonObject.optString("message", "Email not found.");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("SERVER_DEBUG", "JSON Error: " + e.getMessage());
                        Toast.makeText(this, "Server error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("SERVER_DEBUG", "Volley Error: " + error.toString());
                    Toast.makeText(this, "Connection Error. Check XAMPP.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}