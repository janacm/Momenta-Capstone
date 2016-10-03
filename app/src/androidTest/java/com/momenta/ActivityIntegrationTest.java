package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.widget.DatePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test
 */
public class ActivityIntegrationTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    private static final String TAG = "ActivityIntegrationTest";
    Context context;
    helperPreferences helperPreferences;
    DatabaseReference reference;

    @Before
    public void before() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext();

        helperPreferences = new helperPreferences(context);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.goOffline();
        reference = firebaseDatabase.getReference();

//        Calendar deadline = Calendar.getInstance();
//        deadline.setTimeInMillis(deadline.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.HOURS));
//        Task task = new Task("Initial Task Name", 400, deadline,
//                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
//
//        reference.child(directory).push().setValue(task);
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSettingsActivity() {
        //Go to settings activity.
        onView(withId(R.id.action_settings)).perform(click());
        onView(withText(R.string.interval_time_title)).perform(click());

        insertInterval("99", "99");
        //Go back to MainActivity
        pressBack();

        String name = "Ball over net game practise", hours="1", minutes="23";
        int year = 2030, month = 5, day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        insertActivity(name, hours, minutes, year, month, day);

        onView(withText(R.string.tab_title_log)).perform(click());
        onView(withId(R.id.activity_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.task_name_edit_text)).check(matches(withText(name)));
    //    onView(withId(R.id.task_hour_edit_text)).check(matches(withText(hours)));
     //   onView(withId(R.id.task_minute_edit_text)).check(matches(withText(minutes)));
        String expected = Task.getDateFormat(cal);
    //    onView(withId(R.id.task_time_set_deadline)).check(matches(withText(expected)));

    }

    private void insertActivity(String activityName, String hours, String minutes,
                                int year, int month, int day) {
        onView(withId(R.id.fab)).perform(click());
        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.newtask_name_edit_text)).perform(typeText(activityName));
        onView(withId(R.id.newtask_deadline_layout)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.newtask_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(replaceText(hours));
        onView(withId(R.id.dialog_minute_edittext)).perform(replaceText(minutes));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.yes)).perform(click());

        onView(withId(R.id.add_task_done_button)).perform(click());
    }

    private void insertInterval(String hours, String mins) {
        onView(withText(R.string.interval_time_title)).perform(click());
        onView(withId(R.id.pref_hour_textview)).perform(replaceText(hours));
        onView(withId(R.id.pref_minute_textview)).perform(replaceText(mins));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withText(context.getString(R.string.interval_time_summary) + " " + hours + " "
                + context.getString(R.string.interval_time_summary_hours) + " " + mins + " "
                + context.getString(R.string.interval_time_summary_minutes))).check(matches(isDisplayed()));
    }
}
