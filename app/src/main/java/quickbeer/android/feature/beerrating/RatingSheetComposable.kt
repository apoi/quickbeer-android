package quickbeer.android.feature.beerrating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import quickbeer.android.R
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RatingSheetComposable() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = Colors.cardBackgroundColor,
        sheetDragHandle = { BottomSheetDefaults.DragHandle(color = Colors.cardFeatureColor) },
        sheetContent = {
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
    ) {}
}
