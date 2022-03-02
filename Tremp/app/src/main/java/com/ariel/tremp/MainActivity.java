package com.ariel.tremp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        Intent openLogin = new Intent(this, LoginActivity.class);
        startActivity(openLogin);
    }

    public void signUp(View view) {
        Intent openSignUp = new Intent(this, SignUpActivity.class);
        startActivity(openSignUp);
    }
}