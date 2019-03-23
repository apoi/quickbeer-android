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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import quickbeer.android.providers.ProgressStatusProvider.ProgressStatus;
import quickbeer.android.providers.ProgressStatusProvider.Status;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static quickbeer.android.providers.ProgressStatusProvider.Status.INDEFINITE;
import static quickbeer.android.providers.ProgressStatusProvider.Status.LOADING;

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
    private ProgressStatus currentProgress = new ProgressStatus(Status.IDLE, 0.0f);

    @NonNull
    private ProgressStatus nextProgress = new ProgressStatus(Status.IDLE, 0.0f);

    public ProgressIndicatorBar(Context context) {
        super(context);
        addPreDrawListener();
    }

    public ProgressIndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        addPreDrawListener();
    }

    public void setProgress(@NonNull ProgressStatus progress) {
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

        // Check if there's new status to apply
        if (statusesEqual(nextProgress, currentProgress)) {
            return;
        }

        currentProgress = nextProgress;

        switch (currentProgress.getStatus()) {
            case INDEFINITE:
                animateScroller();
                break;
            case LOADING:
                animateToProgress(currentProgress.getProgress());
                break;
            case IDLE:
                animateToEnd();
                break;
        }
    }

    private boolean isUninterruptibleAnimation() {
        if (progressBar == null || progressBar.getAnimation() == null) {
            return false;
        }

        if (progressBar.getAnimation().hasEnded()) {
            return false;
        }

        return true;
    }

    private void animateScroller() {
        Timber.v("animateScroller()");
        checkNotNull(progressBar);

        int animEnd = getWidth() - progressBarWidth;

        TranslateAnimation animation = new TranslateAnimation(0, animEnd, 0, 0);
        animation.setDuration(ANIMATION_SCROLL_DURATION);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (nextProgress.getStatus() != INDEFINITE) {
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
        checkNotNull(progressBar);

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
        checkNotNull(progressBar);

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
        checkNotNull(progressBar);

        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setStartOffset(ANIMATION_END_PAUSE_DURATION);
        animation.setDuration(ANIMATION_END_FADE_DURATION);
        animation.setFillAfter(true);

        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private static boolean statusesEqual(@NonNull ProgressStatus first, @NonNull ProgressStatus second) {
        if (second.equals(first)) {
            return true;
        }

        // Consider progress only if loading, otherwise matching status is enough
        return first.getStatus() == second.getStatus()
               && (first.getStatus() != LOADING || first.equals(second));
    }
}
