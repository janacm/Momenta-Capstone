package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
        //Switching to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        String activityName = "Test Activity 1";

        //add activity 1 with a name only
        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.new_activity_add_button)).perform(click());

        //Switching to Dashboard tab
        onView(withText(ctx.getString(R.string.tab_title_dashboard))).perform(click());
        onView(withText(ctx.getString(R.string.dummy_button))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.dummy_button))).perform(click());

        //check activity 2 is added in listView
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withText(activityName)).perform(click());

        //Click done button
        onView(withId(R.id.action_done)).perform(click());
    }

    @Test
    public void testSelectMultipleActivities() {
        //Switching to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        String activityName = "Test Activity 1";
        String activityName2 = "Test Activity 2";

        //add activity 1 with a name only
        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.new_activity_add_button)).perform(click());

        //add activity 2 with a name only
        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName2));
        onView(withId(R.id.new_activity_add_button)).perform(click());

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
    }


    @Test
    public void testNoActivitySelected() {
        //Initialize intent
        Intents.init();

        //Switching to Log tab
        onView(withText(ctx.getString(R.string.tab_title_log))).perform(click());
        String activityName = "Test Activity 1";

        //Add activity with a name only
        onView(withId(R.id.new_activity_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.new_activity_add_button)).perform(click());

        //Switching to Dashboard tab
        onView(withText(ctx.getString(R.string.tab_title_dashboard))).perform(click());
        onView(withText(ctx.getString(R.string.dummy_button))).check(matches(isDisplayed()));
        onView(withText(ctx.getString(R.string.dummy_button))).perform(click());

        //Check if the list contains the activities
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName))));

        //Check that
        onView(allOf(withId(R.id.item_select_item_checkbox))).check(matches(not(isChecked())));

        //Select the next key
        onView(withId(R.id.action_done)).perform(click());

        //Verify that same activity is running and AddTaskToTime Activity wasn't executed
        intended(hasComponent(AddTaskTimeActivity.class.getName()), times(0));

        //Release intent
        Intents.release();
    }

}
