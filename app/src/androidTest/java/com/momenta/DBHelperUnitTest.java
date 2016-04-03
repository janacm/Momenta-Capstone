package com.momenta;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Unit Test for the DBHelper class
 */
@RunWith(AndroidJUnit4.class)
public class DBHelperUnitTest {

    DBHelper db;
    private final static Calendar constantCalendar = Calendar.getInstance();
    ArrayList<Task> setupTaskList = new ArrayList<>();
    ArrayList<Long> setupIdList = new ArrayList<>();

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        Context ctx = instrumentation.getTargetContext();
        db = DBHelper.getInstance(ctx);

        tearDown();
        Calendar deadline = Calendar.getInstance();
        deadline.setTimeInMillis(constantCalendar.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.HOURS));
        setupTaskList.add(new Task("Task 1", 400, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar));
        setupTaskList.add(new Task("Task 2", 500, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar));
        setupTaskList.add(new Task("Task 3", 600, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar));
        setupTaskList.add(new Task("Task 4", 500, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar));

        for (Task t : setupTaskList) {
            setupIdList.add( db.insertTask(t) );
        }
    }

    @After
    public void tearDown() {
        db.getReadableDatabase().delete(DBHelper.SAMPLE_TABLE, null, null);
    }

    @Test
    public void testInsertTaskAndGetTask() {
        String taskName = "Task Name"; int duration = 12;
        Calendar deadline = Calendar.getInstance();
        deadline.setTimeInMillis(deadline.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.HOURS));
        Calendar dateCreated = Calendar.getInstance();
        dateCreated.setTimeInMillis( dateCreated.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(20, TimeUnit.HOURS));
        Calendar lastModified = Calendar.getInstance();
        lastModified.setTimeInMillis(lastModified.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(50, TimeUnit.DAYS));


        long id = db.insertTask(new Task(taskName, duration, deadline,
                dateCreated.getTimeInMillis(), lastModified));

        Task taskAdded = db.getTask((int) id);
        assertThat(id, greaterThan(0l));
        assertEquals(taskName, taskAdded.getName());
        assertEquals(duration,taskAdded.getDuration());
        assertEquals(deadline,taskAdded.getDeadline());
        assertEquals(dateCreated.getTimeInMillis(), taskAdded.getDateCreated());
        assertEquals(lastModified, taskAdded.getLastModified());
    }

    @Test
    public void testUpdateTask() {
        String taskName = "Task Name"; int duration = 12;
        Calendar deadline = Calendar.getInstance();
        Calendar dateCreated = Calendar.getInstance();
        Calendar lastModified = Calendar.getInstance();

        Task taskExpected = new Task(taskName, duration, deadline, dateCreated.getTimeInMillis(), lastModified);
        Long id = db.insertTask(taskExpected);
        taskExpected.setId(id.intValue());

        deadline.setTimeInMillis(deadline.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.HOURS));
        taskExpected.setDeadline(deadline);
        lastModified.setTimeInMillis(lastModified.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(50, TimeUnit.DAYS));
        taskExpected.setLastModified(lastModified);


        db.updateTask(taskExpected);
        Task taskActual = db.getTask(taskExpected.getId());

        assertEquals(taskExpected.getName(), taskActual.getName());
        assertEquals(taskExpected.getDuration(), taskActual.getDuration());
        assertEquals(taskExpected.getDeadline(), taskActual.getDeadline());
        assertEquals(taskExpected.getLastModified(), taskActual.getLastModified());
    }

    @Test
    public void testGetAllTasks() {
        HashMap<String, Task> expectedMap = new HashMap<>();

        Calendar deadline = Calendar.getInstance();
        deadline.setTimeInMillis(constantCalendar.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.HOURS));
        Task task1 = new Task("Task 1", 400, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar);
        expectedMap.put(task1.getName(), task1);

        Task task2 = new Task("Task 2", 500, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar);
        expectedMap.put(task2.getName(), task2);

        Task task3 = new Task("Task 3", 600, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar);
        expectedMap.put(task3.getName(), task3);

        Task task4 = new Task("Task 4", 500, deadline,
                constantCalendar.getTimeInMillis(), constantCalendar);
        expectedMap.put(task4.getName(), task4);


        ArrayList<Task> actualList= (ArrayList)db.getAllTasks();

        Iterator iterator = actualList.iterator();
        assertEquals(4, actualList.size());
        while ( iterator.hasNext() ) {
            Task actualTask = (Task)iterator.next();
            Task expectedTask  = expectedMap.get(actualTask.getName());
            expectedTask.equals(actualTask);
        }
    }

    @Test
    public void testDeleteTask() {

        for (Long l : setupIdList) {
            db.deleteTask(l.intValue());
            assertNull(db.getTask(l.intValue()) );
        }

    }


}
