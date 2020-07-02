package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResearcherAnswersActivityTest {

    @Rule
    public ActivityTestRule<ResearcherAnswersActivity> mActivityTestRule = new ActivityTestRule<ResearcherAnswersActivity>(ResearcherAnswersActivity.class);

    private ResearcherAnswersActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View answersRecyclerView = mActivity.findViewById(R.id.answers_recycler_view);

        assertNotNull(answersRecyclerView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}