package com.darothub.galleria.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.darothub.galleria.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
internal class MainActivityTest{
    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun testThatWhen_MainActivity_Is_Launched_Search_EditText_Is_present() {
        Espresso.onView(withId(R.id.search_image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}