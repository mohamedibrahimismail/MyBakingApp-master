package com.example.santiago.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.example.santiago.bakingapp.Fragments.StepDetailFragment;
import com.example.santiago.bakingapp.Model.Step;

import java.util.List;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.ChangeStepClickListener {
    private static final String TAG = StepDetailActivity.class.getSimpleName();

    private int mStepId;
    private List<Step> stepsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Intent intent = getIntent();
        mStepId = intent.getIntExtra("id", 0);
        stepsList = intent.getParcelableArrayListExtra("steps_extra");
        if (savedInstanceState == null) {
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setStepList(stepsList);
            stepDetailFragment.setStepData(mStepId);
            getSupportFragmentManager().beginTransaction().add(R.id.rec, stepDetailFragment).commit();
        }

    }

    @Override
    public void changeStepClickListener(int newInt) {
        int step = newInt + 1;
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setStepList(stepsList);
        stepDetailFragment.setStepData(step);
        getSupportFragmentManager().beginTransaction().replace(R.id.rec, stepDetailFragment).commit();

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
