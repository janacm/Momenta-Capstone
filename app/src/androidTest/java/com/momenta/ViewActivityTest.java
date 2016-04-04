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
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
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

    private Long taskID;
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

        taskID = db.insertTask(new Task("Initial Task Name", 400, deadline,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance()));

        Intent intent = new Intent();
        intent.putExtra(DBHelper.ACTIVITY_ID, taskID.intValue());
        rule.launchActivity(intent);
    }

    @Test
    public void testUpdateActivityName() {
        String name = "Rumpelstiltskin once upon a time";
        //Replace the text in teh textView
        onView(withId(R.id.task_name_edit_text))
                .perform(replaceText(name), closeSoftKeyboard());

        restartTaskActivity(taskID);

        //Ensure the change has taken place
        onView(withId(R.id.task_name_edit_text)).check(matches(withText(name)));
    }

    @Test
    public void testUpdateActivityGoal() {
        long id = taskID;
        //Input new values into the dialog 3H 20M
        onView(withId(R.id.task_hour_edit_text)).perform(replaceText("3"));
        onView(withId(R.id.task_minute_edit_text)).perform(replaceText("20"));

        restartTaskActivity(id);

        //Verify time value was saved (3H 20M)
        onView(withId(R.id.task_hour_edit_text)).check(matches(withText("3")));
        onView(withId(R.id.task_minute_edit_text)).check(matches(withText("20")));
    }

    @Test
    public void testUpdateActivityDeadline() {
        long id = taskID;
        int year = 2030;
        int month = 5;//June, for some reason 0 is January
        int day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        String expected = new SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(cal.getTime());


        //Click on the deadline layout to popup views
        onView(withId(R.id.task_deadline_layout)).perform(click());

        //Set the date of the picker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());

        restartTaskActivity(id);

        //Verify the date was changed
        onView(withId(R.id.task_time_set_deadline)).check(matches(withText(expected)));
    }

    @Test
    public void testUpdateActivityPriority(){
        long id = taskID;
        String priority = "Very High";

        //Click on the spinner
        onView(withId(R.id.task_priority_spinner)).perform(click());

        // Select a priority from the spinner
        onData(allOf(is(instanceOf(String.class)), is(priority))).perform(click());

        restartTaskActivity(id);

        //Verify the value of the spinner
        onView(withId(R.id.task_priority_spinner)).check(matches(withSpinnerText(containsString(priority))));
    }

    /**
     * Convenience method to restart the TaskActivity.
     * Clicks on the done button and then calls the setup method.
     */
    public void restartTaskActivity(Long id) {
        //Click on the back button and restart the TaskActivity.
        onView(withId(R.id.action_done)).perform(click());

        Intent intent = new Intent();
        intent.putExtra(DBHelper.ACTIVITY_ID, id.intValue());
        rule.launchActivity(intent);
    }
}
