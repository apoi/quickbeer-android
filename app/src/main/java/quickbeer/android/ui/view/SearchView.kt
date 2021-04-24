package quickbeer.android.ui.view

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt
import quickbeer.android.R
import quickbeer.android.databinding.SearchViewBinding
import quickbeer.android.ui.listener.LayoutTransitionEndListener
import quickbeer.android.ui.listener.OnTextChangedListener
import quickbeer.android.util.ktx.hideKeyboard
import quickbeer.android.util.ktx.onGlobalLayout
import quickbeer.android.util.ktx.setMargins
import quickbeer.android.util.ktx.showKeyboard

@Suppress("MagicNumber")
class SearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding = SearchViewBinding.inflate(LayoutInflater.from(context), this)
    private val expandable: Boolean

    private val transitionDuration = resources
        .getInteger(android.R.integer.config_mediumAnimTime)
        .toLong()

    var query: String
        get() = binding.searchEditText.text.toString()
        set(value) {
            binding.searchEditText.setText(value)
            binding.searchEditText.setSelection(value.length)
            queryChangedCallback?.invoke(value)
        }

    private var _navigationMode = NavigationMode.SEARCH
    var navigationMode: NavigationMode
        get() = _navigationMode
        set(value) {
            _navigationMode = value
        }

    var querySubmitCallback: ((String) -> Unit)? = null
    var searchFocusChangeCallback: ((Boolean) -> Unit)? = null
    var navigateBackCallback: (() -> Unit)? = null
    var queryChangedCallback: ((String) -> Unit)? = null
    var barcodeCallback: (() -> Unit)? = null

    // Should be moved to attributes and style
    private val black = resources.getColor(R.color.black, null)
    private val white = resources.getColor(R.color.white, null)
    private val dark = resources.getColor(R.color.gray_85, null)
    private val light = resources.getColor(R.color.gray_80, null)
    private val text = resources.getColor(R.color.gray_40, null)
    private val orange = resources.getColor(R.color.orange, null)
    private val transparent = resources.getColor(R.color.transparent, null)

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchView)
        expandable = ta.getBoolean(R.styleable.SearchView_expandable, true)
        ta.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initLayout()
    }

    private fun initLayout() {
        setNormalMode()

        if (!expandable) {
            binding.searchEditText.isFocusable = false
            binding.searchEditText.setOnClickListener {
                searchFocusChangeCallback?.invoke(true)
            }
        }

        // Search box
        binding.searchEditText.apply {
            hint = resources.getString(R.string.search_hint)
            setTextColor(white)
            clearFocus()

            addTextChangedListener(
                OnTextChangedListener {
                    queryChangedCallback?.invoke(it)
                    binding.searchClear.isVisible = it.isNotEmpty()
                }
            )

            setOnEditorActionListener { _, _, _ ->
                submitQuery(text.toString())
                return@setOnEditorActionListener true
            }
        }

        // Clear button
        binding.searchClear.apply {
            isVisible = query.isNotEmpty()
            setOnClickListener {
                binding.searchEditText.text = null
                binding.searchEditText.requestFocus()
            }
        }

        // Barcode search
        binding.searchBarcode.apply {
            isVisible = query.isEmpty()
            setOnClickListener {
                barcodeCallback?.invoke()
            }
        }

        // Navigation
        binding.searchNavigation.apply {
            setOnClickListener {
                when (navigationMode) {
                    NavigationMode.BACK -> navigateBackCallback?.invoke()
                    else -> searchFocusChangeCallback?.invoke(true)
                }
            }
        }

        // Delay adding transition so that it doesn't animate initially
        onGlobalLayout {
            layoutTransition = LayoutTransition().apply {
                enableTransitionType(LayoutTransition.CHANGING)
                setDuration(transitionDuration)
            }
            binding.searchLayout.layoutTransition = LayoutTransition().apply {
                enableTransitionType(LayoutTransition.CHANGING)
                setDuration(transitionDuration)
            }
        }
    }

    private fun setNormalMode() {
        hideKeyboard()
        searchFocusChangeCallback?.invoke(false)

        // Card view
        setMargins(6.dp())
        setHeight(48.dp())
        setCardBackgroundColor(dark)

        strokeColor = light
        strokeWidth = 1.dp()
        cardElevation = 6.dp().toFloat()
        radius = 16.dp().toFloat()

        // Top element anchor
        binding.searchTopAnchor.setHeight(48.dp())

        // Navigation icon
        val drawable = ResourcesCompat.getDrawable(resources, navigationMode.icon, null)
        binding.searchNavigation.setImageDrawable(drawable)

        // Search box
        binding.searchEditText.setMargins(2.dp(), 0, 0, 0)
    }

    private fun setInputMode(showKeyboard: Boolean) {
        searchFocusChangeCallback?.invoke(true)

        // Focusing may open another view instead of activating search
        if (!expandable) return

        // We may want to just expand the search box without keyboard
        if (showKeyboard) showKeyboard()

        // Card view
        setHeight(60.dp())
        setMargins(0)

        strokeWidth = 0.dp()
        radius = 0F

        // Top element anchor = normal height + margins
        binding.searchTopAnchor.setHeight(60.dp())

        // Navigation icon
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_back, null)
        binding.searchNavigation.setImageDrawable(drawable)

        // Search box
        binding.searchEditText.setMargins(8.dp(), 0, 0, 0)
    }

    fun openSearchView(showKeyboard: Boolean = true) {
        if (showKeyboard) {
            setInputMode(true)
        } else {
            setInputMode(false)
        }
    }

    fun closeSearchView(): Boolean {
        if (binding.searchEditText.hasFocus()) {
            binding.searchEditText.clearFocus()
            return true
        }

        return false
    }

    private fun showKeyboard() {
        binding.searchEditText.showKeyboard()
        binding.searchEditText.requestFocus()
    }

    private fun hideKeyboard() {
        binding.searchEditText.hideKeyboard()
        binding.searchEditText.clearFocus()
    }

    private fun submitQuery(query: String) {
        val listener = LayoutTransitionEndListener {
            querySubmitCallback?.invoke(query)
        }

        binding.searchLayout.layoutTransition.addTransitionListener(listener)
        binding.searchEditText.hideKeyboard()
    }

    private fun View.setHeight(height: Int) {
        layoutParams = layoutParams.apply {
            this.height = height
            this.width = MATCH_PARENT
        }
    }

    private fun Int.dp(): Int {
        return (resources.displayMetrics.density * this).roundToInt()
    }

    enum class NavigationMode(val icon: Int) {
        SEARCH(R.drawable.ic_search),
        BACK(R.drawable.ic_back)
    }
}
