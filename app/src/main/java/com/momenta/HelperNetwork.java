package com.momenta;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 2016-06-02.
 * For Momenta-Capstone
 */
public class HelperNetwork {
    private static Context context;
    // store the overall connectivity status
    private static int connectivity_status = Constants.CONNECTIVITY_NO_NETWORK;

    public static int getConnectivityStatus() {
        return connectivity_status;
    }

    public static void setConnectivityStatus(int conn_status) {
        connectivity_status = conn_status;
    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //Call this method to know if the device is connected to a network. Call before uploading stats/info to DB
    public static int evaluateConnectivityStatus(Activity our_activity) {

        if (isOnline(our_activity)) {
            connectivity_status = Constants.CONNECTIVITY_OK;
        } else {
            connectivity_status = Constants.CONNECTIVITY_NO_NETWORK;
        }
        return connectivity_status;
    }

    /**
     * @param db - reference to the local database
     * @return A JSONArray of all the tasks contained in the local DB
     */
    public static JSONArray getTasksJSONArray(DBHelper db) {
        JSONArray json_tasks = new JSONArray();
        List<Task> tasks = db.getAllTasks();
        for (int i = 0; i < tasks.size(); i++) {
            JSONObject jObj = new JSONObject();
            try {
                jObj.put(Constants.JSONTAG_ACTIVITY_ID, tasks.get(i).getId());
                jObj.put(Constants.JSONTAG_ACTIVITY_NAME, tasks.get(i).getName());
                jObj.put(Constants.JSONTAG_ACTIVITY_DURATION, tasks.get(i).getGoal());
                jObj.put(Constants.JSONTAG_ACTIVITY_DEADLINE, tasks.get(i).getDeadlineValue().getTimeInMillis());
                jObj.put(Constants.JSONTAG_ACTIVITY_PRIORITY, tasks.get(i).getPriorityValue().name());
                jObj.put(Constants.JSONTAG_ACTIVITY_LAST_MODIFIED, tasks.get(i).getLastModifiedValue().getTimeInMillis());
                jObj.put(Constants.JSONTAG_ACTIVITY_DATE_CREATED, tasks.get(i).getDateCreated());

            } catch (JSONException e) {
            }

            json_tasks.put(jObj);
        }
        return json_tasks;
    }

    /**
     * @param activity - current activity
     * @brief Sends an HTTP Post request to the server containing all the tasks in the local DB and sends feedback to the user
     * base on the server's response
     */
    public static void uploadTasksToServer(Activity activity) {

        context = (Context) activity;

        String ping_url = "";//TODO: Specify correct URL

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // open the database
        DBHelper db = DBHelper.getInstance(activity);

        // Serialize the tasks
        JSONArray json_tasks = getTasksJSONArray(db);
        params.add(new BasicNameValuePair("tasks", json_tasks.toString()));

        new HttpTask(ping_url, "POST", params) {

            @Override
            protected void onPostExecute(JSONObject json) {
                super.onPostExecute(json);
                try {
                    JSONArray response = json.getJSONArray(Constants.JSONTAG_UPLOAD_TASKS);
                    JSONObject games = response.getJSONObject(0);
                    String status = games.getString(Constants.JSONTAG_STATUS);

                    //TODO Check status response from server after upload request
                 /*   if (status.equals("1")) {
                        Toast.makeText(context, R.string.toast_notloggedin, Toast.LENGTH_LONG).show();
                    } else if (status.equals("2")) {
                        Toast.makeText(context, R.string.toast_notconfigured, Toast.LENGTH_LONG).show();
                    } else if (status.equals("3")) {
                        Toast.makeText(context, R.string.toast_noperms, Toast.LENGTH_LONG).show();
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    /**
     * ***********************************************************************
     *
     * @param
     * @return ************************************************************************
     * @brief
     */
    public static JSONArray getTaskJSONArray(DBHelper db, int taskId) {
        JSONArray json_task = new JSONArray();
        Task task = db.getTask(taskId);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put(Constants.JSONTAG_ACTIVITY_ID, task.getId());
            jObj.put(Constants.JSONTAG_ACTIVITY_NAME, task.getName());
            jObj.put(Constants.JSONTAG_ACTIVITY_DURATION, task.getGoal());
            jObj.put(Constants.JSONTAG_ACTIVITY_DEADLINE, task.getDeadlineValue().getTimeInMillis());
            jObj.put(Constants.JSONTAG_ACTIVITY_PRIORITY, task.getPriorityValue().name());
            jObj.put(Constants.JSONTAG_ACTIVITY_LAST_MODIFIED, task.getLastModifiedValue().getTimeInMillis());
            jObj.put(Constants.JSONTAG_ACTIVITY_DATE_CREATED, task.getDateCreated());

        } catch (JSONException e) {
        }

        json_task.put(jObj);

        return json_task;
    }

    /**
     * ***********************************************************************
     *
     * @param
     * @return
     * @usage
     */
    public static void uploadTaskToServer(Activity activity, int taskId) {

        context = (Context) activity;

        String ping_url = "";//TODO: Specify correct URL

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // open the database
        DBHelper db = DBHelper.getInstance(activity);

        // Serialize the task
        JSONArray json_task = getTaskJSONArray(db, taskId);
        params.add(new BasicNameValuePair("task", json_task.toString()));

        new HttpTask(ping_url, "POST", params) {

            @Override
            protected void onPostExecute(JSONObject json) {
                super.onPostExecute(json);
                try {
                    JSONArray response = json.getJSONArray(Constants.JSONTAG_UPLOAD_TASKS);
                    JSONObject games = response.getJSONObject(0);
                    String status = games.getString(Constants.JSONTAG_STATUS);

                    //TODO Check status response from server after upload request
                 /*   if (status.equals("1")) {
                        Toast.makeText(context, R.string.toast_notloggedin, Toast.LENGTH_LONG).show();
                    } else if (status.equals("2")) {
                        Toast.makeText(context, R.string.toast_notconfigured, Toast.LENGTH_LONG).show();
                    } else if (status.equals("3")) {
                        Toast.makeText(context, R.string.toast_noperms, Toast.LENGTH_LONG).show();
                    } */

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }
}