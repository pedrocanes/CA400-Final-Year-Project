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

public class RedirectTest {

    @Rule
    public ActivityTestRule<Redirect> mActivityTestRule = new ActivityTestRule<Redirect>(Redirect.class);

    private Redirect mActivity = null;

    Instrumentation.ActivityMonitor userMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor researcherMonitor = getInstrumentation().addMonitor(Researcher.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View progressBarView = mActivity.findViewById(R.id.progressBar3);
        View loadingView = mActivity.findViewById(R.id.loading);

        assertNotNull(progressBarView);
        assertNotNull(loadingView);
    }

    @Test
    public void testLaunchOfUserOnRegister() {
        Activity userActivity = getInstrumentation().waitForMonitorWithTimeout(userMonitor, 5000);

        assertNull(userActivity);
    }

    @Test
    public void testLaunchOfResearcherOnRegister() {
        Activity resActivity = getInstrumentation().waitForMonitorWithTimeout(researcherMonitor, 5000);

        assertNull(resActivity);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}