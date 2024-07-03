package quickbeer.android.feature.beerrating

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import quickbeer.android.R
import quickbeer.android.domain.rating.Rating
import quickbeer.android.ui.compose.element.BottomSheet
import quickbeer.android.ui.compose.element.Button
import quickbeer.android.ui.compose.style.ButtonStyles
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens
import quickbeer.android.util.ktx.bottomElevation

@Composable
fun RatingSheetComposable(initialRating: Rating, viewModel: BeerRatingViewModel) {
    val scrollState = rememberScrollState()
    var rating = remember { mutableStateOf(initialRating) }

    BottomSheet(
        scrollState = scrollState,
        content = {
            RatingContent(
                scrollState = scrollState,
                rating = rating.value,
                onRatingChange = { rating.value = it }
            )
        },
        stickyContent = {
            ActionButtons(
                scrollState = scrollState,
                rating = rating.value,
                publish = viewModel::publish,
                saveDraft = viewModel::saveDraft
            )
        }
    )
}

@Composable
private fun ColumnScope.RatingContent(
    scrollState: ScrollState,
    rating: Rating,
    onRatingChange: (Rating) -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1.0f)
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(Dimens.spacingL),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RatingSlider(
            title = stringResource(R.string.rating_aroma),
            description = stringResource(R.string.rating_aroma_hint),
            value = rating.aroma,
            maxValue = 10,
            onValueChange = { onRatingChange(rating.copy(aroma = it)) }
        )
        RatingSlider(
            title = stringResource(R.string.rating_appearance),
            description = stringResource(R.string.rating_appearance_hint),
            value = rating.appearance,
            maxValue = 5,
            onValueChange = { onRatingChange(rating.copy(appearance = it)) }
        )
        RatingSlider(
            title = stringResource(R.string.rating_flavor),
            description = stringResource(R.string.rating_flavor_hint),
            value = rating.flavor,
            maxValue = 10,
            onValueChange = { onRatingChange(rating.copy(flavor = it)) }
        )
        RatingSlider(
            title = stringResource(R.string.rating_mouthfeel),
            description = stringResource(R.string.rating_mouthfeel_hint),
            value = rating.mouthfeel,
            maxValue = 5,
            onValueChange = { onRatingChange(rating.copy(mouthfeel = it)) }
        )
        RatingSlider(
            title = stringResource(R.string.rating_overall),
            description = stringResource(R.string.rating_overall_hint),
            value = rating.overall,
            maxValue = 20,
            onValueChange = { onRatingChange(rating.copy(overall = it)) }
        )

        // Description text input
        DescriptionInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.spacingM),
            text = rating.comments.orEmpty(),
            onTextChange = { onRatingChange(rating.copy(comments = it)) }
        )
    }
}

@Composable
private fun ActionButtons(
    scrollState: ScrollState,
    rating: Rating,
    publish: (Rating) -> Unit,
    saveDraft: (Rating) -> Unit
) {
    Surface(
        color = Colors.cardBackgroundColor,
        elevation = scrollState.bottomElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingM)
        ) {
            Button(
                onClick = { publish(rating) },
                style = ButtonStyles.primary(),
                enabled = rating.isValid(),
                text = stringResource(id = R.string.rating_action_publish)
            )
            Button(
                onClick = { saveDraft(rating) },
                style = ButtonStyles.secondary(),
                text = stringResource(id = R.string.rating_action_draft)
            )
        }
    }
}
