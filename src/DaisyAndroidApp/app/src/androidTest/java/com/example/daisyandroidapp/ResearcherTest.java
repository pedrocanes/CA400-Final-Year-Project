package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResearcherTest {

    @Rule
    public ActivityTestRule<Researcher> mActivityTestRule = new ActivityTestRule<Researcher>(Researcher.class);

    private Researcher mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View yourProfileView = mActivity.findViewById(R.id.textView4);
        View logOutBtnView = mActivity.findViewById(R.id.back);
        View profileNameView = mActivity.findViewById(R.id.profileName);
        View profileEmailView = mActivity.findViewById(R.id.profileEmail);
        View questionsBtnView = mActivity.findViewById(R.id.questionsBtn);
        View answersBtnView = mActivity.findViewById(R.id.answersBtn);
        View viewUserView = mActivity.findViewById(R.id.viewUser);
        View loadingSpinView = mActivity.findViewById(R.id.loadingSpin);

        assertNotNull(yourProfileView);
        assertNotNull(logOutBtnView);
        assertNotNull(profileEmailView);
        assertNotNull(profileNameView);
        assertNotNull(questionsBtnView);
        assertNotNull(answersBtnView);
        assertNotNull(viewUserView);
        assertNotNull(loadingSpinView);

    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}