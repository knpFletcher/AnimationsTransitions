package com.teamtreehouse.albumcover;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teamtreehouse.albumcover.transition.Fold;
import com.teamtreehouse.albumcover.transition.Scale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//activity transitions define animations of individual views when moving between activities
public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";
    private TransitionManager mTransitionManager;
    private Scene mExpandedScene;
    private Scene mCollapsedScene;
    private Scene mCurrentScene;

    @BindView(R.id.album_art) ImageView albumArtView;
    @BindView(R.id.fab) ImageButton fab;
    @BindView(R.id.title_panel) ViewGroup titlePanel;
    @BindView(R.id.track_panel) ViewGroup trackPanel;
    @BindView(R.id.detail_container) ViewGroup detailContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
        setupTransitions();
    }

    /*//property animation framework
    private void animate(){

        *//*
        define in XML instead: declarative, easier to read, easier to reuse

        ObjectAnimator scalex = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1);
        ObjectAnimator scaley = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);
        AnimatorSet scaleFab = new AnimatorSet();
        scaleFab.playTogether(scalex, scaley);
        *//*

        Animator scaleFab = AnimatorInflater.loadAnimator(this, R.animator.scale);
        scaleFab.setTarget(fab);
        //what view to pass this animation into

        *//*
        fab.setScaleX(0);
        fab.setScaleY(0);
        //start at nothing and scale to appropriate size
        fab.animate().scaleX(1).scaleY(1).start();
        //animate() is wrapper for property animation API
        *//*

        //ObjectAnimator
        int titleStartValue = titlePanel.getTop();
        int titleEndValue = titlePanel.getBottom();
        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(
                titlePanel, "bottom", titleStartValue, titleEndValue);
            //object to animate, property to animate, start and finish
        animatorTitle.setInterpolator(new AccelerateInterpolator());
            //interpolators apply on spatial movements of obj and any property of obj

        int trackStartValue = trackPanel.getTop();
        int trackEndValue = trackPanel.getBottom();
        ObjectAnimator animatorTrack = ObjectAnimator.ofInt(
                trackPanel, "bottom", trackStartValue, trackEndValue);
        animatorTrack.setInterpolator(new DecelerateInterpolator());

        //hide animations initially
        titlePanel.setBottom(titleStartValue);
        trackPanel.setBottom(titleStartValue);
        fab.setScaleX(0);
        fab.setScaleY(0);

        //animatorTitle.setDuration(1000);
        //animatorTrack.setDuration(1000);
        //animatorTitle.setStartDelay(1000);

        //choreograph animations with AnimatorSet
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animatorTitle, animatorTrack, scaleFab);
        set.start();
    }*/

    //replacing above animation code with Transition
    private Transition createTransition(){
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        //custom transitions
        Transition tFab = new Scale();
        tFab.setDuration(150);
        tFab.addTarget(fab);

        Transition tTitle = new Fold();
        tTitle.setDuration(150);
        tTitle.addTarget(titlePanel);

        Transition tTrack = new Fold();
        tTrack.setDuration(150);
        tTrack.addTarget(trackPanel);

        set.addTransition(tTrack);
        set.addTransition(tTitle);
        set.addTransition(tFab);

        return set;
    }



    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {
        Transition transition = createTransition();
        TransitionManager.beginDelayedTransition(detailContainer, transition);
        fab.setVisibility(View.INVISIBLE);
        titlePanel.setVisibility(View.INVISIBLE);
        trackPanel.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.track_panel)
    public void onTrackPanelClicked(View view){
        //toggle between views
        if (mCurrentScene == mExpandedScene){
            mCurrentScene = mCollapsedScene;
        } else{
            mCurrentScene = mExpandedScene;
        }
        mTransitionManager.transitionTo(mCurrentScene);
    }

    private void setupTransitions() {
//        Slide slide = new Slide(Gravity.BOTTOM);
//        slide.excludeTarget(android.R.id.statusBarBackground, true);
//            //exclude views from showing in transition
//            //delete color attribute in xml for transparent animation background
//        getWindow().setEnterTransition(slide);
//        getWindow().setSharedElementsUseOverlay(false);
//            //FAB doesn't hide behind album art overlay

        mTransitionManager = new TransitionManager();
        ViewGroup transitionRoot = detailContainer;

        //Expanded Scene
        mExpandedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail_expanded, this);

        //set image during runtime, manually restore states of views
        mExpandedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                //need to bind views again
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mExpandedScene;
            }
        });


        TransitionSet expandTransitionSet = new TransitionSet();
        expandTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200);
        expandTransitionSet.addTransition(changeBounds);

        Fade fadeLyrics = new Fade();
        fadeLyrics.addTarget(R.id.lyrics);
        fadeLyrics.setDuration(150);
        expandTransitionSet.addTransition(fadeLyrics);

        //Collapsed Scene
        mCollapsedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail, this);
        mCollapsedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                //need to bind views again
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mCollapsedScene;
            }
        });


        TransitionSet collapseTransitionSet = new TransitionSet();
        collapseTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Fade fadeOutLyrics = new Fade();
        fadeOutLyrics.addTarget(R.id.lyrics);
        fadeOutLyrics.setDuration(150);
        collapseTransitionSet.addTransition(fadeOutLyrics);

        ChangeBounds resetBounds = new ChangeBounds();
        resetBounds.setDuration(200);
        collapseTransitionSet.addTransition(resetBounds);

        mTransitionManager.setTransition(mExpandedScene, mCollapsedScene, collapseTransitionSet);
        mTransitionManager.setTransition(mCollapsedScene, mExpandedScene, expandTransitionSet);
        mCollapsedScene.enter();

        //postponeEnterTransition();
            //postpone Transition until album art has loaded
    }

    private void populate() {
        //use handler to add 1 sec delay to simulate latency of async image loading from internet
//        new Handler().postDelayed(new Runnable(){
//
//            @Override
//            public void run() {
//                int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
//                albumArtView.setImageResource(albumArtResId);
//
//                Bitmap albumBitmap = getReducedBitmap(albumArtResId);
//                colorizeFromImage(albumBitmap);
//
//                startPostponedEnterTransition();
//            }
//        }, 1000);
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);

        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);
    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        // reduce image size in memory to avoid memory errors
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }

    private void colorizeFromImage(Bitmap image) {
        Palette palette = Palette.from(image).generate();

        // set panel colors
        int defaultPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;
        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultPanelColor));

        // set fab colors
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaultFabColor)
        };
        //fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
