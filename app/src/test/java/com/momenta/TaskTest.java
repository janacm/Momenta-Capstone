package com.momenta;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;

/**
 * Unit Test for class task
 */
public class TaskTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testTaskDuration() {
        Task task = new Task("Test task", 60);
        assertEquals(1, task.getHours());
        assertEquals(0, task.getMinutes());

        task = new Task("Test task", 0);
        assertEquals(0, task.getHours());
        assertEquals(0, task.getMinutes());

        task = new Task("Test task", 61);
        assertEquals(1, task.getHours());
        assertEquals(1, task.getMinutes());
    }

    @Test
    public void testAddMinute() {
        Task task = new Task("Test task", 0);
        task.addMinute(310);
        assertEquals(5, task.getHours());
        assertEquals(10, task.getMinutes());

        task = new Task("Test task", 30);
        task.addMinute(30);
        assertEquals(1, task.getHours());
        assertEquals(0, task.getMinutes());

        task = new Task("Test task", 61);
        task.addMinute(25);
        assertEquals(1, task.getHours());
        assertEquals(26, task.getMinutes());
    }

    @Test
    public void testNegativeNumbers() {
        exception.expect(IllegalArgumentException.class);
        new Task("Test task", -1);

        Task task = new Task("Test task", -0);
        exception.expect(IllegalArgumentException.class);
        task.addMinute(-30);

        task = new Task("Test task", 60);
        exception.expect(IllegalArgumentException.class);
        task.setDuration(-30, 40);

        task = new Task("Test task", 60);
        exception.expect(IllegalArgumentException.class);
        task.setDuration(30, -40);
    }

    @Test
    public void testGetTime() {
        Task task = new Task("Test task", 198);
        task.setDuration(7, 14);
        assertEquals(434, task.getTime());

        task.addMinute(60);
        assertEquals(494, task.getTime());

        assertEquals(8, task.getHours());
        assertEquals(14, task.getMinutes());
    }
}
