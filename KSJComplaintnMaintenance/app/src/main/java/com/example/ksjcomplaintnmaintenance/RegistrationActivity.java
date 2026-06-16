package com.example.ksjcomplaintnmaintenance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout registration = findViewById(R.id.register_btn);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This works exactly like a button click
                Log.d("TAG", "Card clicked!");

                // Navigate to another activity
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
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