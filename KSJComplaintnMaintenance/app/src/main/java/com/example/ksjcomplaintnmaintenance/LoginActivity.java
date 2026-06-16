package com.example.ksjcomplaintnmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    TextView go_to_signup, forgot_pass_txt;
    EditText username, passwords;
    Button login_btn;
    CheckBox remember_me_chkbox;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    SharedPreferences Preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        LinearLayout registration = findViewById(R.id.register_btn);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This works exactly like a button click
                Log.d("TAG", "Card clicked!");

                /*----- to check the user role in database--------------
                private void checkUserRole(String email, String roleName){
                    User Admin = userRepository.findByEmail(email);
                    if (user != null) {
                        return admin.getRoles().stream()
                                .anyMatch(role -> role.getName().equals(roleName));
                    }
                    return false;
                }*/

                //to decide the next activity per role of user
                if (userRole = student){
                    Intent intent = new Intent(LoginActivity.this, StudentDashboard.class);
                    startActivity(intent);
                }else if (userRole = staff){
                    Intent intent = new Intent(LoginActivity.this, StaffDashboard.class);
                }else if(userRole = admin){
                    Intent intent = new Intent(LoginActivity.this, AdminDashboard.class);
                }else
                    System.out.print("Your are not in the system. Please register first if you are a student.");

            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        remember_me_chkbox = findViewById(R.id.remember_me_chkbox);
        forgot_pass_txt = findViewById(R.id.forgot_pass_txt);
        go_to_signup = findViewById(R.id.go_to_signup);
        username = findViewById(R.id.username);
        passwords = findViewById(R.id.passwords);
        login_btn = findViewById(R.id.register_btn);

        Preferences = getSharedPreferences(Config.preference, MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();

        remember_me_chkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = Preferences.edit();
            editor.putString(Config.remember_me, isChecked ? "true" : "false");
            editor.apply();
        });

        login_btn.setOnClickListener(v -> {
            if (Validate()) {
                userLogin();
            }
        });

        forgot_pass_txt.setOnClickListener(view -> recover_password_dialog());

        go_to_signup.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrationActivity.class));
        });
    }

    // ================= LOGIN =================
    private void userLogin() {
        String email = username.getText().toString().trim();
        String password = passwords.getText().toString().trim();

        progressDialog.show();

        // Perform Firebase Auth Login
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // After successful Auth, find the user in the database
                            checkUserRole(user.getUid());
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Session error.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        // This shows exactly why login failed (e.g., wrong password)
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


}