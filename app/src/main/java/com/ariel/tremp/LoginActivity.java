package com.ariel.tremp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ariel.tremp.model.AppLocalDB;
import com.ariel.tremp.model.Model;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOG IN TAG";
    TextInputEditText emailEt;
    TextInputEditText passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.login_frg_email_textinput_et);
        passwordEt = findViewById(R.id.login_frg_password_textinput_et);
    }

    public void login(View view) {
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String uid = firebaseUser.getUid();
                Log.i(TAG, "login: UID=" + uid);

                Model.getUserByUID(uid, new GetUser() {
                    @Override
                    public void handleAfter(User user) {
                        Global global = (Global) getApplicationContext();
                        global.user = user;

                        Intent openHome = new Intent(getApplicationContext(), HomeActivity2.class);
                        startActivity(openHome);
                    }

                    @Override
                    public void handleError() {

                    }
                });
            }
            else Log.i(TAG, "login: error");
        });
    }
}
