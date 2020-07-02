package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View yourProfileView = mActivity.findViewById(R.id.textView4);
        View logOutView = mActivity.findViewById(R.id.back);
        View locationSendingView = mActivity.findViewById(R.id.textView);
        View profileNameView = mActivity.findViewById(R.id.profileName);
        View profileEmailView = mActivity.findViewById(R.id.profileEmail);
        View loadingProgressView = mActivity.findViewById(R.id.loadingProgress);
        View openPinDialogView = mActivity.findViewById(R.id.openPinDialog);
        View userPairedTextView = mActivity.findViewById(R.id.userPairedText);
        View mainActivityRecyclerView = mActivity.findViewById(R.id.recycler_view_ma);

        assertNotNull(yourProfileView);
        assertNotNull(logOutView);
        assertNotNull(locationSendingView);
        assertNotNull(profileEmailView);
        assertNotNull(profileNameView);
        assertNotNull(loadingProgressView);
        assertNotNull(openPinDialogView);
        assertNotNull(userPairedTextView);
        assertNotNull(mainActivityRecyclerView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}