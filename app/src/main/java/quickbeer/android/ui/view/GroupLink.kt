package quickbeer.android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import quickbeer.android.R
import quickbeer.android.databinding.GroupLinkBinding

class GroupLink @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GroupItem(context, attrs, defStyleAttr) {

    private val binding = GroupLinkBinding.inflate(LayoutInflater.from(context), this, true)

    val label: TextView
        get() = binding.label

    val icon: ImageView
        get() = binding.icon

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GroupLink)
        val icon = ta.getResourceId(R.styleable.GroupLink_icon, 0)
        val label = ta.getResourceId(R.styleable.GroupLink_label, 0)
        val type = Type.values()[ta.getInt(R.styleable.GroupLink_type, 0)]
        ta.recycle()

        binding.icon.setImageResource(icon)
        binding.icon.isVisible = icon != 0
        binding.label.setText(label)
        binding.chevron.setImageResource(type.icon)
    }

    enum class Type(val icon: Int) {
        LINK(R.drawable.ic_hero_chevron),
        EXTERNAL(R.drawable.ic_hero_external_link),
        ACTION(0)
    }
}
