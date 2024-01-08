package com.example.projetofinal.view;

import android.os.Bundle;

import com.example.projetofinal.R;
import com.example.projetofinal.viewmodel.FirebaseHelperRegister;

public class Register extends FirebaseHelperRegister {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerUser();
    }
}

