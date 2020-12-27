package quickbeer.android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import kotlin.math.roundToInt
import quickbeer.android.R
import quickbeer.android.databinding.ValueLabelBinding

@Suppress("MagicNumber")
class ValueLabel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ValueLabelBinding.inflate(LayoutInflater.from(context), this)

    var value: String?
        get() = binding.value.text.toString()
        set(value) {
            binding.value.text = value
        }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        clipToPadding = false
        clipChildren = false

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ValueLabel)
        val title = ta.getString(R.styleable.ValueLabel_title)
        val value = ta.getString(R.styleable.ValueLabel_value)
        val paragraph = ta.getBoolean(R.styleable.ValueLabel_paragraph, false)
        val divider = ta.getBoolean(R.styleable.ValueLabel_top_divider, false)
        val selectable = ta.getBoolean(R.styleable.ValueLabel_selectable, false)

        val backgroundRes = if (divider) {
            R.attr.selectableItemBackground
        } else R.attr.selectableItemBackgroundBorderless

        val outValue = TypedValue()
        context.theme.resolveAttribute(backgroundRes, outValue, true)
        background = ResourcesCompat.getDrawable(resources, outValue.resourceId, context.theme)

        binding.value.text = value
        binding.value.setTextIsSelectable(selectable)
        binding.topDivider.isVisible = divider

        if (!title.isNullOrEmpty()) {
            binding.title.text = title
        } else binding.title.isVisible = false

        if (paragraph) {
            binding.value.textSize = 14f
            binding.value.textAlignment = TEXT_ALIGNMENT_TEXT_START
        }

        if (divider) {
            setPadding(16.dp(), 0, 16.dp(), 8.dp())
        } else {
            setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
        }

        ta.recycle()
    }

    private fun Int.dp(): Int {
        return (resources.displayMetrics.density * this).roundToInt()
    }
}
