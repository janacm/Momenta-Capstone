package com.momenta;

import java.util.ArrayList;
import java.util.List;

/**
 * Blueprint of a user activity.
 */
public class Task {

    private int id;
    private String name;
    private int hours;
    private int minutes;

    /**
     * Creates a task with hours and minutes equals zero.
     * @param name the name of the task
     */
    public Task ( String name ) {
        this.name = name;
        hours = 0;
        minutes = 0;
    }

    /**
     * Creates a task with name and duration
     * @param name the name of the task
     * @param duration the duration in minutes of the task
     */
    public Task ( String name, int duration  ) {
        //TODO Can this value be negative?
        if ( duration < 0 ) {
            throw new IllegalArgumentException("Duration cannot be negative: " + duration);
        }
        this.name = name;
        hours = 0; minutes = 0;
        addMinute(duration);
    }

    /**
     * Creates a task with its ID, name and duration
     * @param id the id of the task
     * @param name the name of the task
     * @param duration the duration in minutes of the task
     */
    public Task (int id, String name, int duration) {
        this.id = id;
        this.name = name;
        //TODO Can this value be negative?
        if ( duration < 0 ) {
            throw new IllegalArgumentException("Duration cannot be negative: " + duration);
        }
        hours = 0; minutes = 0;
        addMinute(duration);
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
     * Used to get the hours and minute values in a string
     * @return String in format 0h 00m
     */
    public String getTimeString() {
        if ( hours>0 && minutes>0 ) {
            return hours + "h " + minutes + "m";
        } else if ( hours==0 && minutes>0 ) {
            return minutes + "m";
        } else if ( hours>0 ) {
            return hours + "h";
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
    public int getTime() {
        return (this.hours*60) + minutes;
    }

    public int getHours(){
        return hours;
    }

    public int getMinutes(){
        return minutes;
    }

}
