package com.momenta;

/**
 * Created by Vedha on 3/29/2016.
 */
import android.os.AsyncTask;
import android.support.test.runner.AndroidJUnit4;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@RunWith(AndroidJUnit4.class)

public class RESTfulAPITest extends AsyncTask {
    //Class Variables used throughout the test cases
    int responseCode;
    URL url;
    HttpURLConnection urlConnection = null;
    /*
     * Simple get request to verify that there is a connection between the server/db and application
     */
    @Test
    public void GetRequestToDBTest(){

        try {
            url = new URL("http://momenta.herokuapp.com");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            responseCode = urlConnection.getResponseCode();

            assertEquals(200, responseCode);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /*
    * POST request that adds a user 121 to the database
    */
    @Test
    public void PostRequestToDBTest(){

        try {
            String urlParameters = "lastname=test121" + "&firstname=test121";

            url = new URL("http://momenta.herokuapp.com/people");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            urlConnection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (urlConnection.getOutputStream());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close();

            responseCode = urlConnection.getResponseCode();
            assertEquals(201, responseCode);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
    /*
     * DELETE request that deletes a user 121 to the database
     */
    @Test
    public void DeleteRequestToDBTest(){

        try {
            String urlParameters = "lastname=test121" + "&firstname=test121";
            url = new URL("http://momenta.herokuapp.com/people/");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (urlConnection.getOutputStream());
            wr.writeBytes (urlParameters);
            wr.flush();
            wr.close();

            responseCode = urlConnection.getResponseCode();
            assertEquals(204, responseCode);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /*
     * Test to the database to verify that Smith (lastname) does exist.
     */
//    @Test
//    public void DatabaseInformationVerifyTest(){
//
//        try {
//            url = new URL("http://momenta.herokuapp.com/people/Smith");
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(urlConnection.getInputStream()));
//            String inputLine;
//
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            //print result
//            //System.out.println(response.toString());
//
//            responseCode = urlConnection.getResponseCode();
//
//            assertEquals("Smith", response.toString());
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//    }
    /*
     * Test cases are ran in the order below. First do a simple get request to verify the connection
     * to the DB/server.
     * In order to avoid the HTTP status code that throws 'item already exists in DB' we delete the
     * existing entry and then post the same entry so we can reuse the same test case over and over
     * again.
     */
    @Override
    protected Object doInBackground(Object[] params) {
        GetRequestToDBTest();
        PostRequestToDBTest();
        DeleteRequestToDBTest();

        //DatabaseInformationVerifyTest();
        return null;
    }

}