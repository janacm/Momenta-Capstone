package com.momenta_app;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Unit Test for class task
 */
public class TaskTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    public final String TASK_NAME = "TASK_NAME";

    @Test
    public void testFormattedTimeSpentWithNoTimeSpent() {
        Task task = new Task();
        Task t1 = new Task();
        t1.setTimeSpent(0);

        assertEquals("0M", task.getFormattedTimeSpent());
        assertEquals("0M", t1.getFormattedTimeSpent());
    }

    @Test
    public void testFormattedTimeSpentWithNoMinutes() {
        Task task = new Task();
        task.setTimeSpent(540);
        Task t1 = new Task();
        t1.setTimeSpent(120);

        assertEquals("9H", task.getFormattedTimeSpent());
        assertEquals("2H", t1.getFormattedTimeSpent());
    }

    @Test
    public void testFormattedTimeSpentWithNoHours() {
        Task task = new Task();
        task.setTimeSpent(1);
        Task t1 = new Task();
        t1.setTimeSpent(59);
        Task t2 = new Task();
        t2.setTimeSpent(30);

        assertEquals("1M", task.getFormattedTimeSpent());
        assertEquals("59M", t1.getFormattedTimeSpent());
        assertEquals("30M", t2.getFormattedTimeSpent());
    }

    @Test
    public void testFormattedTimeSpentWithHoursAndMinutes() {
        Task task = new Task();
        task.setTimeSpent(61);
        Task t1 = new Task();
        t1.setTimeSpent(119);
        Task t2 = new Task();
        t2.setTimeSpent(160);

        assertEquals("1H 1M", task.getFormattedTimeSpent());
        assertEquals("1H 59M", t1.getFormattedTimeSpent());
        assertEquals("2H 40M", t2.getFormattedTimeSpent());
    }

    @Test
    public void testTaskFormattedGoal() {
        Task task = new Task(TASK_NAME, 60, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        assertEquals("1H", task.getFormattedGoal());

        task = new Task(TASK_NAME, 0, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        assertEquals("", task.getFormattedGoal());

        task = new Task("Test task", 61, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        assertEquals("1H 1M", task.getFormattedGoal());
    }

    @Test
    public void testFormattedDeadline() {
        Calendar deadline = Calendar.getInstance();

        Task t = new Task();
        t.setDeadlineValue(deadline);
        String expectedDeadlineFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA)
                .format(deadline.getTime());

        assertEquals(expectedDeadlineFormat, t.getFormattedDeadline());
    }

    @Test
    public void testAddMinute() {
        Task task = new Task(TASK_NAME, 0, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        task.setTimeSpent(310);
        assertEquals(310, task.getTimeSpent());

        task = new Task(TASK_NAME, 30, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        task.setTimeSpent(30);
        assertEquals(30, task.getTimeSpent());

        task = new Task(TASK_NAME, 61, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        task.setTimeSpent(25);
        assertEquals(25, task.getTimeSpent());
    }

    @Test
    public void testGetGoalTime() {
        Task task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        task.setGoal(434);
        assertEquals(434, task.getGoal());
    }

    @Test
    public void testDeadlineCalendarAccessModifiers() {
        Calendar cal = Calendar.getInstance();
        Task task = new Task(TASK_NAME, 198, cal,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);

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
                cal.getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());

        cal.set(1850, 1, 1);
        task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                cal.getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());

        cal.set( 1990, 12, 31);
        task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                cal.getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());

        cal.set( 3098, 6, 30);
        task = new Task(TASK_NAME, 198, Calendar.getInstance(),
                cal.getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);
        assertEquals(cal.getTimeInMillis(), task.getDateCreated());
    }

    @Test
    public void testLastModifiedAccessModifiers() {
        Calendar cal = Calendar.getInstance();
        Task task = new Task(TASK_NAME, 198, cal,
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);

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
                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance(), Task.Type.DEADLINE);

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

    @Test
    public void testToMap() {
        int goal = 300;
        int timeSpent = 120;
        String id = "1980";
        String name = "Task Name";
        String owner = "Owner Owner";
        String lastModifiedBy = "Last Modified By";
        Calendar deadline = Calendar.getInstance();
        Calendar dateCreated = Calendar.getInstance();
        Calendar lastModified = Calendar.getInstance();
        Task.Priority priority = Task.Priority.HIGH;
        ArrayList<String> teamMembers = new ArrayList();
        teamMembers.add(owner);


        Task task = new Task(id, name, goal, deadline, dateCreated.getTimeInMillis(),
                lastModified, timeSpent);
        task.setOwner(owner);
        task.setLastModifiedBy(lastModifiedBy);
        task.setPriorityValue(priority);
        task.addTeamMembers(teamMembers);
        task.addTeamMember(name);

        Map map = task.toMap();
        teamMembers.add(name);

        assertEquals(id, map.get(Task.ID));
        assertEquals(name, map.get(Task.NAME));
        assertEquals(goal, map.get(Task.GOAL));
        assertEquals(deadline.getTimeInMillis(), map.get(Task.DEADLINE));
        assertEquals(dateCreated.getTimeInMillis(), map.get(Task.DATE_CREATED));
        assertEquals(owner, map.get(Task.OWNER));
        assertEquals(lastModified.getTimeInMillis(), map.get(Task.LAST_MODIFIED));
        assertEquals(lastModifiedBy, map.get(Task.LAST_MODIFIED_BY));
        assertEquals(timeSpent, map.get(Task.TIME_SPENT));
        assertEquals(priority.toString(), map.get(Task.PRIORITY).toString());
        assertEquals(teamMembers, map.get(Task.TEAM));
    }

    @Test
    public void testAddTeamMembers() {
        String owner = "Owner Owner";
        String tony = "Tony";
        String adam = "Adam";

        ArrayList<String> teamMembers = new ArrayList();
        teamMembers.add(owner);
        teamMembers.add(tony);
        teamMembers.add(adam);

        Task task = new Task();
        task.addTeamMembers(teamMembers);

        assertEquals(teamMembers, task.getTeamMembers());
    }

    @Test
    public void testAddTeamMember() {
        String owner = "Owner Owner";
        String tony = "Tony";
        String adam = "Adam";

        ArrayList<String> teamMembers = new ArrayList();
        teamMembers.add(owner);
        teamMembers.add(tony);
        teamMembers.add(adam);

        Task task = new Task();
        task.addTeamMember(owner);
        task.addTeamMember(tony);
        task.addTeamMember(adam);

        assertEquals(teamMembers, task.getTeamMembers());
    }

}
