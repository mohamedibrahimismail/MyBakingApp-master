package com.e.k.m.a.bakingapp.listeners;

import com.e.k.m.a.bakingapp.models.Recipe;
import com.e.k.m.a.bakingapp.models.Step;

public interface StepListener {

    void setSelectedStep(Step step, Recipe recipe, int position);
}
