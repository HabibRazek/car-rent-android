package com.habib.car_rent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize inputs and buttons
        emailInput = findViewById(R.id.emailOrPhoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button logInButton = findViewById(R.id.logInButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        // Set click listener for log in button
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });

        // Set click listener for sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the SignUpActivity
                Intent intent = new Intent(SignInActivity.this, com.habib.car_rent.SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(SignInActivity.this, "Authentication success.",
                                Toast.LENGTH_SHORT).show();

                        // Redirect to ListCarsActivity
                        Intent intent = new Intent(SignInActivity.this, PanelActivity.class);
                        startActivity(intent);
                        finish(); // Close the SignInActivity
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailInput.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Required.");
            valid = false;
        } else {
            emailInput.setError(null);
        }

        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Required.");
            valid = false;
        } else {
            passwordInput.setError(null);
        }

        return valid;
    }
}
