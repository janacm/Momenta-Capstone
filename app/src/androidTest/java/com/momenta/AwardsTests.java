package com.momenta;


import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class AwardsTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void addActivityLogTime() {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab),
                        withParent(allOf(withId(R.id.mainCoordinatorLayout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton.perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.newtask_name_edit_text),
                        withParent(allOf(withId(R.id.newtask_top_layout),
                                withParent(withId(R.id.activity_newtask)))),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.newtask_name_edit_text),
                        withParent(allOf(withId(R.id.newtask_top_layout),
                                withParent(withId(R.id.activity_newtask)))),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Activity 1"), closeSoftKeyboard());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.newtask_goal_layout),
                        withParent(allOf(withId(R.id.activity_newtask),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.dialog_hour_edittext), withText("2"), isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.dialog_hour_edittext), withText("2"), isDisplayed()));
        appCompatEditText4.perform(replaceText("0"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.dialog_minute_edittext), withText("30"), isDisplayed()));
        appCompatEditText5.perform(replaceText("1"));

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.add_task_done_button),
                        withParent(allOf(withId(R.id.activity_newtask),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        relativeLayout2.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withText("Goals"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.list_item_name), withText("Activity 1"),
                        childAtPosition(
                                allOf(withId(R.id.container_linear_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Activity 1")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.list_item_time_spent), withText("0M"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_recycler_view),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("0M")));

        ViewInteraction appCompatTextView2 = onView(
                allOf(withText("Awards"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withText("Goals"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.activity_recycler_view), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction relativeLayout3 = onView(
                allOf(withId(R.id.task_timespent_layout),
                        withParent(allOf(withId(R.id.scrollable_relative_laytout),
                                withParent(withId(R.id.task_scrollView))))));
        relativeLayout3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.dialog_minute_edittext), withText("00"), isDisplayed()));
        appCompatEditText6.perform(replaceText("1"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_done), withContentDescription("Done"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatTextView4 = onView(
                allOf(withText("Awards"), isDisplayed()));
        appCompatTextView4.perform(click());

        //Neophyte Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0)).check(matches(atPosition(0, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0)).check(matches(atPosition(0, hasDescendant(withText("1/1")))));

        //Perfectionnist Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(1)).check(matches(atPosition(1, hasDescendant(withText("0.03")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(1)).check(matches(atPosition(1, hasDescendant(withText("1/5")))));

        //Productive Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(2)).check(matches(atPosition(2, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(2)).check(matches(atPosition(2, hasDescendant(withText("1/5")))));

        //Trend Setter Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(3)).check(matches(atPosition(3, hasDescendant(withText("0.03")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(3)).check(matches(atPosition(3, hasDescendant(withText("1/1")))));

        //Committed Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(4)).check(matches(atPosition(4, hasDescendant(withText("0")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(4)).check(matches(atPosition(4, hasDescendant(withText("1/1")))));

        //Punctual Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(5)).check(matches(atPosition(5, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(5)).check(matches(atPosition(5, hasDescendant(withText("1/5")))));

        //Multi-Tasker Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(6)).check(matches(atPosition(6, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(6)).check(matches(atPosition(6, hasDescendant(withText("1/5")))));
    }

    @Test
    public void perfectionnistToLvl5() {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab),
                        withParent(allOf(withId(R.id.mainCoordinatorLayout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton.perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.newtask_name_edit_text),
                        withParent(allOf(withId(R.id.newtask_top_layout),
                                withParent(withId(R.id.activity_newtask)))),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.newtask_name_edit_text),
                        withParent(allOf(withId(R.id.newtask_top_layout),
                                withParent(withId(R.id.activity_newtask)))),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Activity 1"), closeSoftKeyboard());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.newtask_goal_layout),
                        withParent(allOf(withId(R.id.activity_newtask),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.dialog_hour_edittext), withText("2"), isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.dialog_hour_edittext), withText("2"), isDisplayed()));
        appCompatEditText4.perform(replaceText("0"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.dialog_minute_edittext), withText("30"), isDisplayed()));
        appCompatEditText5.perform(replaceText("1"));

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.add_task_done_button),
                        withParent(allOf(withId(R.id.activity_newtask),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        relativeLayout2.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withText("Goals"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.list_item_name), withText("Activity 1"),
                        childAtPosition(
                                allOf(withId(R.id.container_linear_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Activity 1")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.list_item_time_spent), withText("0M"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_recycler_view),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("0M")));

        ViewInteraction appCompatTextView2 = onView(
                allOf(withText("Awards"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withText("Goals"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.activity_recycler_view), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction relativeLayout3 = onView(
                allOf(withId(R.id.task_timespent_layout),
                        withParent(allOf(withId(R.id.scrollable_relative_laytout),
                                withParent(withId(R.id.task_scrollView))))));
        relativeLayout3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.dialog_minute_edittext), withText("00"), isDisplayed()));
        appCompatEditText6.perform(replaceText("1"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_done), withContentDescription("Done"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatTextView4 = onView(
                allOf(withText("Awards"), isDisplayed()));
        appCompatTextView4.perform(click());

        //Neophyte Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0)).check(matches(atPosition(0, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0)).check(matches(atPosition(0, hasDescendant(withText("1/1")))));

        //Perfectionnist Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(1)).check(matches(atPosition(1, hasDescendant(withText("0.03")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(1)).check(matches(atPosition(1, hasDescendant(withText("1/5")))));

        //Productive Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(2)).check(matches(atPosition(2, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(2)).check(matches(atPosition(2, hasDescendant(withText("1/5")))));

        //Trend Setter Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(3)).check(matches(atPosition(3, hasDescendant(withText("0.03")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(3)).check(matches(atPosition(3, hasDescendant(withText("1/1")))));

        //Committed Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(4)).check(matches(atPosition(4, hasDescendant(withText("0")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(4)).check(matches(atPosition(4, hasDescendant(withText("1/1")))));

        //Punctual Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(5)).check(matches(atPosition(5, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(5)).check(matches(atPosition(5, hasDescendant(withText("1/5")))));

        //Multi-Tasker Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(6)).check(matches(atPosition(6, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(6)).check(matches(atPosition(6, hasDescendant(withText("1/5")))));
    }

    @Test
    public void punctualToLvl5() {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab),
                        withParent(allOf(withId(R.id.mainCoordinatorLayout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton.perform(click());

        //Delay for a few secs while reveal animation plays
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.newtask_name_edit_text),
                        withParent(allOf(withId(R.id.newtask_top_layout),
                                withParent(withId(R.id.activity_newtask)))),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.newtask_name_edit_text),
                        withParent(allOf(withId(R.id.newtask_top_layout),
                                withParent(withId(R.id.activity_newtask)))),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Activity 1"), closeSoftKeyboard());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.newtask_goal_layout),
                        withParent(allOf(withId(R.id.activity_newtask),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.dialog_hour_edittext), withText("2"), isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.dialog_hour_edittext), withText("2"), isDisplayed()));
        appCompatEditText4.perform(replaceText("0"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.dialog_minute_edittext), withText("30"), isDisplayed()));
        appCompatEditText5.perform(replaceText("1"));

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.add_task_done_button),
                        withParent(allOf(withId(R.id.activity_newtask),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        relativeLayout2.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withText("Goals"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.list_item_name), withText("Activity 1"),
                        childAtPosition(
                                allOf(withId(R.id.container_linear_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Activity 1")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.list_item_time_spent), withText("0M"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_recycler_view),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("0M")));

        ViewInteraction appCompatTextView2 = onView(
                allOf(withText("Awards"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withText("Goals"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.activity_recycler_view), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction relativeLayout3 = onView(
                allOf(withId(R.id.task_timespent_layout),
                        withParent(allOf(withId(R.id.scrollable_relative_laytout),
                                withParent(withId(R.id.task_scrollView))))));
        relativeLayout3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.dialog_minute_edittext), withText("00"), isDisplayed()));
        appCompatEditText6.perform(replaceText("1"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_done), withContentDescription("Done"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatTextView4 = onView(
                allOf(withText("Awards"), isDisplayed()));
        appCompatTextView4.perform(click());

        //Neophyte Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0)).check(matches(atPosition(0, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0)).check(matches(atPosition(0, hasDescendant(withText("1/1")))));

        //Perfectionnist Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(1)).check(matches(atPosition(1, hasDescendant(withText("0.03")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(1)).check(matches(atPosition(1, hasDescendant(withText("1/5")))));

        //Productive Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(2)).check(matches(atPosition(2, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(2)).check(matches(atPosition(2, hasDescendant(withText("1/5")))));

        //Trend Setter Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(3)).check(matches(atPosition(3, hasDescendant(withText("0.03")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(3)).check(matches(atPosition(3, hasDescendant(withText("1/1")))));

        //Committed Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(4)).check(matches(atPosition(4, hasDescendant(withText("0")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(4)).check(matches(atPosition(4, hasDescendant(withText("1/1")))));

        //Punctual Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(5)).check(matches(atPosition(5, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(5)).check(matches(atPosition(5, hasDescendant(withText("1/5")))));

        //Multi-Tasker Award Progress check
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(6)).check(matches(atPosition(6, hasDescendant(withText("1")))));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(6)).check(matches(atPosition(6, hasDescendant(withText("1/5")))));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    
}
