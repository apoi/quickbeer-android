package quickbeer.android.feature.beerrating

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import quickbeer.android.Constants
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens
import quickbeer.android.ui.compose.style.TextStyles

@Composable
fun DescriptionInput(text: String, modifier: Modifier = Modifier) {
    var text = remember { mutableStateOf(text) }
    var textLength = text.value.length
    var hasLength = textLength >= Constants.RATING_MIN_LENGTH

    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        modifier = modifier.padding(bottom = Dimens.spacingL),
        colors = Colors.textFieldColors(),
        textStyle = TextStyles.textM,
        /*
        label = Text(
            style = TextStyles.detailsTitle,
            text = stringResource(R.string.rating_description).uppercase()
        ),*/
        /*
        supportingText = Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                color = if (hasLength) Colors.textLight else Colors.textError,
                style = TextStyles.detailsTitle,
                text = stringResource(R.string.rating_description_length)
                    .format(textLength, Constants.RATING_MIN_LENGTH)
            )
        },*/
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { /* Handle the done action */ })
    )
}
