package com.example.daisyandroidapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.daisyandroidapp.DaisyApp.CHANNEL_1_ID;

public class DatabaseJobService extends JobService {

    private static final String TAG = "DatabaseJobServiceLogs";
    private static final String STEP1 = "---STEP 1 = ";
    private static final String STEP2 = "------STEP 2 = ";
    private static final String STEP3 = "---------STEP 3 = ";
    private static final String STEP4 = "-----------STEP 4 = ";
    private boolean jobCancelled = false;

    private DaisyApi daisyApi;

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String userId = fAuth.getCurrentUser().getUid();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = fStore.collection("users");
    private DocumentReference documentReference = collectionReference.document(userId);

    private Call<List<DBUserDetails>> call;

    ArrayList<String> idsList = new ArrayList<>();

    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);

        call = daisyApi.getUserDetails(userId);

        notificationManager = NotificationManagerCompat.from(this);

        Log.d(TAG, "JOB STARTED");
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

                //database check
                call.enqueue(new Callback<List<DBUserDetails>>() {
                    @Override
                    public void onResponse(Call<List<DBUserDetails>> call, Response<List<DBUserDetails>> response) {
                        if (!response.isSuccessful()){
                            Log.d(TAG, STEP1 + "GET USER DETAILS RESPONSE ERROR: " + response.code());

                            if (response.code() == 404)
                                Log.d(TAG, STEP1 + "NO QUESTIONS FOR THIS USER");

                            return;
                        }

                        //all good
                        Log.d(TAG, STEP1 + "GET USER DETAILS RESPONSE SUCCESSFUL: " + response.code());
                        List<DBUserDetails> userDetails = response.body();

                        if (userDetails.isEmpty())
                            Log.d(TAG, STEP1 + "NO QUESTIONS FOR THIS USER");

                        int i = 1;

                        for (DBUserDetails userDetail : userDetails) {
                            Log.d(TAG, "ID: " + userDetail.getId());
                            Log.d(TAG, "USER-ID: " + userDetail.getUserId());
                            Log.d(TAG, "QUESTION-ID: " + userDetail.getQuestionId());
                            Log.d(TAG, "DEVICE TO USE: " + userDetail.getDeviceToUse());
                            Log.d(TAG, "ASK QUESTION: " + userDetail.getAskQuestion());
                            Log.d(TAG, "USER AVAILABILITY: " + userDetail.getUserAvailable());

                            //get Question from Database
                            if (userDetail.getDeviceToUse().equals("phone")){
                                getCallQuestion(userDetail);

                                //notification
                                if (!idsList.contains(userDetail.getId())) {

                                    //id hasnt sent notification yet

                                    //send notification
                                    sendNotification(i);


                                    idsList.add(userDetail.getId());
                                }else {
                                    //do nothing
                                }


                            }

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            i += 1;

                        }


                    }

                    @Override
                    public void onFailure(Call<List<DBUserDetails>> call, Throwable t) {
                        Log.d(TAG, STEP1 + "GET USER DETAILS CALL ERROR: " + t.getMessage());
                    }
                });


                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private void sendNotification(int i) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_question)
                .setContentTitle("New question received")
                .setContentText("A new question has been sent to you. Open the app to send your answer")
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(i, notification);
    }

    private void getCallQuestion(DBUserDetails userDetail) {

        Call<DBQuestion> dbQuestionCall = daisyApi.getQuestionWithId(userDetail.getQuestionId());
        dbQuestionCall.enqueue(new Callback<DBQuestion>() {
            @Override
            public void onResponse(Call<DBQuestion> call, Response<DBQuestion> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, STEP2 + "GET QUESTION CALL RESPONSE ERROR: " + response.code());
                    return;
                }

                Log.d(TAG, STEP2 + "GET QUESTION CALL RESPONSE SUCCESSFUL: " + response.code());

                DBQuestion dbQuestion = response.body();

                Log.d(TAG, STEP2 + "DB QUESTION ID: " + dbQuestion.getId());
                Log.d(TAG, STEP2 + "DB QUESTION: " + dbQuestion.getQuestion());
                Log.d(TAG, STEP2 + "DB QUESTION RES ID: " + dbQuestion.getResearcherId());

                //response successful
                //Question retrieved
                //retrieve answers to question
                getCallAnswers(userDetail, dbQuestion);

            }

            @Override
            public void onFailure(Call<DBQuestion> call, Throwable t) {
                Log.d(TAG, STEP2 + "GET QUESTION CALL ERROR: " + t.getMessage());
            }
        });
    }

    private void getCallAnswers(DBUserDetails userDetail, DBQuestion dbQuestion) {

        Call<List<DBAnswer>> dbAnswerCall = daisyApi.getAnswersWithQuestionId(userDetail.getQuestionId());
        dbAnswerCall.enqueue(new Callback<List<DBAnswer>>() {
            @Override
            public void onResponse(Call<List<DBAnswer>> call, Response<List<DBAnswer>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, STEP3 + "GET ANSWER CALL RESPONSE ERROR: " + response.code());
                    return;
                }

                Log.d(TAG, STEP3 + "GET ANSWER CALL RESPONSE SUCCESSFUL: " + response.code());

                //question retrieved
                //answers retrieved
                //send info to firebase

                List<DBAnswer> dbAnswerList = response.body();

                Log.d(TAG, STEP3 + "DB ANSWER LIST SIZE: " + dbAnswerList.size());


                sendQuestionToFirebase(userDetail, dbQuestion, dbAnswerList);

            }

            @Override
            public void onFailure(Call<List<DBAnswer>> call, Throwable t) {
                Log.d(TAG, STEP3 + "GET QUESTION CALL ERROR: " + t.getMessage());
            }
        });
    }

    private void sendQuestionToFirebase(DBUserDetails userDetail, DBQuestion dbQuestion, List<DBAnswer> dbAnswerList) {

        String questionTitle = dbQuestion.getQuestion();
        String questionId = userDetail.getQuestionId();
        String researcherId = dbQuestion.getResearcherId();

        ArrayList<String> answers = new ArrayList<>();
        for (DBAnswer answer : dbAnswerList) {
            answers.add(answer.getAnswer());
        }

        Question question = new Question(questionTitle, questionId, researcherId, userId, answers);

        Log.d(TAG, STEP4 + "TO BE SENT QUESTION ID: " + question.getId());
        Log.d(TAG, STEP4 + "TO BE SENT QUESTION: " + question.getTitle());
        Log.d(TAG, STEP4 + "TO BE SENT QUESTION RES ID: " + question.getResearcherId());
        Log.d(TAG, STEP4 + "TO BE SENT QUESTION USER ID: " + question.getUserId());
        Log.d(TAG, STEP4 + "TO BE SENT ANSWER SIZE: " + question.getAnswers().size());

        documentReference.collection("askedQuestions").document(questionId)
                .set(question)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, STEP4 + "DATABASE UPDATED SUCCESSFULLY");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, STEP4 + "DATABASE UPDATING ERROR: " + e.toString());

                    }
                });
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");

        call.cancel();

        jobCancelled = true;
        return true;
    }
}
