package com.example.projetofinal.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";
    public static final Integer DBversion = 1;
    public DBHelper(Context context) {
        super(context, "Login.db", null, DBversion);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table localizacao(idlocalizacao INTEGER PRIMARY KEY AUTOINCREMENT, ID TEXT, Latitude TEXT, Longitude TEXT, Data TEXT, Distancia TEXT,Velocidade TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int currentVersion) {
        String update1 = "drop Table if exists localizacao";
        if (oldVersion == 1) {
            MyDB.execSQL(update1);
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void inserelocalizacao(String usuarioId, String latitude, String longitude, String distancia, String velocidade){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("ID", usuarioId);
        initialValues.put("Latitude", latitude);
        initialValues.put("Longitude", longitude);
        initialValues.put("Data", getDateTime());
        initialValues.put("Distancia", distancia);
        initialValues.put("Velocidade", velocidade);
        MyDB.insert("localizacao", null, initialValues);

        insereFireBase(usuarioId,latitude,longitude,distancia,velocidade);
    }

    public Cursor selectlocalizacao()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor  = DB.rawQuery("Select * from localizacao", null);
        return cursor;
    }

    public void insereFireBase(String usuario, String latitude, String longitude, String distancia,String velocidade) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> location = new HashMap<>();
        location.put("ID", usuario);
        location.put("Latitude", latitude);
        location.put("Longitude", longitude);
        location.put("Data", getDateTime());
        location.put("Distancia", distancia);
        location.put("Velocidade", velocidade);

        
        db.collection("Locations")
                .add(location)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("onSucces", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("onFailure", "Error adding document", e);
                    }
                });
    }
}
