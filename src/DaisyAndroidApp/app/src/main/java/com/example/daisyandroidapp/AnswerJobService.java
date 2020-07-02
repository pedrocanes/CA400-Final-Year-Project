package com.example.daisyandroidapp;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.daisyandroidapp.DaisyApp.CHANNEL_2_ID;

public class AnswerJobService extends JobService {
    private static final String TAG = "AnswerJobService";
    private boolean jobCancelled = false;

    private DaisyApi daisyApi;

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String userId = fAuth.getCurrentUser().getUid();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference researcherQuestionAnsweredReference = fStore.collection("users").document(userId).collection("answeredQuestions");

    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);

        notificationManager = NotificationManagerCompat.from(this);

        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (jobCancelled) {
                    return;
                }

                //do api stuff
                //step 1: get request from Answer-returned table - make into list
                //step 2: get answers from collection("users").document(userId).collection("answersInfo") - make into list
                //step 3: compare list and look for answers in answer returned list that are in answers info list
                //step 4: add them to collection("users").document(userId).collection("answererQuestions")

                getCallAnswerReturned();



                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private void getCallAnswerReturned() {
        Call<DBAnswerReturnedList> dbAnswerReturnedListCall = daisyApi.getAnswerReturned();
        dbAnswerReturnedListCall.enqueue(new Callback<DBAnswerReturnedList>() {
            @Override
            public void onResponse(Call<DBAnswerReturnedList> call, Response<DBAnswerReturnedList> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "GET CALL ANSWER RETURNED RESPONSE ERROR: " + response.code());
                    return;
                }

                //all good
                DBAnswerReturnedList dbAnswerReturnedList = response.body();
                List<DBAnswerReturned> dbAnswerReturneds = dbAnswerReturnedList.getDbAnswerReturnedList();

                //step 1 finished
                //step 2 get answers from collection("users").document(userId).collection("answersInfo")
                getAnswersInfoFromFirebase(dbAnswerReturneds);
            }

            @Override
            public void onFailure(Call<DBAnswerReturnedList> call, Throwable t) {
                Log.d(TAG, "GET CALL ANSWER RETURNED ERROR: " + t.getMessage());
            }
        });
    }

    private void getAnswersInfoFromFirebase(List<DBAnswerReturned> dbAnswerReturneds) {
        CollectionReference answersInfoReference = fStore.collection("users").document(userId).collection("answersInfo");

        ArrayList<HashMap<String, Object>> answerObjects = new ArrayList<>();

        answersInfoReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i = 1;

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            String answerId = documentSnapshot.getString("answerId");


                            for (DBAnswerReturned dbAnswerReturned : dbAnswerReturneds){
                                if (dbAnswerReturned.getAnswerId().equals(answerId)) {

                                    createNotification(i);
                                    i += 1;

                                    HashMap<String, Object> answerObject = new HashMap<>();
                                    answerObject.put("question", documentSnapshot.getString("question"));
                                    answerObject.put("answer", documentSnapshot.getString("answer"));

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                    String currentDateandTime = sdf.format(new Date());

                                    answerObject.put("timeAnswered", currentDateandTime);
                                    answerObject.put("userQuestioned", documentSnapshot.getString("userId"));

                                    String qId = documentSnapshot.getString("questionId");

                                    DocumentReference usersRef = fStore.collection("users").document(documentSnapshot.getString("userId"));
                                    usersRef.get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    String uName = documentSnapshot.getString("name");
                                                    answerObject.put("username" , uName);

                                                    researcherQuestionAnsweredReference.document(qId).set(answerObject);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "ERROR GETTING USERNAME FOR QUESTION " + qId + ": " + e.toString());
                                                }
                                            });

                                }
                            }

                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void createNotification(int i) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_answer)
                .setContentTitle("New answer received")
                .setContentText("A new answer has been sent to you. Open the app to view it")
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(i, notification);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
