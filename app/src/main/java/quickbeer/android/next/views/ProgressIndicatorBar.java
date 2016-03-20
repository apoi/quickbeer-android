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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import quickbeer.android.next.R;

public class ProgressIndicatorBar extends FrameLayout {
    private static final int ANIMATION_SCROLL_DURATION = 1200;
    private static final int ANIMATION_END_SCALE_DURATION = 400;
    private static final int ANIMATION_END_PAUSE_DURATION = 300;
    private static final int ANIMATION_END_FADE_DURATION = 300;

    public enum Status {
        IDLE,
        INDEFINITE,
        LOADING;
    }

    private View progressBar;
    private float loadingProgress = 0;

    private int counter = 0;

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
                getResources().getDimensionPixelSize(R.dimen.progress_indicator_width),
                ViewGroup.LayoutParams.MATCH_PARENT);

        progressBar = new View(getContext());
        progressBar.setBackgroundColor(getResources().getColor(R.color.orange));
        progressBar.setLayoutParams(params);

        addView(progressBar);

        progressBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                progressBar.getViewTreeObserver().removeOnPreDrawListener(this);
                animateScroller();
                return true;
            }
        });
    }

    public void setStatus(Status status) {
        switch (status) {
            case IDLE:
                animateToEnd();
                break;
            case INDEFINITE:
                animateScroller();
                break;
            case LOADING:
                break;
        }
    }

    public void setProgress(float progress) {

    }

    private void animateScroller() {
        int animEnd = getWidth() - progressBar.getWidth();

        TranslateAnimation animation = new TranslateAnimation(0, animEnd, 0, 0);
        animation.setDuration(ANIMATION_SCROLL_DURATION);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (counter++ == 1) {
                    animateToEnd();
                }
            }
        });

        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToEnd() {
        ScaleAnimation animation = new ScaleAnimation(1, (getWidth() / progressBar.getWidth()) + 1,
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

        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }

    private void animateToHidden() {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setStartOffset(ANIMATION_END_PAUSE_DURATION);
        animation.setDuration(ANIMATION_END_FADE_DURATION);
        animation.setFillAfter(true);

        progressBar.clearAnimation();
        progressBar.startAnimation(animation);
    }
}
