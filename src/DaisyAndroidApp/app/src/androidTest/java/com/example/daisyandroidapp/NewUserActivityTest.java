package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewUserActivityTest {

    @Rule
    public ActivityTestRule<NewUserActivity> mActivityTestRule = new ActivityTestRule<NewUserActivity>(NewUserActivity.class);

    private NewUserActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View pinView = mActivity.findViewById(R.id.edit_text_pin);
        View addPinConfirmView = mActivity.findViewById(R.id.addPinConfirm);
        View loadingSpinView = mActivity.findViewById(R.id.loadingSpinWheel);
        View exitButton = mActivity.findViewById(R.id.exitFloating);

        assertNotNull(pinView);
        assertNotNull(addPinConfirmView);
        assertNotNull(loadingSpinView);
        assertNotNull(exitButton);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}