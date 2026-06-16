package com.example.ksjcomplaintnmaintenance;

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

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordOTP extends AppCompatActivity {

    private TextView goToLogin, resend_email;
    private EditText reset_otp;
    private LinearLayout continue_resetBtn;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password_otp);

        // 1. Retrieve the email passed from ResetPasswordEmail activity
        userEmail = getIntent().getStringExtra("EMAIL");

        reset_otp = findViewById(R.id.reset_otp);
        continue_resetBtn = findViewById(R.id.continue_resetBtn);
        goToLogin = findViewById(R.id.go_to_login);
        resend_email = findViewById(R.id.resend_email);

        // 2. CONTINUE BUTTON: Verify OTP via PHP
        continue_resetBtn.setOnClickListener(v -> {
            String enteredOtp = reset_otp.getText().toString().trim();

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call server to verify the code
            verifyOtpOnServer(userEmail, enteredOtp);
        });

        // 3. RESEND EMAIL: Call forgot_password.php again
        resend_email.setOnClickListener(v -> {
            resendOtpRequest(userEmail);
        });

        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordOTP.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void verifyOtpOnServer(String email, String code) {
        String url = Config.getUrl("verify.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("SERVER_DEBUG", "Verify Response: " + response);
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show();

                        // Proceed to ResetNewPassword Activity
                        Intent intent = new Intent(ResetPasswordOTP.this, ResetNewPassword.class);
                        intent.putExtra("EMAIL", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error: Check XAMPP", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("code", code);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void resendOtpRequest(String email) {
        String url = Config.getUrl("forgot_password.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "New OTP sent to your email!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Failed to resend. Check connection.", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
