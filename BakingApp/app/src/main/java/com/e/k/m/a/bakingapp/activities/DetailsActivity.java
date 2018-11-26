package com.e.k.m.a.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.e.k.m.a.bakingapp.R;
import com.e.k.m.a.bakingapp.fragments.DetailsActivityFragment;
import com.e.k.m.a.bakingapp.fragments.MediaActivityFragment;
import com.e.k.m.a.bakingapp.listeners.StepListener;
import com.e.k.m.a.bakingapp.models.Recipe;
import com.e.k.m.a.bakingapp.models.Step;


public class DetailsActivity extends AppCompatActivity implements StepListener {

    DetailsActivityFragment detailsActivityFragment;
    MediaActivityFragment mediaActivityFragment;
    boolean isTwoPane = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "DF", detailsActivityFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null != findViewById(R.id.flMedia)) {
            isTwoPane = true;
        }

        if (savedInstanceState == null) {
            detailsActivityFragment = new DetailsActivityFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.flSteps, detailsActivityFragment, "").commit();
        } else {
            detailsActivityFragment = (DetailsActivityFragment) getSupportFragmentManager().getFragment(savedInstanceState, "DF");
        }

        detailsActivityFragment.setStepListener(this);
    }

    @Override
    public void setSelectedStep(Step step, Recipe recipe, int position) {

        if (!isTwoPane) {
            Intent stepIntent = new Intent(this, MediaActivity.class);
            stepIntent.putExtra("STEP", step);
            stepIntent.putExtra("RCP", recipe);
            stepIntent.putExtra("POS", position);
            startActivity(stepIntent);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable("STEP", step);
            bundle.putParcelable("RCP", recipe);
            bundle.putInt("POS", position);

            mediaActivityFragment = new MediaActivityFragment();
            mediaActivityFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.flMedia, mediaActivityFragment, "").commit();

        }
    }
}
