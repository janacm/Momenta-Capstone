package com.momenta;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

/**
 * Created by joesi on 2016-02-16.
 */
@RunWith(AndroidJUnit4.class)
public class AddActivityTest{
    @Rule public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    DBHelper db;
    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        Context ctx = instrumentation.getTargetContext();
        db = DBHelper.getInstance(ctx);

    }
    @Test
    public void addActivityDBTesting(){
        String taskName = "TaskName";
        int duration = 12;
        Calendar deadline = Calendar.getInstance();
        long id = db.insertTask(new Task(taskName, duration, deadline,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance()));//TODO Update test case: Task constructor modified

        Task taskAdded = db.getTask((int) id);
        assertEquals(taskName,taskAdded.getName());
        assertEquals(duration,taskAdded.getDuration());
        assertEquals(deadline,taskAdded.getDeadline());
    }

    @Test
    public void addActivity(){
       /* onView(withId(R.id.new_activity_edit_text)).perform(typeText("Test activity"));
        onView(withId(R.id.new_activity_add_button)).perform(click());*/
    }
}