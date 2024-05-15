package quickbeer.android.feature.beerrating

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import quickbeer.android.Constants
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens
import quickbeer.android.ui.compose.style.TextStyle

@Composable
fun DescriptionInput(
    text: String,
    modifier: Modifier = Modifier
) {
    var text = remember { mutableStateOf(text) }
    var textLength = text.value.length
    var hasLength = textLength >= Constants.RATING_MIN_LENGTH

    OutlinedTextField(
        modifier = modifier.padding(bottom = Dimens.spacingL),
        value = text.value,
        colors = Colors.textFieldColors(),
        textStyle = TextStyle.textM,
        onValueChange = { text.value = it },
        label = {
            Text(
                style = TextStyle.detailsTitle,
                text = "Notes".uppercase()
            )
        },
        supportingText = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    color = if (hasLength) Colors.textLight else Colors.textError,
                    style = TextStyle.detailsTitle,
                    text = "%s / 80".format(textLength)
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {/* Handle the done action */ })
    )
}
