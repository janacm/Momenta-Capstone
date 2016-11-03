package com.momenta;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous Task to create events in Google Calendar
 */

public class GoogleCalendarIntegration extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "CalendarIntegration";
    private com.google.api.services.calendar.Calendar mService = null;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private String summary;
    private Integer eventDuration;
    private Calendar calendar;
    private Context context;
    private Exception mLastError = null;

    public GoogleCalendarIntegration(Context context, Account account, String summary, int totalTime) {
        calendar = Calendar.getInstance();
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        this.summary = summary;
        this.context = context;
        com.momenta.helperPreferences helperPreferences = new helperPreferences(context);

        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                context.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccount(account);

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Momenta")
                .build();

        String intervalHours = helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "1");
        String intervalMins = helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0");
        Integer hours = Integer.valueOf(intervalHours);
        Integer mins = Integer.valueOf(intervalMins);
        Long intervalMillis = TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS)
                + TimeUnit.MILLISECONDS.convert(mins, TimeUnit.MINUTES);
        eventDuration = intervalMillis.intValue();
        totalTime = (int)TimeUnit.MILLISECONDS.convert(totalTime, TimeUnit.MINUTES);
        if ( totalTime > eventDuration) {
            eventDuration = totalTime;
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            createEvent();
            return true;
        } catch (IOException e) {
            mLastError = e;
            cancel(true);
            return false;
        }
    }

    /**
     * Creates the calendar event
     */
    private void createEvent() throws IOException {
        Event event = new Event().setSummary(summary);

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis( calendar.getTimeInMillis() - eventDuration.longValue() );
        DateTime startDateTime = new DateTime( startCal.getTime() );
        EventDateTime eventStart = new EventDateTime().setDateTime(startDateTime);
        event.setStart(eventStart);

        DateTime endDateTime = new DateTime( calendar.getTime() );
        EventDateTime eventEnd = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(eventEnd);

        String calendarId = "primary";
        mService.events().insert(calendarId, event).execute();
    }

    @Override
    protected void onPostExecute(Boolean created) {
        //TODO Toast message if a calender event is created?
        super.onPostExecute(created);
        if (created) {
            Toast.makeText(context, R.string.created_event_string, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Activity activity = (AppCompatActivity)context;
        if (mLastError != null) {
            if (mLastError instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        TaskActivity.REQUEST_AUTHORIZATION);
            }
        }
    }
}
