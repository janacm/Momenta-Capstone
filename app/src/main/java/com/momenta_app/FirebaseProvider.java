package com.momenta_app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Content provider for firebase.
 */
public class FirebaseProvider {

    private static FirebaseDatabase firebaseDatabase;

    /**
     * Access method for singleton firebase instance
     * @return FirebaseDatabase instance
     */
    public static FirebaseDatabase getInstance() {
        if ( firebaseDatabase == null ) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

    /**
     * Setter method for the firebase instance
     * @param database the database to set the
     */
    public static void setFirebaseDatabase(FirebaseDatabase database) {
        firebaseDatabase = database;
    }

    /**
     * Returns a User object created from the Google user currently logged in
     * @return The User currently logged in; null if no user is logged in.
     */
    public static User getUser() {
        FirebaseUser mUser=  FirebaseAuth.getInstance().getCurrentUser();
        User user = new User();
        if ( mUser != null ) {
            user .setDisplayName(mUser.getDisplayName());
            user.setPath( mUser.getEmail().replace(".",",") );
        }
        return user;
    }

    /**
     * Gets the path of the user currently logged in.
     * @return the current user's path, empty String if no user is loggged in
     */
    public static String getUserPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String path = "";
        if (user != null) {
            path = user.getEmail().replace(".", ",");
        }
        return path;
    }
}
