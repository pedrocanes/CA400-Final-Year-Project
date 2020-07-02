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

public class RegisterTest {

    @Rule
    public ActivityTestRule<Register> mActivityTestRule = new ActivityTestRule<Register>(Register.class);

    private Register mActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(Login.class.getName(), null, false);
    Instrumentation.ActivityMonitor userMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor researcherMonitor = getInstrumentation().addMonitor(Researcher.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View appNameView = mActivity.findViewById(R.id.app_name);
        View appCreateAccountInfoView = mActivity.findViewById(R.id.app_create_account_info);
        View fullNameView = mActivity.findViewById(R.id.fullName);
        View emailView = mActivity.findViewById(R.id.email);
        View passwordView = mActivity.findViewById(R.id.password);
        View userTypeView = mActivity.findViewById(R.id.userType);
        View registerBtnView = mActivity.findViewById(R.id.registerBtn);
        View loginTextView = mActivity.findViewById(R.id.loginText);
        View progressBarView = mActivity.findViewById(R.id.progressBar);

        assertNotNull(appNameView);
        assertNotNull(appCreateAccountInfoView);
        assertNotNull(fullNameView);
        assertNotNull(emailView);
        assertNotNull(passwordView);
        assertNotNull(userTypeView);
        assertNotNull(registerBtnView);
        assertNotNull(loginTextView);
        assertNotNull(progressBarView);
    }

    @Test
    public void testLaunchOfLoginOnClick() {
        assertNotNull(mActivity.findViewById(R.id.loginText));

        onView(withId(R.id.loginText)).perform(click());
        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

        assertNotNull(loginActivity);

    }

    @Test
    public void testLaunchOfUserOnRegister() {
        assertNotNull(mActivity.findViewById(R.id.registerBtn));

        onView(withId(R.id.registerBtn)).perform(click());
        Activity userActivity = getInstrumentation().waitForMonitorWithTimeout(userMonitor, 5000);

        assertNull(userActivity);
    }

    @Test
    public void testLaunchOfResearcherOnRegister() {
        assertNotNull(mActivity.findViewById(R.id.registerBtn));

        onView(withId(R.id.registerBtn)).perform(click());
        Activity resActivity = getInstrumentation().waitForMonitorWithTimeout(researcherMonitor, 5000);

        assertNull(resActivity);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}