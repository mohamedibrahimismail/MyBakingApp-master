package com.example.santiago.bakingapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.santiago.bakingapp.Model.Step;
import com.example.santiago.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santiago on 29/01/2018.
 */

public class RecyclerStepsAdapter extends RecyclerView.Adapter<RecyclerStepsAdapter.StepsViewHolder> {
    List<Step> stepsList = new ArrayList<>();
    private Context mContext;
    final private StepOnclickListener mStepOnclickListener;

    public interface StepOnclickListener {
        void onClick(Step stepClicked);
    }

    public RecyclerStepsAdapter(Context context, StepOnclickListener stepOnclickListener) {
        mContext = context;
        mStepOnclickListener = stepOnclickListener;
    }


    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_step, parent, false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {
        Step step = stepsList.get(position);
        holder.sTepTextView.setText(step.getShortDescription());
        String thumbnailUrl = step.getThumbnailUrl();

        if (thumbnailUrl!=null && !thumbnailUrl.isEmpty()){
            Picasso.get().load(Uri.parse(thumbnailUrl)).
                    error(R.drawable.default_step_image)
                    .into(holder.thumbnail);
        }
        holder.stepIndex.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        if (stepsList == null) return 0;
        return stepsList.size();
    }

    public void setData(List<Step> stepsListReceived) {
        stepsList = stepsListReceived;
        notifyDataSetChanged();
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView sTepTextView;
        final ImageView thumbnail;
        private final TextView stepIndex;

        // private final ImageView stepImageView;
        public StepsViewHolder(View itemView) {
            super(itemView);
            sTepTextView = itemView.findViewById(R.id.title_step);
            //stepImageView = itemView.findViewById(R.id.step_image);
            stepIndex = itemView.findViewById(R.id.step_number);
            thumbnail = itemView.findViewById(R.id.image_step);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mStepOnclickListener.onClick(stepsList.get(getAdapterPosition()));
        }
    }
}