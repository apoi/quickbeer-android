package quickbeer.android.feature.beerrating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import quickbeer.android.R
import quickbeer.android.ui.compose.element.BottomSheet
import quickbeer.android.ui.compose.style.Dimens

@Composable
fun RatingSheetComposable() {
    BottomSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = Dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RatingSlider(
                title = stringResource(R.string.rating_aroma),
                description = stringResource(R.string.rating_aroma_hint),
                maxValue = 10
            )
            RatingSlider(
                title = stringResource(R.string.rating_appearance),
                description = stringResource(R.string.rating_appearance_hint),
                maxValue = 5
            )
            RatingSlider(
                title = stringResource(R.string.rating_flavor),
                description = stringResource(R.string.rating_flavor_hint),
                maxValue = 10
            )
            RatingSlider(
                title = stringResource(R.string.rating_mouthfeel),
                description = stringResource(R.string.rating_mouthfeel_hint),
                maxValue = 5
            )
            RatingSlider(
                title = stringResource(R.string.rating_overall),
                description = stringResource(R.string.rating_overall_hint),
                maxValue = 20
            )
        }
    }
}
