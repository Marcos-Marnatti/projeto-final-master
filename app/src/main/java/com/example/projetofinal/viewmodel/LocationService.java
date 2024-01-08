package com.example.projetofinal.viewmodel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.projetofinal.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;

public class LocationService extends Service {

    DBHelper db = new DBHelper(this);
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Location lastLoc = null;
    Float velocidade = 0F;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            Location loc = location;
            double latitude = locationResult.getLastLocation().getLatitude();
            double longitude = locationResult.getLastLocation().getLongitude();
            velocidade = locationResult.getLastLocation().getSpeed();
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            float distancia = 0;

            if (locationResult.getLastLocation() != null) {
                if (lastLoc != null) {
                    distancia = loc.distanceTo(lastLoc);

                    if ((distancia >= 10)){
                        db.inserelocalizacao(userID, Double.toString(latitude), Double.toString(longitude), distancia, velocidade);
                        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                            Toast.makeText(LocationService.this, "Entrei no FIRESTORE", Toast.LENGTH_SHORT).show();
                            db.insereFireBase(userID, Double.toString(latitude), Double.toString(longitude), distancia, velocidade);
                        }
                    }
                    else if (velocidade < 1){
                    Log.d("LOCATION_UPDATE", "Imovel");
                    }
//                    else if ((velocidade >= 8.1 ) && (distancia >= 100) ){
//                        Log.d("LOCATION_UPDATE", "Distancia maior que 100: " + distancia);
//                        db.inserelocalizacao(userID, Double.toString(latitude), Double.toString(longitude), Float.toString(distancia), Float.toString(velocidade));
//                    }
                }
            }
            lastLoc = loc;
            Log.d("LOCATION_UPDATE",
                    "Latitude: " + latitude + " Longitude: " + longitude + " Distancia: " + distancia);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @SuppressLint({"MissingPermission", "UnspecifiedImmutableFlag"})
    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    resultIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        setLocationType(velocidade, builder);
    }

    private void setLocationType(Float v, NotificationCompat.Builder builder) {
        LocationRequest locationRequest = LocationRequest.create();

        if (v >= 35) {
            locationRequest
                    .setInterval(30000)
                    .setFastestInterval(20000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            Log.d("Carro", "Entrou Carro: " + v);
        } else {
            locationRequest
                    .setInterval(3000)
                    .setFastestInterval(2000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            Log.d("Pessoa", "Entrou pessoa: " + v);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(ConstantsActivities.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ConstantsActivities.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(ConstantsActivities.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
