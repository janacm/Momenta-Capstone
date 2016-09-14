package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.not;

/**
 * Created by joesi on 2016-02-16.
 */
@RunWith(AndroidJUnit4.class)
public class AddActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    Context ctx;
    helperPreferences helperPreferences;
    DatabaseReference reference;
    String directory = "tests";

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        ctx = instrumentation.getTargetContext();
        helperPreferences = new helperPreferences(ctx);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.goOffline();
        reference = firebaseDatabase.getReference();
    }

    @Test
    public void addActivityDBTesting() {
        String taskName = "TaskName";
        int duration = 12;
        Calendar deadline = Calendar.getInstance();
        Task task = new Task(taskName, duration, deadline,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        reference.child(directory).push().setValue(task);

//        Task taskAdded = db.getTask((int) id);
//        assertEquals(taskName, taskAdded.getName());
//        assertEquals(duration, taskAdded.getGoal());
//        assertEquals(deadline, taskAdded.getDeadlineValue());
    }

    @Test
    public void addActivityDefaultStringFieldValuesUITests() {
        //Switching to Log tab
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Checking Default Values in text fields and text views
        //onView(withId(R.id.new_activity_edit_text)).check(matches(withHint(ctx.getString(R.string.new_activity_hint))));TODO cannot get string to pass

        //Verify String values
        onView(withText(ctx.getString(R.string.goal_string))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.deadline_string))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.priority_string))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.timespent_string))).check(matches(isDisplayed()));
    }

    @Test
    public void addActivityActionNoNameUITests() {
        //Click Add Activity button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //try to add activity without a name
        onView(withId(R.id.add_task_done_button)).perform(click());

        onView(withText(ctx.getString(R.string.toast_no_name_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void addActivityActionNameOnlyUITests() {
        String activityName = "Test Activity1";
        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

<<<<<<< HEAD
        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //add activity with a name only
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));
=======
        //check toast is displayed
        onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
>>>>>>> firetrends

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //Add activity
        onView(withId(R.id.add_task_done_button)).perform(click());

        //TODO Snackbar validation here
        //check toast is displayed
        //onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        //Navigate to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());

        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));
    }

    @Test
    public void addActivityActionNameGoalUITests() {
        String activityName = "Test Activity2";

        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Add activity with a name and goal(97H 34M)
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.newtask_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("97"));
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("34"));

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        onView(withText(R.string.yes)).perform(click());

        //Add activity
        onView(withId(R.id.add_task_done_button)).perform(click());

        //TODO Snackbar validation here
        //check toast is displayed
        //onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText("0M"))));
    }

    @Test
    public void addActivityActionNameDeadlineUITests() {
        String activityName = "Test Activity3";

        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Add activity with a name goal and deadline
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.newtask_deadline_layout)).perform(click());

        int year = 2030;
        int month = 5;//June, for some reason 0 is January
        int day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        //Set the date of the picker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withText("OK")).perform(click());

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.add_task_done_button)).perform(click());

        //TODO Snackbar validation here
        //check toast is displayed
        //onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));
    }

    @Test
    public void addActivityActionNameGoalDeadlineUITests() {
        String activityName = "Test Activity4";

        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //add activity with a name and deadline
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.newtask_deadline_layout)).perform(click());

        int year = 2030;
        int month = 5;//June, for some reason 0 is January
        int day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        //Set the date of the picker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withText("OK")).perform(click());


        onView(withId(R.id.newtask_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("97"));
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("34"));


        //close soft keyboard
        Espresso.closeSoftKeyboard();

        onView(withText(R.string.yes)).perform(click());

        //Add activity
        onView(withId(R.id.add_task_done_button)).perform(click());

        //TODO Snackbar validation here
        //check toast is displayed
        //onView(withText(ctx.getString(R.string.toast_activity_added))).inRoot(withDecorView(not(main.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        //check new activity is added in listView
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withId(R.id.activity_recycler_view)).check(matches(hasDescendant(withText("0M"))));
    }
}