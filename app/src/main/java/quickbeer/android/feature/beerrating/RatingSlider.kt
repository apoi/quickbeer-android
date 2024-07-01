package quickbeer.android.feature.beerrating

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import quickbeer.android.R
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens
import quickbeer.android.ui.compose.style.TextStyles

@Composable
fun RatingSlider(
    title: String,
    description: String,
    value: Int?,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
) {
    var sliderPosition by remember { mutableFloatStateOf(value?.toFloat() ?: 0f) }
    var infoExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { infoExpanded = !infoExpanded },
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                style = TextStyles.detailsTitle,
                color = Colors.textLight,
                text = title.uppercase()
            )
            Spacer(modifier = Modifier.size(Dimens.spacingM))
            Icon(
                modifier = Modifier
                    .size(Dimens.icon)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.ic_hero_info),
                tint = Colors.iconLight,
                contentDescription = "Details"
            )
        }

        AnimatedVisibility(visible = infoExpanded) {
            Text(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .fillMaxWidth(),
                style = TextStyles.detailsDescription,
                color = Colors.textLight,
                text = description
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                colors = Colors.sliderColors(),
                onValueChange = {
                    sliderPosition = it
                    onValueChange(it.roundToInt())
                },
                value = sliderPosition,
                valueRange = 0f..maxValue.toFloat(),
                steps = maxValue - 1
            )
            Spacer(modifier = Modifier.size(Dimens.spacingM))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
                    .background(Colors.highlight)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyles.title,
                    color = Colors.textDark,
                    text = sliderPosition.roundToInt().toString()
                )
            }
        }
    }
}
