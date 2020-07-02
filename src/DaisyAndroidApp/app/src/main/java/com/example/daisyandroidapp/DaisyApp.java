package com.example.daisyandroidapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.multidex.MultiDexApplication;

public class DaisyApp extends MultiDexApplication {

    public static final String CHANNEL_1_ID = "newQuestion";
    public static final String CHANNEL_2_ID = "newAnswer";
    public static final String CHANNEL_3_ID = "appListening";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationsChannel();
    }

    private void createNotificationsChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "New Question",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("A new question has been received");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "New Answer",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("A new answer has been received");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Daisy Listening",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }
    }
}
