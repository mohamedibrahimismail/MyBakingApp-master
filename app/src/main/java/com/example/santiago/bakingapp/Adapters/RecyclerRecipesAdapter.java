package com.example.santiago.bakingapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.santiago.bakingapp.Model.Recipe;
import com.example.santiago.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Santiago on 28/01/2018.
 */

public class RecyclerRecipesAdapter extends RecyclerView.Adapter<RecyclerRecipesAdapter.RecipeViewHolder> {
    private List<Recipe> mRecipesList;
    private Context mContext;
    final private RecipesOnClickListener mRecipesOnClickListener;

    public interface RecipesOnClickListener {
        void onClick(Recipe recipeClicked);
    }

    public RecyclerRecipesAdapter(Context context, RecipesOnClickListener recipesOnClickListener) {
        mContext = context;
        mRecipesOnClickListener = recipesOnClickListener;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView recipeName;
        public final TextView recipeServings;
        public final ImageView recipeImage;
        public final ImageView addRecipeWidget;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeServings = itemView.findViewById(R.id.recipe_servings);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            addRecipeWidget = itemView.findViewById(R.id.add_recipe_widget);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mRecipesOnClickListener.onClick(mRecipesList.get(getAdapterPosition()));
        }

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_recipes, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        Recipe actualRecipe = mRecipesList.get(position);
        holder.recipeName.setText(actualRecipe.getRecipeName());
        holder.recipeServings.setText(String.valueOf(actualRecipe.getServings()));
        String recipeUrl = actualRecipe.getImageUrl();
        if (recipeUrl != null && !recipeUrl.isEmpty()) {
            Picasso.get().load(Uri.parse(recipeUrl)).into(holder.recipeImage);
        } else {
            holder.recipeImage.setImageBitmap(actualRecipe.getImageBitmap());
        }
    }

    @Override
    public int getItemCount() {
        if (null == mRecipesList) return 0;
        return mRecipesList.size();
    }

    public void setData(List<Recipe> recipesList) {
        mRecipesList = recipesList;
        notifyDataSetChanged();
    }

}
