package com.momenta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Provides a connection to android local database.
 */
public class DBHelper extends SQLiteOpenHelper {

    //DBHelper Instance
    public static DBHelper mInstance = null;

    //Database Constants, DB_VERSION must be incremented if the schema is changed.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Momenta.db";

    //Sample Table Name
    public static final String SAMPLE_TABLE = "SAMPLE_TABLE";

    //Sample Table Columns
    public static final String ACTIVITY_ID = "ACTIVITY_ID";
    public static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    public static final String ACTIVITY_DURATION = "ACTIVITY_DURATION";
    public static final String ACTIVITY_DEADLINE = "ACTIVITY_DEADLINE";
    public static final String ACTIVITY_PRIORITY = "ACTIVITY_PRIORITY";


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SAMPLE_TABLE = "CREATE TABLE " + SAMPLE_TABLE +  "("
                + ACTIVITY_ID + " INTEGER PRIMARY KEY, " + ACTIVITY_NAME
                + " CHAR(32) NOT NULL, " + ACTIVITY_DURATION + " INTEGER NOT NULL, "
                + ACTIVITY_DEADLINE + " long default 0 " + ACTIVITY_PRIORITY
                + " CHAR(32) NOT NULL )";
        db.execSQL(CREATE_SAMPLE_TABLE);

        //Inserting dummy data
        db.execSQL("INSERT INTO " +  SAMPLE_TABLE
                + " VALUES ( 1, 'Study for law exam', 120, 0, 'MEDIUM');");
        db.execSQL("INSERT INTO " + SAMPLE_TABLE
                + " VALUES ( 2, 'Go to the gym', 50, 0, 'LOW');");
        db.execSQL("INSERT INTO " + SAMPLE_TABLE
                + " VALUES ( 3, 'Organize House', 72, 0, 'VERY_HIGH');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if ( oldVersion == 1 ) {
            db.execSQL("ALTER TABLE " + SAMPLE_TABLE + " ADD COLUMN " + ACTIVITY_PRIORITY
                    + " CHAR(32) NOT NULL DEFAULT 'MEDIUM'");
        }
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
        values.put(ACTIVITY_PRIORITY, task.getPriority().name());
        if ( task.getDeadline() != null) {
            values.put(ACTIVITY_DEADLINE, task.getDeadline().getTimeInMillis());
        }

        return db.insert(SAMPLE_TABLE, null, values);
    }

    /**
     * Used to retrieve all the shows in the database.
     * @return ArrayList containing all shows.
     */
    public List<Task> getAllTasks() {
        SQLiteDatabase db = getReadableDatabase();
        List<Task> taskList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        Cursor cursor = db.query(SAMPLE_TABLE,
                new String[]{ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_DURATION, ACTIVITY_DEADLINE, ACTIVITY_PRIORITY},
                null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ACTIVITY_ID));
            String name = cursor.getString(cursor.getColumnIndex(ACTIVITY_NAME));
            int duration = cursor.getInt(cursor.getColumnIndex(ACTIVITY_DURATION));
            cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ACTIVITY_DEADLINE)));
            String priority = cursor.getString(cursor.getColumnIndex(ACTIVITY_PRIORITY));

            Task t = new Task(id, name, duration, cal);
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
        Calendar cal = Calendar.getInstance();
        Task task;
        Cursor cursor = db.query(SAMPLE_TABLE,
                new String[]{ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_DURATION, ACTIVITY_DEADLINE, ACTIVITY_PRIORITY},
                ACTIVITY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int dbID = cursor.getInt(cursor.getColumnIndex(ACTIVITY_ID));
            String name = cursor.getString(cursor.getColumnIndex(ACTIVITY_NAME));
            int duration = cursor.getInt(cursor.getColumnIndex(ACTIVITY_DURATION));
            long along = cursor.getLong(cursor.getColumnIndex(ACTIVITY_DEADLINE));
            cal.setTimeInMillis( along );
            String priority = cursor.getString(cursor.getColumnIndex(ACTIVITY_PRIORITY));

            task = new Task(dbID, name, duration , cal);
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
        cv.put(ACTIVITY_DURATION, task.getTime());
        cv.put(ACTIVITY_DEADLINE, task.getDeadline().getTimeInMillis());
        cv.put(ACTIVITY_PRIORITY, task.getPriority().name());
        String[] whereArgs = new String[]{task.getId() + ""};
        int result = 0;
        try {
            result = db.update(SAMPLE_TABLE, cv, ACTIVITY_ID + " = ?", whereArgs);
        } catch (Exception e) {
        }
        return result > 0;
    }
}
