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
fun RatingSheetComposable(initialRating: Rating?) {
    val scrollState = rememberScrollState()
    val rating = remember { mutableStateOf(initialRating) }

    BottomSheet(
        scrollState = scrollState,
        content = { RatingContent(rating.value, scrollState) },
        stickyContent = { ActionButtons(rating.value, scrollState) }
    )
}

@Composable
private fun ColumnScope.RatingContent(rating: Rating?, scrollState: ScrollState) {
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
            value = rating?.aroma,
            maxValue = 10
        )
        RatingSlider(
            title = stringResource(R.string.rating_appearance),
            description = stringResource(R.string.rating_appearance_hint),
            value = rating?.appearance,
            maxValue = 5
        )
        RatingSlider(
            title = stringResource(R.string.rating_flavor),
            description = stringResource(R.string.rating_flavor_hint),
            value = rating?.flavor,
            maxValue = 10
        )
        RatingSlider(
            title = stringResource(R.string.rating_mouthfeel),
            description = stringResource(R.string.rating_mouthfeel_hint),
            value = rating?.mouthfeel,
            maxValue = 5
        )
        RatingSlider(
            title = stringResource(R.string.rating_overall),
            description = stringResource(R.string.rating_overall_hint),
            value = rating?.overall,
            maxValue = 20
        )

        // Description text input
        DescriptionInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.spacingM),
            text = rating?.comments.orEmpty()
        )
    }
}

@Composable
private fun ActionButtons(rating: Rating?, scrollState: ScrollState) {
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
                onClick = { /*TODO*/ },
                style = ButtonStyles.primary(),
                text = "Submit"
            )
            Button(
                onClick = { /*TODO*/ },
                style = ButtonStyles.secondary(),
                text = "Save draft"
            )
        }
    }
}
