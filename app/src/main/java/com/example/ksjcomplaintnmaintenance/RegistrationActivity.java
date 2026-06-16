package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, otpInput;
    private LinearLayout registrationLayout, verificationLayout, registerBtn, forgotpassBtn;
    private Button verifyFinalBtn;
    private String tempEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Initialize Registration UI
        registrationLayout = findViewById(R.id.registration_layout);
        nameInput = findViewById(R.id.user_name);
        emailInput = findViewById(R.id.user_email);
        passwordInput = findViewById(R.id.passwords);
        registerBtn = findViewById(R.id.register_btn);

        // 2. Initialize Verification UI
        verificationLayout = findViewById(R.id.verification_layout);
        otpInput = findViewById(R.id.otp_input);
        verifyFinalBtn = findViewById(R.id.verify_final_btn);

        // Initially make sure verification is hidden
        verificationLayout.setVisibility(View.GONE);


        // Initially make sure verification is hidden
        verificationLayout.setVisibility(View.GONE);

        // 3. Handle Register Button Click
        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            // 1. Check for empty fields
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. RESTRICT EMAIL FORMAT: Must end with @graduate.utm.my
            if (!email.endsWith("@graduate.utm.my")) {
                emailInput.setError("Please use your student email");
                Toast.makeText(this, "Please use your student email", Toast.LENGTH_LONG).show();
                return;
            }

            // Check if the password is within the requirement
            if (!isAcceptablePassword(pass)) {
                passwordInput.setError("Password must be 8-30 characters with uppercase, lowercase, digits and special characters.");
                Toast.makeText(this, "Invalid password format", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. If everything is okay, proceed to register
            registerUser(name, email, pass);
        });

        // 4. Handle Verify Button Click
        verifyFinalBtn.setOnClickListener(v -> {
            String code = otpInput.getText().toString().trim();
            if (code.length() == 6) {
                if (tempEmail != null) {
                    verifyUser(tempEmail, code);
                } else {
                    Toast.makeText(this, "Error: Email missing", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter 6-digit code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        String url = Config.getUrl("register.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("SERVER_DEBUG", "Register Response: " + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response.trim());

                        if (jsonObject.getString("status").equals("success")) {
                            tempEmail = email; 
                            registrationLayout.setVisibility(View.GONE);
                            verificationLayout.setVisibility(View.VISIBLE);

                            Toast.makeText(this, "Code sent! Check your email.", Toast.LENGTH_LONG).show();

                        } else {
                            String errorMsg = jsonObject.optString("message", "Registration failed");
                            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("SERVER_DEBUG", "JSON Error: " + e.getMessage());
                        if (response.contains("Duplicate entry")) {
                            Toast.makeText(this, "ID Error: Please fix AUTO_INCREMENT in database.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Server error. Check XAMPP/PHP.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    Log.e("SERVER_DEBUG", "Volley Error: " + error.toString());
                    Toast.makeText(this, "Request timed out. Check internet/XAMPP.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", name);
                params.put("email", email.toLowerCase().trim());
                params.put("password", password);
                params.put("role", "user");
                return params;
            }
        };

        // 1. INCREASE TIMEOUT TO 30 SECONDS
        // 2. SET MAX RETRIES TO 0 (Prevents multiple OTPs)
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, 
                0, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    private void verifyUser(String email, String code) {
        String url = Config.getUrl("verify.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("SERVER_DEBUG", "Verify Response: " + response);
                    String cleanResponse = response.trim();

                    if (cleanResponse.equalsIgnoreCase("success")) {
                        Toast.makeText(this, "Verification Successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to Log n or Dashboard
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Shows "wrong_code" or actual PHP error
                        Toast.makeText(this, "Failed: " + cleanResponse, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("SERVER_DEBUG", "Volley Error: " + error.toString());
                    Toast.makeText(this, "Network Error: Check XAMPP", Toast.LENGTH_SHORT).show();
                }) {
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

    // Source - https://stackoverflow.com/a/9962509
    // Posted by Luiggi Mendoza, modified by community. See post 'Timeline' for change history
    // Retrieved 2026-05-09, License - CC BY-SA 3.0
    public static final String SPECIAL_CHARACTERS = "!@#$%^&*()~`-=_+[]{}|:\";',./<>?";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 30;

    public static boolean isAcceptablePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            System.out.println("empty string.");
            return false;
        }
        password = password.trim();
        int len = password.length();
        if(len < MIN_PASSWORD_LENGTH || len > MAX_PASSWORD_LENGTH) {
            System.out.println("wrong size, it must have at least 8 characters and less than 30.");
            return false;
        }
        char[] aC = password.toCharArray();
        for(char c : aC) {
            if (Character.isUpperCase(c)) {
                System.out.println(c + " is uppercase.");
            } else
            if (Character.isLowerCase(c)) {
                System.out.println(c + " is lowercase.");
            } else
            if (Character.isDigit(c)) {
                System.out.println(c + " is digit.");
            } else
            if (SPECIAL_CHARACTERS.indexOf(String.valueOf(c)) >= 0) {
                System.out.println(c + " is valid symbol.");
            } else {
                System.out.println(c + " is an invalid character in the password.");
                return false;
            }
        }
        return true;
    }
}