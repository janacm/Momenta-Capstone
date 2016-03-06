package com.momenta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a connection to android local database.
 */
public class DBHelper extends SQLiteOpenHelper {

    //DBHelper Instance
    private static DBHelper mInstance = null;

    //Database Constants, DB_VERSION must be incremented if the schema is changed.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Momenta.db";

    //Sample Table Name
    public static final String SAMPLE_TABLE = "SAMPLE_TABLE";

    //Sample Table Columns
    private static final String ACTIVITY_ID = "ACTIVITY_ID";
    private static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    private static final String ACTIVITY_DURATION = "ACTIVITY_DURATION";


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SAMPLE_TABLE = "CREATE TABLE " + SAMPLE_TABLE +  "("
                + ACTIVITY_ID + " INTEGER PRIMARY KEY, " + ACTIVITY_NAME
                + " CHAR(32) NOT NULL, " + ACTIVITY_DURATION + " INTEGER NOT NULL)";
        db.execSQL(CREATE_SAMPLE_TABLE);

        //Inserting dummy data
        db.execSQL("INSERT INTO " +  SAMPLE_TABLE
                + " VALUES ( 1, 'Study for law exam', 120);");
        db.execSQL("INSERT INTO " + SAMPLE_TABLE
                + " VALUES ( 2, 'Go to the gym', 50);");
        db.execSQL("INSERT INTO " + SAMPLE_TABLE
                + " VALUES ( 3, 'Organize House', 72);");
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

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Inserts a task into the temporary table, sample.
     * @param task The task to be inserted into the database.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACTIVITY_NAME, task.getName());
        values.put(ACTIVITY_DURATION, task.getTime());

        return db.insert(SAMPLE_TABLE, null, values);
    }

    /**
     * Used to retrieve all the shows in the database.
     * @return ArrayList containing all shows.
     */
    public List<Task> getTasksList() {
        SQLiteDatabase db = getReadableDatabase();
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = db.query(SAMPLE_TABLE,
                new String[]{ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_DURATION},
                null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Task task = new Task(cursor.getInt(cursor.getColumnIndex(ACTIVITY_ID)), cursor.getString(cursor
                    .getColumnIndex(ACTIVITY_NAME)), cursor.getInt(cursor.getColumnIndex(ACTIVITY_DURATION)));
            taskList.add(task);
        }
        if (cursor != null) {
            cursor.close();
        }
        return taskList;
    }
}
