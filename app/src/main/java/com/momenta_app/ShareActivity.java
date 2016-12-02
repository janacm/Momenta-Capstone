package com.momenta_app;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.momenta_app.TaskActivity.REQUEST_INVITE;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";

    MultiAutoCompleteTextView textView;
    private boolean usersValid = true;
    private ArrayList<String> result;
    private ArrayList<String> teamMembers;
    private ArrayList<User> users;
    private String owner;
    private String lastModifiedBy;
    private Long lastModified;

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

        users = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            teamMembers = bundle.getStringArrayList(Task.TEAM);
            owner = bundle.getString(Task.OWNER);
            lastModified = bundle.getLong(Task.LAST_MODIFIED);
            lastModifiedBy = bundle.getString(Task.LAST_MODIFIED_BY);
        }

        DatabaseReference ref = FirebaseProvider.getInstance().getReference();
        ref.child("/users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        setOwner(dataSnapshot);
                        setLastModified(dataSnapshot);
                        setTeamMembers(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );



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
        textView = (MultiAutoCompleteTextView) findViewById(R.id.share_mail_field);
        textView.setAdapter(adapter);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode +
                ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent.
                String[] ids = AppInviteInvitation
                        .getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
                finish();
            } else {
                // Sending failed or it was canceled, show failure message to
                // the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    @Override
    public void finish() {
        if (result == null) {
            result = new ArrayList<>();
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Task.TEAM, result);
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
    }

    /**
     * Sets the owner text view
     * @param data The dataSnapshot to get the owner info from
     */
    private void setOwner(DataSnapshot data) {
        if (owner != null) {
            TextView ownerText = (TextView)findViewById(R.id.share_owner_value);
            ownerText.setText( (String)data.child(owner + "/displayName").getValue() );
        }
    }

    /**
     * Sets the last modified text view
     * @param data the dataSnapshot to get the user info from
     */
    private void setLastModified(DataSnapshot data) {
        if (lastModifiedBy != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(lastModified);
            String lastModText = SettingsActivity.formatDate(cal.getTime(), "MMM dd");
            lastModText += " " + getString(R.string.share_by) + " ";
            lastModText += (String)data.child(lastModifiedBy + "/displayName").getValue();
            TextView last = (TextView)findViewById(R.id.share_last_modified_value);
            last.setText(lastModText);
        }
    }

    /**
     * Initializes the team members RecyclerView and sets the data
     * @param data the dataSnapshot to get teh user info from
     */
    private void setTeamMembers(DataSnapshot data) {
        if (teamMembers != null) {
            for (String member : teamMembers) {
                User user = new User();
                user.setDisplayName( (String)data.child(member + "/displayName").getValue() );
                user.setPath(member);
                users.add(user);
            }
        }

        UserAdapter adapter = new UserAdapter(users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.share_recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
    }

    /**
     * Invites the users to join the task
     */
    private void invite() {
        String toParse = textView.getText().toString().toLowerCase().trim();
        String[] mails = toParse.replace(" ", "").split(",");
        for (String mail: mails) {
            if ( !isValidEmail(mail) ) {
                Toast.makeText(this, mail + getString(R.string.enter_valid_email),
                        Toast.LENGTH_LONG ).show();
                return;
            }
        }
        validateUsers(mails);
    }

    /**
     * Checks if the emails belong to users of the system
     * Adds the members that are users of the system & discards the others
     * @param mails
     */
    private void validateUsers(final String[] mails) {
        DatabaseReference ref = FirebaseProvider.getInstance().getReference();
        result = new ArrayList<>();
        final ArrayList<String> newUsers = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersValid = true;
                for (String mail : mails) {
                    mail = mail.replace(".",",");
                    boolean exists = dataSnapshot.child(mail).exists();
                    if (exists) {
                        result.add(mail);
                    } else {
                        newUsers.add(mail);
                    }
                    usersValid = usersValid && exists;
                }

                if ( !newUsers.isEmpty() ) {
                    sendAppInvitation(newUsers);
                } else {
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Invites a list of users to Momenta
     * @param newUsers the list of users to invite to Momenta
     */
    private void sendAppInvitation(ArrayList<String> newUsers) {
        // TODO translation of these strings.
        String message = "";
        if (newUsers.size() == 1) {
            message = "Invite " + newUsers.get(0) + " to use Momenta?";
        } else if (newUsers.size() == 2) {
            message = "Invite " + newUsers.get(0) + " & " + newUsers.get(1)
                    + " to use Momenta?";
        } else {
            message = "Invite " + newUsers.get(0) + ", " + newUsers.get(1)
                    + " & others to use Momenta?";
        }
        AlertDialog dialog = new AlertDialog.Builder(ShareActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startInviteActivity();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        dialog.show();
    }

    private void startInviteActivity(){
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
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

    /**
     * Adapter for the team members RecyclerView
     */
    private class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{

        ArrayList<User> list;

        UserAdapter(ArrayList<User> list) {
            this.list = list;
        }
        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_item, parent, false);
            return new UserViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User user = list.get(position);
            holder.displayName.setText(user.getDisplayName());
            holder.email.setText(user.getPath().replace(",","."));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    /**
     * ViewHolder for users RecyclerView
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView displayName;
        TextView email;

        UserViewHolder(View itemView) {
            super(itemView);
            displayName = (TextView) itemView.findViewById(R.id.user_display_name);
            email = (TextView) itemView.findViewById(R.id.user_email);
        }
    }
}
