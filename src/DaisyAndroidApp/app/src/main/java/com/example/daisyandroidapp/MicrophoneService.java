package com.example.daisyandroidapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.daisyandroidapp.DaisyApp.CHANNEL_3_ID;

public class MicrophoneService extends Service {
    protected static SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    Context c;

    private String userResults = "";
    private boolean listening;

    public static final String TAG = "MicrophoneService";

    private DaisyApi daisyApi;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();

    @Override
    public void onCreate() {
        super.onCreate();
        c = getApplicationContext();

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setContentTitle("Daisy App")
                .setContentText("Daisy is listening")
                .setSmallIcon(R.drawable.ic_voice)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(3, notification);


        //if condition is met then do this
        listen();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: stop listening");
                listening = false;
                mSpeechRecognizer.stopListening();
                Log.d(TAG, "FINAL RESULTS: " + userResults);
                getDevice();
            }
        }, 20 * 1000);


        return START_NOT_STICKY;
    }

    private void getDevice() {
        Call<List<DBUserDetails>> dbUserDetailsCall = daisyApi.getUserDetails(userId);
        dbUserDetailsCall.enqueue(new Callback<List<DBUserDetails>>() {
            @Override
            public void onResponse(Call<List<DBUserDetails>> call, Response<List<DBUserDetails>> response) {
                if (!response.isSuccessful()){
                    //not succssfull
                    Log.d(TAG, "GET USER DETAILS CALL RESPONSE ERROR: " + response.code());
                    Log.d(TAG, "Service stopped");
                    stopSelf();
                    return;
                }

                //all good
                String device = response.body().get(0).getDeviceToUse();
                Log.d(TAG, "GET DEVICE: " + device);

                updateDatabase(device);

            }

            @Override
            public void onFailure(Call<List<DBUserDetails>> call, Throwable t) {
                Log.d(TAG, "GET USER DETAILS CALL ERROR: " + t.getMessage());
                Log.d(TAG, "Service stopped");
                stopSelf();
            }
        });
    }

    private void updateDatabase(String device) {
        String userAvail;
        if (userResults.length() > 2){
            userAvail = "False";
        } else {
            userAvail = "True";
        }
        Log.d(TAG, "UPDATE DATABASE: " + userAvail);
        Call<DBPutResponse> dbPutResponseCall = daisyApi.updateUserDetails(userId, true, device, userAvail);
        dbPutResponseCall.enqueue(new Callback<DBPutResponse>() {
            @Override
            public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "USER DETAILS PUT REQUEST RESPONSE ERROR: " + response.code());
                    Log.d(TAG, "Service stopped");
                    stopSelf();
                    return;
                }

                //all good
                Log.d(TAG, "USER DETAILS PUT REQUEST RESPONSE SUCCESSFUL: " + response.code());
                Log.d(TAG, "USER DETAILS HAS BEEN UPDATED FOR USER ID: " + userId + ", askQuestion: " + true + ", deviceToUse: " + device + " and userAvaialble: " + userAvail);

                Log.d(TAG, "Service stopped");
                stopSelf();
            }

            @Override
            public void onFailure(Call<DBPutResponse> call, Throwable t) {
                Log.d(TAG, "USER DETAILS PUT REQUEST FAILED: " + t.getMessage());
                Log.d(TAG, "Service stopped");
                stopSelf();
            }
        });
    }

    private void listen() {
        SpeechRecognitionListener h = new SpeechRecognitionListener();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(h);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        Log.d(TAG, " " + mSpeechRecognizer.isRecognitionAvailable(this));
        if (mSpeechRecognizer.isRecognitionAvailable(this))
            Log.d(TAG, "onBeginingOfSpeech");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        Log.d(TAG, "Listening now");
        listening = true;
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onDestroy() {
        mSpeechRecognizer.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {

            Log.d(TAG, "ready service");
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {
            Log.d(TAG,"ERROR: " + i);
            if ((i == SpeechRecognizer.ERROR_NO_MATCH)
                    || (i == SpeechRecognizer.ERROR_SPEECH_TIMEOUT))
            {
                Log.d(TAG, "didn't recognize anything");
                userResults += "";
                // keep going
                if (listening) {
                    Log.d(TAG, "Listening again");
                    mSpeechRecognizer.destroy();
                    listen();
                }
            }
        }

        @Override
        public void onResults(Bundle resultsBundle) {
            Log.d(TAG, "onResults: " + resultsBundle.toString());
            ArrayList<String> matches = resultsBundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches.get(0) != "null")
                userResults += " " + matches.get(0);
            else
                userResults += "";
            if (matches != null)
                Log.d(TAG, "matches: " + matches.get(0));
            Log.d(TAG, "I have listen to: " + userResults);
            if (listening) {
                Log.d(TAG, "Listening again");
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    }
}
