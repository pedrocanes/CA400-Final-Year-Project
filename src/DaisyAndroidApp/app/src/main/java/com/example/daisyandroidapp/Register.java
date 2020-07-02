package com.example.daisyandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    private static final String RESEARCHER = "researcher";
    private static final String REGULAR = "regular";
    EditText mName, mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    CollectionReference collectionReference;
    CollectionReference usernameCollectionReference;
    DocumentReference usernameDocument;
    String userID;
    Switch researchSwitch;
    Boolean researcher = false;
    Boolean userRegular;
    private DaisyApi daisyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.loginText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        collectionReference = fStore.collection("users");
        usernameCollectionReference = fStore.collection("usernames");
        progressBar = findViewById(R.id.progressBar);

        researchSwitch = findViewById(R.id.userType);

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);


        //checks if user is logged in

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Redirect.class));
            finish();
        }


        //Register Button clicked

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String name = mName.getText().toString();
                usernameDocument = usernameCollectionReference.document(name);

                if (TextUtils.isEmpty(name)) {
                    mName.setError("Username is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 5) {
                    mPassword.setError("Password must be at least 5 characters");
                    return;
                }

                usernameCollectionReference
                        .whereEqualTo("username", name)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    mName.setError("Username is taken, please try a different one");
                                } else {
                                    testApi(name, email, password);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mName.setError("Service is down, please try again later");
                        Log.w("RegisterActivity", "ERROR;" + e.toString());
                    }
                });


            }
        });


        researchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                // Toggle disabled
                researcher = isChecked;
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void testApi(final String sName, final String sEmail, String sPassword) {
        progressBar.setVisibility(View.VISIBLE);

        Call<DBUserList> testCall = daisyApi.getUsers();
        testCall.enqueue(new Callback<DBUserList>() {
            @Override
            public void onResponse(Call<DBUserList> call, Response<DBUserList> response) {
                if (response.isSuccessful()) {
                    Log.d("RegisterSuccess", "API Test Successful");
                    continueProgram(sName, sEmail, sPassword);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "ERROR: " + response.code() + ". Please try again.", Toast.LENGTH_SHORT).show();
                    Log.d("RegisterError", "RESPONSE ERROR TESTING API: " + response.code());
                    return;

                }
            }

            @Override
            public void onFailure(Call<DBUserList> call, Throwable t) {
                Toast.makeText(Register.this, "ERROR: " + t.getMessage() + ". Please try again.", Toast.LENGTH_SHORT).show();
                Log.d("RegisterError", "ERROR TESTING API: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
                return;
            }
        });
    }

    private void continueProgram(final String sName, final String sEmail, String sPassword) {


        //Register user in Firebase

        fAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this, "User created On Firebase", Toast.LENGTH_SHORT).show();

                    userID = fAuth.getCurrentUser().getUid();


                    final DocumentReference documentReference = collectionReference.document(userID);

                    User user = new User(null, sName, sEmail);
                    if (researcher == true) {
                        user.setType(RESEARCHER);

                        userRegular = false;
                    } else {
                        user.setType(REGULAR);

                        userRegular = true;
                    }
                    user.setUserId(userID);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });

                    if (researcher != true && userRegular == true) {
                        createUser(sName);
                    } else {
                        createResearcher(sName, sEmail);
                    }
                } else {
                    Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

                //put username into collection of used usernames
                HashMap<String, Object> username = new HashMap<>();
                username.put("username", sName);
                usernameDocument
                        .set(username)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.w("RegisterActivity", "Collection usernames updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("RegisterActivity", "Collection users not updated: " + e.toString());
                            }
                        });
            }
        });
    }

    private void createResearcher(String userName, String email) {
        DBResearcher dbResearcher = new DBResearcher(userID, userName, email);

        Call<DBResearcher> call = daisyApi.createResearcher(dbResearcher);

        call.enqueue(new Callback<DBResearcher>() {
            @Override
            public void onResponse(Call<DBResearcher> call, Response<DBResearcher> response) {
                if (!response.isSuccessful()) {
                    Log.d("RegisterError", "ERROR CREATING RESEARCHER: " + response.code());
                    Log.d("RegisterError", "ERROR CREATING RESEARCHER: " + response.message());
                    Log.d("RegisterError", "ERROR CREATING RESEARCHER: " + response.raw().toString());
                    return;
                }

                Log.d("RegisterSuccess", "RESEARCHER CREATED SUCCESSFULLY CODE: " + response.code() + response.message());

                startActivity(new Intent(getApplicationContext(), Researcher.class));
                finish();

            }

            @Override
            public void onFailure(Call<DBResearcher> call, Throwable t) {
                Log.d("RegisterError", "RESEARCHER POST REQUEST ERROR: " + t.getMessage());
            }
        });
    }

    private void createUser(String userName) {
        DBUser dbUser = new DBUser(userID, userName, "null");

        Log.d("Register", "ID: " + dbUser.getId());
        Log.d("Register", "USERNAME: " + dbUser.getUsername());
        Log.d("Register", "PAIR PIN: " + dbUser.getPair_pin());

        Call<DBUser> call = daisyApi.createUser(dbUser);

        call.enqueue(new Callback<DBUser>() {
            @Override
            public void onResponse(Call<DBUser> call, Response<DBUser> response) {

                if (!response.isSuccessful()) {
                    Log.d("RegisterError", "ERROR CREATING USER: " + response.code());
                    Log.d("RegisterError", "ERROR CREATING USER: " + response.message());
                    Log.d("RegisterError", "ERROR CREATING USER: " + response.raw().toString());
                    return;
                }

                DBUser userResponse = response.body();
                Log.d("RegisterSuccess", "USER CREATED SUCCESSFULLY CODE: " + response.code() + response.message());
                Log.d("RegisterSuccess", "ID RESPONSE: " + userResponse.getUser().getId());
                Log.d("RegisterSuccess", "PAIR_PIN RESPONSE: " + userResponse.getUser().getPair_pin());
                Log.d("RegisterSuccess", "USERNAME RESPONSE: " + userResponse.getUser().getUsername());


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 2000ms
                        createPhone();
                    }
                }, 500);

            }

            @Override
            public void onFailure(Call<DBUser> call, Throwable t) {
                Log.d("RegisterError", "USER POST REQUEST ERROR: " + t.getMessage());
            }
        });
    }

    private void createPhone() {
        String serialKey = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String phoneId = UUID.randomUUID().toString().substring(0, 28);

        DBPhone dbPhone = new DBPhone(phoneId, serialKey, "null", userID);

        Call<DBPhone> call = daisyApi.createPhone(dbPhone);
        call.enqueue(new Callback<DBPhone>() {
            @Override
            public void onResponse(Call<DBPhone> call, Response<DBPhone> response) {
                if (!response.isSuccessful()) {
                    //responde not successful
                    Log.d("RegisterError", "ERROR CREATING PHONE: " + response.code());
                    Log.d("RegisterError", "ERROR CREATING PHONE: " + response.message());
                    Log.d("RegisterError", "ERROR CREATING PHONE: " + response.raw().toString());
                    Log.d("RegisterError", "ERROR BODY: " + response.errorBody());
                    return;
                }

                DBPhone userResponse = response.body();
                Log.d("RegisterSuccess", "PHONE CREATED SUCCESSFULLY CODE: " + response.code() + response.message());
                Log.d("RegisterSuccess", "ID RESPONSE: " + userResponse.getId());
                Log.d("RegisterSuccess", "SERIALKEY RESPONSE: " + userResponse.getSerial_key());
                Log.d("RegisterSuccess", "USERID RESPONSE: " + userResponse.getUser_ID());


                sendPhoneIdToFirestore(phoneId);
            }

            @Override
            public void onFailure(Call<DBPhone> call, Throwable t) {
                Log.d("RegisterError", "PHONE POST REQUEST ERROR: " + t.getMessage());
            }
        });
    }

    private void sendPhoneIdToFirestore(String phoneId) {

        DocumentReference phoneDocument = collectionReference.document(userID).collection("phoneDetails").document("phoneDetails");
        HashMap<String, Object> phoneDetails = new HashMap<>();
        phoneDetails.put("phoneId", phoneId);

        phoneDocument.set(phoneDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("RegisterSuccess", "PHONE ID SUCCESSFULLY ADDED TO FIRESTORE");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("RegisterSuccess", "ERROR: PHONE NOT ADDED TO FIRESTORE: " + e.toString());
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

}
