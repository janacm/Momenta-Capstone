package com.momenta;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joesi on 2016-09-25.
 */

public class awardManager {
    private Context context;
    private helperPreferences helperPreferences;
    private static awardManager instance;
    private DatabaseReference databaseReference;
    private Award award;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    public static awardManager getInstance(Context context) {
        if (instance == null) {
            instance = new awardManager(context);
        }
        return instance;
    }

    private awardManager(Context context) {
        helperPreferences = new helperPreferences(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }
        databaseReference = firebaseDatabase.getReference();
    }


    public void increaseAwardProgress(final String awardId, final double progressAmount, final Task task) {
        Log.d("Progress amount", String.valueOf(progressAmount));
        final String award_id = helperPreferences.getPreferences(awardId, "");
        award = new Award();
        databaseReference.child(user.getUid() + "/awards" + "/" + award_id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        award = dataSnapshot.getValue(Award.class);
                        if (award != null) {
                            if (handleAwardTaskProgressIncrease(award, award_id, task)) {
                                //Check first if award is not completed
                                if (!(award.getCurrentProgress() >= award.getProgressLimitEachLevel().get(award.getMaxLevel() - 1))) {
                                    //Case 1: Normal case where the award gets progress without making it increase to the next level
                                    if (award.getCurrentProgress() + progressAmount <= award.getProgressLimitEachLevel().get(award.getCurrentLevel())) {
                                        award.setCurrentProgress(award.getCurrentProgress() + progressAmount);
                                    }
                                    //Case 2: Case where the award gets progress making it progress to the next level
                                    else if (award.getCurrentProgress() + progressAmount > award.getProgressLimitEachLevel().get(award.getCurrentLevel())) {
                                        if (award.getCurrentLevel() + 1 <= award.getMaxLevel()) {
                                            award.setCurrentLevel(award.getCurrentLevel() + 1);
                                            award.setCurrentProgress(progressAmount);
                                        }
                                    }
                                }
                                //Case 3: Case where the award gets completed
                                else {
                                    //TODO:Make something cool when award is complete;
                                }
                            }
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(user.getUid() + "/awards" + "/" + award_id, award.toMap());
                            databaseReference.updateChildren(childUpdates);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("DatabaseError", databaseError.toString());
                    }

                }
        );
    }
    //handles some extra awards porgress logic
    public boolean handleAwardTaskProgressIncrease(Award award, String award_id, Task task) {
        String task_id = task.getId();
        //NOTE: Task id array list is empty when get(0) == ""
        if (award_id.equals(helperPreferences.getPreferences(Constants.SHARE_COMMITTED_AWARD_ID, ""))) {
            if (award.getTaskIDs().get(0).equals("")) {
                award.getTaskIDs().set(0, task_id);
                return false;
            }
            if (award.getTaskIDs().contains(task_id)) {
                return true;
            } else {
                award.getTaskIDs().add(task_id);
                return false;
            }
        } else if (award_id.equals(helperPreferences.getPreferences(Constants.SHPREF_NEOPHYTE_AWARD_ID, ""))) {
            return true;
        } else if (award_id.equals(helperPreferences.getPreferences(Constants.SHPREF_TREND_SETTER_AWARD_ID, ""))) {
            if (award.getTaskIDs().get(0).equals("")) {
                award.getTaskIDs().set(0, task_id);
            }
            if (award.getTaskIDs().indexOf(task_id) == 0) {
                if (award.getCurrentProgress() < task.getTimeSpent() / 60.0) {
                    award.setCurrentProgress(task.getTimeSpent() / 60.0);
                    return true;
                }
            } else {
                if (award.getCurrentProgress() < task.getTimeSpent() / 60.0) {
                    award.getTaskIDs().set(0, task_id);
                    award.setCurrentProgress(task.getTimeSpent() / 60.0);
                    return true;
                }
                return false;
            }
        } else if (award_id.equals(helperPreferences.getPreferences(Constants.SHPREF_MULTI_TASKER_AWARD_ID, ""))) {
            if (award.getTaskIDs().get(0).equals("")) {
                award.getTaskIDs().set(0, task_id);
                return true;
            }
            if (award.getTaskIDs().contains(task_id)) {
                return false;
            } else {
                return true;
            }
        } else if (award_id.equals(helperPreferences.getPreferences(Constants.SHPREF_PRODUCTIVE_AWARD_ID, ""))) {
            return true;
        } else if (award_id.equals(helperPreferences.getPreferences(Constants.SHPREF_PERFECTIONIST_AWARD_ID, ""))) {
            if (award.getTaskIDs().get(0).equals("")) {
                award.getTaskIDs().set(0, task_id);
            }
            if (award.getTaskIDs().indexOf(task_id) == 0) {
                if (award.getCurrentProgress() < task.getTimeSpent() / 60.0) {
                    award.setCurrentProgress(task.getTimeSpent() / 60.0);
                    return true;
                }
            } else {
                if (award.getCurrentProgress() < task.getTimeSpent() / 60.0) {
                    award.getTaskIDs().set(0, task_id);
                    award.setCurrentProgress(task.getTimeSpent() / 60.0);
                    return true;
                }
                return false;
            }
        } else if (award_id.equals(helperPreferences.getPreferences(Constants.SHPREF_PUNCTUAL_AWARD_ID, ""))) {
            return true;
        }
        return false;
    }
}
