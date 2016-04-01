package com.momenta;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;

import static org.hamcrest.Matchers.not;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import static junit.framework.Assert.assertEquals;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Created by joesi on 2016-02-16.
 */
@RunWith(AndroidJUnit4.class)
public class AddActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    DBHelper db;
    Context ctx;

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        ctx = instrumentation.getTargetContext();
        db = DBHelper.getInstance(ctx);

    }

    @Test
    public void addActivityDBTesting() {
        String taskName = "TaskName";
        int duration = 12;
        Calendar deadline = Calendar.getInstance();
        long id = db.insertTask(new Task(taskName, duration, deadline));

        Task taskAdded = db.getTask((int) id);
        assertEquals(taskName, taskAdded.getName());
        assertEquals(duration, taskAdded.getTime());
        assertEquals(deadline, taskAdded.getDeadline());
    }

    @Test
    public void addActivityDefaultStringFieldValuesUITests() {
        //Switching to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        //Checking Default Values in text fields and text views
        onView(withId(R.id.new_activity_edit_text)).check(matches(withHint(ctx.getString(R.string.new_activity_hint))));
        onView(withText(ctx.getString(R.string.goal_string))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.deadline_string))).check(matches(isDisplayed()));
        onView(withId(R.id.new_activity_goal_edit_text)).check(matches(withHint(ctx.getString(R.string.goal_hint))));
        onView(withId(R.id.new_activity_deadline_edit_text)).check(matches(withHint(ctx.getString(R.string.deadline_hint))));
    }

    @Test
    public void addActivityActionNoNameUITests() {
        //Navigate to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());

        //try to add activity without a name
        onView(withId(R.id.new_activity_add_button)).perform(click());
        onView(withText(ctx.getString(R.string.toast_no_name_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void addActivityActionNameOnlyUITests() {
        String activityName = "Test Activity1";
        //Navigate to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        //add activity with a name only
        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.new_activity_add_button)).perform(click());

        //check toast is displayed
        onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));
    }

    @Test
    public void addActivityActionNameGoalUITests() {
        String activityName = "Test Activity2";
        //Navigate to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        //Add activity with a name and goal(97H 34M)
        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.new_activity_goal_edit_text)).perform(click());
        onView(withId(R.id.buttonNine)).perform(click());
        onView(withId(R.id.buttonSeven)).perform(click());
        onView(withId(R.id.buttonThree)).perform(click());
        onView(withId(R.id.buttonFour)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.new_activity_add_button)).perform(click());

        //check toast is displayed
        onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText("97H 34M"))));

    }

    @Test
    public void addActivityActionNameDeadlineUITests() {
        String activityName = "Test Activity3";
        //Navigate to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        //Add activity with a name goal and deadline

        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.new_activity_deadline_edit_text)).perform(click());
        int year = 2030;
        int month = 5;//June, for some reason 0 is January
        int day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        //Set the date of the picker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.new_activity_add_button)).perform(click());

        //check toast is displayed
        onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));

    }

    @Test
    public void addActivityActionNameGoalDeadlineUITests() {
        String activityName = "Test Activity4";
        //Navigate to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        //add activity with a name and deadline
        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.new_activity_deadline_edit_text)).perform(click());
        int year = 2030;
        int month = 5;//June, for some reason 0 is January
        int day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        //Set the date of the picker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.new_activity_goal_edit_text)).perform(click());
        onView(withId(R.id.buttonNine)).perform(click());
        onView(withId(R.id.buttonSeven)).perform(click());
        onView(withId(R.id.buttonThree)).perform(click());
        onView(withId(R.id.buttonFour)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.new_activity_add_button)).perform(click());

        //check toast is displayed
        onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText("97H 34M"))));
    }
}