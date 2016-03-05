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

    public Task ( String name ) {
        this.name = name;
        hours = 1;
        minutes = 45;
    }

    public Task ( String name, int duration  ) {
        //TODO Can this value be negative?
        if ( duration < 0 ) {
            throw new IllegalArgumentException("Duration cannot be negative: " + duration);
        }
        this.name = name;
        hours = 0;
        minutes = 0;
        addMinute(duration);
    }

    public Task (int id, String name ) {
        this.id = id;
        this.name = name;
        hours = 1;
        minutes = 45;
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

    public String getDuration() {
        return hours + "h " + minutes + "m";
    }

    public void setDuration(int hours, int minutes) {
        //TODO Can either of these values be negative?
        if ( hours < 0 || minutes < 0 ) {
            throw new IllegalArgumentException("Duration cannot be negative: " + hours + " " + minutes);
        }
        this.hours = hours;
        this.minutes = minutes;
    }

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

    public int getHours(){
        return hours;
    }

    public int getMinutes(){
        return minutes;
    }

    //Method to create filler activites
    public static List<Task> createTasks() {
        List activites = new ArrayList<Task>();
        activites.add( new Task("Study for law exam") );
        activites.add( new Task("Go to the gym") );
        activites.add( new Task("Organize house") );
        return activites;
    }

}
