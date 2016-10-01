package com.momenta;

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
}
