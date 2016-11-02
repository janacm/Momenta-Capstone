package com.momenta;

/**
 * User class
 */

public class User {
    private String displayName;
    private String path;

    /**
     * Empty constructor
     */
    public User(){
    }

    public User(String displayName, String email) {
        this.displayName = displayName;
        this.path = email.replace("." ,".");
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
