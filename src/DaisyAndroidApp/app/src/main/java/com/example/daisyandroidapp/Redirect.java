package com.example.daisyandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Redirect extends AppCompatActivity {

    private static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CollectionReference collectionReference;
    String userId;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        collectionReference = fStore.collection("users");

        DocumentReference documentReference = collectionReference.document(userId);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            userType = user.getType();

                            if(!userType.equals(null)) {
                                if(userType.equals("regular")){
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                                else if(userType.equals("researcher")){
                                    startActivity(new Intent(getApplicationContext(), Researcher.class));
                                    finish();
                                }
                            }else{
                                Toast.makeText(Redirect.this, "Error logging in. Returning to Login page", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "User type null");
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                finish();
                            }


                        }else{
                            Toast.makeText(Redirect.this, "User not found. Returning to Login page", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Redirect.this, "Error logging in. Returning to Login page", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                });

    }
}
