package com.e.k.m.a.bakingapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.k.m.a.bakingapp.R;
import com.e.k.m.a.bakingapp.models.Recipe;
import com.e.k.m.a.bakingapp.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.squareup.picasso.Picasso;

import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MediaActivityFragment extends Fragment implements ExoPlayer.EventListener {

    @BindView(R.id.exoStepVideo)
    SimpleExoPlayerView exoStepVideo;

    @BindView(R.id.ivThumb)
    ImageView ivThumb;

    @BindView(R.id.tvStepDesc)
    TextView tvStepDesc;

    @BindView(R.id.llNavigation)
    LinearLayout llNavigation;

    @BindView(R.id.tvStepNum)
    TextView tvStepNum;

    @BindView(R.id.ibNext)
    ImageButton ibNext;

    @BindView(R.id.ibPrev)
    ImageButton ibPrev;

    Step step;
    Recipe recipe;
    int playlistPosition;
    long videoPosition;
    boolean videoState;

    private SimpleExoPlayer exoPlayer;

    public MediaActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("STP", step);
        outState.putParcelable("RC", recipe);
        outState.putInt("PP", playlistPosition);
        outState.putLong("VP", videoPosition);
        outState.putBoolean("VS", videoState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            step = savedInstanceState.getParcelable("STP");
            recipe = savedInstanceState.getParcelable("RC");
            playlistPosition = savedInstanceState.getInt("PP");
            videoPosition = savedInstanceState.getLong("VP");
            videoState = savedInstanceState.getBoolean("VS");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState == null) {
            Intent stepData = getActivity().getIntent();
            step = stepData.getParcelableExtra("STEP");
            if (step == null) {
                step = getArguments().getParcelable("STEP");
                recipe = getArguments().getParcelable("RCP");
                playlistPosition = getArguments().getInt("POS", 1);
                llNavigation.setVisibility(View.GONE);
            } else {
                recipe = stepData.getParcelableExtra("RCP");
                playlistPosition = stepData.getIntExtra("POS", 1);
                getActivity().setTitle(recipe.getName());
            }
        } else {
            step = savedInstanceState.getParcelable("STP");
            recipe = savedInstanceState.getParcelable("RC");
            playlistPosition = savedInstanceState.getInt("PP");
            videoPosition = savedInstanceState.getLong("VP");
            videoState = savedInstanceState.getBoolean("VS");
            if (!isTablet()) {
                getActivity().setTitle(recipe.getName());
            } else {
                llNavigation.setVisibility(View.GONE);
            }
        }

        if (step.getVideoUrl().equals("") && step.getThumbnailUrl().equals("")) {
            exoStepVideo.setVisibility(View.GONE);
        } else {
            if (savedInstanceState == null) {
                exoStepVideo.setVisibility(View.VISIBLE);
                if (!step.getVideoUrl().equals("")) {
                    initializeVideoPlayer(Uri.parse(step.getVideoUrl()));
                } else {
                    if (isVideo(step.getThumbnailUrl())) {
                        initializeVideoPlayer(Uri.parse(step.getThumbnailUrl()));
                        ivThumb.setVisibility(View.GONE);
                    } else {
                        ivThumb.setVisibility(View.VISIBLE);
                        Picasso.with(getContext())
                                .load(step.getThumbnailUrl())
                                .into(ivThumb);
                        exoStepVideo.setVisibility(View.GONE);
                    }
                }
            } else {
                if (step.getVideoUrl().equals("") && step.getThumbnailUrl().equals("")) {
                    exoStepVideo.setVisibility(View.GONE);
                } else {
                    if (!step.getVideoUrl().equals("")) {
                        initializeVideoPlayer(Uri.parse(step.getVideoUrl()));
                        exoPlayer.seekTo(videoPosition);
                        exoPlayer.setPlayWhenReady(videoState);
                    } else {
                        if (isVideo(step.getThumbnailUrl())) {
                            initializeVideoPlayer(Uri.parse(step.getThumbnailUrl()));
                            exoPlayer.seekTo(videoPosition);
                            exoPlayer.setPlayWhenReady(videoState);
                            ivThumb.setVisibility(View.GONE);
                        } else {
                            ivThumb.setVisibility(View.VISIBLE);
                            Picasso.with(getContext())
                                    .load(step.getThumbnailUrl())
                                    .into(ivThumb);
                            exoStepVideo.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }

        tvStepDesc.setText(step.getDescription());

        String playlist = playlistPosition + 1 + "/" + recipe.getSteps().size();
        tvStepNum.setText(playlist);

        if (recipe.getSteps().size() == playlistPosition + 1) {
            ibNext.setVisibility(View.INVISIBLE);
        }

        if (playlistPosition == 0) {
            ibPrev.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            videoPosition = exoPlayer.getCurrentPosition();
            videoState = exoPlayer.getPlayWhenReady();
        }
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @OnClick(R.id.ibNext)
    public void next(View view) {
        Step nextStep = recipe.getSteps().get(++playlistPosition);
        step = nextStep;
        releasePlayer();
        if (nextStep.getVideoUrl().equals("") && nextStep.getThumbnailUrl().equals("")) {
            exoStepVideo.setVisibility(View.GONE);
        } else {
            exoStepVideo.setVisibility(View.VISIBLE);
            if (!nextStep.getVideoUrl().equals("")) {
                initializeVideoPlayer(Uri.parse(nextStep.getVideoUrl()));
            } else {
                if (isVideo(step.getThumbnailUrl())) {
                    initializeVideoPlayer(Uri.parse(nextStep.getThumbnailUrl()));
                } else {
                    ivThumb.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(nextStep.getThumbnailUrl())
                            .into(ivThumb);
                    exoStepVideo.setVisibility(View.GONE);
                }
            }
        }
        tvStepDesc.setText(nextStep.getDescription());

        String playlist = playlistPosition + 1 + "/" + recipe.getSteps().size();
        tvStepNum.setText(playlist);

        if (recipe.getSteps().size() == playlistPosition + 1) {
            view.setVisibility(View.INVISIBLE);
        }

        if (playlistPosition != 0) {
            ibPrev.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ibPrev)
    public void prev(View view) {
        Step prevStep = recipe.getSteps().get(--playlistPosition);
        step = prevStep;
        releasePlayer();
        if (prevStep.getVideoUrl().equals("") && prevStep.getThumbnailUrl().equals("")) {
            exoStepVideo.setVisibility(View.GONE);
        } else {
            exoStepVideo.setVisibility(View.VISIBLE);
            if (!prevStep.getVideoUrl().equals("")) {
                initializeVideoPlayer(Uri.parse(prevStep.getVideoUrl()));
            } else {
                if (isVideo(step.getThumbnailUrl())) {
                    initializeVideoPlayer(Uri.parse(prevStep.getThumbnailUrl()));
                } else {
                    ivThumb.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(prevStep.getThumbnailUrl())
                            .into(ivThumb);
                    exoStepVideo.setVisibility(View.GONE);
                }
            }
        }
        initializeVideoPlayer(Uri.parse(prevStep.getVideoUrl()));
        tvStepDesc.setText(prevStep.getDescription());

        String playlist = playlistPosition + 1 + "/" + recipe.getSteps().size();
        tvStepNum.setText(playlist);

        if (playlistPosition == 0) {
            view.setVisibility(View.INVISIBLE);
        }

        if (recipe.getSteps().size() != playlistPosition + 1) {
            ibNext.setVisibility(View.VISIBLE);
        }
    }

    public boolean isVideo(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public boolean isTablet() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        double xInches = metrics.widthPixels / metrics.xdpi;
        double yInches = metrics.heightPixels / metrics.ydpi;

        double diagonalInches = Math.sqrt(Math.pow(xInches, 2) + Math.pow(yInches, 2));

        return diagonalInches >= 6.9;
    }

    private void initializeVideoPlayer(Uri videoUri) {
        if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            exoStepVideo.setPlayer(exoPlayer);
            exoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getContext(), "StepVideo");
            MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                    new DefaultDataSourceFactory(getActivity(), userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
