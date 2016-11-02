package com.momenta;

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
     * Gets the path of the user currently logged in.
     * @return the current user's path, null in no user is logged in
     */
    public static String getUserPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String path = null;
        if (user != null) {
            path = user.getEmail().replace(".", ",");
        }
        return path;
    }

    /**
     * Gets the path of a registered user given thier email
     * @param email the email of the user
     * @return path of the user
     */
    public static String getUserPath(String email) {
        return email.replace(".", ",");
    }
}
