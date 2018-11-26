package com.e.k.m.a.bakingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e.k.m.a.bakingapp.R;
import com.e.k.m.a.bakingapp.adapters.StepsAdapter;
import com.e.k.m.a.bakingapp.listeners.RecyclerItemClickListener;
import com.e.k.m.a.bakingapp.listeners.StepListener;
import com.e.k.m.a.bakingapp.models.Recipe;
import com.e.k.m.a.bakingapp.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivityFragment extends Fragment {

    @BindView(R.id.rvSteps)
    RecyclerView rvSteps;

    LinearLayoutManager llmVerticalSteps;
    StepsAdapter stepsAdapter;

    Recipe recipeDetails;
    String ingredients;

    ArrayList<String> steps = new ArrayList<>();

    StepListener stepListener;

    public void setStepListener(StepListener stepListener) {
        this.stepListener = stepListener;
    }

    public DetailsActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("S", steps);
        outState.putParcelable("R", recipeDetails);
        outState.putInt("P", llmVerticalSteps.findFirstCompletelyVisibleItemPosition());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            steps = savedInstanceState.getStringArrayList("S");
            recipeDetails = savedInstanceState.getParcelable("R");
            rvSteps.scrollToPosition(savedInstanceState.getInt("P"));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState == null) {
            Intent recipeData = getActivity().getIntent();
            recipeDetails = recipeData.getParcelableExtra("RECIPE");
            getActivity().setTitle(recipeDetails.getName());
            ingredients = recipeData.getStringExtra("ING");
            steps.add(ingredients);

            for (int i = 0; i < recipeDetails.getSteps().size(); i++) {
                steps.add((i + 1) + ". " + recipeDetails.getSteps().get(i).getShortDescription());
            }

            stepsAdapter = new StepsAdapter(getContext(), steps);
            rvSteps.setAdapter(stepsAdapter);
            llmVerticalSteps = new LinearLayoutManager(getActivity());
            llmVerticalSteps.setOrientation(LinearLayoutManager.VERTICAL);
            rvSteps.setLayoutManager(llmVerticalSteps);

        } else {
            recipeDetails = savedInstanceState.getParcelable("R");
            getActivity().setTitle(recipeDetails.getName());
            steps = savedInstanceState.getStringArrayList("S");
            stepsAdapter = new StepsAdapter(getContext(), steps);
            rvSteps.setAdapter(stepsAdapter);
            llmVerticalSteps = new LinearLayoutManager(getActivity());
            llmVerticalSteps.setOrientation(LinearLayoutManager.VERTICAL);
            rvSteps.setLayoutManager(llmVerticalSteps);
        }

        rvSteps.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvSteps,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        if (!steps.isEmpty() && position != 0) {
                            Step step = recipeDetails.getSteps().get(position - 1);
                            stepListener.setSelectedStep(step, recipeDetails, position - 1);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                }));

        return rootView;
    }
}
