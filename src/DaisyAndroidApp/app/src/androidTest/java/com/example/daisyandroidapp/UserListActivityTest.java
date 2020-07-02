package com.example.daisyandroidapp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserListActivityTest {

    @Rule
    public ActivityTestRule<UserListActivity> mActivityTestRule = new ActivityTestRule<UserListActivity>(UserListActivity.class);

    private UserListActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View userRecyclerView = mActivity.findViewById(R.id.recycler_view);
        View addUserView = mActivity.findViewById(R.id.button_add_user);
        View backButtonView = mActivity.findViewById(R.id.back_button);
        View loadingUsersView = mActivity.findViewById(R.id.loadingUsers);

        assertNotNull(userRecyclerView);
        assertNotNull(addUserView);
        assertNotNull(backButtonView);
        assertNotNull(loadingUsersView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}