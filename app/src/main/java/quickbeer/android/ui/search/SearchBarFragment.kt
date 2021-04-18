package quickbeer.android.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import quickbeer.android.domain.beer.Beer
import quickbeer.android.feature.barcode.BarcodeScannerActivity
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.ui.view.SearchView

abstract class SearchBarFragment(@LayoutRes layout: Int) : MainFragment(layout) {

    abstract val searchHint: Int

    private val closeOnBackHandler = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            searchView().closeSearchView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackHandler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView().apply {
            queryChangedCallback = ::onSearchQueryChanged
            querySubmitCallback = ::onSearchQuerySubmit
            searchFocusChangeCallback = ::onSearchFocusChanged
            navigateBackCallback = requireActivity()::onBackPressed
            barcodeCallback = ::onScanBarcode
        }
    }

    protected abstract fun searchView(): SearchView

    protected open fun onSearchQueryChanged(query: String) = Unit

    protected open fun onSearchQuerySubmit(query: String) = Unit

    protected open fun onBarcodeScanResult(barcode: String, beers: List<Beer>) = Unit

    private fun onScanBarcode() {
        val intent = Intent(context, BarcodeScannerActivity::class.java)
        startActivityForResult(intent, BarcodeScannerActivity.BARCODE_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BarcodeScannerActivity.BARCODE_RESULT && resultCode == Activity.RESULT_OK) {
            val barcode = data?.getStringExtra(BarcodeScannerActivity.KEY_BARCODE)
            val beers = data?.getParcelableArrayListExtra<Beer>(BarcodeScannerActivity.KEY_BEERS)
            if (barcode != null) onBarcodeScanResult(barcode, beers?.toList().orEmpty())
        }
    }

    @CallSuper
    protected open fun onSearchFocusChanged(hasFocus: Boolean) {
        closeOnBackHandler.isEnabled = hasFocus
    }
}
