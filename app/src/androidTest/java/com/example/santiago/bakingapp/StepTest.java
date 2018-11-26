package com.example.santiago.bakingapp;

import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.example.santiago.bakingapp.Adapters.RecyclerStepsAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Instrumentation.ActivityResult;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class StepTest {

    @Rule public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<>(MainActivity.class);


    @Before
    public void Stub_Intent(){
        intending(not(isInternal())).
              respondWith(new ActivityResult(Activity.RESULT_OK,null));
    }

    @Test
    public void check_ingredients(){
        onView(withId(R.id.recycler_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        intended(allOf(
                hasExtra("id","1"),
                hasExtra("recipe_name","Nutella Pie")
        ));
        onView(withId(R.id.ingrediets_recycler)).check(matches(isDisplayed()));

        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                childAtPosition(
                        withId(R.id.tab_layout),
                        0),
                1),
                isDisplayed()));
        tabView.perform(click());

        onView(withId(R.id.steps_recycler_view)).perform(
                RecyclerViewActions.scrollToHolder(withStepsRecyclerView("Recipe Introduction")));

        onView(withId(R.id.steps_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0,click()));





    }

    public static org.hamcrest.Matcher< RecyclerView . ViewHolder > withStepsRecyclerView(final String text) {
        return new BoundedMatcher<RecyclerView.ViewHolder, RecyclerStepsAdapter.StepsViewHolder>(RecyclerStepsAdapter.StepsViewHolder.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText(text + " Not found");
            }

            @Override
            protected boolean matchesSafely(RecyclerStepsAdapter.StepsViewHolder item) {
                TextView textView = item.itemView.findViewById(R.id.title_step);
                return textView != null && textView.getText().toString().contains(text);
            }
        };
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
}
