package com.example.santiago.bakingapp.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.santiago.bakingapp.Model.Ingredient;
import com.example.santiago.bakingapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Santiago on 12/02/2018.
 */

public class IngredientsWidgetProvider extends AppWidgetProvider {
    public static final String KEY_EXTRA_INGREDIENTS = "key_extra_ingredients";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] widgetIds = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(new ComponentName(context, IngredientsWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.widget_ingredients_list);
        }
        super.onReceive(context, intent);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String recipeName) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);
        Intent intent = new Intent(context, ServiceBindWidget.class);
        remoteViews.setRemoteAdapter(R.id.widget_ingredients_list, intent);
        remoteViews.setTextViewText(R.id.widget_title, recipeName);
        remoteViews.setEmptyView(R.id.widget_ingredients_list,R.id.empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String recipeName = getRecipeName(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName);
        }
    }

    public static List<Ingredient> getIngredientList(Context context) {
        List<Ingredient> ingredients;
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences",
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String result = sharedPreferences.getString("ingredients", null);
        Ingredient[] arrayIngredient = gson.fromJson(result, Ingredient[].class);
        if (arrayIngredient != null) {
            ingredients = Arrays.asList(arrayIngredient);
            ingredients = new ArrayList<>(ingredients);
            return ingredients;
        }
        return null;
    }

    public String getRecipeName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences",
                Context.MODE_PRIVATE);
        return sharedPreferences.getString("recipe_name", null);
    }
}
