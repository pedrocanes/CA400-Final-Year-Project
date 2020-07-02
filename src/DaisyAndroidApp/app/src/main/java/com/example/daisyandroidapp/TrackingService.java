package com.example.daisyandroidapp;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrackingService extends Service {


    private static final String TAG = "TrackingService";

    String emailString, passwordString;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference;
    CollectionReference collectionReference;
    String userId;

    String NOTIFICATION_CHANNEL_ID = "com.example.daisyapp";

    FusedLocationProviderClient client;
    LocationCallback locationCallback;

    String phoneId;
    private DaisyApi daisyApi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
            buildNotification();
        } else {
            startForeground(1, new Notification());
            // Create the persistent notification//

            PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                    this, 0, new Intent("stop"), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.tracking_enabled_notif))

                    //Make this notification ongoing so it can’t be dismissed by the user//

                    .setOngoing(true)
                    .setContentIntent(broadcastIntent)
                    .setSmallIcon(R.drawable.tracking_enabled);
            startForeground(1, builder.build());
        }

        registerReceiver(stopReceiver, new IntentFilter("stop"));

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        collectionReference = fStore.collection("users");

        userId = fAuth.getCurrentUser().getUid();

        //Get a reference to the database, so your app can perform read and write operations//

        documentReference = collectionReference.document(userId);

        client = LocationServices.getFusedLocationProviderClient(this);
        requestLocationUpdates();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String channelName = "Daisy location service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        phoneId = intent.getStringExtra("phoneId");

        emailString = intent.getStringExtra("EMAIL");
        passwordString = intent.getStringExtra("PASSWORD");
        return START_STICKY;
    }

    //Create the persistent notification//

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification//

        String channelName = "Daisy location notification";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

                //Make this notification ongoing so it can’t be dismissed by the user//

                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.tracking_enabled);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    //Initiate the request to track the device's location//

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        //Specify how often your app should request the device’s location//

        request.setInterval(30000);

        //Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

            //...then request location updates//

            client.requestLocationUpdates(request, locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        //Save the location data to the database//
                        Log.w("TrackService", "Service is trying to send location");
                        documentReference.collection(path).document(path).set(location)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.w("TrackService", "Service has updated location");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TrackService", "Service has failed to update location: " + e.toString());
                            }
                        });
                        Log.d(TAG, location.toString());

                        String latLong = location.getLatitude() + "," + location.getLongitude();

                        Log.d(TAG, "LOCATION: " + latLong);

                        updatePhone(latLong);

                    } else {
                        Log.w("TrackService", "Location is null");
                    }
                }
            }, null);
        }
    }

    private void updatePhone(String latLong) {
        Call<DBPutResponse> call = daisyApi.updatePhoneCoords(phoneId, latLong);
        call.enqueue(new Callback<DBPutResponse>() {
            @Override
            public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                if (!response.isSuccessful()){
                    //response not successful
                    Log.d("TrackServiceAPIError", "Response not successful");
                    Log.d("TrackServiceAPIError", "CODE: " + response.code() + response.message());
                    return;
                }

                //response successful
                Log.d("TrackServiceSuccess", "Database updated coords successfully");
                Log.d("TrackServiceSuccess", "LatLong: " + latLong);
                Log.d("TrackServiceSuccess", "CODE: " + response.code());
            }

            @Override
            public void onFailure(Call<DBPutResponse> call, Throwable t) {
                Log.d("TrackServiceAPIError", "PUT REQUEST ERROR: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        client.removeLocationUpdates(locationCallback);
        unregisterReceiver(stopReceiver);
        Log.w("TrackService", "Service is shutting down");
        super.onDestroy();
    }
}