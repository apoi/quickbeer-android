package quickbeer.android.feature.beerrating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens
import quickbeer.android.ui.compose.style.TextStyles

@Composable
fun DescriptionInput(text: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val textField = remember { mutableStateOf(TextFieldValue(text)) }
    val textLength = textField.value.text.length
    val hasLength = textLength >= Constants.RATING_MIN_LENGTH
    val icon = if (hasLength) R.drawable.ic_hero_check_circle else R.drawable.ic_hero_error_circle
    val iconColor = if (hasLength) Colors.iconLight else Colors.textError

    Column {
        OutlinedTextField(
            value = textField.value,
            onValueChange = {
                textField.value = it
                onTextChange(it.text)
            },
            modifier = modifier.padding(bottom = Dimens.spacingM),
            colors = Colors.textFieldColors(),
            textStyle = TextStyles.textM,
            minLines = 3,
            label = {
                Text(
                    style = TextStyles.detailsTitle,
                    text = stringResource(R.string.rating_description).uppercase()
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                color = Colors.textLight,
                style = TextStyles.detailsTitle,
                text = stringResource(R.string.rating_description_length)
                    .format(textLength, Constants.RATING_MIN_LENGTH)
            )
            Icon(
                modifier = Modifier
                    .size(Dimens.icon)
                    .padding(start = Dimens.spacingS)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = icon),
                tint = iconColor,
                contentDescription = "Length status"
            )
        }
    }
}
