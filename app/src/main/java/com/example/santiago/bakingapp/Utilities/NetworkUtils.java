package com.example.santiago.bakingapp.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.santiago.bakingapp.Model.Ingredient;
import com.example.santiago.bakingapp.Model.Recipe;
import com.example.santiago.bakingapp.Model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * Created by Santiago on 27/01/2018.
 */

public final class  NetworkUtils {
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static JSONArray completeJson;

    public static URL createUrl() {
        Uri uri = Uri.parse(BASE_URL).buildUpon().build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Recipe> getJsonRecipes(Context context, String json) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        JSONArray jsonInitialArray = new JSONArray(json);
        completeJson = jsonInitialArray;
        for (int i = 0; i < jsonInitialArray.length(); i++) {
            JSONObject recipe = jsonInitialArray.getJSONObject(i);
            String recipeId = recipe.getString("id");
            String recipeName = recipe.getString("name");
            String recipeServings = String.valueOf(recipe.getInt("servings"));
            JSONArray recipeSteps = recipe.getJSONArray("steps");
            String recipeImageUrl = recipe.getString("image");
            JSONObject step = recipeSteps.getJSONObject(recipeSteps.length() - 1);
            String videoUrl = step.getString("videoURL");
            Bitmap bitmapImage = null;
            try {
                bitmapImage = Utils.retrieveVideoFrameFromVideo(videoUrl);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            Recipe actualRecipe = new Recipe(recipeId, recipeName, recipeServings, bitmapImage,recipeImageUrl);
            recipes.add(actualRecipe);
        }
        return recipes;
    }

    public static List<Ingredient> getRecipeIngredients(String id) throws JSONException {
        List<Ingredient> ingredients = new ArrayList<>();
        JSONObject recipe = completeJson.getJSONObject(Integer.valueOf(id) - 1);
        JSONArray recipeIngredients = recipe.getJSONArray("ingredients");
        for (int i = 0; i < recipeIngredients.length(); i++) {
            JSONObject ingredient = recipeIngredients.getJSONObject(i);
            String quantity = String.valueOf(ingredient.getInt("quantity"));
            String measure = ingredient.getString("measure");
            String ingredientName = ingredient.getString("ingredient");
            Ingredient actualIngredient = new Ingredient(quantity, measure, ingredientName);
            ingredients.add(actualIngredient);
        }
        Log.d(TAG, "getRecipeIngredients: " + recipeIngredients);
        return ingredients;
    }

    public static List<Step> getRecipeSteps(int id) throws JSONException {
        List<Step> steps = new ArrayList<>();
        JSONObject recipe = completeJson.getJSONObject(id - 1);
        JSONArray recipeSteps = recipe.getJSONArray("steps");
        for (int i = 0; i < recipeSteps.length(); i++) {
            JSONObject step = recipeSteps.getJSONObject(i);
            String shortDescription = step.getString("shortDescription");
            String description = step.getString("description");
            String videoUrl = "";
            if (step.getString("videoURL") != null) {
                videoUrl = step.getString("videoURL");
            }
            String thumbnailUrl = "";
            if (step.getString("thumbnailURL")!=null){
                thumbnailUrl = step.getString("thumbnailURL");
            }
            Step actualStep = new Step(i + 1, shortDescription, description, videoUrl,thumbnailUrl);
            Log.d(TAG, "getRecipeSteps: " + shortDescription);
            steps.add(actualStep);
        }
        Log.d(TAG, "recipes steps networks utils: " + recipeSteps);
        return steps;
    }

}


