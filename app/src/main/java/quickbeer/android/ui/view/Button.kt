package quickbeer.android.ui.view

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import quickbeer.android.R

class Button @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {

    private val buttonText = text
    private val loadingIcon: Drawable =
        ResourcesCompat.getDrawable(resources, R.drawable.loading_icon_animated, context.theme)!!

    private var _isLoading: Boolean = false

    var isLoading: Boolean
        get() = _isLoading
        set(value) {
            if (value != _isLoading) {
                _isLoading = value
                if (_isLoading) isEnabled = false
                updateText()
                updateIcon()
            }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateIcon()
    }

    private fun updateText() {
        text = if (isLoading) null else buttonText
    }

    private fun updateIcon() {
        if (!isLoading) {
            (loadingIcon as AnimatedVectorDrawable).stop()
            setCompoundDrawables(null, null, null, null)
            return
        }

        val width = loadingIcon.intrinsicWidth
        val height = loadingIcon.intrinsicHeight
        val left = (measuredWidth - iconPadding - width - paddingEnd - paddingStart) / 2

        loadingIcon.setBounds(left, 0, left + width, height)
        (loadingIcon as AnimatedVectorDrawable).start()

        setCompoundDrawables(loadingIcon, null, null, null)
    }
}
