package com.example.daisyandroidapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class MicrophoneJobService extends JobService {
    public static final String TAG = "MicrophoneJobService";
    private boolean jobCancelled = false;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        doBackgroundWork(params);


        return true;
    }

    private void doBackgroundWork(JobParameters params) {

        if (jobCancelled)
            return;

        //startMicrophoneService
        Intent serviceIntent = new Intent(this, MicrophoneService.class);
        ContextCompat.startForegroundService(this, serviceIntent);



        Log.d(TAG, "Job finished");
        jobFinished(params, false);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
