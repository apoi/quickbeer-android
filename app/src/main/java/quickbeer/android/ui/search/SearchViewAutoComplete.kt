package quickbeer.android.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ListPopupWindow
import android.widget.PopupWindow
import androidx.appcompat.R
import androidx.appcompat.widget.SearchView
import quickbeer.android.util.ktx.onGlobalLayout
import kotlin.math.max

@SuppressLint("RestrictedApi")
class SearchViewAutoComplete @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.autoCompleteTextViewStyle
) : SearchView.SearchAutoComplete(context, attrs, defStyle) {

    /**
     * Set dropdown to fill the screen
     */
    override fun showDropDown() {
        val displayFrame = Rect().also { getWindowVisibleDisplayFrame(it) }
        val locationOnScreen = IntArray(2).also { getLocationOnScreen(it) }
        val offset = (resources.displayMetrics.density * 4).toInt()
        dropDownHeight = displayFrame.bottom - locationOnScreen[1] - height - offset

        super.showDropDown()

        preventAutoClose()
    }

    /**
     * Horrible hack to prevent the popup from autoclosing when tapping the search field
     */
    private fun preventAutoClose() {
        val listPopupField = AutoCompleteTextView::class.java.getDeclaredField("mPopup")
        val popupWindowField = ListPopupWindow::class.java.getDeclaredField("mPopup")

        listPopupField.isAccessible = true
        popupWindowField.isAccessible = true

        val listPopup = listPopupField.get(this) as ListPopupWindow
        val popupWindow = popupWindowField.get(listPopup) as PopupWindow

        popupWindow.setTouchInterceptor(object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.x < 0 || event.x > popupWindow.width) return true
                if (event.y < 0 || event.y > popupWindow.height) return true

                return false
            }
        })
    }

    /**
     * Don't dismiss
     */
    override fun dismissDropDown() {
        return
    }

    /**
     * Only dismiss on detach
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        super.dismissDropDown()
    }

    /**
     * Show suggestions even if empty
     */
    override fun enoughToFilter(): Boolean {
        return true
    }

    /**
     * Zero results closes suggestions dropdown. Override.
     */
    override fun onFilterComplete(count: Int) {
        super.onFilterComplete(max(count, 1))
    }

    /**
     * Don't close the dropdown when keyboard closes
     */
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false
        }

        return super.onKeyPreIme(keyCode, event)
    }

    /**
     * Show suggestions automatically when focused
     */
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if (enoughToFilter() && isFocused && windowVisibility == VISIBLE) {
            showDropDown()
        }
    }
}
