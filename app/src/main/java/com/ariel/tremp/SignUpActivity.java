package com.ariel.tremp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.Toast;

import com.ariel.tremp.model.Model;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SIGN UP TAG";
    TextInputEditText firstNameEt;
    TextInputEditText lastNameEt;
    TextInputEditText emailEt;
    TextInputEditText passwordEt;
    TextInputEditText phoneEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstNameEt = findViewById(R.id.signup_firstname_textinput_et);
        lastNameEt = findViewById(R.id.signup_lastname_textinput_et);
        emailEt = findViewById(R.id.signup_email_textfield);
        passwordEt = findViewById(R.id.signup_password_textfield);
        phoneEt = findViewById(R.id.et_phone);
        Button createAccountBtn = findViewById(R.id.view_profile_save_btn);

        createAccountBtn.setOnClickListener(v -> {
            signUp(emailEt.getText().toString(), passwordEt.getText().toString());
        });
    }

    private void signUp(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = firebaseUser.getUid();
                User user = new User(uid, email, firstNameEt.getText().toString(), lastNameEt.getText().toString(), password, phoneEt.getText().toString());

                db.collection("users")
                        .document(uid)
                        .set(user.toJson())
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show();

                            Intent openLogin = new Intent(this, LoginActivity.class);
                            startActivity(openLogin);
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
            } else {
                Toast.makeText(this, "ERROR " + task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    Model.modelFirebase.signUp(email, password, firstName, lastName, new OnComplete() {
//        @Override
//        public void handleAfter() {
//            Toast.makeText(getApplicationContext(), "User created!", Toast.LENGTH_SHORT).show();
//            Intent openLogin = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(openLogin);
//        }
//
//        @Override
//        public void handleError() {
//            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
//        }
//    });
}