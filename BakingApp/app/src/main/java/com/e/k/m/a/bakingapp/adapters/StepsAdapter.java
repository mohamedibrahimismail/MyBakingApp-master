package com.e.k.m.a.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e.k.m.a.bakingapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.mViewHolder> {
    private Context mContext;
    private ArrayList<String> stepList;

    public StepsAdapter(Context mContext, ArrayList<String> stepList) {
        this.mContext = mContext;
        this.stepList = stepList;
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvStep)
        TextView tvStep;

        mViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_step, parent, false);

        return new mViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        String step = stepList.get(position);

        holder.tvStep.setText(step);
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

}
