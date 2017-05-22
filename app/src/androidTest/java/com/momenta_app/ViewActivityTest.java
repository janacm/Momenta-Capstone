package com.momenta_app;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.DatePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class ViewActivityTest {

    private Context context;
    @Rule
    public ActivityTestRule<TaskActivity> rule =
            new ActivityTestRule<TaskActivity>(TaskActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    FirebaseDatabase database = mock(FirebaseDatabase.class);
                    DatabaseReference reference = mock(DatabaseReference.class);

                    when(database.getReference()).thenReturn(reference);
                    when(reference.child(null)).thenReturn(reference);
                    when(reference.child(any(String.class))).thenReturn(reference);

                    FirebaseProvider.setFirebaseDatabase(database);
                }

                @Override
                protected void afterActivityLaunched() {
                    super.afterActivityLaunched();
                    // TODO: Currently only testing Task.Type.DEADLINE
                    Calendar calendar = Calendar.getInstance();
                    final Task testTask = new Task("Learn the Guitar", 500, calendar, calendar.getTimeInMillis(),
                            calendar, Task.Type.DEADLINE);
                    testTask.setPriorityValue(Task.Priority.HIGH);

                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rule.getActivity().initializeFields(testTask);
                            }
                        });
                    } catch (Throwable e) {
                        Log.getStackTraceString(e);
                    }


                }
            };

    @Before
    public void setUp() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        context= instrumentation.getTargetContext();
    }

    @Test
    public void testUpdateActivityName() {
        String name = "Rumpelstiltskin once upon a time";
        //Replace the text in teh textView
        onView(withId(R.id.task_name_edit_text))
                .perform(replaceText(name), closeSoftKeyboard());

        //Ensure the change has taken place
        onView(withId(R.id.task_name_edit_text)).check(matches(withText(name)));
    }

    @Test
    public void testUpdateGoal() {
        onView(withId(R.id.task_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("07"));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("34"));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());

        //Verify time value was saved (7H 34M)
        String goal = "7 hours & 34 minutes";
        onView(withId(R.id.task_goal_value)).check(matches(withText(goal)));
    }

    @Test
    public void testEmptyGoal() {
        onView(withId(R.id.task_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText(""));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());

        String goal = "0 minutes";
        onView(withId(R.id.task_goal_value)).check(matches(withText(goal)));
    }

    @Test
    public void testZeroGoalHours() {
        onView(withId(R.id.task_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("0"));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("34"));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());

        String goal = "34 minutes";
        onView(withId(R.id.task_goal_value)).check(matches(withText(goal)));
    }

    @Test
    public void testZeroGoalMinutes() {
        onView(withId(R.id.task_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("7"));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("0"));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());

        String goal = "7 hours";
        onView(withId(R.id.task_goal_value)).check(matches(withText(goal)));
    }

    @Test
    public void testSingularGoalValue() {
        onView(withId(R.id.task_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("1"));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("1"));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());

        String goal = "1 hour & 1 minute";
        onView(withId(R.id.task_goal_value)).check(matches(withText(goal)));
    }

    @Test
    public void testAddMinutes() {
        onView(withId(R.id.task_timespent_layout)).perform(scrollTo());
        onView(withId(R.id.task_timespent_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("50"));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("23"));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());
    }

    @Test
    public void testAddZeroMinutes() {
        onView(withId(R.id.task_timespent_layout)).perform(scrollTo());
        onView(withId(R.id.task_timespent_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText("0"));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText("0"));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());
    }

    @Test
    public void testAddEmptyMinutes() {
        onView(withId(R.id.task_timespent_layout)).perform(scrollTo());
        onView(withId(R.id.task_timespent_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(clearText());
        onView(withId(R.id.dialog_hour_edittext)).perform(typeText(""));
        onView(withId(R.id.dialog_minute_edittext)).perform(clearText());
        onView(withId(R.id.dialog_minute_edittext)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());
    }

    @Test
    public void testUpdateActivityDeadline() {
        int year = 2030;
        int month = 5;//June, for some reason 0 is January
        int day = 15;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        String expected = Task.getDateFormat(cal);

        //Perform scroll for smaller screens
        onView(allOf(withId(R.id.task_deadline_layout),
                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(scrollTo());

        //Click on the deadline layout to popup views
        onView(allOf(withId(R.id.task_deadline_layout),
                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(click());

        //Set the date of the picker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());

        //Verify the date was changed
        onView(withId(R.id.task_deadline_value)).check(matches(withText(expected)));
    }

    @Test
    public void testUpdateActivityPriority(){
        String priority = "Very High";

        onView(allOf(withId(R.id.task_priority_spinner),
                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(scrollTo());

        onView(allOf(withId(R.id.task_priority_spinner),
                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(click());

        onData(allOf(is(instanceOf(String.class)), is(priority))).perform(click());
    }

}
