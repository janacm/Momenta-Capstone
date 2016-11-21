package com.momenta_app;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe on 2016-07-30.
 * For Momenta-Capstone
 */
public class Award {
    //class for awards
    //Firebase fields
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESC_1 = "description_1";
    public static final String DESC_2 = "description_2";
    public static final String CURRENT_PROGRESS = "currentProgress";
    public static final String CURRENT_LEVEL = "currentLevel";
    public static final String MAX_LEVEL = "maxLevel";
    public static final String PROGRESS_LIMIT_EACH_LEVEL = "progressLimitEachLevel";
    public static final String TASK_IDS = "taskIDs";

    private String id;
    private String name;
    private String description_1;
    private String description_2;
    private double currentProgress;
    private int currentLevel;
    private int maxLevel;
    private ArrayList<Integer> progressLimitEachLevel;
    private ArrayList<String> taskIDs;

    /**
     * Empty constructor used by Firebase.
     */
    public Award() {
    }

    /**
     * Creates an award with basic details parameters
     *
     * @param name                   the name of the award
     * @param description_1          the first part of the description of the award
     * @param description_2          the second part of the description of the award
     * @param progressLimitEachLevel the maximum limit that has to be reached at each level to progress to the each level
     */
    public Award(String name, String description_1, String description_2, ArrayList<Integer> progressLimitEachLevel, ArrayList<String> taskIDs) {
        this.name = name;
        this.description_1 = description_1;
        this.description_2 = description_2;
        this.currentProgress = 0;
        this.currentLevel = 0; //0 being the first level
        this.maxLevel = progressLimitEachLevel.size();
        this.progressLimitEachLevel = progressLimitEachLevel;
        this.taskIDs = taskIDs;

    }

    /**
     * Creates an award with its ID and other parameters
     *
     * @param name                   the name of the award
     * @param description_1          the first part of the description of the award
     * @param description_2          the second part of the description of the award
     * @param progressLimitEachLevel the maximum limit that has to be reached at each level to progress to the each level
     */
    public Award(String id, String name, String description_1, String description_2, ArrayList<Integer> progressLimitEachLevel, ArrayList<String> taskIDs) {
        this(name, description_1, description_2, progressLimitEachLevel, taskIDs);
        this.id = id;
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

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setCurrentProgress(double currentProgress) {
        this.currentProgress = currentProgress;
    }

    public double getCurrentProgress() {
        return currentProgress;
    }


    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public ArrayList<Integer> getProgressLimitEachLevel() {
        return progressLimitEachLevel;
    }

    public void setProgressLimitEachLevel(ArrayList<Integer> progressLimitEachLevel) {
        this.progressLimitEachLevel = new ArrayList<>(progressLimitEachLevel);
    }

    public ArrayList<String> getTaskIDs() {
        return taskIDs;
    }

    public void setTaskIDs(ArrayList<String> taskIDs) {
        this.taskIDs = taskIDs;
    }

    public String getDescription_1() {
        return description_1;
    }

    public void setDescription_1(String description_1) {
        this.description_1 = description_1;
    }

    public String getDescription_2() {
        return description_2;
    }

    public void setDescription_2(String description_2) {
        this.description_2 = description_2;
    }

    /**
     * Convenience method to map all the fields to Firebase
     *
     * @return map containing Firebase keys & values
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ID, getId());
        result.put(NAME, getName());
        result.put(DESC_1, getDescription_1());
        result.put(DESC_2, getDescription_2());
        result.put(CURRENT_PROGRESS, getCurrentProgress());
        result.put(CURRENT_LEVEL, getCurrentLevel());
        result.put(MAX_LEVEL, getMaxLevel());
        result.put(PROGRESS_LIMIT_EACH_LEVEL, getProgressLimitEachLevel());
        result.put(TASK_IDS, getTaskIDs());

        return result;
    }

}



