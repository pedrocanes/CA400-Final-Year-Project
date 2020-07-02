package com.example.daisyandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.LocationManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements pinDialog.PinDialogListener {

    TextView fullname, email, userPairedText;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    DocumentReference documentReference;

    User user;

    String userId = fAuth.getCurrentUser().getUid();
    String username;

    ProgressBar loadingProgress;

    Button openPinDialog;

    private CollectionReference askedQuestionRef = fStore.collection("users").document(userId).collection("askedQuestions");
    private AskedQuestionAdapter adapter;


    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullname = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        loadingProgress = findViewById(R.id.loadingProgress);

        collectionReference = fStore.collection("users");

        userPairedText = findViewById(R.id.userPairedText);
        openPinDialog = findViewById(R.id.openPinDialog);
        openPinDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        documentReference = collectionReference.document(userId);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            user = documentSnapshot.toObject(User.class);

                            username = user.getName();
                            fullname.setText(username);
                            email.setText(user.getEmail());

                            if (!user.getPaired()) {
                                openPinDialog.setVisibility(View.VISIBLE);
                                userPairedText.setVisibility(View.GONE);
                            }else{
                                userPairedText.setVisibility(View.VISIBLE);
                                startJobScheduler();
                            }


                        } else {
                            fullname.setText("no Full Name found");
                            email.setText("no Email found");
                        }
                        fullname.setVisibility(View.VISIBLE);
                        email.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error getting User Details", Toast.LENGTH_SHORT).show();
                        fullname.setText("no Full Name found");
                        email.setText("no Email found");
                    }
                });

        setUpRecyclerView();


        //Check whether GPS tracking is enabled//

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        //Check whether this app has access to the location permission//

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int micPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        //If the location permission has been granted, then start the TrackerService//



        if (permission == PackageManager.PERMISSION_GRANTED && micPermission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
            startMicrophoneJob();
        } else {

            //If the app doesnt currently have access to the user’s location, then request access//
            if(permission == PackageManager.PERMISSION_DENIED && micPermission == PackageManager.PERMISSION_DENIED){
                //ask both permissions

                String[] PERMISSIONS = {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.RECORD_AUDIO
                };

                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);

            }else if(permission == PackageManager.PERMISSION_DENIED && micPermission == PackageManager.PERMISSION_GRANTED) {
                //ask location

                String[] PERMISSIONS = {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                };

                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);


            }else if(permission == PackageManager.PERMISSION_GRANTED && micPermission == PackageManager.PERMISSION_DENIED) {
                //ask mic

                String[] PERMISSIONS = {
                        Manifest.permission.RECORD_AUDIO
                };

                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);

            }
        }
    }

    private void startMicrophoneJob() {
        ComponentName micComponentName = new ComponentName(this, MicrophoneJobService.class);
        JobInfo micInfo = new JobInfo.Builder(234, micComponentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(5 * 60 * 60 * 1000) //5 hours
                .build();

        JobScheduler micScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = micScheduler.schedule(micInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("MainActivity", "Microphone Job Scheduled");
        } else {
            Log.d("MainActivity", "Microphone Job Scheduling failed");
        }
    }

    private void stopMicrophoneJob() {
        JobScheduler micScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        micScheduler.cancel(234);
        Log.d("MainActivity", "Microphone Job Canceled");
    }

    private void setUpRecyclerView() {
        Query query = askedQuestionRef;

        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query, Question.class)
                .build();

        adapter = new AskedQuestionAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_ma);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AskedQuestionAdapter.onQuestionItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Question question = documentSnapshot.toObject(Question.class);
                String docummentId = documentSnapshot.getId();

                Intent questionIntent = new Intent(getApplicationContext(), QuestionAnswerActivity.class);
                questionIntent.putExtra("documentId", docummentId);
                questionIntent.putExtra("questionId", question.getId());
                questionIntent.putExtra("question", question.getTitle());
                questionIntent.putExtra("answers", question.getAnswers());
                questionIntent.putExtra("userId", userId);
                questionIntent.putExtra("researcherId", question.getResearcherId());
                questionIntent.putExtra("username", username);

                startActivity(questionIntent);

            }
        });

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        //If the permission has been granted...//

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //...then start the GPS tracking service//

            startTrackerService();
        } else {

            //If the user denies the permission request, then display a toast with some more information//

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    //Start the TrackerService//

    private void startTrackerService() {

        DocumentReference phoneDocument = collectionReference.document(userId).collection("phoneDetails").document("phoneDetails");
        phoneDocument.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.d("MainActivitySuccess", "Success getting phone Id from Firestore");

                        Intent intentService = new Intent(MainActivity.this, TrackingService.class);
                        String phoneId = documentSnapshot.get("phoneId").toString();

                        intentService.putExtra("phoneId", phoneId);
                        startService(intentService);

                        //Notify the user that tracking has been enabled//

                        Toast.makeText(MainActivity.this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MainActivityError" , "ERROR getting phone Id from Firestore: " + e.toString());
                    }
                });


    }


    //Stop the TrackerService//

    private void stopTrackerService() {
        stopService(new Intent(this, TrackingService.class));
        Log.w("TrackService", "Service has been stopped");

        //Notify the user that tracking has been disabled//

        Toast.makeText(this, "GPS tracking disabled", Toast.LENGTH_SHORT).show();

    }

    public void openDialog() {
        pinDialog pinDialogViewer = new pinDialog();
        pinDialogViewer.setUserCredentials(userId, username);
        pinDialogViewer.show(getSupportFragmentManager(), "Pin Dialog");
    }

    //Log out button

    public void logout(View view) {
        stopTrackerService();
        cancelJobScheduler();
        stopMicrophoneJob();
        stopMicrophoneService();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    private void stopMicrophoneService() {
        stopService(new Intent(this, MicrophoneService.class));
        Log.w("MicrophoneService", "Service has been stopped");
    }

    private void cancelJobScheduler() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d("MainActivity", "JOB CANCELED");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void checkFlag(Boolean flag) {
        if (flag) {
            openPinDialog.setVisibility(View.GONE);
            userPairedText.setVisibility(View.VISIBLE);
            user.setPaired(true);
            documentReference
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.w("UserActivity", "User has been paired with Picroft in database");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("UserActivity", "Database was not updated: " + e.toString());
                        }
                    });

            startJobScheduler();

        }
    }

    private void startJobScheduler() {
        //start job scheduler
        ComponentName componentName = new ComponentName(this, DatabaseJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("MainActivity", "JOB SCHEDULED");
        } else {
            Log.d("MainActivity", "JOB SCHEDULING FAILED");
        }
    }
}