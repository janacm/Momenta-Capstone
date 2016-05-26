package com.momenta;

import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Joe on 2016-03-17.
 * For Momenta-Capstone
 */
public abstract class HttpTask extends AsyncTask<String, Void, JSONObject> {
    private String RequestUrl;
    private String RequestMethod;
    private List<NameValuePair> params;
    JSONParser jParser = new JSONParser();
    public HttpTask(String Url, String Method)
    {
        this.RequestUrl = Url;
        this.RequestMethod = Method;
    }
    public HttpTask(String Url, String Method, List<NameValuePair> parameters)
    {
        this.RequestUrl = Url;
        this.RequestMethod = Method;
        params = parameters;
    }
    @Override
    protected JSONObject doInBackground(String... args) {
        JSONObject json = null;
        try {
            json = jParser.makeHttpRequest(RequestUrl, RequestMethod, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}

