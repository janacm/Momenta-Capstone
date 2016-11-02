package com.momenta;

import android.content.Context;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Blueprint of a user activity.
 * TODO Format class & look at naming.
 */
public class Task {

    public enum Priority{VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW}
    public static final String DATE_FORMAT = "MMMM dd, yyyy";
    private String id;
    private String name;
    private int goal;
    private int timeSpent;
    private Calendar deadline = Calendar.getInstance();
    private Calendar lastModified = Calendar.getInstance();
    private long dateCreated;
    private Priority priority;
    private ArrayList<String> team;

    //Firebase fields
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String GOAL = "goal";
    public static final String TIME_SPENT = "timeSpent";
    public static final String DEADLINE = "deadline";
    public static final String LAST_MODIFIED = "lastModified";
    public static final String DATE_CREATED = "dateCreated";
    public static final String PRIORITY = "priority";
    public static final String TEAM = "team";

    /**
     * Empty constructor used by Firebase.
     */
    public Task () {
    }

    /**
     * Creates a task with name, goal and deadline
     * @param name the name of the task
     * @param goal the goal in minutes of the task
     * @param deadline the deadline of the task
     * @param dateCreated the time the task was created
     * @param lastModified the last time the activity was modified
     */
    public Task (String name, int goal, Calendar deadline,
                 long dateCreated, Calendar lastModified) {
        this.name = name; this.goal = 0; timeSpent =0;
        priority = Priority.MEDIUM;
        setGoal(goal);
        setDeadlineValue(deadline);
        this.dateCreated = dateCreated;
        setLastModifiedValue(lastModified);
    }

    /**
     * Creates a task with its ID, name and goal
     * @param id the id of the task
     * @param name the name of the task
     * @param goal the goal in minutes of the task
     * @param deadline the deadline of the task
     * @param dateCreated the time the task was created
     * @param lastModified the last time the activity was modified
     */
    public Task (String id, String name, int goal, Calendar deadline,
                 long dateCreated, Calendar lastModified, int timeSpent) {
        this(name, goal, deadline, dateCreated, lastModified);
        this.id = id;
        this.timeSpent = timeSpent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Used to get the goal of this task in minutes
     * @return Integer value of the total time of this task in minutes.
     */
    public int getGoal() {
        return goal;
    }

    /**
     * Used the set the goal of this task in minutes.
     * @param minutes the new value of the task goal in minutes
     */
    public void setGoal(int minutes ) {
        goal = minutes;
    }

    /**
     * Used to get the time spent on a task in minutes
     * @return the time spent on a task in minutes
     */
    public int getTimeSpent(){
        return timeSpent;
    }

    /**
     * Used by Firebase to set the time spent
     * @param timeSpent the time spent
     */
    public void setTimeSpent(int timeSpent){
        this.timeSpent = timeSpent;
    }

    /**
     * Used by Firebase to get the priority to the Task
     * @return The String value of the Priority
     */
    public String getPriority() {
        return priority.toString();
    }

    /**
     * Getter method of priority
     * @return The Priority of the Task
     */
    @Exclude
    public Priority getPriorityValue() {
        return priority;
    }

    /**
     * Used by Firebase to set Priority
     * @param priority the priority of the task
     */
    public void setPriority(String priority) {
        this.priority = Priority.valueOf(priority);
    }

    /**
     * Setter method for the priority
     * @param priority the priority to be set
     */
    public void setPriorityValue(Priority priority) {
        this.priority = priority;
    }

    /**
     * Used by Firebase to get the deadline
     * @return the deadline of the task
     */
    public long getDeadline() {
        return deadline.getTimeInMillis();
    }

    /**
     * Getter method for deadline
     * @return the deadline for the task
     */
    @Exclude
    public Calendar getDeadlineValue() {
        return deadline;
    }

    /**
     * Used by Firebase to set deadline
     * @param deadline the deadline of the task
     */
    public void setDeadline(Long deadline) {
        this.deadline.setTimeInMillis( deadline );
    }

    /**
     * Setter method for the deadline of the task
     * @param deadline the new deadline to be set
     */
    @Exclude
    public void setDeadlineValue(Calendar deadline) {
        setDeadline( deadline.getTimeInMillis() );
    }

    /**
     * Used by Firebase to get the last modified
     * @return
     */
    public long getLastModified() {
        return lastModified.getTimeInMillis();
    }

    /**
     * Getter method for last modiifed
     * @return the lastModified
     */
    @Exclude
    public Calendar getLastModifiedValue() {
        return lastModified;
    }

    /**
     * Used by Firebase to set Priority
     * @param lastModified the last modified date of the task
     */
    public void setLastModified(long lastModified) {
        this.lastModified.setTimeInMillis( lastModified );
    }

    /**
     * Setter Method for last modified
     * @param lastModified the lastModified to be set
     */
    @Exclude
    public void setLastModifiedValue(Calendar lastModified) {
        setLastModified( lastModified.getTimeInMillis() );
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Returns the deadline in the format MMMM dd, YYYY.
     * Must check deadline is not null first using the getDeadlineValue() method.
     * @return The string value of the deadline date,
     *         empty string "" if the deadline is null.
     */
    @Exclude
    public String getFormattedDeadline() {
        if ( deadline != null ) {
            return Task.getDateFormat(deadline);
        } else {
            return "";
        }
    }

    /**
     * Used to get the taskHour and taskMinutes values in a string
     * @return String in format 0H 00M
     * TODO is method useful?
     */
    @Exclude
    public String getFormattedGoal() {
        int taskMinutes = goal, taskHours = 0;

        if ( ! (taskMinutes < 60) ) {
            taskHours = taskMinutes/60;
            taskMinutes = taskMinutes % 60;
        }

        if ( taskHours >0 && taskMinutes>0 ) {
            return taskHours + "H " + taskMinutes + "M";
        } else if ( taskHours ==0 && taskMinutes>0 ) {
            return taskMinutes + "M";
        } else if ( taskHours >0 ) {
            return taskHours + "H";
        } else {
            return "";
        }
    }

    /**
     * Used to get the taskHour and taskMinutes values in a string
     * @return String in format 0H 00M
     */
    @Exclude
    public String getFormattedTimeSpent() {
        int taskMinutes = timeSpent, taskHours = 0;

        if ( taskMinutes == 0 ) {
            return "0M";
        }

        if ( ! (taskMinutes < 60) ) {
            taskHours = taskMinutes/60;
            taskMinutes = taskMinutes % 60;
        }

        if ( taskHours >0 && taskMinutes>0 ) {
            return taskHours + "H " + taskMinutes + "M";
        } else if ( taskHours ==0 && taskMinutes>0 ) {
            return taskMinutes + "M";
        } else if ( taskHours >0 ) {
            return taskHours + "H";
        } else {
            return "";
        }
    }

    /**
     * Adds a team member to the task
     * @param teamMember the team member to be added
     */
    @Exclude
    public void addTeamMember(String teamMember) {
        if (team == null) {
            team = new ArrayList<>();
        }
        team.add(teamMember);
    }

    /**
     * Adds multiple team members to the task
     * @param teamMembers the list of team members to be added
     */
    @Exclude
    public void addTeamMembers(ArrayList<String> teamMembers) {
        if (team == null) {
            team = new ArrayList<>();
        }
        for (String member : teamMembers) {
            if ( !team.contains(member) ) {
                team.add(member);
            }
        }
    }

    /**
     * Retrieves all the team members from the task
     * @return the team members of the task
     */
    @Exclude
    public ArrayList<String> getTeamMembers() {
        if (team == null) {
            team = new ArrayList<>();
        }
        return team;
    }


    /**
     * Used to add time to the task
     * @param minutes the time in minutes to be added to the task
     * @param context
     */
    public void logTimeSpent(int minutes, Context context) {
        if ( minutes < 0 ) {
            throw new IllegalArgumentException("Goal cannot be negative: " + minutes);
        }
        timeSpent += minutes;
        lastModified = Calendar.getInstance();

        if (context != null) {
        }
    }

    /**
     * Convenience method to format Calendar objects to strings.
     * @param calendar The calendar object with to be formatted
     * @return The formatted String value for the Calendar.
     */
    public static String getDateFormat(Calendar calendar) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.CANADA).format(calendar.getTime());
    }

    /**
     * Convenience method to map all the fields to Firebase
     * @return map containing Firebase keys & values
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ID, getId());
        result.put(NAME, getName());
        result.put(GOAL, getGoal());
        result.put(DEADLINE, getDeadline());
        result.put(DATE_CREATED, getDateCreated());
        result.put(LAST_MODIFIED, getLastModified());
        result.put(TIME_SPENT, getTimeSpent());
        result.put(PRIORITY, getPriority());
        result.put(TEAM, getTeamMembers());

        return result;
    }

    /**
     * Equals method for comparing if two tasks are the same.
     * @param another The task object to be compared with
     * @return true if the tasks are the same and false otherwise
     */
    public boolean equals(Task another) {
        return this.getName().equals(another.getName())
                && this.getGoal() == another.getGoal()
                && this.getDeadlineValue().getTimeInMillis() == another.getDeadlineValue().getTimeInMillis()
                && this.getLastModifiedValue().getTimeInMillis() == another.getLastModifiedValue().getTimeInMillis()
                && this.getDateCreated() == another.getDateCreated()
                && this.getPriority().equals(another.getPriority());
    }

}
