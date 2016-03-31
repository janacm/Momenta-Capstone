package com.momenta;

/**
 * Created by Vedha on 3/29/2016.
 */
import android.os.AsyncTask;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by joesi on 2016-02-16.
 */
@RunWith(AndroidJUnit4.class)

public class BackendTest extends AsyncTask {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void TestConnection(){

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://momenta.herokuapp.com");

            urlConnection.setRequestMethod("GET");
            urlConnection = (HttpURLConnection) url
                    .openConnection();


            int code = urlConnection.getResponseCode();

            InputStream in = urlConnection.getInputStream();
            assertEquals(code, 200);

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                System.out.print(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

    }

    @Override
    protected Object doInBackground(Object[] params) {
        TestConnection();
        return null;
    }
}