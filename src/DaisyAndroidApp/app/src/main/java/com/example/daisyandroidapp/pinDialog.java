package com.example.daisyandroidapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class pinDialog extends AppCompatDialogFragment {

    private String userid;
    private String username;

    private Boolean pairSuccessFlag = false;

    private TextView genPin;
    private Button checkPairedBtn;
    private ProgressBar pairCheck;
    private ImageView pairSuccess;
    private ImageView pairNoSuccess;
    private PinDialogListener listener;

    private FirebaseFirestore fStore;
    private CollectionReference collectionReference;
    private DocumentReference pairPinDocument;

    private DaisyApi daisyApi;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //API Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://daisy-project.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        daisyApi = retrofit.create(DaisyApi.class);

        fStore = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Pair Home Assitant")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //User presses cancel
                        if (pairSuccessFlag)
                            listener.checkFlag(true);
                        else
                            updateUser("null");


                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //user presses Ok
                        if (pairSuccessFlag)
                            listener.checkFlag(pairSuccessFlag);
                        else
                            updateUser("null");
                    }
                });

        pairSuccess = view.findViewById(R.id.pairSuccess);
        pairNoSuccess = view.findViewById(R.id.pairNoSuccess);

        checkPairedBtn = view.findViewById(R.id.checkButton);
        pairCheck = view.findViewById(R.id.pairCheck);
        pairCheck.setVisibility(View.VISIBLE);


        checkPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairNoSuccess.setVisibility(View.INVISIBLE);
                pairCheck.setVisibility(View.VISIBLE);
                getHomeAssistants();
            }
        });

        genPin = view.findViewById(R.id.genPin);

        collectionReference = fStore.collection("pairPins");

        generateRandomPin();

        return builder.create();
    }

    private void getHomeAssistants() {


        Call<DBHomeAssistantList> call = daisyApi.getHomeAssistants();
        call.enqueue(new Callback<DBHomeAssistantList>() {
            @Override
            public void onResponse(Call<DBHomeAssistantList> call, Response<DBHomeAssistantList> response) {
                if (!response.isSuccessful()) {
                    pairNoSuccess.setVisibility(View.VISIBLE);
                    Log.d("PinDialogError", "ERROR GETTING HOME ASSISTANTS: " + response.code());
                    Log.d("PinDialogError", "ERROR GETTING HOME ASSISTANTS: " + response.message());
                    Log.d("PinDialogError", "ERROR GETTING HOME ASSISTANTS: " + response.raw().toString());
                    return;
                }

                DBHomeAssistantList dbHomeAssistantList = response.body();
                Log.d("PinDialog", "List: " + dbHomeAssistantList.getDbHomeAssistantList().toString());
                for (DBHomeAssistant dbHomeAssistant : dbHomeAssistantList.getDbHomeAssistantList()) {
                    Log.d("PinDialog", "User ID: " + dbHomeAssistant.getUserId());
                    if (dbHomeAssistant.getUserId().equals(userid)) {
                        pairSuccessFlag = true;
                        pairCheck.setVisibility(View.INVISIBLE);
                        pairSuccess.setVisibility(View.VISIBLE);

                    }
                }

                Log.d("PinDialog", "pairSuccessFlag" + pairSuccessFlag.toString());

                if (pairSuccessFlag.equals(false)) {
                    if (pairCheck.getVisibility() == View.VISIBLE)
                        pairCheck.setVisibility(View.INVISIBLE);
                    pairNoSuccess.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<DBHomeAssistantList> call, Throwable t) {
                pairNoSuccess.setVisibility(View.VISIBLE);
                Log.d("PinDialogError", "ERROR CONNECTING TO API: " + t.getMessage());
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (PinDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement PinDialog Listener");
        }
    }

    public String getRandomString(int i) {
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        while (i > 0) {
            Random rand = new Random();
            result.append(alpha.charAt(rand.nextInt(alpha.length())));
            i--;
        }
        return result.toString();
    }

    public void generateRandomPin() {

        DecimalFormat df = new DecimalFormat("0000000");
        final Random randomNumber = new Random();
        String randomIntenger = df.format(randomNumber.nextInt(9999999));
        String randomString = getRandomString(2);
        final String randomPin = randomString + randomIntenger;

        //check if randomPin is in database
        Task<QuerySnapshot> pairPinQueried = collectionReference
                .whereEqualTo("pairPin", randomPin)
                .get();

        pairPinQueried
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            //pairPin not taken
                            updateUser(randomPin);
                            continueProcess(randomPin);
                        } else {
                            generateRandomPin();
                            //pairPin taken, try again
                        }
                    }
                });

    }

    private void updateUser(String randomPin) {
        Call<DBPutResponse> call = daisyApi.updatePairPin(userid, randomPin);
        call.enqueue(new Callback<DBPutResponse>() {
            @Override
            public void onResponse(Call<DBPutResponse> call, Response<DBPutResponse> response) {
                if (!response.isSuccessful()){
                    //response not successful
                    Log.d("PinDialogError", "Response not successful");
                    Log.d("PinDialogError", "CODE: " + response.code() + response.message());
                    return;
                }
                //response successful
                Log.d("PinDialogSuccess", "User pair pin updated successfully with: " + randomPin);
                Log.d("PinDialogSuccess", "CODE: " + response.code());
            }

            @Override
            public void onFailure(Call<DBPutResponse> call, Throwable t) {
                Log.d("PinDialogError", "PUT REQUEST ERROR: " + t.getMessage());
            }
        });
    }

    public void continueProcess(String computedPin) {
        pairCheck.setVisibility(View.INVISIBLE);
        genPin.setText(computedPin);


        HashMap<String, Object> pinMap = new HashMap<>();
        pinMap.put("pairPin", computedPin);

        pairPinDocument = collectionReference.document(computedPin);
        pairPinDocument
                .set(pinMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("PinDialog", "Pair pin added to database successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("PinDialog", "Error: " + e.toString());
                    }
                });
    }

    public void setUserCredentials(String uId, String uName) {
        userid = uId;
        username = uName;
    }


    public interface PinDialogListener {
        void checkFlag(Boolean flag);
    }
}
