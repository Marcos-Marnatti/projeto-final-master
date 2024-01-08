package com.example.projetofinal.view;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetofinal.R;
import com.example.projetofinal.adapter.Adapter;
import com.example.projetofinal.viewmodel.DBHelper;
import com.example.projetofinal.viewmodel.FirebaseHelperLogin;

import java.util.ArrayList;

public class MainActivityAdmin extends FirebaseHelperLogin {

    RecyclerView recyclerView;
    ArrayList<String> usuario, latitude, longitude, data, velocidade, distancia;
    DBHelper DB;
    Adapter adapter;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        logoutAdmin();
        displayData();
    }

    private void StartComponents() {

        DB = new DBHelper(this);
        usuario = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        data = new ArrayList<>();
        distancia = new ArrayList<>();
        velocidade = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new Adapter(this, usuario, latitude, longitude, data, distancia, velocidade);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tv = findViewById(R.id.distanciaP);
    }

    private void displayData() {
        StartComponents();
        Cursor cursor = DB.selectlocalizacao();
        Cursor cursor2 = DB.selectDistancia();

        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivityAdmin.this, "Ainda não possui marcações de deslocamento", Toast.LENGTH_SHORT).show();
        } else {
            cursor2.moveToFirst();

            while (cursor.moveToNext()) {
                usuario.add(cursor.getString(1));
                latitude.add(cursor.getString(2));
                longitude.add(cursor.getString(3));
                data.add(cursor.getString(4));
                distancia.add(cursor.getString(5));
                velocidade.add(cursor.getString(6));
            }
            tv.setText("Distancia Percorrida: " + cursor2.getString(0) + " Metros");
        }
    }
}