/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import quickbeer.android.R;
import quickbeer.android.providers.ProgressStatusProvider.Status;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class ProgressIndicatorBar extends FrameLayout {

    private static final int ANIMATION_SCROLL_DURATION = 1200;
    private static final int ANIMATION_PROGRESS_DURATION = 400;
    private static final int ANIMATION_END_SCALE_DURATION = 400;
    private static final int ANIMATION_END_PAUSE_DURATION = 300;
    private static final int ANIMATION_END_FADE_DURATION = 300;

    private final int progressBarWidth = getResources().getDimensionPixelSize(R.dimen.progress_indicator_width);

    private float barScale = 1.0f;

    @Nullable
    private View progressBar;

    @NonNull
    private Pair<Status, Float> currentProgress = Pair.create(Status.IDLE, 0.0f);

    @Nullable
    private Pair<Status, Float> nextProgress = Pair.create(Status.IDLE, 0.0f);

    public ProgressIndicatorBar(Context context) {
        super(context);
        addPreDrawListener();
    }

    public ProgressIndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        addPreDrawListener();
    }

    public void setProgress(@NonNull Pair<Status, Float> progress) {
        if (progress.equals(nextProgress)) {
            return;
        }

        Timber.v("New progress: " + get(progress));

        nextProgress = progress;

        // Only indefinite progress animation can be interrupted
        if (!isUninterruptibleAnimation()) {
            applyNextStatus();
        }
    }

    private void addPreDrawListener() {
        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                initLayout();
                return true;
            }
        });
    }

    private void initLayout() {
        LayoutParams params = new LayoutParams(
                progressBarWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);

        progressBar = new View(getContext());
        progressBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange));
        progressBar.setLayoutParams(params);
        progressBar.setVisibility(INVISIBLE);

        addView(progressBar);
        applyNextStatus();
    }

    private void applyNextStatus() {
        // Initialisation may not be finished yet
        if (progressBar == null) {
            return;
        }

        // Check there's new status to apply
        if (nextProgress == null) {
            return;
        }

        currentProgress = nextProgress;
        nextProgress = null;

        switch (currentProgress.first) {
            case INDEFINITE:
                animateScroller();
                break;
            case LOADING:
                animateToProgress(currentProgress.second);
                break;
            case IDLE:
                animateToEnd();
                break;
        }
    }

    private boolean isUninterruptibleAnimation() {
        if (progressBar == null) {
            return false;
        }

        Animation animation = progressBar.getAnimation();

        if (animation == null || animation.hasEnded()) {
            return false;
        }

        return true;
    }

    private void animateScroller() {
        Timber.v("animateScroller()");

        int animEnd = getWidth() - progressBarWidth;

        TranslateAnimation animation = new TranslateAnimation(0, animEnd, 0, 0);
        animation.setDuration(ANIMATION_SCROLL_DURATION);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            private int repeatCounter = 0;

            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (nextProgress != null && nextProgress.first != Status.INDEFINITE) {
                    applyNextStatus();
                }
            }
        });

        progressBar.setVisibility(VISIBLE);
        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToProgress(float progress) {
        Timber.v("animateToProgress(%s)", progress);

        float newScale = (getWidth() * progress / progressBarWidth) + 1;

        Timber.v("animate scale %s -> %s", barScale, newScale);

        ScaleAnimation animation = new ScaleAnimation(barScale, newScale,
                progressBar.getHeight(), progressBar.getHeight());
        animation.setDuration(ANIMATION_PROGRESS_DURATION);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                barScale = newScale;
                applyNextStatus();
            }
        });

        progressBar.setVisibility(VISIBLE);
        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToEnd() {
        Timber.v("animateToEnd()");

        if (progressBar.getAnimation() == null) {
            // Non-active progress bar doesn't need end animation
            return;
        }

        ScaleAnimation animation = new ScaleAnimation(barScale, (getWidth() / (float) progressBarWidth) + 1,
                progressBar.getHeight(), progressBar.getHeight());
        animation.setDuration(ANIMATION_END_SCALE_DURATION);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                barScale = 1.0f;
                animateToHidden();
            }
        });

        progressBar.setVisibility(VISIBLE);
        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToHidden() {
        Timber.v("animateToHidden()");

        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setStartOffset(ANIMATION_END_PAUSE_DURATION);
        animation.setDuration(ANIMATION_END_FADE_DURATION);
        animation.setFillAfter(true);

        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }
}
