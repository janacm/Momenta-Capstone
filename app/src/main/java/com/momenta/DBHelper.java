package com.momenta;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Provides a connection to android local database.
 */
public class DBHelper extends SQLiteOpenHelper {

    //DBHelper Instance
    public static DBHelper mInstance = null;

    //Database Constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Momenta.db";

    //Sample Table Name
    public static final String SAMPLE_TABLE = "SAMPLE_TABLE";

    //Sample Table Columns
    public static final String ACTIVITY_ID = "ACTIVITY_ID";
    public static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    public static final String ACTIVITY_DURATION = "ACTIVITY_DURATION";
    public static final String ACTIVITY_DEADLINE = "ACTIVITY_DEADLINE";
    public static final String ACTIVITY_PRIORITY = "ACTIVITY_PRIORITY";
    public static final String ACTIVITY_LAST_MODIFIED = "ACTIVITY_LAST_MODIFIED";
    public static final String ACTIVITY_DATE_CREATED = "ACTIVITY_DATE_CREATED";
    public static final String ACTIVITY_TIME_TOWARDS_GOAL = "ACTIVITY_TIME_TOWARDS_GOAL";

    //Fields to specify the column & order to sort the result by
    public static final String COLUMN = "COLUMN";
    public static final String ORDER = "ORDER";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    private helperPreferences helperPreferences;


    @Override
    public void onCreate(SQLiteDatabase db) {
        /*The DATABASE_VERSION must be incremented if this statement is altered;
            and the onUpgrade method must be updated
         */
        String CREATE_SAMPLE_TABLE = "CREATE TABLE " + SAMPLE_TABLE +  "("
                + ACTIVITY_ID + " INTEGER PRIMARY KEY, " + ACTIVITY_NAME
                + " CHAR(32) NOT NULL, " + ACTIVITY_DURATION + " INTEGER NOT NULL, "
                + ACTIVITY_DEADLINE + " LONG DEFAULT 0, " + ACTIVITY_PRIORITY
                + " CHAR(32) NOT NULL, " + ACTIVITY_LAST_MODIFIED + " LONG NOT NULL DEFAULT 0, "
                + ACTIVITY_DATE_CREATED + " LONG NOT NULL DEFAULT 0)";
        db.execSQL(CREATE_SAMPLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if ( oldVersion == 1 ) {
            db.execSQL("ALTER TABLE " + SAMPLE_TABLE + " ADD COLUMN " + ACTIVITY_PRIORITY
                    + " CHAR(32) NOT NULL DEFAULT 'MEDIUM'");
            oldVersion++;
        } if ( oldVersion == 2 ) {
            db.execSQL("ALTER TABLE " + SAMPLE_TABLE + " ADD COLUMN "+ ACTIVITY_LAST_MODIFIED
                    + " LONG NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE " + SAMPLE_TABLE + " ADD COLUMN "+ ACTIVITY_DATE_CREATED
                    + " LONG NOT NULL DEFAULT 0");
        }
    }

    /**
     * Create a helper object to read from & write data to
     * @param context application context.
     */
    private DBHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION );
        this.helperPreferences = new helperPreferences((Activity) context);
    }

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
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
        values.put(ACTIVITY_DURATION, task.getGoalInMinutes());
        values.put(ACTIVITY_PRIORITY, task.getPriority().name());
        values.put(ACTIVITY_DEADLINE, task.getDeadline().getTimeInMillis());
        values.put(ACTIVITY_DATE_CREATED, task.getDateCreated());
        values.put(ACTIVITY_LAST_MODIFIED, task.getLastModified().getTimeInMillis());

        return db.insert(SAMPLE_TABLE, null, values);
    }

    /**
     * Used to retrieve all the tasks in the database.
     * @return ArrayList containing all shows.
     */
    public List<Task> getAllTasks() {
        SQLiteDatabase db = getReadableDatabase();

        String column = helperPreferences.getPreferences(COLUMN, ACTIVITY_LAST_MODIFIED);
        String order = helperPreferences.getPreferences(ORDER, DESC);
        List<Task> taskList = new ArrayList<>();
        Calendar calDue = Calendar.getInstance();
        Calendar calModified = Calendar.getInstance();
        column += " COLLATE NOCASE " + order + ";";

        Cursor cursor = db.query(SAMPLE_TABLE,
                new String[]{ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_DURATION, ACTIVITY_DEADLINE,
                        ACTIVITY_PRIORITY, ACTIVITY_DATE_CREATED, ACTIVITY_LAST_MODIFIED},
                null, null, null, null, column, null);
        while (cursor != null && cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ACTIVITY_ID));
            String name = cursor.getString(cursor.getColumnIndex(ACTIVITY_NAME));
            int duration = cursor.getInt(cursor.getColumnIndex(ACTIVITY_DURATION));
            calDue.setTimeInMillis( cursor.getLong(cursor.getColumnIndex(ACTIVITY_DEADLINE)) );
            String priority = cursor.getString(cursor.getColumnIndex(ACTIVITY_PRIORITY));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(ACTIVITY_DATE_CREATED));
            calModified.setTimeInMillis( cursor.getLong(cursor.getColumnIndex(ACTIVITY_LAST_MODIFIED)) );

            Task t = new Task(id, name, duration, calDue, dateCreated, calModified);
            t.setPriority( Task.Priority.valueOf(priority) );

            taskList.add( t );
        }
        if (cursor != null) {
            cursor.close();
        }
        return taskList;
    }

    /**
     * Used to retrieve a single task from the db
     * @param id of the task to be retrieved from the db
     * @return Task object of the task with the corresponding id
     *         or null if no task with the id could be found.
     */
    public Task getTask(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Calendar calDue = Calendar.getInstance();
        Calendar calModified = Calendar.getInstance();
        Task task;
        Cursor cursor = db.query(SAMPLE_TABLE,
                new String[]{ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_DURATION, ACTIVITY_DEADLINE
                        , ACTIVITY_PRIORITY, ACTIVITY_DATE_CREATED, ACTIVITY_LAST_MODIFIED},
                ACTIVITY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int dbID = cursor.getInt(cursor.getColumnIndex(ACTIVITY_ID));
            String name = cursor.getString(cursor.getColumnIndex(ACTIVITY_NAME));
            int duration = cursor.getInt(cursor.getColumnIndex(ACTIVITY_DURATION));
            long deadlineLong = cursor.getLong(cursor.getColumnIndex(ACTIVITY_DEADLINE));
            long dateCreated =  cursor.getLong(cursor.getColumnIndex(ACTIVITY_DATE_CREATED));
            long lastModified = cursor.getLong(cursor.getColumnIndex(ACTIVITY_LAST_MODIFIED));
            calDue.setTimeInMillis( deadlineLong );
            calModified.setTimeInMillis(lastModified);

            task = new Task(dbID, name, duration , calDue, dateCreated, calModified);
            String priority = cursor.getString(cursor.getColumnIndex(ACTIVITY_PRIORITY));
            task.setPriority( Task.Priority.valueOf(priority) );
        } else {
            return null;
        }
        cursor.close();
        return task;
    }

    /**
     * Used to update the fields of a task in the DB matching the id of
     * the provided task, with its fields.
     * @param task the updated task fields to be matched
     * @return true if the operation was successful and false otherwise.
     */
    public boolean updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ACTIVITY_NAME, task.getName());
        cv.put(ACTIVITY_DURATION, task.getGoalInMinutes());
        cv.put(ACTIVITY_DEADLINE, task.getDeadline().getTimeInMillis());
        cv.put(ACTIVITY_PRIORITY, task.getPriority().name());
        cv.put(ACTIVITY_LAST_MODIFIED, task.getLastModified().getTimeInMillis());
        String[] whereArgs = new String[]{task.getId() + ""};
        int result = 0;
        try {
            result = db.update(SAMPLE_TABLE, cv, ACTIVITY_ID + " = ?", whereArgs);
        } catch (Exception e) {
        }
        return result > 0;
    }

    /**
     * Method to delete task from the databse
     * @param id the id to be deleted
     */
    public void deleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SAMPLE_TABLE, ACTIVITY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
