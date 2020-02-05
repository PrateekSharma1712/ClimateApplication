package com.prateek.weatherapplication.ui.forecast

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.prateek.weatherapplication.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<LandingActivity> =
        ActivityTestRule(LandingActivity::class.java)

    @Test
    fun allViewsDisplayed() {
        onView(withId(R.id.loaderFrameLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.cityTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.weatherByDayRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.noNetworkImageView)).check(matches(isDisplayed()))
    }
}