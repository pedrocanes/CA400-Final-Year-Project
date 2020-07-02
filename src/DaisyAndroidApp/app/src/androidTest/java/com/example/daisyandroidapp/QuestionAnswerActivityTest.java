package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionAnswerActivityTest {

    @Rule
    public ActivityTestRule<QuestionAnswerActivity> mActivityTestRule = new ActivityTestRule<QuestionAnswerActivity>(QuestionAnswerActivity.class);

    private QuestionAnswerActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View questionHeaderView = mActivity.findViewById(R.id.questionHeader);
        View questionTitleView = mActivity.findViewById(R.id.question_answer_title);
        View answerRadioGroupView = mActivity.findViewById(R.id.answer_radio_group);
        View buttonSubmitAnswerView = mActivity.findViewById(R.id.button_submit_answer);
        View questionAskerView = mActivity.findViewById(R.id.question_asked_by);

        assertNotNull(questionHeaderView);
        assertNotNull(questionTitleView);
        assertNotNull(answerRadioGroupView);
        assertNotNull(buttonSubmitAnswerView);
        assertNotNull(questionAskerView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}