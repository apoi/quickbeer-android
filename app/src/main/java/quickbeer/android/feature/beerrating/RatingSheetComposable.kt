package quickbeer.android.feature.beerrating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import quickbeer.android.R
import quickbeer.android.ui.compose.element.BottomSheet
import quickbeer.android.ui.compose.element.Button
import quickbeer.android.ui.compose.style.ButtonStyles
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens

@Composable
fun RatingSheetComposable() {
    BottomSheet {
        Column(
            modifier = Modifier
                .background(Colors.cardBackgroundColor)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(bottom = Dimens.spacingL)
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

            // Description text input
            DescriptionInput(
                modifier = Modifier.fillMaxWidth(),
                text = ""
            )

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
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
                Button(
                    onClick = { /*TODO*/ },
                    style = ButtonStyles.secondary(),
                    text = "Cancel"
                )
            }
        }
    }
}
