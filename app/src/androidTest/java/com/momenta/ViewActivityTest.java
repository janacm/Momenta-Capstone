package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;
import android.support.test.espresso.contrib.PickerActions;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class ViewActivityTest {

    @Rule
    public ActivityTestRule<TaskActivity> rule =
            new ActivityTestRule(TaskActivity.class, true, false);

    @Before
    public void setUp() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        Context ctx = instrumentation.getTargetContext();
        DBHelper db = DBHelper.getInstance(ctx);

        Calendar deadline = Calendar.getInstance();
        deadline.setTimeInMillis(deadline.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.HOURS));

        db.insertTask(new Task("Task 1", 400, deadline,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance()));

        Intent intent = new Intent();
        intent.putExtra(DBHelper.ACTIVITY_ID, 1);
        rule.launchActivity(intent);
    }

    @Test
    public void testUpdateActivityName() {
        //Replace the text in teh textview
        onView(withId(R.id.task_name_edit_text))
                .perform(replaceText("Rumpelstiltskin once upon a time"), closeSoftKeyboard());

        restartTaskActivity();

        //Ensure the change has taken place
        onView(withId(R.id.task_name_edit_text)).check(matches(withText("Rumpelstiltskin once upon a time")));
    }

//    @Test TODO FIX TEST
//    public void testUpdateActivityGoal() {
//        //Click on the goal time layout
//        onView(withId(R.id.task_time_layout)).perform(click());
//
//
//
//        //Remove previous values from the dialog
//        onView(withId(R.id.buttonOne)).perform(click());
//
//        //Input new values into the dialog 3H 20M
//        onView(withId(R.id.buttonThree)).perform(click());
//        onView(withId(R.id.buttonTwo)).perform(click());
//        onView(withId(R.id.buttonZero)).perform(click());
//        onView(withId(android.R.id.button1)).perform(click());//click done on the dialog
//
//        restartTaskActivity();
//
//        //Verify time value was saved (3H 20M = 200M)
//        onView(withId(R.id.task_time_set_time))
//                .check(matches(withText(TimeDialogBuilder.getTimeString(200))));
//    }

    @Test
    public void testUpdateActivityDeadline() {
        int year = 2030;
        int month = 5;//June, for some reason 0 is January
        int day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        String expected = new SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(cal.getTime());


        //Click on the deadline layout to popup views
        onView(withId(R.id.task_deadline_layout)).perform(click());

        //Set the date of the picker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month+1, day));
        onView(withId(android.R.id.button1)).perform(click());

        restartTaskActivity();

        //Verify the date was changed
        onView(withId(R.id.task_time_set_deadline)).check(matches(withText(expected)));
    }

    @Test
    public void testUpdateActivityPriority(){
        String priority = "Very High";

        //Click on the spinner
        onView(withId(R.id.task_priority_spinner)).perform(click());

        // Select a priority from the spinner
        onData(allOf(is(instanceOf(String.class)), is(priority))).perform(click());

        restartTaskActivity();

        //Verify the value of the spinner
        onView(withId(R.id.task_priority_spinner)).check(matches(withSpinnerText(containsString(priority))));
    }

    /**
     * Convenience method to restart the TaskActivity.
     * Clicks on the done button and then calls the setup method.
     */
    public void restartTaskActivity() {
        //Click on the back button and restart the TaskActivity.
        onView(withId(R.id.action_done)).perform(click());
        setUp();
    }
}
