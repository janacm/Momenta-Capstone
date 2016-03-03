package com.momenta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Provides a connection to android local database.
 */
public class DBHelper extends SQLiteOpenHelper {

    //Database Constants
    public static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "Momenta.db";

    //Sample Table Name
    public static final String SAMPLE_TABLE = "Sample";

    //Sample Table Columns
    private static final String ACTIVITY_ID = "ACTIVITY_ID";
    private static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    private static final String ACTIVITY_DURATION = "ACTIVITY_DURATION";


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SAMPLE_TABLE = "CREATE TABLE " + SAMPLE_TABLE +  "("
                + ACTIVITY_ID + " INTEGER PRIMARY KEY, " + ACTIVITY_NAME
                + " CHAR(32) NOT NULL, " + ACTIVITY_DURATION + " )";
        db.execSQL(CREATE_SAMPLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Updating older versions to newer versions
    }

    /**
     * Create a helper object to read from & write data to
     * @param context application context.
     */
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }
}
