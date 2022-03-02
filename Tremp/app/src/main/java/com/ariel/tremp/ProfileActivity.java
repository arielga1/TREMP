package com.ariel.tremp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import javax.microedition.khronos.opengles.GL;

public class ProfileActivity extends AppCompatActivity {

    private Global global;
    private TextView tvName, tvEmail, tvPhone, tvPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        global = (Global) getApplicationContext();

        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvPass = findViewById(R.id.tv_pass);

        tvName.setText(global.user.getUserFirstName() + " " + global.user.getUserLastName());
        tvEmail.setText(global.user.getUserEmail());
        tvPhone.setText(global.user.getUserPhone());
        tvPass.setText(global.user.getUserPassword());
    }
}
