package com.example.ksjcomplaintnmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView goToSignup, forgotpassBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.passwords);
        loginBtn = findViewById(R.id.register_btn);
        goToSignup = findViewById(R.id.go_to_signup);
        forgotpassBtn = findViewById(R.id.forgot_passBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating...");

        forgotpassBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordEmail.class);
            startActivity(intent); // You were missing this line
        });

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, pass);
            }
        });

        goToSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        progressDialog.show();

        String url = Config.getUrl("login.php");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d("SERVER_DEBUG", "Response: " + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response.trim());
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            String role = jsonObject.getString("role");
                            String userId = jsonObject.getString("user_id");
                            String fullName = jsonObject.optString("fullname", "");

                            // Save user info to SharedPreferences
                            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit()
                                    .putString("USER_ID", userId)
                                    .putString("FULL_NAME", fullName)
                                    .putString("USER_ROLE", role)
                                    .apply();

                            Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();

                            Intent intent;
                            if (role.equalsIgnoreCase("admin")) {
                                // If admin, go to Admin Dashboard
                                intent = new Intent(LoginActivity.this, AdminDashboard.class);
                            } else if (role.equalsIgnoreCase("staff")) {
                                // If staff, go to Staff Dashboard
                                intent = new Intent(LoginActivity.this, StaffDashboard.class);
                            } else {
                                intent = new Intent(LoginActivity.this, StudentDashboard.class);
                            }

                            // PASS THE USER ID AND NAME TO THE NEXT ACTIVITY
                            intent.putExtra("USER_ID", userId);
                            intent.putExtra("FULL_NAME", fullName);
                            startActivity(intent);
                            finish();

                        } else {
                            String message = jsonObject.optString("message", "Invalid Email or Password");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("SERVER_DEBUG", "JSON Error: " + e.getMessage());
                        Toast.makeText(this, "Server Error: Invalid Format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Check XAMPP connection", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}