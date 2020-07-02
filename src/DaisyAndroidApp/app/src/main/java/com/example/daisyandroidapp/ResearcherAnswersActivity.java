package com.example.daisyandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ResearcherAnswersActivity extends AppCompatActivity {
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String userId = fAuth.getCurrentUser().getUid();
    private CollectionReference collectionReference = fStore.collection("users").document(userId).collection("answeredQuestions");

    private AnswerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_answers);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = collectionReference.orderBy("timeAnswered", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Answer> options = new FirestoreRecyclerOptions.Builder<Answer>()
                .setQuery(query, Answer.class)
                .build();

        adapter = new AnswerAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.answers_recycler_view);
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
}
