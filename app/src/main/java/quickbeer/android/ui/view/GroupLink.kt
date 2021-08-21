package quickbeer.android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import quickbeer.android.R
import quickbeer.android.databinding.GroupLinkBinding

class GroupLink @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GroupItem(context, attrs, defStyleAttr) {

    private val binding = GroupLinkBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GroupLink)
        val icon = ta.getResourceId(R.styleable.GroupLink_icon, 0)
        val label = ta.getResourceId(R.styleable.GroupLink_label, 0)
        ta.recycle()

        binding.icon.setImageResource(icon)
        binding.label.setText(label)
    }
}
