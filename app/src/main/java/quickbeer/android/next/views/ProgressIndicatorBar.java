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
package quickbeer.android.next.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import io.reark.reark.utils.Log;
import quickbeer.android.next.R;
import quickbeer.android.next.viewmodels.ProgressIndicatorViewModel;
import quickbeer.android.next.viewmodels.ProgressIndicatorViewModel.Status;
import rx.android.schedulers.AndroidSchedulers;

public class ProgressIndicatorBar extends FrameLayout {
    private static final String TAG = ProgressIndicatorBar.class.getSimpleName();

    private static final int ANIMATION_SCROLL_DURATION = 1200;
    private static final int ANIMATION_PROGRESS_DURATION = 400;
    private static final int ANIMATION_END_SCALE_DURATION = 400;
    private static final int ANIMATION_END_PAUSE_DURATION = 300;
    private static final int ANIMATION_END_FADE_DURATION = 300;

    private final int progressBarWidth = getResources().getDimensionPixelSize(R.dimen.progress_indicator_width);
    private float progress = 0;

    private View progressBar;
    private Status currentStatus = Status.IDLE;
    private Status nextStatus = Status.IDLE;

    public ProgressIndicatorBar(Context context) {
        super(context);
        addPreDrawListener();
    }

    public ProgressIndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        addPreDrawListener();
    }

    private void addPreDrawListener() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
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

    public void setViewModel(ProgressIndicatorViewModel progressViewModel) {
        progressViewModel
                .getProgress()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progress -> setProgress(progress.first, progress.second));
    }

    public void setProgress(Status status, float progress) {
        Log.v(TAG, "New progress: " + status + ", " + progress);

        this.nextStatus = status;
        this.progress = progress;

        // Indefinite status waits until next repeat before applying new status
        if (currentStatus != Status.INDEFINITE) {
            applyNextStatus();
        }
    }

    private void applyNextStatus() {
        // Initialisation may not be finished yet
        if (progressBar == null) {
            return;
        }

        currentStatus = nextStatus;

        switch (nextStatus) {
            case INDEFINITE:
                animateScroller();
                break;
            case LOADING:
                animateToProgress(progress);
                break;
            case IDLE:
                animateToEnd();
                break;
        }
    }

    private void animateScroller() {
        Log.v(TAG, "animateScroller()");

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
                if (++repeatCounter % 2 == 0 && nextStatus != Status.INDEFINITE) {
                    applyNextStatus();
                }
            }
        });

        progressBar.setVisibility(VISIBLE);
        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToProgress(float progress) {
        Log.v(TAG, "animateToProgress(" + progress + ")");

        float newScale = (getWidth() * progress / progressBarWidth) + 1;

        ScaleAnimation animation = new ScaleAnimation(1, newScale,
                progressBar.getHeight(), progressBar.getHeight());
        animation.setDuration(ANIMATION_PROGRESS_DURATION);
        animation.setFillAfter(true);

        progressBar.setVisibility(VISIBLE);
        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToEnd() {
        Log.v(TAG, "animateToEnd()");

        if (progressBar.getAnimation() == null) {
            // Non-active progress bar doesn't need end animation
            return;
        }

        ScaleAnimation animation = new ScaleAnimation(1, (getWidth() / progressBarWidth) + 1,
                progressBar.getHeight(), progressBar.getHeight());
        animation.setDuration(ANIMATION_END_SCALE_DURATION);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animateToHidden();
            }
        });

        progressBar.setVisibility(VISIBLE);
        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToHidden() {
        Log.v(TAG, "animateToHidden()");

        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setStartOffset(ANIMATION_END_PAUSE_DURATION);
        animation.setDuration(ANIMATION_END_FADE_DURATION);
        animation.setFillAfter(true);

        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }
}
