package com.example.daisyandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

public class AskQuestionActivity extends AppCompatActivity {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userId = fAuth.getCurrentUser().getUid();
    private CollectionReference questionReference = db.collection("users").document(userId).collection("questionsAsked");
    String askedUserId;

    private QuestionAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        Intent intent = getIntent();
        askedUserId = intent.getStringExtra("userId");

        FloatingActionButton backButton = findViewById(R.id.ask_question_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton buttonAddQuestion = findViewById(R.id.button_add_question);
        buttonAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questionIntent = new Intent(getApplicationContext(), NewQuestionActivity.class);
                questionIntent.putExtra("askedUserId", askedUserId);
                startActivity(questionIntent);
            }
        });

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        Query query = questionReference.whereEqualTo("userId", askedUserId);

        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query, Question.class)
                .build();

        adapter = new QuestionAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.question_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
