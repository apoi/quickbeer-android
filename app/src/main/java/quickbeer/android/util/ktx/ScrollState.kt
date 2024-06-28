package quickbeer.android.util.ktx

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import quickbeer.android.ui.compose.style.Dimens

val ScrollState.topElevation: Dp
    get() = if (value != 0) Dimens.elevation else 0.dp

val ScrollState.bottomElevation: Dp
    get() = if (canScrollForward) Dimens.elevation else 0.dp
