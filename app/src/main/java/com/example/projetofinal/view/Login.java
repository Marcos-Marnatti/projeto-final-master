package com.example.projetofinal.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.projetofinal.R;
import com.example.projetofinal.viewmodel.FirebaseHelperLogin;

public class Login extends FirebaseHelperLogin {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerUserActivity();
        login();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userValidation();
    }

    private void registerUserActivity() {
        TextView register_here = findViewById(R.id.register);
        register_here.setOnClickListener(v -> startActivity(new Intent(this, Register.class)));
    }
}

