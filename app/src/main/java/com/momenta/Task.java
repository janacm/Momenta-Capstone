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
//    private int taskHours;
//    private int taskMinutes;
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
        this.name = name; goal = 0;
//        taskHours = 0; this.taskMinutes = 0;
        priority = Priority.MEDIUM;
        addTimeInMinutes(duration);
        setDeadline(deadline);
        this.dateCreated = dateCreated;
        setLastModified(lastModified);
        timeSpent = 100;
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
                 long dateCreated, Calendar lastModified) {
        this(name, goal, deadline, dateCreated, lastModified);
        this.id = id;
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
     * Used the set the time value of this task in minutes.
     * @param minutes the new value of the task goal in minutes
     */
    public void setTimeInMinutes( int minutes ) {
//        taskHours = 0; this.taskMinutes = 0;
        goal =0;
        addTimeInMinutes(minutes);
    }

    /**
     * Used to add time to the task
     * @param minutes the time in minutes to be added to the task
     */
    public void addTimeInMinutes(int minutes) {
        if ( minutes < 0 ) {
            throw new IllegalArgumentException("Goal cannot be negative: " + minutes);
        }
//        this.taskMinutes += minutes;
//        if ( ! (this.taskMinutes < 60) ) {
//            this.taskHours += this.taskMinutes/60;
//            this.taskMinutes = this.taskMinutes % 60;
//        }
        goal += minutes;
    }

    /**
     * Used to get the goal of this task in minutes
     * @return Integer value of the total time of this task in minutes.
     */
    public int getGoalInMinutes() {
//        return (this.taskHours *60) + taskMinutes;
        return goal;
    }

//    /**
//     * Getter method for the hour field of the task
//     * @return the value of the hour field of the task.
//     */
//    public int getTaskHours(){
//        return taskHours;
//    }

//    /**
//     * Getter method for the minutes field of the task.
//     * @return the value of the minute field of the task.
//     */
//    public int getTaskMinutes(){
//        return taskMinutes;
//    }

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

    public int getProgressPercentage() {
        return (timeSpent / getGoalInMinutes()) * 100;
    }

//    /**
//     * Helper method to convert HHMM string to minutes
//     * @param string the time in HHMM format, should not only contain strings.
//     * @return Integer value of the time in minutes
//     */
//    public static int convertHourMinuteToMinute(String string) {
//        String temp = "00000" + stripNonDigits(string);
//        int minutes = Integer.valueOf( temp.substring( temp.length()-2, temp.length() ) );
//        int hour = Integer.valueOf( temp.substring( temp.length()-5, temp.length()-2 ) );
//        return (hour*60) + minutes;
//    }

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
