package com.example.projetofinal.viewmodel;

import static com.example.projetofinal.viewmodel.ConstantsActivities.BLANK_REGISTER;
import static com.example.projetofinal.viewmodel.ConstantsActivities.SUCCES_REGISTER;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.projetofinal.R;
import com.example.projetofinal.view.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseHelperRegister extends AppCompatActivity {

    private EditText register_email, register_name, register_password;
    private AppCompatButton register_button;

    protected void registerUser() {
        startComponents();
        register_button.setOnClickListener(v -> {
            String check_name = register_name.getText().toString();
            String check_email = register_email.getText().toString();
            String check_password = register_password.getText().toString();

            if (check_name.isEmpty() || check_email.isEmpty() || check_password.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v,BLANK_REGISTER,Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(check_email,check_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerSuccessful(v);
                        } else {
                            registerError(task, v);
                        }
                    }
                });
            }
        });
    }

    private void saveUserData() {
        startComponents();

        String check_name = register_name.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> users = new HashMap<>();
        users.put("Name",check_name);
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DocumentReference document_reference = db.collection("Users").document(userId);
        document_reference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db","Sucesso ao salvar os dados");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error","Erro ao salvar os dados" + e);
                    }
                });
    }

    private void registerSuccessful(View v) {
        saveUserData();

        Snackbar snackbar = Snackbar.make(v,SUCCES_REGISTER,Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        },1000);
    }

    private void registerError(@NonNull Task<AuthResult> task, View v) {
        String error;
        try {
            throw Objects.requireNonNull(task.getException());

        }catch (FirebaseAuthWeakPasswordException e){
            error = "Digite uma senha com no mínimo 6 caracteres";
        }catch (FirebaseAuthUserCollisionException e) {
            error = "Conta já existente";
        }catch (FirebaseAuthInvalidCredentialsException e) {
            error = "Email inválido";
        }catch (Exception e) {
            error = "Erro ao cadastrar usuário";
        }
        Snackbar snackbar = Snackbar.make(v,error,Snackbar.LENGTH_INDEFINITE);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void startComponents() {
        register_name = findViewById(R.id.register_name);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_button = findViewById(R.id.bt_register);
    }
}
