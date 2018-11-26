package com.e.k.m.a.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.k.m.a.bakingapp.R;
import com.e.k.m.a.bakingapp.models.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.mViewHolder> {
    private Context mContext;
    private ArrayList<Recipe> recipeList;
    private int[] photos;

    public RecipesAdapter(Context mContext, ArrayList<Recipe> recipeList, int[] photos) {
        this.mContext = mContext;
        this.recipeList = recipeList;
        this.photos = photos;
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivRecipe) ImageView ivRecipe;
        @BindView(R.id.tvRecipeName) TextView tvRecipeName;

        mViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_recipe, parent, false);

        return new mViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.ivRecipe.setImageResource(photos[position]);
        holder.tvRecipeName.setText(recipe.getName());

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

}