/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>
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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.ui.progress

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import quickbeer.android.R
import timber.log.Timber

class ProgressIndicatorBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val progressBarWidth = resources.getDimensionPixelSize(R.dimen.progress_indicator_width)
    private var barScale = 1.0f
    private var currentProgress = ProgressStatus(Status.IDLE, 0.0f)
    private var nextProgress = ProgressStatus(Status.IDLE, 0.0f)

    private lateinit var progressBar: View

    fun setProgress(progress: ProgressStatus) {
        Timber.v("New progress: $progress")
        nextProgress = progress

        // Only indefinite progress animation can be interrupted
        if (!isUninterruptibleAnimation) {
            applyNextStatus()
        }
    }

    private fun addPreDrawListener() {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                initLayout()
                return true
            }
        })
    }

    private fun initLayout() {
        val params = LayoutParams(
            progressBarWidth,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        progressBar = View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
            layoutParams = params
            visibility = INVISIBLE
        }

        addView(progressBar)
        applyNextStatus()
    }

    private fun applyNextStatus() {
        // Check if there's new status to apply
        if (statusesEqual(nextProgress, currentProgress)) {
            return
        }

        currentProgress = nextProgress

        when (currentProgress.status) {
            Status.INDEFINITE -> animateScroller()
            Status.LOADING -> animateToProgress(currentProgress.progress)
            Status.IDLE -> animateToEnd()
        }
    }

    private val isUninterruptibleAnimation: Boolean
        get() {
            if (progressBar.animation == null) {
                return false
            }
            return !progressBar.animation.hasEnded()
        }

    private fun animateScroller() {
        Timber.v("animateScroller()")
        val animEnd = width - progressBarWidth
        val animation = TranslateAnimation(0F, animEnd.toFloat(), 0F, 0F).apply {
            duration = ANIMATION_SCROLL_DURATION.toLong()
            fillAfter = true
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE

            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) = Unit
                override fun onAnimationEnd(animation: Animation) = Unit
                override fun onAnimationRepeat(animation: Animation) {
                    if (nextProgress.status !== Status.INDEFINITE) {
                        applyNextStatus()
                    }
                }
            })
        }

        progressBar.apply {
            visibility = VISIBLE
            clearAnimation()
            startAnimation(animation)
        }
    }

    private fun animateToProgress(progress: Float) {
        Timber.v("animateToProgress(%s)", progress)
        val newScale = width * progress / progressBarWidth + 1
        val barHeight = progressBar.height.toFloat()

        Timber.v("animate scale %s -> %s", barScale, newScale)
        val animation = ScaleAnimation(barScale, newScale, barHeight, barHeight).apply {
            duration = ANIMATION_PROGRESS_DURATION.toLong()
            fillAfter = true

            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) = Unit
                override fun onAnimationRepeat(animation: Animation) = Unit
                override fun onAnimationEnd(animation: Animation) {
                    barScale = newScale
                    applyNextStatus()
                }
            })
        }

        progressBar.apply {
            visibility = VISIBLE
            clearAnimation()
            startAnimation(animation)
        }
    }

    private fun animateToEnd() {
        Timber.v("animateToEnd()")

        if (progressBar.animation == null) {
            // Non-active progress bar doesn't need end animation
            return
        }

        val animation = ScaleAnimation(
            barScale,
            width / progressBarWidth.toFloat() + 1,
            progressBar.height.toFloat(),
            progressBar.height.toFloat()
        ).apply {
            duration = ANIMATION_END_SCALE_DURATION.toLong()
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) = Unit
                override fun onAnimationRepeat(animation: Animation) = Unit
                override fun onAnimationEnd(animation: Animation) {
                    barScale = 1.0f
                    animateToHidden()
                }
            })
        }

        progressBar.visibility = VISIBLE
        progressBar.clearAnimation()
        progressBar.startAnimation(animation)
    }

    private fun animateToHidden() {
        Timber.v("animateToHidden()")

        val animation = AlphaAnimation(1.0f, 0.0f).apply {
            startOffset = ANIMATION_END_PAUSE_DURATION.toLong()
            duration = ANIMATION_END_FADE_DURATION.toLong()
            fillAfter = true
        }

        progressBar.clearAnimation()
        progressBar.startAnimation(animation)
    }

    companion object {
        private const val ANIMATION_SCROLL_DURATION = 1200
        private const val ANIMATION_PROGRESS_DURATION = 400
        private const val ANIMATION_END_SCALE_DURATION = 400
        private const val ANIMATION_END_PAUSE_DURATION = 300
        private const val ANIMATION_END_FADE_DURATION = 300

        private fun statusesEqual(first: ProgressStatus, second: ProgressStatus): Boolean {
            if (second == first) return true

            // Consider progress only if loading, otherwise matching status is enough
            return first.status === second.status &&
                (first.status !== Status.LOADING || first == second)
        }
    }
}
