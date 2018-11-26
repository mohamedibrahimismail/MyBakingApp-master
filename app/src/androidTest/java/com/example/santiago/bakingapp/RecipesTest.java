package com.example.santiago.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.santiago.bakingapp.Adapters.RecyclerRecipesAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RecipesTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void check_recipe_name() {
        onView(withId(R.id.recycler_recipes)).perform(RecyclerViewActions.scrollToHolder(withRecyclerView("Nutella Pie")));
        onView(withId(R.id.recycler_recipes)).perform(RecyclerViewActions.scrollToHolder(withRecyclerView("Brownies")));
        onView(withId(R.id.recycler_recipes)).perform(RecyclerViewActions.scrollToHolder(withRecyclerView("Yellow Cake")));
        onView(withId(R.id.recycler_recipes)).perform(RecyclerViewActions.scrollToHolder(withRecyclerView("Cheesecake")));
    }
    public static Matcher< RecyclerView . ViewHolder >  withRecyclerView(final String text) {
        return new BoundedMatcher<RecyclerView.ViewHolder, RecyclerRecipesAdapter.RecipeViewHolder>(RecyclerRecipesAdapter.RecipeViewHolder.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("No viewHolder found with text = "+text);
            }
            @Override
            protected boolean matchesSafely(RecyclerRecipesAdapter.RecipeViewHolder item) {
                TextView timeViewText = item.itemView.findViewById(R.id.recipe_name);
                return timeViewText != null && timeViewText.getText().toString().contains(text);
            }
        };
    }

}
