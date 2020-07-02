package com.example.daisyandroidapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class LoginTest {

    @Rule
    public ActivityTestRule<Login> mActivityTestRule = new ActivityTestRule<Login>(Login.class);

    private Login mActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(Redirect.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View appNameView = mActivity.findViewById(R.id.app_name);
        View appSignInView = mActivity.findViewById(R.id.app_sign_in_info);
        View emailView = mActivity.findViewById(R.id.email);
        View passwordView = mActivity.findViewById(R.id.password);
        View loginBtnView = mActivity.findViewById(R.id.loginBtn);
        View regTextView = mActivity.findViewById(R.id.regText);
        View progressBar2View = mActivity.findViewById(R.id.progressBar2);

        assertNotNull(appNameView);
        assertNotNull(appSignInView);
        assertNotNull(emailView);
        assertNotNull(passwordView);
        assertNotNull(loginBtnView);
        assertNotNull(regTextView);
        assertNotNull(progressBar2View);

    }

    @Test
    public void testLaunchOfUserOnRegister() {
        assertNotNull(mActivity.findViewById(R.id.loginBtn));

        onView(withId(R.id.loginBtn)).perform(click());
        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

        assertNotNull(loginActivity);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}