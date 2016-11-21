package com.momenta_app;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

/**
 * Unit Test for class task
 */
public class TaskTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    public final String TASK_NAME = "TASK_NAME";

    @Test
    public void testTaskFormattedGoal() {
        Task task = new Task(TASK_NAME, 60, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        assertEquals("1H", task.getFormattedGoal());

        task = new Task(TASK_NAME, 0, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        assertEquals("", task.getFormattedGoal());

        task = new Task("Test task", 61, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        assertEquals("1H 1M", task.getFormattedGoal());
    }

    @Test
    public void testAddMinute() {
        Task task = new Task(TASK_NAME, 0, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        task.logTimeSpent(310, null);
        assertEquals(310, task.getTimeSpent());

        task = new Task(TASK_NAME, 30, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        task.logTimeSpent(30, null);
        assertEquals(30, task.getTimeSpent());

        task = new Task(TASK_NAME, 61, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        task.logTimeSpent(25, null);
        assertEquals(25, task.getTimeSpent());
    }

    @Test
    public void testNegativeNumbers() {
        exception.expect(IllegalArgumentException.class);
        new Task(TASK_NAME, -1, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());

        Task task = new Task(TASK_NAME, -0, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        exception.expect(IllegalArgumentException.class);
        task.logTimeSpent(-30, null);
    }

    @Test
    public void testGetGoalTime() {
        Task task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
        task.setGoal(434);
        assertEquals(434, task.getGoal());
    }

    @Test
    public void testDeadlineCalendarAccessModifiers() {
        Calendar cal = Calendar.getInstance();
        Task task = new Task(TASK_NAME, 198, cal,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());

        cal.set( 2022, 11, 10);
        task.setDeadlineValue(cal);
        assertEquals( cal, task.getDeadlineValue());

        cal.set( 1850, 1, 1);
        task.setDeadlineValue(cal);
        assertEquals( cal, task.getDeadlineValue());

        cal.set(1990, 12, 31);
        task.setDeadlineValue(cal);
        assertEquals(cal, task.getDeadlineValue());

        cal.set(3098, 6, 30);
        task.setDeadlineValue(cal);
        assertEquals( cal, task.getDeadlineValue());
    }

    @Test
    public void testDateCreatedAccessModifiers() {
        Calendar cal = Calendar.getInstance();
        cal.set(2022, 11, 10);
        Task task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                cal.getTimeInMillis(), Calendar.getInstance());
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());

        cal.set(1850, 1, 1);
        task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                cal.getTimeInMillis(), Calendar.getInstance());
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());

        cal.set( 1990, 12, 31);
        task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                cal.getTimeInMillis(), Calendar.getInstance());
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());

        cal.set( 3098, 6, 30);
        task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                cal.getTimeInMillis(), Calendar.getInstance());
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());
    }

    @Test
    public void testLastModifiedAccessModifiers() {
        Calendar cal = Calendar.getInstance();
        Task task = new Task(TASK_NAME, 198, cal,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());

        cal.set( 2022, 11, 10);
        task.setLastModifiedValue(cal);
        assertEquals( cal, task.getLastModifiedValue());

        cal.set( 1850, 1, 1);
        task.setLastModifiedValue(cal);
        assertEquals( cal, task.getLastModifiedValue());

        cal.set( 1990, 12, 31);
        task.setLastModifiedValue(cal);
        assertEquals( cal, task.getLastModifiedValue());

        cal.set( 3098, 6, 30);
        task.setLastModifiedValue(cal);
        assertEquals( cal, task.getLastModifiedValue());
    }

    @Test
    public void testPriorityPriorityAccessModifiers() {
        Task task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());

        task.setPriorityValue(Task.Priority.VERY_LOW);
        assertEquals(Task.Priority.VERY_LOW, task.getPriorityValue());

        task.setPriorityValue(Task.Priority.LOW);
        assertEquals(Task.Priority.LOW, task.getPriorityValue());

        task.setPriorityValue(Task.Priority.MEDIUM);
        assertEquals(Task.Priority.MEDIUM, task.getPriorityValue());

        task.setPriorityValue(Task.Priority.HIGH);
        assertEquals(Task.Priority.HIGH, task.getPriorityValue());

        task.setPriorityValue(Task.Priority.VERY_HIGH);
        assertEquals(Task.Priority.VERY_HIGH, task.getPriorityValue());
    }

}
