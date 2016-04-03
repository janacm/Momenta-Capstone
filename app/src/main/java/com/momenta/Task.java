package com.momenta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Blueprint of a user activity.
 */
public class Task {

    public enum Priority{VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW}
    private int id;
    private String name;
    private int hours;
    private int minutes;
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
        this.name = name; hours = 0; minutes = 0;
        priority = Priority.MEDIUM;
        addMinute(duration);
        setDeadline(deadline);
        this.dateCreated = dateCreated;
        setLastModified(lastModified);
    }

    /**
     * Creates a task with its ID, name and duration
     * @param id the id of the task
     * @param name the name of the task
     * @param duration the duration in minutes of the task
     * @param deadline the deadline of the task
     * @param dateCreated the time the task was created
     * @param lastModified the last time the activity was modified
     */
    public Task (int id, String name, int duration, Calendar deadline,
                 long dateCreated, Calendar lastModified) {
        this(name, duration, deadline, dateCreated, lastModified);
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
     * Used to get the goalHours and goalMinute values in a string
     * @return String in format 0H 00M
     */
    public String getTimeString() {
        if ( hours>0 && minutes>0 ) {
            return hours + "H " + minutes + "M";
        } else if ( hours==0 && minutes>0 ) {
            return minutes + "M";
        } else if ( hours>0 ) {
            return hours + "H";
        } else {
            return "";
        }
    }

    /**
     * Used to set the hours and minutes of the task
     * @param hours Sets the hour value of this task
     * @param minutes Sets the minute value of this task
     */
    public void setDuration(int hours, int minutes) {
        //TODO Can either of these values be negative?
        if ( hours < 0 || minutes < 0 ) {
            throw new IllegalArgumentException("Duration cannot be negative: " + hours + " " + minutes);
        }
        this.hours = hours;
        this.minutes = minutes;
    }

    public void setTimeInMinutes( int minutes ) {
        hours = 0; this.minutes = 0;
        addMinute(minutes);
    }

    /**
     * Used to add time to task
     * @param minutes the time in minutes to be added to the task
     */
    public void addMinute(int minutes) {
        //TODO Can this value be negative?
        if ( minutes < 0 ) {
            throw new IllegalArgumentException("Duration cannot be negative: " + minutes);
        }

        this.minutes += minutes;
        if ( ! (this.minutes < 60) ) {
            this.hours += this.minutes/60;
            this.minutes = this.minutes % 60;
        }
    }

    /**
     * Used to get the time of this task in minutes
     * @return the total time of this task in minutes.
     */
    public int getDuration() {
        return (this.hours*60) + minutes;
    }

    /**
     * Getter method for the hour field of the task
     * @return the value of the hour field of the task.
     */
    public int getHours(){
        return hours;
    }

    /**
     * Getter method for the minutes field of the task.
     * @return the value of the minute field of the task.
     */
    public int getMinutes(){
        return minutes;
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
            return new SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(deadline.getTime());
        } else {
            return "";
        }
    }

    public void setDeadline(Calendar deadline) {
        this.deadline.setTimeInMillis( deadline.getTimeInMillis() );
    }

    /**
     * Method to access the priority of the task.
     * @return The priority of the task
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Modifier for the priority of a task object
     * @param priority the new priority of the task.
     */
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

    /**
     * Helper method to convert HHMM string to minutes
     * @param string the time in HHMM format, should not only contain strings.
     * @return Integer value of the time in minutes
     */
    public static int convertHourMinuteToMinute(String string) {
        String temp = "00000" + stripNonDigits(string);
        int minutes = Integer.valueOf( temp.substring( temp.length()-2, temp.length() ) );
        int hour = Integer.valueOf( temp.substring( temp.length()-5, temp.length()-2 ) );
        return (hour*60) + minutes;
    }

    public boolean equals(Task another) {
        return this.getName().equals(another.getName())
                && this.getDuration() == another.getDuration()
                && this.getDeadline().getTimeInMillis() == another.getDeadline().getTimeInMillis()
                && this.getLastModified().getTimeInMillis() == another.getLastModified().getTimeInMillis()
                && this.getDateCreated() == another.getDateCreated()
                && this.getPriority() == another.getPriority();
    }

}
