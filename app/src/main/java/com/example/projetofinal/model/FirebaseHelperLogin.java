package com.example.projetofinal.model;

import static com.example.projetofinal.viewmodel.ConstantsActivities.INVALID_LOGIN;
import static com.example.projetofinal.viewmodel.ConstantsActivities.LOGIN_AUTH_ERROR;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.projetofinal.R;
import com.example.projetofinal.view.MainActivityAdmin;
import com.example.projetofinal.view.MainActivityUser;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class FirebaseHelperLogin extends AppCompatActivity {

    private final FirebaseFirestore dbfire = FirebaseFirestore.getInstance();
    private AppCompatButton bt_login, bt_logout;
    private EditText email, password;
    private TextView tv_email, tv_name;
    private ProgressBar progressBar;


    protected void login() {
        startComponents();
        bt_login.setOnClickListener(v -> {
            String check_email = email.getText().toString();
            String check_password = password.getText().toString();

            if(check_email.isEmpty() || check_password.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v,LOGIN_AUTH_ERROR,Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            } else {
                userAuth(v);
            }
        });
    }

    protected void userAuth(View v) {
        String check_email = email.getText().toString();
        String check_password = password.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(check_email,check_password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity();
                    }
                },2000);
            } else {
//                String error;
//                try {
//                    throw Objects.requireNonNull(task.getException());
//                }catch (FirebaseAuthWeakPasswordException e){
//                    error = "Senha de no mínimo 6 caracteres";
//                }catch (FirebaseAuthInvalidCredentialsException e) {
//                    error = "Email inválido";
//                }catch (Exception e) {
//                    error = "Login inválido! ";
//                }
                Snackbar snackbar = Snackbar.make(v,INVALID_LOGIN,Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }
        });
    }

    protected void userValidation() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            mainActivity();
        }
    }

    protected void fillUserPerfil() {
        startComponents();
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = dbfire.collection("Users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    tv_name.setText(documentSnapshot.getString("Name"));
                    tv_email.setText(email);
                }

            }
        });
    }

//    protected void logout() {
//        startComponents();
//        bt_logout.setOnClickListener(v ->{
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(getApplicationContext(), Login.class));
//            finish();
//            stopLocationService();
//        });
//    }

    protected void mainActivity() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        if (userId.equals("BdcmoNyWkzLiCNoHGlVLXUJkCce2")) {
            startActivity(new Intent(getApplicationContext(), MainActivityAdmin.class));
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivityUser.class));
        }
        finish();
        Log.i("USERID", "mainActivity: " + userId);
    }

    private void startComponents() {
        bt_login = findViewById(R.id.login_button);
        bt_logout = findViewById(R.id.bt_exit);
        email = findViewById(R.id.edit_email);
        password = findViewById(R.id.edit_password);
        tv_email = findViewById(R.id.text_user_email);
        tv_name = findViewById(R.id.text_user_name);
        progressBar = findViewById(R.id.progress_bar);
    }

}
