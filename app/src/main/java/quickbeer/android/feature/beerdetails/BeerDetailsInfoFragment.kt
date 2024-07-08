/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.feature.beerdetails

import android.animation.LayoutTransition
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt
import quickbeer.android.R
import quickbeer.android.databinding.BeerDetailsInfoFragmentBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.RatingBinder
import quickbeer.android.domain.style.Style
import quickbeer.android.feature.beerdetails.BeerDetailsFragmentDirections.Companion.toActions
import quickbeer.android.feature.beerdetails.BeerDetailsFragmentDirections.Companion.toRating
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.feature.beerdetails.model.BeerDetailsState
import quickbeer.android.feature.beerdetails.model.RatingAction
import quickbeer.android.feature.beerdetails.model.RatingAction.CreateDraft
import quickbeer.android.feature.beerdetails.model.RatingAction.CreateTick
import quickbeer.android.feature.beerdetails.model.RatingAction.DeleteDraft
import quickbeer.android.feature.beerdetails.model.RatingAction.DeleteRating
import quickbeer.android.feature.beerdetails.model.RatingAction.EditDraft
import quickbeer.android.feature.beerdetails.model.RatingAction.EditRating
import quickbeer.android.feature.beerdetails.model.RatingState
import quickbeer.android.feature.beerdetails.model.Tick
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.actionmenu.Action
import quickbeer.android.ui.actionmenu.ActionSheetFragment
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ToastProvider
import quickbeer.android.util.ktx.formatDateTime
import quickbeer.android.util.ktx.getNavigationResult
import quickbeer.android.util.ktx.observeSuccess
import quickbeer.android.util.ktx.setNegativeAction
import quickbeer.android.util.ktx.setPositiveAction
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class BeerDetailsInfoFragment :
    BaseFragment(R.layout.beer_details_info_fragment),
    RatingBar.OnRatingBarChangeListener {

    @Inject
    lateinit var toastProvider: ToastProvider

    private val beerId by lazy { requireArguments().getInt(NavParams.ID) }
    private val binding by viewBinding(BeerDetailsInfoFragmentBinding::bind)
    private val viewModel by viewModels<BeerDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ratingBar.onRatingBarChangeListener = this

        binding.actionLogin.setOnClickListener {
            showLoginDialog()
        }

        binding.actionAddRating.setOnClickListener {
            onActionSelected(CreateDraft(beerId))
        }

        binding.actionAddTick.setOnClickListener {
            onActionSelected(CreateTick(beerId))
        }

        binding.beerRatingOverall.setOnClickListener {
            showToast(R.string.description_rating_overall)
        }

        binding.beerRatingStyle.setOnClickListener {
            showToast(R.string.description_rating_style)
        }

        binding.beerRatingAbv.setOnClickListener {
            showToast(R.string.description_abv)
        }

        binding.beerRatingIbu.setOnClickListener {
            showToast(R.string.description_ibu)
        }
    }

    override fun observeViewState() {
        observeSuccess(viewModel.viewState, ::setValues)

        getNavigationResult(
            fragmentId = R.id.beer_details_fragment,
            key = ActionSheetFragment.ACTION_RESULT,
            action = ::onActionSelected
        )
    }

    private fun setValues(state: BeerDetailsState) {
        setBeer(state.beer)
        state.brewer?.let(::setBrewer)
        state.style?.let(::setStyle)
        state.address?.let(::setAddress)
        state.rating?.let(::setRating)
        state.tick?.let(::setTick)

        // Set action buttons
        val showLoginAction = state.user == null
        val showRatingAction = state.rating is RatingState.ShowAction
        val showTickAction = state.tick is RatingState.ShowAction

        binding.actionLogin.isVisible = showLoginAction
        binding.actionAddRating.isVisible = showRatingAction
        binding.actionAddTick.isVisible = showTickAction
        binding.actionLayout.isVisible = showLoginAction || showRatingAction || showTickAction

        // Animate layout changes after initial layout is done
        binding.mainLayout.layoutTransition = LayoutTransition()
        binding.actionLayout.layoutTransition = LayoutTransition()
    }

    private fun setBeer(beer: Beer) {
        binding.infoDescription.value = beer.description
            ?.takeIf(String::isNotEmpty)
            ?: getString(R.string.no_description)

        binding.beerRatingOverall.value = beer.overallRating
            ?.takeIf { it > 0 }
            ?.roundToInt()
            ?.toString()
            ?: "?"

        binding.beerRatingStyle.value = beer.styleRating
            ?.takeIf { it > 0 }
            ?.roundToInt()
            ?.toString()
            ?: "?"

        binding.beerRatingAbv.value = beer.alcohol
            ?.takeIf { it > 0 }
            ?.toString()
            ?.let { "%s%%".format(it) }
            ?: "?"

        binding.beerRatingIbu.value = beer.ibu
            ?.takeIf { it > 0 }
            ?.roundToInt()
            ?.toString()
            ?: getString(R.string.not_available)

        binding.ratingBar.rating = beer.tickValue
            ?.takeIf { beer.isTicked() }
            ?.toFloat()
            ?: 0F

        binding.tickedDate.text = beer.tickDate
            ?.takeIf { beer.isTicked() }
            ?.formatDateTime(getString(R.string.beer_tick_date))
    }

    private fun setRating(rating: RatingState<Rating>) {
        binding.ownRating.ratingCard.isVisible = rating is RatingState.ShowRating

        if (rating is RatingState.ShowRating) {
            RatingBinder.bind(requireContext(), rating.value, binding.ownRating, true)
            binding.ownRating.actions.setOnClickListener {
                showRatingActionsMenu(rating.value)
            }
        }
    }

    private fun setTick(tick: RatingState<Tick>) {
        binding.starRatingCard.isVisible = tick is RatingState.ShowRating

        if (tick is RatingState.ShowRating) {
            // Tick is shown if available and no rating
            binding.ratingBar.rating = tick.value.tick?.toFloat() ?: 0F
            binding.tickedDate.text = tick.value.tickDate
                ?.formatDateTime(getString(R.string.beer_tick_date))
        }
    }

    private fun showTickCard() {
        binding.ratingBar.rating = 0f
        binding.tickedDate.text = getString(R.string.tick_explanation)
        binding.starRatingCard.isVisible = true
        binding.actionAddTick.isVisible = false
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        if (!fromUser) return
        viewModel.tickBeer(rating.toInt())
    }

    private fun setBrewer(brewer: Brewer) {
        binding.infoBrewer.value = brewer.name
        binding.infoBrewer.setOnClickListener {
            navigate(Destination.Brewer(brewer.id))
        }
    }

    private fun setStyle(style: Style) {
        binding.infoStyle.value = style.name
        binding.infoStyle.setOnClickListener {
            navigate(Destination.Style(style.id))
        }
    }

    private fun setAddress(address: Address) {
        binding.infoOrigin.value = address.cityAndCountry()
        binding.infoOrigin.setOnClickListener {
            navigate(Destination.Country(address.countryId))
        }
    }

    private fun showToast(@StringRes resource: Int) {
        toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }

    private fun showRatingActionsMenu(rating: Rating) {
        val actions = if (rating.isDraft) {
            listOf(EditDraft(beerId, rating.id), DeleteDraft(beerId, rating.id))
        } else {
            listOf(EditRating(beerId, rating.id), DeleteRating(beerId, rating.id))
        }

        navigate(toActions(actions.toTypedArray()))
    }

    private fun onActionSelected(action: Action) {
        when (action) {
            is CreateDraft -> navigate(toRating(action))
            is CreateTick -> showTickCard()
            is EditDraft -> navigate(toRating(action))
            is DeleteDraft -> confirmAction(action, viewModel::deleteRating)
            is EditRating -> navigate(toRating(action))
            is DeleteRating -> confirmAction(action, viewModel::deleteRating)
            else -> error("Invalid action $action")
        }
    }

    private fun confirmAction(action: RatingAction, callback: (Int) -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(action.confirmTitle!!)
            .setMessage(action.confirmMessage!!)
            .setPositiveAction(R.string.action_yes) { callback(action.ratingId!!) }
            .setNegativeAction(R.string.action_no) { it.cancel() }
            .show()
    }

    private fun showLoginDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.login_dialog_title)
            .setMessage(R.string.login_to_rate_message)
            .setPositiveAction(R.string.action_yes, {})
            .setNegativeAction(R.string.action_no, DialogInterface::cancel)
            .show()
    }

    companion object {
        fun create(beerId: Int): Fragment {
            return BeerDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to beerId)
            }
        }
    }
}
