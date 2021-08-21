package quickbeer.android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import com.google.android.material.card.MaterialCardView
import quickbeer.android.R
import quickbeer.android.util.ktx.setMargins

open class GroupItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val position: Position
    private val applyTopMargin: Boolean

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GroupItem)
        position = Position.values()[ta.getInt(R.styleable.GroupItem_position, 0)]
        applyTopMargin = ta.getBoolean(R.styleable.GroupItem_applyTopMargin, true)
        ta.recycle()

        updateShape()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateMargins()
        updateDivider()
    }

    private fun updateShape() {
        val radius = context.resources.getDimensionPixelOffset(R.dimen.radius_l).toFloat()
        val shape = shapeAppearanceModel.toBuilder()

        when (position) {
            Position.ONLY -> {
                shape.setTopLeftCornerSize(radius)
                    .setTopRightCornerSize(radius)
                    .setBottomRightCornerSize(radius)
                    .setBottomLeftCornerSize(radius)
            }
            Position.FIRST -> {
                shape.setTopLeftCornerSize(radius)
                    .setTopRightCornerSize(radius)
                    .setBottomRightCornerSize(0f)
                    .setBottomLeftCornerSize(0f)
            }
            Position.MIDDLE -> {
                shape.setTopLeftCornerSize(0f)
                    .setTopRightCornerSize(0f)
                    .setBottomRightCornerSize(0f)
                    .setBottomLeftCornerSize(0f)
            }
            Position.LAST -> {
                shape.setTopLeftCornerSize(0f)
                    .setTopRightCornerSize(0f)
                    .setBottomRightCornerSize(radius)
                    .setBottomLeftCornerSize(radius)
            }
        }

        shapeAppearanceModel = shape.build()
    }

    private fun updateDivider() {
        when (position) {
            Position.FIRST, Position.MIDDLE -> {
                val divider = ImageView(context).also(::addView)
                divider.setImageResource(R.drawable.divider)
                divider.layoutParams = (divider.layoutParams as LayoutParams).apply {
                    height = context.resources.getDimensionPixelOffset(R.dimen.divider_height)
                    gravity = Gravity.BOTTOM
                }
            }
            else -> Unit
        }
    }

    private fun updateMargins() {
        val margin = context.resources.getDimensionPixelOffset(R.dimen.spacing_l)
        val top = when (position) {
            Position.ONLY, Position.FIRST -> if (applyTopMargin) margin else 0
            else -> 0
        }
        val bottom = when (position) {
            Position.ONLY, Position.LAST -> margin
            else -> 0
        }

        setMargins(margin, top, margin, bottom)
    }

    enum class Position {
        ONLY,
        FIRST,
        MIDDLE,
        LAST
    }
}
