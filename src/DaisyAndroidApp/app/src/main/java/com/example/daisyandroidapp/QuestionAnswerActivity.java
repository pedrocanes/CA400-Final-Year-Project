package com.example.daisyandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionAnswerActivity extends AppCompatActivity {

    private String TAG = "QuestionAnswerActivityLogs";

    RadioGroup answerRadioGroup;
    RadioButton checkedRadioButton;
    TextView questionAnswerTitle, questionAskedBy;

    Button buttonSubmitAnswer;

    String documentId, questionId, question, researcherId, username;
    ArrayList<String> answers;
    ArrayList<RadioButton> radioButtonsAnswers = new ArrayList<>();
    List<DBAnswer> dbAnswerList;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userId = fAuth.getCurrentUser().getUid();

    String userDetailsId;

    private CollectionReference questionReference = fStore.collection("users").document(userId).collection("askedQuestions");
    private CollectionReference researcherQuestionReference;

    private DaisyApi daisyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);

        answerRadioGroup = findViewById(R.id.answer_radio_group);
        questionAnswerTitle = findViewById(R.id.question_answer_title);
        questionAskedBy = findViewById(R.id.question_asked_by);
        buttonSubmitAnswer = findViewById(R.id.button_submit_answer);

        Intent mainActivityIntent = getIntent();
        documentId = mainActivityIntent.getStringExtra("documentId");
        questionId = mainActivityIntent.getStringExtra("questionId");
        question = mainActivityIntent.getStringExtra("question");
        researcherId = mainActivityIntent.getStringExtra("researcherId");
        answers = mainActivityIntent.getStringArrayListExtra("answers");
        username = mainActivityIntent.getStringExtra("username");

        researcherQuestionReference = fStore.collection("users").document(researcherId).collection("questionsAsked");

        questionAnswerTitle.setText(question);
        questionAskedBy.setText("Question asked by: " + researcherId);

        int i = 1;
        for (String answer : answers) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(answer);
            radioButton.setTag("answer_radio_" + i);

            ViewGroup.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            radioButton.setLayoutParams(params);

            ViewGroup.MarginLayoutParams parameter =  (CoordinatorLayout.LayoutParams) radioButton.getLayoutParams();
            parameter.setMargins(8, 8, 8, 8);
            radioButton.setLayoutParams(parameter);


            radioButtonsAnswers.add(radioButton);
            answerRadioGroup.addView(radioButton);

            i += 1;
        }

        getAnswersCall(questionId);

        buttonSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = answerRadioGroup.getCheckedRadioButtonId();

                checkedRadioButton = findViewById(radioId);
                String answer = checkedRadioButton.getText().toString();

                if (!dbAnswerList.isEmpty() || dbAnswerList != null) {
                    for (DBAnswer dbAnswer : dbAnswerList) {
                        if (dbAnswer.getAnswer().equals(answer)){

                            String answerId = dbAnswer.getId();


                            //api post call answered question
                            String answerReturnedId = NewQuestionActivity.getRandomString(10);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                            String currentDateandTime = sdf.format(new Date());

                            DBAnswerReturned dbAnswerReturned = new DBAnswerReturned(answerReturnedId, answerId, userId, "phone", currentDateandTime);
                            createAnswerReturned(dbAnswerReturned);


                            //remove question from firestore
                            removeQuestionFromFirebase(questionId);


                            //deleteUserDetails
                            deleteUserDetails(questionId);



                            finish();
                        }
                    }
                }
                else {
                    //get answers call not successful
                    //try again
                    getAnswersCall(questionId);

                    Toast.makeText(QuestionAnswerActivity.this, "Error: Please try again", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    private void deleteUserDetails(String questionId) {
        Call<List<DBUserDetails>> dbUserDetailsCall = daisyApi.getUserDetails(userId);
        dbUserDetailsCall.enqueue(new Callback<List<DBUserDetails>>() {
            @Override
            public void onResponse(Call<List<DBUserDetails>> call, Response<List<DBUserDetails>> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "GET USER DETAILS CALL RESPONSE ERROR: " + response.code());
                    return;
                }

                List<DBUserDetails> dbUserDetailsList = response.body();

                for (DBUserDetails dbUserDetails : dbUserDetailsList) {
                    if (dbUserDetails.getQuestionId().equals(questionId)) {

                        userDetailsId = dbUserDetails.getId();
                    }
                }

                removeUserDetailsWithId();

            }

            @Override
            public void onFailure(Call<List<DBUserDetails>> call, Throwable t) {
                Log.d(TAG, "GET USER DETAILS CALL ERROR: " + t.getMessage());
            }
        });
    }

    private void removeUserDetailsWithId() {
        Call<DBPutResponse> deleteUserDetailsCall = daisyApi.deleteUserDetails(userDetailsId);

        deleteUserDetailsCall.enqueue(new Callback<DBPutResponse>() {
            @Override
            public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "DELETE USER DETAILS RESPONSE ERROR: " + response.code());
                    return;
                }

                Log.d(TAG, "DELETE USER DETAILS RESPONSE SUCCESS: " + response.code());
            }

            @Override
            public void onFailure(Call<DBPutResponse> call, Throwable t) {
                Log.d(TAG, "DELETE USER DETAILS ERROR: " + t.getMessage());
            }
        });
    }

    private void removeQuestionFromFirebase(String questionId) {

        updateUserDetails();

        //collection("users").document(userId).collection("askedQuestions")
        questionReference.document(questionId).delete();
        Log.d(TAG, "DOCUMENT " + questionId + " DELETED FROM USERS QUESTION FIREBASE");


        //collection("users").document(researcherId).collection("questionsAsked")
        researcherQuestionReference.whereEqualTo("id", questionId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "SUCCESSFULLY QUERIED FIREBASE FOR RESEARCHER'S QUESTIONS");
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d(TAG, documentSnapshot.getReference().getId() + " HAS BEEN DELETED");
                            documentSnapshot.getReference().delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "ERROR QUERYING RESEARCHER'S QUESTIONS FIREBASE: " + e.toString());
                    }
                });

    }

    private void updateUserDetails() {
        Call<DBPutResponse> dbPutResponseCall = daisyApi.updateUserDetails(userId, false, "null", "null");
        dbPutResponseCall.enqueue(new Callback<DBPutResponse>() {
            @Override
            public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "USER DETAILS PUT REQUEST RESPONSE ERROR: " + response.code());
                    return;
                }

                //all good
                Log.d(TAG, "USER DETAILS PUT REQUEST RESPONSE SUCCESSFUL: " + response.code());
                Log.d(TAG, "USER DETAILS HAS BEEN UPDATED FOR USER ID: " + userId + ", askQuestion: " + false + ", deviceToUse: " + "null" + " and userAvaialble: " + false);
            }

            @Override
            public void onFailure(Call<DBPutResponse> call, Throwable t) {
                Log.d(TAG, "USER DETAILS PUT REQUEST FAILED: " + t.getMessage());
            }
        });
    }

    private void createAnswerReturned(DBAnswerReturned dbAnswerReturned) {
        Call<DBAnswerReturned> call = daisyApi.createAnswerReturned(dbAnswerReturned);
        call.enqueue(new Callback<DBAnswerReturned>() {
            @Override
            public void onResponse(Call<DBAnswerReturned> call, Response<DBAnswerReturned> response) {
                if (!response.isSuccessful()){
                    Log.d(TAG, "CREATE ANSWER RETURNED RESPONSE ERROR: " + response.code());
                    return;
                }

                //post successful
                Log.d(TAG, "CREATE ANSWER RETURNED SUCCESSFUL: " + response.code());
            }

            @Override
            public void onFailure(Call<DBAnswerReturned> call, Throwable t) {
                Log.d(TAG, "CREATE ANSWER RETURNED CALL ERROR: " + t.getMessage());
            }
        });
    }

    private void getAnswersCall(String questionId) {
        Call<List<DBAnswer>> dbAnswerCall = daisyApi.getAnswersWithQuestionId(questionId);
        dbAnswerCall.enqueue(new Callback<List<DBAnswer>>() {
            @Override
            public void onResponse(Call<List<DBAnswer>> call, Response<List<DBAnswer>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "GET ANSWERS CALL RESPONSE ERROR: " + response.code());
                    return;
                }
                Log.d(TAG, "ANSWERS RETRIEVED CORRECTLY: SIZE " + response.body().size());

                dbAnswerList = response.body();
            }

            @Override
            public void onFailure(Call<List<DBAnswer>> call, Throwable t) {
                Log.d(TAG, "Error getting answers from databse: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
