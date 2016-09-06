package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Joe on 2016-04-01.
 * For Momenta-Capstone
 */
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {
    @Rule
    public final ActivityTestRule<SettingsActivity> main = new ActivityTestRule<>(SettingsActivity.class);
    Context ctx;

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        ctx = instrumentation.getTargetContext();
        onView(withText(R.string.interval_time_title)).perform(click());
        onView(withId(R.id.pref_minute_textview)).perform(clearText());
        onView(withId(R.id.pref_hour_textview)).perform(clearText());
        onView(withId(android.R.id.button1)).perform(click());
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void defaultValuesUITest() {
        //Checking Default Values in text fields and text views
        onView(withText(R.string.interval_time_title)).check(matches(isDisplayed()));
        onView(withText(R.string.interval_time_summary_never_remind)).check(matches(isDisplayed()));
    }

    //
    @Test
    public void hourInputUITest() {
        String hours = "21";
        //Click settings
        onView(withText(R.string.interval_time_title)).perform(click());
        //Checking Default Values in text fields and text views
        onView(withId(R.id.pref_hour_textview)).perform(clearText());
        onView(withId(R.id.pref_hour_textview)).perform(typeText(hours));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText(ctx.getString(R.string.interval_time_summary) + " " + hours + " " + ctx.getString(R.string.interval_time_summary_hours))).check(matches(isDisplayed()));
        onView(withText(R.string.interval_time_title)).perform(click());
        onView(withId(R.id.pref_minute_textview)).perform(clearText());
        onView(withId(R.id.pref_hour_textview)).perform(clearText());

    }

    @Test
    public void minuteInputOnlyUITest() {
        String mins = "34";
        //Click settings
        onView(withText(R.string.interval_time_title)).perform(click());
        //Checking Default Values in text fields and text views
        onView(withId(R.id.pref_hour_textview)).perform(clearText());
        onView(withId(R.id.pref_minute_textview)).perform(typeText(mins));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText(ctx.getString(R.string.interval_time_summary) + " " + mins + " " + ctx.getString(R.string.interval_time_summary_minutes))).check(matches(isDisplayed()));
        onView(withText(R.string.interval_time_title)).perform(click());
        onView(withId(R.id.pref_minute_textview)).perform(clearText());
        onView(withId(R.id.pref_hour_textview)).perform(clearText());
    }

    @Test
    public void hourAndMinuteInputUITest() {
        String hours = "59";
        String mins = "34";
        //Click settings
        onView(withText(R.string.interval_time_title)).perform(click());
        //Checking Default Values in text fields and text views
        onView(withId(R.id.pref_minute_textview)).perform(clearText());
        onView(withId(R.id.pref_minute_textview)).perform(typeText(mins));
        onView(withId(R.id.pref_hour_textview)).perform(typeText(hours));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText(ctx.getString(R.string.interval_time_summary) + " " + hours + " " + ctx.getString(R.string.interval_time_summary_hours) + " " + mins + " " + ctx.getString(R.string.interval_time_summary_minutes))).check(matches(isDisplayed()));

    }

}