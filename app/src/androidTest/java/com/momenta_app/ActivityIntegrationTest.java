package com.momenta_app;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test
 */
public class ActivityIntegrationTest {

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

        @Override
        protected void afterActivityLaunched() {
            super.afterActivityLaunched();
            MainActivity ma = getActivity();
            ViewPager vp = ma.getViewerPager();
            ManagerFragmentPagerAdapter manager = (ManagerFragmentPagerAdapter)vp.getAdapter();
            LogFragment logFragment = (LogFragment) manager.getItem(1);

            // Setting up an adapter with test data
            Calendar cal = Calendar.getInstance();
            Task t1 = new Task("01", "Clean up", 60, cal, cal.getTimeInMillis(), cal, 30);
            Task t2 = new Task("02", "Go Running", 60, cal, cal.getTimeInMillis(), cal, 30);
            Task t3 = new Task("03", "Study", 60, cal, cal.getTimeInMillis(), cal, 30);
            ArrayList<Task> arrayList = new ArrayList<>();
            arrayList.add(t1);
            arrayList.add(t2);
            arrayList.add(t3);

            //Setting up the adapter
            Adapter adapter = new Adapter(arrayList);

            logFragment.setAdapter(adapter);
        }
    };

    private static final String TAG = "ActivityIntegrationTest";
    Context context;
    helperPreferences helperPreferences;

    @Before
    public void before() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext();

        helperPreferences = new helperPreferences(context);
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

        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.activity_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
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
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.newtask_deadline_layout)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.newtask_goal_layout)).perform(click());
        onView(withId(R.id.dialog_hour_edittext)).perform(replaceText(hours));
        onView(withId(R.id.dialog_minute_edittext)).perform(replaceText(minutes));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        onView(withText(R.string.dialog_ok)).perform(click());

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

    private class Adapter extends RecyclerView.Adapter<LogFragment.TaskViewHolder>{

        ArrayList<Task> list;

        Adapter(ArrayList<Task> list) {
            this.list = list;
        }
        @Override
        public LogFragment.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new LogFragment.TaskViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(LogFragment.TaskViewHolder holder, int position) {
            Task task = list.get(position);
            holder.name.setText(task.getName());
            holder.timeSpent.setText(task.getFormattedTimeSpent());
            holder.progressBar.setMax(task.getGoal());
            holder.progressBar.setProgress(task.getTimeSpent());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
