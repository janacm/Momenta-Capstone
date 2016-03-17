package com.momenta;

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

    @Test
    public void testTaskDuration() {
        Task task = new Task("Test task", 60, Calendar.getInstance());
        assertEquals(1, task.getHours());
        assertEquals(0, task.getMinutes());

        task = new Task("Test task", 0, Calendar.getInstance());
        assertEquals(0, task.getHours());
        assertEquals(0, task.getMinutes());

        task = new Task("Test task", 61, Calendar.getInstance());
        assertEquals(1, task.getHours());
        assertEquals(1, task.getMinutes());
    }

    @Test
    public void testAddMinute() {
        Task task = new Task("Test task", 0, Calendar.getInstance());
        task.addMinute(310);
        assertEquals(5, task.getHours());
        assertEquals(10, task.getMinutes());

        task = new Task("Test task", 30, Calendar.getInstance());
        task.addMinute(30);
        assertEquals(1, task.getHours());
        assertEquals(0, task.getMinutes());

        task = new Task("Test task", 61, Calendar.getInstance());
        task.addMinute(25);
        assertEquals(1, task.getHours());
        assertEquals(26, task.getMinutes());
    }

    @Test
    public void testNegativeNumbers() {
        exception.expect(IllegalArgumentException.class);
        new Task("Test task", -1, Calendar.getInstance());

        Task task = new Task("Test task", -0, Calendar.getInstance());
        exception.expect(IllegalArgumentException.class);
        task.addMinute(-30);

        task = new Task("Test task", 60, Calendar.getInstance());
        exception.expect(IllegalArgumentException.class);
        task.setDuration(-30, 40);

        task = new Task("Test task", 60, Calendar.getInstance());
        exception.expect(IllegalArgumentException.class);
        task.setDuration(30, -40);
    }

    @Test
    public void testGetTime() {
        Task task = new Task("Test task", 198, Calendar.getInstance());
        task.setDuration(7, 14);
        assertEquals(434, task.getTime());

        task.addMinute(60);
        assertEquals(494, task.getTime());

        assertEquals(8, task.getHours());
        assertEquals(14, task.getMinutes());
    }
}
