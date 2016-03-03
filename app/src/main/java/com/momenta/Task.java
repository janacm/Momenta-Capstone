package com.momenta;

/**
 * Blueprint of a user activity.
 */
public class Task {

    private int id;
    private String name;
    private double duration;

    public Task(int id, String name, double duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
