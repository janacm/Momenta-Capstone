package com.momenta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Blueprint of a user activity.
 */
public class Task {

    public enum Priority{VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW}
    public static final String DATE_FORMAT = "MMMM dd, yyyy";
    private int id;
    private String name;
    private int goal;
    private int timeSpent;
    private Calendar deadline = Calendar.getInstance();
    private Calendar lastModified = Calendar.getInstance();
    private long dateCreated;
    private Priority priority;

    /**
     * Creates a task with name, duration and deadline
     * @param name the name of the task
     * @param duration the duration in minutes of the task
     * @param deadline the deadline of the task
     * @param dateCreated the time the task was created
     * @param lastModified the last time the activity was modified
     */
    public Task (String name, int duration, Calendar deadline,
                 long dateCreated, Calendar lastModified) {
        this.name = name; goal = 0; timeSpent =0;
        priority = Priority.MEDIUM;
        setGoalInMinutes(duration);
        setDeadline(deadline);
        this.dateCreated = dateCreated;
        setLastModified(lastModified);
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
    public Task (int id, String name, int goal, Calendar deadline,
                 long dateCreated, Calendar lastModified, int timeSpent) {
        this(name, goal, deadline, dateCreated, lastModified);
        this.id = id;
        this.timeSpent = timeSpent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Used to get the taskHour and taskMinutes values in a string
     * @return String in format 0H 00M
     */
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
    public String getFormattedTimeSpent() {
        int taskMinutes = timeSpent, taskHours = 0;

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
     * Used the set the goal of this task in minutes.
     * @param minutes the new value of the task goal in minutes
     */
    public void setGoalInMinutes(int minutes ) {
        goal = minutes;
    }

    /**
     * Used to get the goal of this task in minutes
     * @return Integer value of the total time of this task in minutes.
     */
    public int getGoalInMinutes() {
        return goal;
    }

    /**
     * Used to add time to the task
     * @param minutes the time in minutes to be added to the task
     */
    public void addTimeInMinutes(int minutes) {
        if ( minutes < 0 ) {
            throw new IllegalArgumentException("Goal cannot be negative: " + minutes);
        }
        timeSpent += minutes;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    /**
     * Returns the deadline in the format MMMM dd, YYYY.
     * Must check deadline is not null first using the getDeadline() method.
     * @return The string value of the deadline date,
     *         empty string "" if the deadline is null.
     */
    public String getFormattedDeadline() {
        if ( deadline != null ) {
            return Task.getDateFormat(deadline);
        } else {
            return "";
        }
    }

    public void setDeadline(Calendar deadline) {
        this.deadline.setTimeInMillis( deadline.getTimeInMillis() );
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified.setTimeInMillis( lastModified.getTimeInMillis() );
    }

    public long getDateCreated() {
        return dateCreated;
    }

    //Method used to set time spent on a task
    public void setTimeSpent(int timeSpent){
        this.timeSpent = timeSpent;
    }

    /**
     * Used to get the time spent on a task in minutes
     * @return the time spent on a task in minutes
     */
    public int getTimeSpent(){
        return timeSpent;
    }

    /**
     * Removes none digits from a string
     * @param input the string to remove non digits from
     * @return input string with all the none digits removed.
     */
    public static String stripNonDigits(final String input ){
        final StringBuilder sb = new StringBuilder( input.length() );
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
        }
        return sb.toString();
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
     * Equals method for comparing if two tasks are the same.
     * @param another The task object to be compared with
     * @return true if the tasks are the same and false otherwise
     */
    public boolean equals(Task another) {
        return this.getName().equals(another.getName())
                && this.getGoalInMinutes() == another.getGoalInMinutes()
                && this.getDeadline().getTimeInMillis() == another.getDeadline().getTimeInMillis()
                && this.getLastModified().getTimeInMillis() == another.getLastModified().getTimeInMillis()
                && this.getDateCreated() == another.getDateCreated()
                && this.getPriority() == another.getPriority();
    }

}
