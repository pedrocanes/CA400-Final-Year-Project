package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResearcherQuestionsTest {

    @Rule
    public ActivityTestRule<ResearcherQuestions> mActivityTestRule = new ActivityTestRule<ResearcherQuestions>(ResearcherQuestions.class);

    private ResearcherQuestions mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View questionsRecyclerView = mActivity.findViewById(R.id.question_recycler);
        View backButtonView = mActivity.findViewById(R.id.researcher_question_back_button);

        assertNotNull(questionsRecyclerView);
        assertNotNull(backButtonView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}