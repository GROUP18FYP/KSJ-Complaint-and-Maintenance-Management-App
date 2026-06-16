package com.example.ksjcomplaintnmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ResetNewPassword extends AppCompatActivity {

    private LinearLayout resetPasswordBtn;
    private EditText new_password, new_pass_check;
    private TextView go_to_login;
    private String userEmail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_new_password);

        // Get email from intent - Ensure the key "EMAIL" matches the key used in ResetPasswordOTP
        userEmail = getIntent().getStringExtra("EMAIL");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating password...");
        progressDialog.setCancelable(false);

        new_password = findViewById(R.id.new_password);
        new_pass_check = findViewById(R.id.new_pass_check);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        go_to_login = findViewById(R.id.go_to_login);

        resetPasswordBtn.setOnClickListener(v -> {
            String newPass = new_password.getText().toString().trim();
            String checkPass = new_pass_check.getText().toString().trim();

            if (newPass.isEmpty() || checkPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(checkPass)) {
                new_pass_check.setError("Passwords do not match");
                return;
            }

            if (!isAcceptablePassword(newPass)) {
                new_password.setError("Password must be 8-30 characters with uppercase, lowercase, digits and special characters.");
                return;
            }

            // 1. ONLY call the network method here.
            // DO NOT put startActivity() here, otherwise the request will fail.
            updatePasswordOnServer(userEmail, newPass);
        });

        go_to_login.setOnClickListener(v -> {
            startActivity(new Intent(ResetNewPassword.this, LoginActivity.class));
            finish();
        });
    }

    // Password Validation Logic
    public static final String SPECIAL_CHARACTERS = "!@#$%^&*()~`-=_+[]{}|:\";',./<>?";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 30;

    public static boolean isAcceptablePassword(String password) {
        if (TextUtils.isEmpty(password)) return false;
        password = password.trim();
        int len = password.length();
        if (len < MIN_PASSWORD_LENGTH || len > MAX_PASSWORD_LENGTH) return false;

        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (SPECIAL_CHARACTERS.indexOf(String.valueOf(c)) >= 0) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void updatePasswordOnServer(String email, String newPass) {
        // Ensure this filename matches your PHP file in XAMPP htdocs
        String url = Config.getUrl("reset_password_final.php");

        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Log.d("SERVER_DEBUG", "Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response.trim());
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(this, "Password Changed Successfully!", Toast.LENGTH_LONG).show();

                            // 2. NOW switch screens because the server has confirmed success
                            Intent intent = new Intent(ResetNewPassword.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("SERVER_DEBUG", "JSON Error: " + e.getMessage());
                        Toast.makeText(this, "Server Error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Check XAMPP Connection", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", newPass);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}