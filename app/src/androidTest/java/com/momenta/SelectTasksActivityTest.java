package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.SeekBar;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class SelectTasksActivityTest {
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
        tearDown();
    }

    @After
    public void tearDown() {
        db.getReadableDatabase().delete(DBHelper.SAMPLE_TABLE, null, null);
    }


    @Test
    public void testSelectOneActivity() {
        //Initialize intent
        Intents.init();

        String activityName = "Test Activity 1";

        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //add activity with a name only
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //Add activity
        onView(withId(R.id.add_task_done_button)).perform(click());

        //Switching to Dashboard tab
        onView(withText(ctx.getString(R.string.tab_title_dashboard))).perform(click());
        onView(withText(ctx.getString(R.string.dummy_button))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.dummy_button))).perform(click());

        //check activity is added in listView
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withText(activityName)).perform(click());

        //Click done button
        onView(withId(R.id.action_done)).perform(click());

        //Set a progress of 10 to the activity
        onView(withId(R.id.add_time_to_task_seekbar)).perform(setProgress(1));

        //Press next button
        onView(withId(R.id.add_time_to_task_nextBtn)).perform(click());

        //Verify MainActivity is open
        intended(hasComponent(MainActivity.class.getName()));

        Intents.release();
    }

    @Test
    public void testSelectMultipleActivities() {
        //Initialize intent
        Intents.init();

        String activityName = "Test Activity 1";
        String activityName2 = "Test Activity 2";

        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //add activity with a name only
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //Add activity
        onView(withId(R.id.add_task_done_button)).perform(click());

        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //add activity 2 with a name only
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName2));

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //Add activity
        onView(withId(R.id.add_task_done_button)).perform(click());

        //Switching to Dashboard tab
        onView(withText(ctx.getString(R.string.tab_title_dashboard))).perform(click());
        onView(withText(ctx.getString(R.string.dummy_button))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.dummy_button))).perform(click());

        //check activity 1 is added in listView
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withText(activityName)).perform(click());

        //check activity 2 is added in listView
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName2))));
        onView(withText(activityName2)).perform(click());

        //check toast is displayed
        onView(withId(R.id.action_done)).perform(click());

        //Set a progress of 10 to the activity
        onView(withId(R.id.add_time_to_task_seekbar)).perform(setProgress(1));

        //Press next button
        onView(withId(R.id.add_time_to_task_nextBtn)).perform(click());

        //Set a progress of 10 to the activity
        onView(withId(R.id.add_time_to_task_seekbar)).perform(setProgress(1));

        //Press next button
        onView(withId(R.id.add_time_to_task_nextBtn)).perform(click());

        //Verify MainActivity is open
        intended(hasComponent(MainActivity.class.getName()));

        //Release Intent
        Intents.release();
    }


    @Test
    public void testNoActivitySelected() {
        //Initialize intent
        Intents.init();

        //Click Add Activity Button
        onView(withId(R.id.fab)).perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String activityName = "Test Activity 1";

        //Add activity with a name only
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));

        //close soft keyboard
        Espresso.closeSoftKeyboard();

        //Add activity
        onView(withId(R.id.add_task_done_button)).perform(click());

        //Switching to Dashboard tab
        onView(withText(ctx.getString(R.string.tab_title_dashboard))).perform(click());
        onView(withText(ctx.getString(R.string.dummy_button))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.dummy_button))).perform(click());

        //Check if the list contains the activities
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName))));

        //Check item off the list
        onView(allOf(withId(R.id.item_select_item_checkbox))).check(matches(not(isChecked())));

        //Select the next key
        onView(withId(R.id.action_done)).perform(click());

        //Verify that same activity is running and AddTaskToTime Activity wasn't executed
        intended(hasComponent(AddTaskTimeActivity.class.getName()), times(0));

        //Release intent
        Intents.release();
    }

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }
}
