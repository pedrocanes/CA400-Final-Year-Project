package com.example.daisyandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewQuestionActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private Button addAnswerButton, addQuestion, backButton;
    private LinearLayout answerLayout;
    private Integer answerCounter = 2;
    private EditText editTextQuestionTitle;

    private ArrayList<EditText> answerEditTexts = new ArrayList<>();

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userId = fAuth.getCurrentUser().getUid();
    private CollectionReference questionReference = db.collection("users").document(userId).collection("questionsAsked");
    private CollectionReference answerReference = db.collection("users").document(userId).collection("answersInfo");

    private DaisyApi daisyApi;

    private ProgressBar newQuestionLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);

        newQuestionLoading = findViewById(R.id.add_question_loading);

        backButton = findViewById(R.id.new_question_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addQuestion = findViewById(R.id.add_question);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuestionLoading.setVisibility(View.VISIBLE);
                saveQuestion();
            }
        });

        EditText answer1 = findViewById(R.id.edit_text_question_answer1);
        EditText answer2 = findViewById(R.id.edit_text_question_answer2);

        answerEditTexts.add(answer1);
        answerEditTexts.add(answer2);

        editTextQuestionTitle = findViewById(R.id.edit_text_question_title);

        answerLayout = findViewById(R.id.answer_layout);
        addAnswerButton = findViewById(R.id.add_answer_button);
        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerCounter += 1;
                final EditText additionalAnswer = new EditText(getApplicationContext());

                additionalAnswer.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                additionalAnswer.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                additionalAnswer.setInputType(InputType.TYPE_CLASS_TEXT);
                additionalAnswer.setHint("Answer " + answerCounter);
                additionalAnswer.setTag("edit_text_question_answer" + answerCounter);

                answerEditTexts.add(additionalAnswer);

                answerLayout.addView(additionalAnswer);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.question_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_question:
                saveQuestion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveQuestion() {
        String title = editTextQuestionTitle.getText().toString();
        String questionId = getRandomString(10);

        Intent intent = getIntent();
        String askedUserId = intent.getStringExtra("askedUserId");

        ArrayList<String> answers = new ArrayList<>();

        for (EditText answer : answerEditTexts){
            answers.add(answer.getText().toString());
        }


        if (title.trim().isEmpty() || answers.size() < 2){
            Toast.makeText(this, "Please insert a question and at least 2 possible answers", Toast.LENGTH_SHORT).show();
            return;
        }

        Question quest = new Question(title, questionId, userId, askedUserId, answers);

        questionReference.add(quest);

        saveQuestionInAPI(questionId, title, userId, answers, quest);

    }

    private void saveQuestionInAPI(String qId, String questionTitle, String researcherId, ArrayList<String> answerList, Question qt) {
        DBQuestion dbQuestion = new DBQuestion(qId, questionTitle, researcherId);

        //do database thing
        Call<DBPutResponse> call = daisyApi.createQuestion(dbQuestion);
        call.enqueue(new Callback<DBPutResponse>() {
            @Override
            public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(NewQuestionActivity.this, "Error creating question", Toast.LENGTH_SHORT).show();
                    Log.d("NewQuestionError", "QUESTION RESPONSE ERROR: " + response.code());
                    return;
                }

                //all good
                Log.d("NewQuestionSuccess", "QUESTION CREATED SUCCESSFULLY: " + response.code());

                saveUserDetails(qt);
                saveAnswersInAPI(answerList, qId, qt);

            }

            @Override
            public void onFailure(Call<DBPutResponse> call, Throwable t) {
                Toast.makeText(NewQuestionActivity.this, "Error creating question: " + t.getMessage() + "Please try again", Toast.LENGTH_SHORT).show();
                Log.d("NewQuestion", "ERROR CREATING QUESTION: " + t.getMessage());

            }
        });
    }

    private void saveUserDetails(Question qt) {
        String userDetailsId = getRandomString(10);
        DBUserDetails userDetails = new DBUserDetails(userDetailsId, qt.getUserId(), qt.getId(), true, "null", "null");

        Call<DBPutResponse> call = daisyApi.createUserDetails(userDetails);
        call.enqueue(new Callback<DBPutResponse>() {
            @Override
            public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                if (!response.isSuccessful()){
                    Log.d("NewQuestionError", "USER DETAILS RESPONSE ERROR: " + response.code());
                    return;
                }

                //all good
                Log.d("NewQuestionSuccess", "USER DETAILS CREATED SUCCESSFULLY: " + response.code());
            }

            @Override
            public void onFailure(Call<DBPutResponse> call, Throwable t) {
                Log.d("NewQuestion", "ERROR CREATING USER DETAILS: " + t.getMessage());
            }
        });
    }

    private void saveAnswersInAPI(ArrayList<String> answerList, String qId, Question qt) {

        final Boolean[] errorToasted = {false};

        for (String answer : answerList){

            String answerId = getRandomString(28);
            DBAnswer dbAnswer = new DBAnswer(answerId, answer, qId);

            //add Answer info to Firebase
            addAnswerToFirebase(answerId,answer, qt);

            //do database thing
            Call<DBPutResponse> call = daisyApi.createAnswer(dbAnswer);
            call.enqueue(new Callback<DBPutResponse>() {
                @Override
                public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                    if (!response.isSuccessful()){
                        Log.d("NewQuestionError", "ANSWER RESPONSE ERROR: " + response.code());
                        if (errorToasted[0].equals(false)){
                            Toast.makeText(NewQuestionActivity.this, "Error creating Answers", Toast.LENGTH_SHORT).show();
                            errorToasted[0] = true;
                        }
                        return;
                    }

                    //all good
                    Log.d("NewQuestionSuccess", "ANSWER CREATED SUCCESSFULLY: " + response.code());
                }

                @Override
                public void onFailure(Call<DBPutResponse> call, Throwable t) {
                    Log.d("NewQuestion", "ERROR CREATING ANSWER: " + t.getMessage());
                }
            });
        }

        Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void addAnswerToFirebase(String answerId, String answer, Question qt) {
        HashMap<String, Object> answerObject = new HashMap<>();
        answerObject.put("answerId", answerId);
        answerObject.put("answer", answer);
        answerObject.put("question", qt.getTitle());
        answerObject.put("questionId", qt.getId());
        answerObject.put("userId", qt.getUserId());
        answerObject.put("researcherId", qt.getResearcherId());

        answerReference.add(answerObject)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("NewQuestion", "ANSWER OBJECT SUCCESSFULLY ADDED TO FIREBASE");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("NewQuestion", "ANSWER OBJECT FAILED TO BE ADDED TO FIREBASE: " + e.toString());
                    }
                });
    }

    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);

        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}
