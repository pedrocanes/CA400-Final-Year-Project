package com.example.daisyandroidapp;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Researcher extends AppCompatActivity {

    TextView fullname, email;
    ProgressBar loadingSpin;
    Button viewUser;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CollectionReference collectionReference;
    String userId;

    Button questionsBtn, answersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher);

        fullname = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        loadingSpin = findViewById(R.id.loadingSpin);

        questionsBtn = findViewById(R.id.questionsBtn);
        answersBtn = findViewById(R.id.answersBtn);

        viewUser = findViewById(R.id.viewUser);
        viewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                startActivity(intent);
            }
        });

        questionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questionIntent = new Intent(getApplicationContext(), ResearcherQuestions.class);
                startActivity(questionIntent);
            }
        });

        answersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerIntent = new Intent(getApplicationContext(), ResearcherAnswersActivity.class);
                startActivity(answerIntent);
            }
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        collectionReference = fStore.collection("users");

        DocumentReference documentReference = collectionReference.document(userId);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            User user = documentSnapshot.toObject(User.class);

                            fullname.setText(user.getName());
                            email.setText(user.getEmail());

                        } else {
                            fullname.setText("no Full Name found");
                            email.setText("no Email found");
                        }

                        fullname.setVisibility(View.VISIBLE);
                        email.setVisibility(View.VISIBLE);
                        loadingSpin.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Researcher.this, "Error getting User Details", Toast.LENGTH_SHORT).show();
                        fullname.setText("no Full Name found");
                        email.setText("no Email found");

                        fullname.setVisibility(View.VISIBLE);
                        email.setVisibility(View.VISIBLE);
                        loadingSpin.setVisibility(View.GONE);
                    }
                });

        scheduleJob();

    }

    private void scheduleJob() {
        ComponentName componentName = new ComponentName(getApplicationContext(), AnswerJobService.class);
        JobInfo info = new JobInfo.Builder(69, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("Researcher", "Job scheduled");
        } else {
            Log.d("Researcher", "Job scheduling failed");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(69);
        Log.d("Researcher", "Job cancelled");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //Log out button

    public void logout(View view) {
        cancelJob();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}
