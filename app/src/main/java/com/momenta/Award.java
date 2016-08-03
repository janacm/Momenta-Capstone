package com.momenta;

import java.util.List;

/**
 * Created by Joe on 2016-07-30.
 * For Momenta-Capstone
 */
public class Award {

    private int id;
    private String name;
    private String description;
    private int currentProgress;
    private int currentLevel;
    private int maxLevel;
    private List<Integer> progressLimitForEachLevel;

    /**
     * Creates an award with basic details parameters
     *
     * @param name                      the name of the award
     * @param description               the description of the award
     * @param progressLimitForEachLevel the maximum limit that has to be reached at each level to progress to the each level
     */
    public Award(String name, String description, List<Integer> progressLimitForEachLevel) {
        this.name = name;
        this.description = description;
        this.currentProgress = 0;
        this.currentLevel = 0; //0 being the first level
        this.maxLevel = progressLimitForEachLevel.size();
        this.progressLimitForEachLevel = progressLimitForEachLevel;

    }

    /**
     * Creates an award with its ID and other parameters
     *
     * @param name                      the name of the award
     * @param description               the description of the award
     * @param progressLimitForEachLevel the maximum limit that has to be reached at each level to progress to the each level
     */
    public Award(int id, String name, String description, List<Integer> progressLimitForEachLevel) {
        this(name,  description, progressLimitForEachLevel);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void increaseProgress() {
        this.currentProgress ++;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void levelUp() {
        this.currentLevel ++;
    }



}
