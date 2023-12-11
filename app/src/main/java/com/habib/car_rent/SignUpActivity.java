package com.habib.car_rent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef; // Reference to Firebase Storage
    private EditText emailInput, passwordInput, userNameInput, cinInput, phoneInput;
    private ImageView imageUpload;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference(); // Initialize Firebase Storage reference

        userNameInput = findViewById(R.id.userName);
        cinInput = findViewById(R.id.cinNum);
        emailInput = findViewById(R.id.emailOrPhoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneInput = findViewById(R.id.phoneNum);
        imageUpload = findViewById(R.id.imageUpload);

        Button signUpButton = findViewById(R.id.signUpButton);
        Button signInButton = findViewById(R.id.signIpButton);
        Button uploadButton = findViewById(R.id.buttonUpload);

        signUpButton.setOnClickListener(v -> createAccount(emailInput.getText().toString(), passwordInput.getText().toString()));
        uploadButton.setOnClickListener(v -> openFileChooser());
        signInButton.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageUpload.setImageURI(imageUri);
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        // ... Existing account creation logic ...

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (imageUri != null) {
                            uploadImageToFirebase(user);
                        } else {
                            updateUI(user, null);
                        }
                    } else {


                    }
                });
    }


    private void uploadImageToFirebase(FirebaseUser user) {
        // Points to "uploads"
        StorageReference fileReference = mStorageRef.child("uploads/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                updateUI(user, uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper method to get the file extension of the uploaded file
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void updateUI(FirebaseUser currentUser, @Nullable String imageUrl) {
        if (currentUser != null) {
            // Create a new user with the user's information
            Map<String, Object> userValues = new HashMap<>();
            userValues.put("userName", userNameInput.getText().toString());
            userValues.put("cin", cinInput.getText().toString());
            userValues.put("email", currentUser.getEmail());



            // Include the image URL (if available) and phone number
            String phone = phoneInput.getText().toString();
            userValues.put("phone", phone);
            if (imageUrl != null) {
                userValues.put("profileImage", imageUrl);
            }

            // ... Write the userValues to the database
            mDatabase.child("users").child(currentUser.getUid()).setValue(userValues)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                            finish(); // Go back to the previous Activity
                        }
                    });
        }
    }



    // validateForm function
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

        String userName = userNameInput.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            userNameInput.setError("Required.");
            valid = false;
        } else {
            userNameInput.setError(null);
        }

        String cin = cinInput.getText().toString();
        if (TextUtils.isEmpty(cin)) {
            cinInput.setError("Required.");
            valid = false;
        } else {
            cinInput.setError(null);
        }

        String phone = phoneInput.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("Required.");
            valid = false;
        } else {
            phoneInput.setError(null);
        }

        return valid;
    }

    // handleError function
    private void handleError(FirebaseAuthException e) {
        switch (e.getErrorCode()) {
            case "ERROR_INVALID_EMAIL":
                emailInput.setError("The email address is badly formatted.");
                emailInput.requestFocus();
                break;
            case "ERROR_WRONG_PASSWORD":
                passwordInput.setError("The password is invalid or the user does not have a password.");
                passwordInput.requestFocus();
                passwordInput.setText("");
                break;
            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                emailInput.setError("An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.");
                emailInput.requestFocus();
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                emailInput.setError("The email address is already in use by another account.");
                emailInput.requestFocus();
                break;
            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                emailInput.setError("This credential is already associated with a different user account.");
                emailInput.requestFocus();
                break;
            case "ERROR_USER_DISABLED":
                emailInput.setError("The user account has been disabled by an administrator.");
                emailInput.requestFocus();
                break;
            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(this, "The user's credential is no longer valid. The user must sign in again.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_USER_NOT_FOUND":
                emailInput.setError("There is no user record corresponding to this identifier. The user may have been deleted.");
                emailInput.requestFocus();
                break;
            case "ERROR_WEAK_PASSWORD":
                passwordInput.setError("The given password is invalid.");
                passwordInput.requestFocus();
                break;
            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(this, "The custom token corresponds to a different audience.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_SHORT).show();
                break;

        }

    }
}



