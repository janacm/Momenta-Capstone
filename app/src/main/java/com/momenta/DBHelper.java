package com.momenta;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides a connection to android local database.
 */
public class DBHelper extends SQLiteOpenHelper {

    //DBHelper Instance
    public static DBHelper mInstance = null;

    //Database Constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Momenta.db";

    //Activity Table Name
    public static final String ACTIVITIES_TABLE = "ACTIVITIES_TABLE";

    //Awards Table Name
    public static final String AWARDS_TABLE = "AWARDS_TABLE";

    //Activities Table Columns
    public static final String ACTIVITY_ID = "ACTIVITY_ID";
    public static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    public static final String ACTIVITY_DURATION = "ACTIVITY_DURATION";
    public static final String ACTIVITY_DEADLINE = "ACTIVITY_DEADLINE";
    public static final String ACTIVITY_PRIORITY = "ACTIVITY_PRIORITY";
    public static final String ACTIVITY_LAST_MODIFIED = "ACTIVITY_LAST_MODIFIED";
    public static final String ACTIVITY_DATE_CREATED = "ACTIVITY_DATE_CREATED";

    //Activities Table Columns
    private static final String AWARD_ID = "AWARD_ID";
    private static final String AWARD_NAME = "AWARD_NAME";
    private static final String AWARD_DESCRIPTION = "AWARD_DESCRIPTION";
    private static final String AWARD_CURRENT_LEVEL = "AWARD_CURRENT_LEVEL";
    private static final String AWARD_MAX_LEVEL = "AWARD_MAX_LEVEL";
    private static final String AWARD_CURRENT_PROGRESS = "AWARD_CURRENT_PROGRESS";
    private static final String AWARD_LEVEL_1_PROGRESS_LIMIT = "AWARD_LEVEL_1_PROGRESS_LIMIT";
    private static final String AWARD_LEVEL_2_PROGRESS_LIMIT = "AWARD_LEVEL_2_PROGRESS_LIMIT";
    private static final String AWARD_LEVEL_3_PROGRESS_LIMIT = "AWARD_LEVEL_3_PROGRESS_LIMIT";
    private static final String AWARD_LEVEL_4_PROGRESS_LIMIT = "AWARD_LEVEL_4_PROGRESS_LIMIT";
    private static final String AWARD_LEVEL_5_PROGRESS_LIMIT = "AWARD_LEVEL_5_PROGRESS_LIMIT";

    //Common Table Columns
    public static final String USER_ID = "USER_ID";

    //Fields to specify the column & order to sort the result by
    public static final String COLUMN = "COLUMN";
    public static final String ORDER = "ORDER";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    private helperPreferences helperPreferences;

    // ACTIVITIES table create statement
    private static final String CREATE_ACTIVITIES_TABLE = "CREATE TABLE "
            + ACTIVITIES_TABLE + "("
            + ACTIVITY_ID + " INTEGER PRIMARY KEY, "
            + USER_ID + " CHAR(32) NOT NULL, "
            + ACTIVITY_NAME + " CHAR(32) NOT NULL, "
            + ACTIVITY_DURATION + " INTEGER NOT NULL, "
            + ACTIVITY_DEADLINE + " LONG DEFAULT 0, "
            + ACTIVITY_PRIORITY + " CHAR(32) NOT NULL, "
            + ACTIVITY_LAST_MODIFIED + " LONG NOT NULL DEFAULT 0, "
            + ACTIVITY_DATE_CREATED + " LONG NOT NULL DEFAULT 0)";

    // AWARDS table create statement
    private static final String CREATE_AWARDS_TABLE = "CREATE TABLE "
            + AWARDS_TABLE + "("
            + AWARD_ID + " INTEGER PRIMARY KEY, "
            + USER_ID + " CHAR(32) NOT NULL, "
            + AWARD_NAME + " CHAR(32) NOT NULL, "
            + AWARD_DESCRIPTION + " CHAR(32) NOT NULL, "
            + AWARD_CURRENT_LEVEL + " INTEGER NOT NULL DEFAULT 0, "
            + AWARD_CURRENT_PROGRESS + " INTEGER NOT NULL DEFAULT 0, "
            + AWARD_MAX_LEVEL + " INTEGER NOT NULL DEFAULT 0, "
            + AWARD_LEVEL_1_PROGRESS_LIMIT + " INTEGER NOT NULL DEFAULT 0, "
            + AWARD_LEVEL_2_PROGRESS_LIMIT + " INTEGER NOT NULL DEFAULT 0, "
            + AWARD_LEVEL_3_PROGRESS_LIMIT + " INTEGER NOT NULL DEFAULT 0, "
            + AWARD_LEVEL_4_PROGRESS_LIMIT + " INTEGER NOT NULL DEFAULT 0, "
            + AWARD_LEVEL_5_PROGRESS_LIMIT + " INTEGER NOT NULL DEFAULT 0)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*The DATABASE_VERSION must be incremented if this statement is altered;
            and the onUpgrade method must be updated
         */
        db.execSQL(CREATE_ACTIVITIES_TABLE);
        db.execSQL(CREATE_AWARDS_TABLE);
        fillAwardsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE " + ACTIVITIES_TABLE
                    + " ADD COLUMN "
                    + ACTIVITY_PRIORITY + " CHAR(32) NOT NULL DEFAULT 'MEDIUM'");
            oldVersion++;
        }
        if (oldVersion == 2) {
            db.execSQL("ALTER TABLE " + ACTIVITIES_TABLE + " ADD COLUMN " + ACTIVITY_LAST_MODIFIED
                    + " LONG NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE " + ACTIVITIES_TABLE + " ADD COLUMN " + ACTIVITY_DATE_CREATED
                    + " LONG NOT NULL DEFAULT 0");
        }
    }

    /**
     * Create a helper object to read from & write data to
     *
     * @param context application context.
     */
    private DBHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
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
     *
     * @param task The task to be inserted into the database.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertTask(Task task,String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACTIVITY_NAME, task.getName());
        values.put(USER_ID, user_id);
        values.put(ACTIVITY_DURATION, task.getGoalInMinutes());
        values.put(ACTIVITY_PRIORITY, task.getPriority().name());
        values.put(ACTIVITY_DEADLINE, task.getDeadline().getTimeInMillis());
        values.put(ACTIVITY_DATE_CREATED, task.getDateCreated());
        values.put(ACTIVITY_LAST_MODIFIED, task.getLastModified().getTimeInMillis());

        return db.insert(ACTIVITIES_TABLE, null, values);
    }

    /**
     * Used to retrieve all the tasks in the database.
     *
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

        Cursor cursor = db.query(ACTIVITIES_TABLE,
                new String[]{ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_DURATION, ACTIVITY_DEADLINE,
                        ACTIVITY_PRIORITY, ACTIVITY_DATE_CREATED, ACTIVITY_LAST_MODIFIED},
                null, null, null, null, column, null);
        while (cursor != null && cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ACTIVITY_ID));
            String name = cursor.getString(cursor.getColumnIndex(ACTIVITY_NAME));
            int duration = cursor.getInt(cursor.getColumnIndex(ACTIVITY_DURATION));
            calDue.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ACTIVITY_DEADLINE)));
            String priority = cursor.getString(cursor.getColumnIndex(ACTIVITY_PRIORITY));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(ACTIVITY_DATE_CREATED));
            calModified.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ACTIVITY_LAST_MODIFIED)));

            Task t = new Task(id, name, duration, calDue, dateCreated, calModified);
            t.setPriority(Task.Priority.valueOf(priority));

            taskList.add(t);
        }
        if (cursor != null) {
            cursor.close();
        }
        return taskList;
    }

    /**
     * Used to retrieve a single task from the db
     *
     * @param id of the task to be retrieved from the db
     * @return Task object of the task with the corresponding id
     * or null if no task with the id could be found.
     */
    public Task getTask(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Calendar calDue = Calendar.getInstance();
        Calendar calModified = Calendar.getInstance();
        Task task;
        Cursor cursor = db.query(ACTIVITIES_TABLE,
                new String[]{ACTIVITY_ID, ACTIVITY_NAME, ACTIVITY_DURATION, ACTIVITY_DEADLINE
                        , ACTIVITY_PRIORITY, ACTIVITY_DATE_CREATED, ACTIVITY_LAST_MODIFIED},
                ACTIVITY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int dbID = cursor.getInt(cursor.getColumnIndex(ACTIVITY_ID));
            String name = cursor.getString(cursor.getColumnIndex(ACTIVITY_NAME));
            int duration = cursor.getInt(cursor.getColumnIndex(ACTIVITY_DURATION));
            long deadlineLong = cursor.getLong(cursor.getColumnIndex(ACTIVITY_DEADLINE));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(ACTIVITY_DATE_CREATED));
            long lastModified = cursor.getLong(cursor.getColumnIndex(ACTIVITY_LAST_MODIFIED));
            calDue.setTimeInMillis(deadlineLong);
            calModified.setTimeInMillis(lastModified);

            task = new Task(dbID, name, duration, calDue, dateCreated, calModified);
            String priority = cursor.getString(cursor.getColumnIndex(ACTIVITY_PRIORITY));
            task.setPriority(Task.Priority.valueOf(priority));
        } else {
            return null;
        }
        cursor.close();
        return task;
    }

    /**
     * Used to update the fields of a task in the DB matching the id of
     * the provided task, with its fields.
     *
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
            result = db.update(ACTIVITIES_TABLE, cv, ACTIVITY_ID + " = ?", whereArgs);
        } catch (Exception e) {
        }
        return result > 0;
    }

    /**
     * Method to delete task from the databse
     *
     * @param id the id to be deleted
     */
    public void deleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ACTIVITIES_TABLE, ACTIVITY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Inserts an award into the awards table.
     *
     * @param award The task to be inserted into the database.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertAward(Award award,SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(AWARD_NAME, award.getName());
        values.put(AWARD_DESCRIPTION, award.getDescription());
        values.put(AWARD_CURRENT_LEVEL, award.getCurrentLevel());
        values.put(AWARD_CURRENT_PROGRESS, award.getCurrentProgress());
        values.put(AWARD_MAX_LEVEL, award.getMaxLevel());
        for (int i: award.getProgressLimitForEachLevel()) {
            values.put("AWARD_LEVEL_"+i+"_PROGRESS_LIMIT", i);
        }
        return db.insert(AWARDS_TABLE, null, values);
    }

    /**
     * Used to retrieve all the awards in the database.
     *
     * @return ArrayList containing all awards.
     */
    public List<Award> getAllAwards() {
        SQLiteDatabase db = getReadableDatabase();

        List<Award> awardsList = new ArrayList<>();

        Cursor cursor = db.query(AWARDS_TABLE,
                new String[]{AWARD_ID, AWARD_NAME, AWARD_DESCRIPTION, AWARD_CURRENT_PROGRESS, AWARD_CURRENT_PROGRESS,
                        AWARD_MAX_LEVEL, AWARD_LEVEL_1_PROGRESS_LIMIT,AWARD_LEVEL_2_PROGRESS_LIMIT,AWARD_LEVEL_3_PROGRESS_LIMIT
                        ,AWARD_LEVEL_4_PROGRESS_LIMIT,AWARD_LEVEL_5_PROGRESS_LIMIT,},
                null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(AWARD_ID));
            String name = cursor.getString(cursor.getColumnIndex(AWARD_NAME));
            String description = cursor.getString(cursor.getColumnIndex(AWARD_DESCRIPTION));
            int currentProgress = cursor.getInt(cursor.getColumnIndex(AWARD_CURRENT_PROGRESS));
            int currentLevel = cursor.getInt(cursor.getColumnIndex(AWARD_CURRENT_LEVEL));
            int level1ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_1_PROGRESS_LIMIT));
            int level2ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_2_PROGRESS_LIMIT));
            int level3ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_3_PROGRESS_LIMIT));
            int level4ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_4_PROGRESS_LIMIT));
            int level5ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_5_PROGRESS_LIMIT));
            List<Integer> progressLimitForEachLevel = new ArrayList<>();
            if(level1ProgressLimit!=0){progressLimitForEachLevel.add(level1ProgressLimit);}
            if(level2ProgressLimit!=0){progressLimitForEachLevel.add(level2ProgressLimit);}
            if(level3ProgressLimit!=0){progressLimitForEachLevel.add(level3ProgressLimit);}
            if(level4ProgressLimit!=0){progressLimitForEachLevel.add(level4ProgressLimit);}
            if(level5ProgressLimit!=0){progressLimitForEachLevel.add(level5ProgressLimit);}
            Award a = new Award(id, name, description, progressLimitForEachLevel);
            a.setCurrentLevel(currentLevel);
            a.setCurrentProgress(currentProgress);
            awardsList.add(a);
        }
        if (cursor != null) {
            cursor.close();
        }
        return awardsList;
    }

    /**
     * Used to retrieve a single award from the db
     *
     * @param id of the award to be retrieved from the db
     * @return Award object of the task with the corresponding id
     * or null if no task with the id could be found.
     */
    public Award getAward(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Award award;
        Cursor cursor = db.query(ACTIVITIES_TABLE,
                new String[]{AWARD_ID, AWARD_NAME, AWARD_DESCRIPTION, AWARD_CURRENT_PROGRESS, AWARD_CURRENT_PROGRESS,
                        AWARD_MAX_LEVEL, AWARD_LEVEL_1_PROGRESS_LIMIT,AWARD_LEVEL_2_PROGRESS_LIMIT,AWARD_LEVEL_3_PROGRESS_LIMIT
                        ,AWARD_LEVEL_4_PROGRESS_LIMIT,AWARD_LEVEL_5_PROGRESS_LIMIT},
                AWARD_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int DBid = cursor.getInt(cursor.getColumnIndex(AWARD_ID));
            String name = cursor.getString(cursor.getColumnIndex(AWARD_NAME));
            String description = cursor.getString(cursor.getColumnIndex(AWARD_DESCRIPTION));
            int currentProgress = cursor.getInt(cursor.getColumnIndex(AWARD_CURRENT_PROGRESS));
            int currentLevel = cursor.getInt(cursor.getColumnIndex(AWARD_CURRENT_LEVEL));
            int level1ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_1_PROGRESS_LIMIT));
            int level2ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_2_PROGRESS_LIMIT));
            int level3ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_3_PROGRESS_LIMIT));
            int level4ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_4_PROGRESS_LIMIT));
            int level5ProgressLimit = cursor.getInt(cursor.getColumnIndex(AWARD_LEVEL_5_PROGRESS_LIMIT));
            List<Integer> progressLimitForEachLevel = new ArrayList<>();
            if(level1ProgressLimit!=0){progressLimitForEachLevel.add(level1ProgressLimit);}
            if(level2ProgressLimit!=0){progressLimitForEachLevel.add(level2ProgressLimit);}
            if(level3ProgressLimit!=0){progressLimitForEachLevel.add(level3ProgressLimit);}
            if(level4ProgressLimit!=0){progressLimitForEachLevel.add(level4ProgressLimit);}
            if(level5ProgressLimit!=0){progressLimitForEachLevel.add(level5ProgressLimit);}
            award = new Award(DBid, name, description, progressLimitForEachLevel);
            award.setCurrentLevel(currentLevel);
            award.setCurrentProgress(currentProgress);
        } else {
            return null;
        }
        cursor.close();
        return award;
    }

    /**
     * Used to update the fields of an award in the DB matching the id of
     * the provided task, with its fields.
     *
     * @param award the updated task fields to be matched
     * @return true if the operation was successful and false otherwise.
     */
    public boolean updateAward(Award award) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AWARD_NAME, award.getName());
        cv.put(AWARD_DESCRIPTION, award.getDescription());
        cv.put(AWARD_CURRENT_PROGRESS, award.getCurrentProgress());
        cv.put(AWARD_CURRENT_LEVEL, award.getCurrentLevel());
        cv.put(AWARD_LEVEL_1_PROGRESS_LIMIT, (award.getProgressLimitForEachLevel().get(0) != null) ? award.getProgressLimitForEachLevel().get(0) : 0);
        cv.put(AWARD_LEVEL_2_PROGRESS_LIMIT, (award.getProgressLimitForEachLevel().get(1) != null) ? award.getProgressLimitForEachLevel().get(1) : 0);
        cv.put(AWARD_LEVEL_3_PROGRESS_LIMIT, (award.getProgressLimitForEachLevel().get(2) != null) ? award.getProgressLimitForEachLevel().get(2) : 0);
        cv.put(AWARD_LEVEL_4_PROGRESS_LIMIT, (award.getProgressLimitForEachLevel().get(3) != null) ? award.getProgressLimitForEachLevel().get(3) : 0);
        cv.put(AWARD_LEVEL_5_PROGRESS_LIMIT, (award.getProgressLimitForEachLevel().get(4) != null) ? award.getProgressLimitForEachLevel().get(4) : 0);
        cv.put(AWARD_MAX_LEVEL, award.getProgressLimitForEachLevel().size());
        String[] whereArgs = new String[]{award.getId() + ""};
        int result = 0;
        try {
            result = db.update(AWARDS_TABLE, cv, AWARD_ID + " = ?", whereArgs);
        } catch (Exception e) {
        }
        return result > 0;
    }

    /**
     * Method to delete award from the database
     *
     * @param id the id to be deleted
     */
    public void deleteAward(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(AWARDS_TABLE, AWARD_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    //Fill the awrds table with the defined awrds we want in our application
    private void fillAwardsTable(SQLiteDatabase db){
        Award commitedAward = new Award("Committed","Logged time towards an activity you previously committed to.", new ArrayList<>(Collections.singletonList(1)));
        commitedAward.setCurrentLevel(0);
        commitedAward.setCurrentProgress(0);
        insertAward(commitedAward,db);

        Award neophyteAward = new Award("Neophyte","Logged time for the first time towards an activity.", new ArrayList<>(Collections.singletonList(1)));
        neophyteAward.setCurrentLevel(0);
        neophyteAward.setCurrentProgress(0);
        insertAward(neophyteAward,db);

        Award trendSetterAward = new Award("Trend Setter","Completed 5 hours towards a productive activity.", new ArrayList<>(Arrays.asList(1,5,10,50,200)));
        trendSetterAward.setCurrentLevel(0);
        trendSetterAward.setCurrentProgress(0);
        insertAward(trendSetterAward,db);

        Award multiTaskerAward = new Award("Mulit-Tasker","Log time in X different activities.", new ArrayList<>(Arrays.asList(5,10,25,100,200)));
        multiTaskerAward.setCurrentLevel(0);
        multiTaskerAward.setCurrentProgress(0);
        insertAward(multiTaskerAward,db);

        Award productiveAward = new Award("Productive","Successfully log time towards an activity after seeing the notification X times.", new ArrayList<>(Arrays.asList(10,50,100,500,2000)));
        productiveAward.setCurrentLevel(0);
        productiveAward.setCurrentProgress(0);
        insertAward(productiveAward,db);

        Award perfectionnistAward = new Award("Perfectionnist","Spend more than X hours towards an activity.", new ArrayList<>(Arrays.asList(10,20,50,200,500)));
        perfectionnistAward.setCurrentLevel(0);
        perfectionnistAward.setCurrentProgress(0);
        insertAward(perfectionnistAward,db);

        Award ponctualAward = new Award("Ponctual","Achieve time goal before deadline on X activities.", new ArrayList<>(Arrays.asList(5,10,25,100,200)));
        ponctualAward.setCurrentLevel(0);
        ponctualAward.setCurrentProgress(0);
        insertAward(ponctualAward,db);


    }
}
