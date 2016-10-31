package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.SeekBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class SelectTasksActivityTest {
    @Rule
    public final ActivityTestRule<SelectTasksActivity> main = new ActivityTestRule<SelectTasksActivity>(SelectTasksActivity.class){
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            FirebaseDatabase database = mock(FirebaseDatabase.class);
            DatabaseReference reference = mock(DatabaseReference.class);
            Query query = mock(Query.class);

            when(database.getReference()).thenReturn(reference);
            when(reference.child(null)).thenReturn(reference);
            when(reference.child(any(String.class))).thenReturn(reference);
            when(reference.orderByChild(any(String.class))).thenReturn(query);
            FirebaseProvider.setFirebaseDatabase(database);
        }

        @Override
        protected void afterActivityLaunched() {
            super.afterActivityLaunched();
            SelectTasksActivity sa = getActivity();

            // Setting up an adapter with test data
            Calendar cal = Calendar.getInstance();
            Task t = new Task("01", "Clean up", 60, cal, cal.getTimeInMillis(), cal, 30);
            ArrayList<Task> arrayList = new ArrayList<Task>();
            arrayList.add(t);
            SelectTasksAdapter adapter = new SelectTasksAdapter(getActivity(), arrayList);
            adapter.notifyDataSetChanged();

            // Setting the activity to use the adapter with test data
            sa.setAdapter(adapter);

        }
    };

    Context ctx;

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        ctx = instrumentation.getTargetContext();
        Intents.init();
    }

    @After
    public void After() {
        Intents.release();
    }



    @Test
    public void testSelectOneActivity() {
        String activityName = "Clean up";

        //check activity is added in listView
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName))));
        onView(withText(activityName)).perform(click());

        //Click done button
        onView(withId(R.id.action_done)).perform(click());

        //Set a progress of 10 to the activity
        onView(withId(R.id.add_time_to_task_seekbar)).perform(setProgress(1));

        //Press next button
        onView(withId(R.id.add_time_to_task_nextBtn)).perform(click());
    }

    @Test
    public void testSelectMultipleActivities() {
        String activityName1 = "Test Activity 1";
        String activityName2 = "Test Activity 2";
        Calendar cal = Calendar.getInstance();
        Task t1 = new Task("01", activityName1, 60, cal, cal.getTimeInMillis(), cal, 30);
        Task t2 = new Task("01", activityName2, 60, cal, cal.getTimeInMillis(), cal, 30);
        ArrayList<Task> arrayList = new ArrayList<Task>();
        arrayList.add(t1);
        arrayList.add(t2);
        SelectTasksAdapter adapter = new SelectTasksAdapter(ctx, arrayList);
        adapter.notifyDataSetChanged();

        // Setting the activity to use the adapter with test data
        SelectTasksActivity sa = main.getActivity();
        sa.setAdapter(adapter);

        //check activity 1 is added in listView
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName1))));
        onView(withText(activityName1)).perform(click());

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
    }


    @Test
    public void testNoActivitySelected() {
        String activityName = "Clean up";

        //Check if the list contains the activities
        onView(withId(R.id.select_tasks_recycler_view)).check(matches(hasDescendant(withText(activityName))));

        //Check item off the list
        onView(allOf(withId(R.id.item_select_item_checkbox))).check(matches(not(isChecked())));

        //Select the next key
        onView(withId(R.id.action_done)).perform(click());
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
