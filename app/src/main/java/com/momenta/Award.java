package com.momenta;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joe on 2016-07-30.
 * For Momenta-Capstone
 */
public class Award {

    //Firebase fields
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESC_1 = "desc_1";
    public static final String DESC_2 = "desc_2";
    public static final String CURRENT_PROGRESS = "currentProgress";
    public static final String CURRENT_LEVEL = "currentLevel";
    public static final String MAX_LEVEL = "maxLevel";
    public static final String PROGRESS_LIMIT_EACH_LEVEL = "progressLimitEachLevel";

    private String id;
    private String name;
    private String description_1;
    private String description_2;
    private int currentProgress;
    private int currentLevel;
    private int maxLevel;
    private List<Integer> progressLimitEachLevel;
    /**
     * Empty constructor used by Firebase.
     */
    public Award () {
    }
    /**
     * Creates an award with basic details parameters
     *
     * @param name                      the name of the award
     * @param description_1             the first part of the description of the award
     * @param description_2             the second part of the description of the award
     * @param progressLimitEachLevel the maximum limit that has to be reached at each level to progress to the each level
     */
    public Award(String name, String description_1, String description_2,List<Integer> progressLimitEachLevel) {
        this.name = name;
        this.description_1 = description_1;
        this.description_2 = description_2;
        this.currentProgress = 0;
        this.currentLevel = 0; //0 being the first level
        this.maxLevel = progressLimitEachLevel.size();
        this.progressLimitEachLevel = progressLimitEachLevel;

    }

    /**
     * Creates an award with its ID and other parameters
     *
     * @param name                      the name of the award
     * @param description_1             the first part of the description of the award
     * @param description_2             the second part of the description of the award
     * @param progressLimitEachLevel the maximum limit that has to be reached at each level to progress to the each level
     */
    public Award(String id, String name, String description_1, String description_2, List<Integer> progressLimitEachLevel) {
        this(name,  description_1, description_2, progressLimitEachLevel);
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

    public String getDescription1() {
        return description_1;
    }

    public void setDescription1(String description_1) {
        this.description_1 = description_1;
    }


    public String getDescription2() {
        return description_2;
    }

    public void setDescription2(String description_2) {
        this.description_2 = description_2;
    }

    public void setCurrentLevel(int currentLevel) {this.currentLevel = currentLevel;}

    public void setCurrentProgress(int currentProgress) {this.currentProgress = currentProgress; }

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

    public int getMaxLevel() {  return maxLevel; }

    public List<Integer> getProgressLimitEachLevel() { return progressLimitEachLevel; }

    /**
     * Convenience method to map all the fields to Firebase
     * @return map containing Firebase keys & values
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ID, getId());
        result.put(NAME, getName());
        result.put(DESC_1, getDescription1());
        result.put(DESC_2, getDescription2());
        result.put(CURRENT_PROGRESS, getCurrentProgress());
        result.put(CURRENT_LEVEL, getCurrentLevel());
        result.put(MAX_LEVEL, getMaxLevel());
        result.put(PROGRESS_LIMIT_EACH_LEVEL, getProgressLimitEachLevel());

        return result;
    }


}
