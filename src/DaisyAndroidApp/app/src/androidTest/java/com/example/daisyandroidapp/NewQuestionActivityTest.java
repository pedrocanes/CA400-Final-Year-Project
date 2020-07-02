package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewQuestionActivityTest {

    @Rule
    public ActivityTestRule<NewQuestionActivity> mActivityTestRule = new ActivityTestRule<NewQuestionActivity>(NewQuestionActivity.class);

    private NewQuestionActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View addQuestionLoadingView = mActivity.findViewById(R.id.add_question_loading);
        View questionTitleView = mActivity.findViewById(R.id.edit_text_question_title);
        View answerLayoutView = mActivity.findViewById(R.id.answer_layout);
        View answer1View = mActivity.findViewById(R.id.edit_text_question_answer1);
        View answer2View = mActivity.findViewById(R.id.edit_text_question_answer2);
        View addAnswerBtnView = mActivity.findViewById(R.id.add_answer_button);
        View addQuestionView = mActivity.findViewById(R.id.add_question);
        View backButtonView = mActivity.findViewById(R.id.new_question_back_button);

        assertNotNull(addAnswerBtnView);
        assertNotNull(questionTitleView);
        assertNotNull(addQuestionLoadingView);
        assertNotNull(answerLayoutView);
        assertNotNull(answer1View);
        assertNotNull(answer2View);
        assertNotNull(addQuestionView);
        assertNotNull(backButtonView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}