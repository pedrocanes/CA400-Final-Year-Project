package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class AskQuestionActivityTest {

    @Rule
    public ActivityTestRule<AskQuestionActivity> mActivityTestRule = new ActivityTestRule<AskQuestionActivity>(AskQuestionActivity.class);

    private AskQuestionActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View questionRecyclerView = mActivity.findViewById(R.id.question_recycler_view);
        View buttonAddQuestionView = mActivity.findViewById(R.id.button_add_question);
        View backButtonView = mActivity.findViewById(R.id.ask_question_back_button);

        assertNotNull(questionRecyclerView);
        assertNotNull(buttonAddQuestionView);
        assertNotNull(backButtonView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}