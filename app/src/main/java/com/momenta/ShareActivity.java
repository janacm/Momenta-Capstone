package com.momenta;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ShareActivity extends AppCompatActivity {

    AutoCompleteTextView textView;
    private boolean usersValid = true;
    private ArrayList<String> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ArrayList<String> emailAddressCollection = new ArrayList<String>();
        ContentResolver cr = getContentResolver();
        Cursor emailCur = null;
        if ( havePermissions(Manifest.permission.READ_CONTACTS) ) {
            emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
        }

        while (emailCur!=null && emailCur.moveToNext()) {
            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            emailAddressCollection.add(email);
        }
        if (emailCur!=null) {
            emailCur.close();
        }

        String[] emailAddresses = new String[emailAddressCollection.size()];
        emailAddressCollection.toArray(emailAddresses);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, emailAddresses);
        textView = (AutoCompleteTextView)findViewById(R.id.share_mail_text);
        textView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_invite:
                invite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void invite() {
        String toParse = textView.getText().toString().trim();
        String[] mails = toParse.split(",");
        for (String mail: mails) {
            if ( !isValidEmail(mail) ) {
                Toast.makeText(this, mail + getString(R.string.enter_valid_email),
                        Toast.LENGTH_LONG ).show();
                return;
            }
        }
        validateUsers(mails);
    }

    private void validateUsers(final String[] mails) {
        DatabaseReference ref = FirebaseProvider.getInstance().getReference();
        result = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersValid = true;
                for (String mail : mails) {
                    mail = mail.replace(".",",");
                    boolean exists = dataSnapshot.child(mail).exists();
                    if (exists) {
                        result.add(mail);
                    }
                    usersValid = usersValid && exists;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Check if the email provided is a valid mail
     * @param target the mail in question
     * @return true if the target mail is valid, false otherwise
     */
    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Check if the specified permissions specified have been granted
     * @param permissionsId the permissions to check on
     * @return True if the all permissions are granted, False Otherwise
     */
    private boolean havePermissions(String... permissionsId) {
        boolean result = true;
        for (String p : permissionsId) {
            result = result && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }
        return result;
    }
}
