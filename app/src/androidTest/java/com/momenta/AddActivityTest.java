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
import com.google.firebase.database.Query;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by joesi on 2016-02-16.
 */
@RunWith(AndroidJUnit4.class)
public class AddActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<MainActivity>(MainActivity.class){
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            FirebaseDatabase database = mock(FirebaseDatabase.class);
            DatabaseReference reference = mock(DatabaseReference.class);
            Query query = mock(Query.class);

            when(database.getReference()).thenReturn(reference);
            when(reference.child(null)).thenReturn(reference);
            when(reference.child(any(String.class))).thenReturn(reference);
            when(reference.push()).thenReturn(reference);
            when(reference.getKey()).thenReturn("TaskId");
            when(reference.orderByChild(any(String.class))).thenReturn(query);
            FirebaseProvider.setFirebaseDatabase(database);
        }
    };
    Context ctx;
    helperPreferences helperPreferences;

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        ctx = instrumentation.getTargetContext();
        helperPreferences = new helperPreferences(ctx);
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

//        TODO:
//        onView(withId(R.id.add_task_done_button)).perform(click());
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
        Espresso.closeSoftKeyboard();
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
        Espresso.closeSoftKeyboard();
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
    }
}