//package com.momenta;
//
//import android.app.Instrumentation;
//import android.content.Context;
//import android.content.Intent;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.espresso.contrib.PickerActions;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//import android.widget.DatePicker;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import org.hamcrest.Matchers;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.Calendar;
//import java.util.concurrent.TimeUnit;
//
//import static android.support.test.espresso.Espresso.onData;
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static android.support.test.espresso.action.ViewActions.replaceText;
//import static android.support.test.espresso.action.ViewActions.scrollTo;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
//import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.Matchers.allOf;
//import static org.hamcrest.Matchers.containsString;
//import static org.hamcrest.Matchers.instanceOf;
//
//
//@RunWith(AndroidJUnit4.class)
//public class ViewActivityTest {
//
//    private static final String TAG = "ViewActivityTest";
//    String id;
//    Context context;
//    helperPreferences helperPreferences;
//    DatabaseReference reference;
//    String directory;
//    @Rule
//    public ActivityTestRule<TaskActivity> rule =
//            new ActivityTestRule(TaskActivity.class, true, false);
//
//    @Before
//    public void setUp() {
//        Instrumentation instrumentation
//                = InstrumentationRegistry.getInstrumentation();
//        context= instrumentation.getTargetContext();
//        helperPreferences = new helperPreferences(context);
//
//        Calendar deadline = Calendar.getInstance();
//        deadline.setTimeInMillis(deadline.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.HOURS));
//
//        Task task = new Task("Initial Task Name", 400, deadline,
//                Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
//        task.setTimeSpent(200);
//
//        FirebaseDatabase firebaseDatabase = FirebaseProvider.getInstance();
//        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (mFirebaseUser != null) {
//            directory = mFirebaseUser.getUid() + "/goals";
//            Log.w(TAG, "Not logged in");
//        } else {
//            Log.w(TAG, "Not logged in");
//        }
//        reference = firebaseDatabase.getReference();
//        id = reference.child(directory).push().getKey();
//        task.setId(id);
//        reference.child(directory + "/" + id).setValue(task);
//
//        Intent intent = new Intent();
//        intent.putExtra(Task.ID, id);
//        rule.launchActivity(intent);
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testUpdateActivityName() {
//        String name = "Rumpelstiltskin once upon a time";
//        //Replace the text in teh textView
//        onView(withId(R.id.task_name_edit_text))
//                .perform(replaceText(name), closeSoftKeyboard());
//
//        restartTaskActivity();
//
//        //Ensure the change has taken place
//        onView(withId(R.id.task_name_edit_text)).check(matches(withText(name)));
//    }
//
//    @Test
//    public void testUpdateActivityGoal() {
//        //Input new values into the dialog 3H 20M
//        onView(withId(R.id.task_hour_edit_text)).perform(replaceText("3"));
//        onView(withId(R.id.task_minute_edit_text)).perform(replaceText("20"));
//
//        restartTaskActivity();
//
//        //Verify time value was saved (3H 20M)
//        onView(withId(R.id.task_hour_edit_text)).check(matches(withText("3")));
//        onView(withId(R.id.task_minute_edit_text)).check(matches(withText("20")));
//    }
//
//    @Test
//    public void testUpdateActivityDeadline() {
//        int year = 2030;
//        int month = 5;//June, for some reason 0 is January
//        int day = 15;
//        Calendar cal = Calendar.getInstance();
//        cal.set(year, month, day);
//        String expected = Task.getDateFormat(cal);
//
//        //Perform scroll for smaller screens
//        onView(allOf(withId(R.id.task_deadline_layout),
//                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(scrollTo());
//
//        //Click on the deadline layout to popup views
//        onView(allOf(withId(R.id.task_deadline_layout),
//                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(click());
//
//        //Set the date of the picker
//        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
//        onView(withId(android.R.id.button1)).perform(click());
//
//        restartTaskActivity();
//
//        //Verify the date was changed
//        onView(withId(R.id.task_time_set_deadline)).check(matches(withText(expected)));
//    }
//
//    @Test
//    public void testUpdateActivityPriority(){
////        long id = taskID;
//        String priority = "Very High";
//
//        //Perform scroll for smaller screens
//        onView(allOf(withId(R.id.task_priority_spinner),
//                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(scrollTo());
//
//        //Click on the spinner
//        onView(allOf(withId(R.id.task_priority_spinner),
//                isDescendantOfA(withId(R.id.scrollable_relative_laytout)))).perform(click());
//
//        // Select a priority from the spinner
//        onData(allOf(is(instanceOf(String.class)), is(priority))).perform(click());
//
//        restartTaskActivity();
//
//        //Verify the value of the spinner
//        onView(allOf(withId(R.id.task_priority_spinner),
//                isDescendantOfA(withId(R.id.scrollable_relative_laytout))))
//                .check(matches(withSpinnerText(containsString(priority))));
//    }
//
//    /**
//     * Convenience method to restart the TaskActivity.
//     * Clicks on the done button and then calls the setup method.
//     */
//    public void restartTaskActivity() {
//        //Click on the back button and restart the TaskActivity.
//        onView(withId(R.id.action_done)).perform(click());
//
//        Intent intent = new Intent();
//        intent.putExtra(DBHelper.ACTIVITY_ID, id);
//        rule.launchActivity(intent);
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
