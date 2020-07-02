package com.example.daisyandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewUserActivity extends AppCompatActivity {
    private EditText editTextPin;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CollectionReference collectionReference;
    CollectionReference followedUsers;
    String userId;
    ProgressBar loadingSpinWheel;
    Button addPinConfirm;
    FloatingActionButton exitFloating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        editTextPin = findViewById(R.id.edit_text_pin);
        loadingSpinWheel = findViewById(R.id.loadingSpinWheel);
        addPinConfirm = findViewById(R.id.addPinConfirm);
        exitFloating = findViewById(R.id.exitFloating);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        collectionReference = fStore.collection("users");
        followedUsers = collectionReference.document(userId).collection("followedUsers");

        addPinConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        exitFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_user:
                saveUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveUser() {
        loadingSpinWheel.setVisibility(View.VISIBLE);

        String pin = editTextPin.getText().toString();

        if(pin.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a username", Toast.LENGTH_SHORT).show();
            return;
        }

        getUsersWithPin(pin);
        finish();
    }

    private void getUsersWithPin(String pin){
        final ArrayList<User> userList = new ArrayList<>();//List of users with requested pairing pin
        final ArrayList<String> userArrayList = new ArrayList<>();//List of user IDs with the requested pairing pin

        //Query all users with the requested pair pin
        collectionReference
                .whereEqualTo("name", pin)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            //Add the user Ids of the users with the requested pin
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                User user = documentSnapshot.toObject(User.class);
                                userList.add(user);
                                userArrayList.add(user.getUserId());
                            }

                            getUsersInResearcherList(userArrayList, userList);
                        }else{
                            Toast.makeText(NewUserActivity.this, "ERROR: No user with such username found", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void getUsersInResearcherList(final ArrayList<String> userArrayList, final ArrayList<User> userList){
        final ArrayList<String> followedUserList = new ArrayList<>();//List of all users researcher is currently following

        //Query all users that the researcher is following
        followedUsers
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            User user = documentSnapshot.toObject(User.class);
                            followedUserList.add(user.getUserId());
                        }

                        //userArrayList = IDs of users with requested pair Pin
                        //followedUserList = IDs of all users researcher is following
                        ArrayList<String> existingUsers = new ArrayList<String>(userArrayList);

                        //List of queried users that already exist
                        existingUsers.retainAll(followedUserList);

                        //userList = List of users with requested Pin

                        Boolean duplicates=false;
                        for(final User usr: userList){
                            if(!existingUsers.contains(usr.getUserId())){
                                followedUsers.document(usr.getUserId())
                                        .set(usr)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.w("UserList", "User added to researcher: "+usr.getUserId());
                                                loadingSpinWheel.setVisibility(View.INVISIBLE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("UserList", "User "+usr.getUserId()+"not added because: "+e.toString());
                                                loadingSpinWheel.setVisibility(View.INVISIBLE);
                                            }
                                        });
                            }else{
                                if(!duplicates.equals(true))
                                    duplicates = true;
                            }
                        }
                        if(duplicates.equals(true)){
                            Toast.makeText(NewUserActivity.this, "Some users already exist", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NewUserActivity.this, "All users have been successfully added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
