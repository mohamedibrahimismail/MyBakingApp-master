package com.e.k.m.a.bakingapp.fragments;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.e.k.m.a.bakingapp.R;
import com.e.k.m.a.bakingapp.activities.DetailsActivity;
import com.e.k.m.a.bakingapp.adapters.RecipesAdapter;
import com.e.k.m.a.bakingapp.listeners.RecyclerItemClickListener;
import com.e.k.m.a.bakingapp.models.Ingredient;
import com.e.k.m.a.bakingapp.models.Recipe;
import com.e.k.m.a.bakingapp.models.Step;
import com.e.k.m.a.bakingapp.widgets.IngredientsWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class RecipesActivityFragment extends Fragment {

    @BindView(R.id.rvRecipes)
    RecyclerView rvRecipes;
    @BindView(R.id.srlRecipes)
    SwipeRefreshLayout srlRecipes;
    @BindView(R.id.pbRecipes)
    ProgressBar pbRecipes;

    LinearLayoutManager llmVertical;
    RecyclerView.LayoutManager lmGrid;
    RecipesAdapter recipesAdapter;
    ArrayList<Recipe> recipeArrayList = new ArrayList<>();

    int[] photos = {R.drawable.nutella_pie, R.drawable.brownies,
            R.drawable.yellow_cake, R.drawable.cheesecake};

    public RecipesActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("R", recipeArrayList);
        if (llmVertical != null)
            outState.putInt("P", llmVertical.findFirstVisibleItemPosition());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            recipeArrayList = savedInstanceState.getParcelableArrayList("R");
            rvRecipes.scrollToPosition(savedInstanceState.getInt("P"));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        ButterKnife.bind(this, rootView);

        Paper.init(getActivity());

        if (savedInstanceState == null) {
            if (isOnline()) {
                new FetchRecipeData().execute();
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        R.string.no_internet, Snackbar.LENGTH_LONG).show();
            }
        } else {
            recipeArrayList = savedInstanceState.getParcelableArrayList("R");
            recipesAdapter = new RecipesAdapter(getContext(), recipeArrayList, photos);
            rvRecipes.setAdapter(recipesAdapter);

            if (isTablet()) {
                lmGrid = new GridLayoutManager(getActivity(), 2);
                rvRecipes.setLayoutManager(lmGrid);
            } else {
                llmVertical = new LinearLayoutManager(getActivity());
                llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                rvRecipes.setLayoutManager(llmVertical);
            }
        }
        srlRecipes.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent, R.color.colorPrimaryDark);
        srlRecipes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    if (recipeArrayList.isEmpty()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new FetchRecipeData().execute();
                                srlRecipes.setRefreshing(false);
                            }
                        }, 2500);
                    } else {
                        srlRecipes.setRefreshing(false);
                    }
                } else {
                    srlRecipes.setRefreshing(false);
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        rvRecipes.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvRecipes,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        if (!recipeArrayList.isEmpty()) {
                            Recipe recipe = recipeArrayList.get(position);
                            Paper.book().write("rec", recipe.getName());
                            Paper.book().write("in", setIngredients(recipe.getIngredients()));
                            updateAllWidgets();
                            Intent recipeIntent = new Intent(getActivity(), DetailsActivity.class);
                            recipeIntent.putExtra("RECIPE", recipe);
                            recipeIntent.putExtra("ING", setIngredients(recipe.getIngredients()));
                            startActivity(recipeIntent);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                }));

        return rootView;
    }

    public String setIngredients(ArrayList<Ingredient> ingredients) {
        StringBuilder returnIngredients = new StringBuilder();
        returnIngredients.append("Ingredients:").append("\n");

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.get(i);
            String s = "• " + ing.getQuantity() + " " + ing.getMeasure() + "(s) " + ing.getIngredient() + ".";
            returnIngredients.append(s).append("\n");
        }

        return returnIngredients.toString();
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchRecipeData extends AsyncTask<Void, Void, ArrayList<Recipe>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbRecipes.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Recipe> doInBackground(Void... params) {

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String responseJSONStr = null;

            try {

                URL url = new URL("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json");

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                responseJSONStr = buffer.toString();

            } catch (Exception e) {
                return recipeArrayList;

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        return recipeArrayList;
                    }
                }
                try {
                    recipeArrayList = getMovieDataFromJson(responseJSONStr);
                } catch (Exception e) {
                    return recipeArrayList;
                }
            }
            return recipeArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            super.onPostExecute(recipes);
            recipesAdapter = new RecipesAdapter(getContext(), recipes, photos);
            rvRecipes.setAdapter(recipesAdapter);
            pbRecipes.setVisibility(View.GONE);

            if (isTablet()) {
                lmGrid = new GridLayoutManager(getActivity(), 2);
                rvRecipes.setLayoutManager(lmGrid);
            } else {
                llmVertical = new LinearLayoutManager(getActivity());
                llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                rvRecipes.setLayoutManager(llmVertical);
            }
        }

        private ArrayList<Recipe> getMovieDataFromJson(String jsonResponse) throws Exception {

            final String ID = "id";
            final String NAME = "name";

            final String INGREDIENTS = "ingredients";
            final String QUANTITY = "quantity";
            final String MEASURE = "measure";
            final String INGREDIENT = "ingredient";
            ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();

            final String STEPS = "steps";
            final String SHORT_DESCRIPTION = "shortDescription";
            final String DESCRIPTION = "description";
            final String VIDEO_URL = "videoURL";
            final String THUMBNAIL_URL = "thumbnailURL";
            ArrayList<Step> stepArrayList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(jsonResponse);

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectRecipes = jsonArray.getJSONObject(i);
                    String id = jsonObjectRecipes.optString(ID);
                    String name = jsonObjectRecipes.optString(NAME);

                    JSONArray jsonArrayIngredients = jsonObjectRecipes.getJSONArray(INGREDIENTS);
                    for (int j = 0; j < jsonArrayIngredients.length(); j++) {
                        JSONObject jsonObjectIngredient = jsonArrayIngredients.getJSONObject(j);
                        String quantity = jsonObjectIngredient.optString(QUANTITY);
                        String measure = jsonObjectIngredient.optString(MEASURE);
                        String ingredient = jsonObjectIngredient.optString(INGREDIENT);
                        Ingredient ingredient1 = new Ingredient(quantity, measure, ingredient);
                        ingredientArrayList.add(ingredient1);
                    }

                    JSONArray jsonArraySteps = jsonObjectRecipes.getJSONArray(STEPS);
                    for (int k = 0; k < jsonArraySteps.length(); k++) {
                        JSONObject jsonObjectStep = jsonArraySteps.getJSONObject(k);
                        String ids = jsonObjectStep.optString(ID);
                        String shDesc = jsonObjectStep.optString(SHORT_DESCRIPTION);
                        String desc = jsonObjectStep.optString(DESCRIPTION);
                        String vUrl = jsonObjectStep.optString(VIDEO_URL);
                        String thUrl = jsonObjectStep.optString(THUMBNAIL_URL);
                        Step step = new Step(ids, shDesc, desc, vUrl, thUrl);
                        stepArrayList.add(step);
                    }

                    Recipe recipe = new Recipe(id, name,
                            (ArrayList<Ingredient>) ingredientArrayList.clone(),
                            (ArrayList<Step>) stepArrayList.clone());
                    recipeArrayList.add(recipe);

                    ingredientArrayList.clear();
                    stepArrayList.clear();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return recipeArrayList;
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }

    public boolean isTablet() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        double xInches = metrics.widthPixels / metrics.xdpi;
        double yInches = metrics.heightPixels / metrics.ydpi;

        double diagonalInches = Math.sqrt(Math.pow(xInches, 2) + Math.pow(yInches, 2));

        return diagonalInches >= 6.9;
    }

    private void updateAllWidgets() {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), IngredientsWidget.class));
            if (appWidgetIds.length > 0) {
            new IngredientsWidget().onUpdate(getContext(), appWidgetManager, appWidgetIds);
        }
    }
}
