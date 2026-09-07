package eu.kanade.presentation.components

import android.view.KeyEvent
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.util.system.isTelevision
import tachiyomi.domain.download.service.DownloadPreferences
import tachiyomi.presentation.core.components.material.IconButtonTokens
import tachiyomi.presentation.core.util.focusHighlight
import uy.kohesive.injekt.injectLazy

internal fun Modifier.commonClickable(
    enabled: Boolean,
    hapticFeedback: HapticFeedback,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) = this
    .focusHighlight()
    .combinedClickable(
        enabled = enabled,
        onLongClick = {
            onLongClick()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onClick = onClick,
        role = Role.Button,
        interactionSource = null,
        indication = ripple(
            bounded = false,
            radius = IconButtonTokens.StateLayerSize / 2,
        ),
    )

internal fun shouldFocusDownloadOnTvRight(
    isTelevision: Boolean,
    itemFocused: Boolean,
    keyCode: Int,
    action: Int,
): Boolean = isTelevision &&
    itemFocused &&
    action == KeyEvent.ACTION_DOWN &&
    keyCode == KeyEvent.KEYCODE_DPAD_RIGHT

internal fun shouldFocusItemOnTvLeft(
    isTelevision: Boolean,
    keyCode: Int,
    action: Int,
): Boolean = isTelevision &&
    action == KeyEvent.ACTION_DOWN &&
    keyCode == KeyEvent.KEYCODE_DPAD_LEFT

internal fun Modifier.focusDownloadOnTvRight(downloadFocusRequester: FocusRequester): Modifier = composed {
    val isTelevision = LocalContext.current.isTelevision()
    val itemFocused = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    this
        .onFocusChanged { itemFocused.value = it.isFocused }
        .onPreviewKeyEvent { event ->
            if (shouldFocusDownloadOnTvRight(
                    isTelevision = isTelevision,
                    itemFocused = itemFocused.value,
                    keyCode = event.nativeKeyEvent.keyCode,
                    action = event.nativeKeyEvent.action,
                )
            ) {
                downloadFocusRequester.requestFocus()
                true
            } else {
                false
            }
        }
}

internal fun Modifier.downloadFocusTarget(
    downloadFocusRequester: FocusRequester,
    itemFocusRequester: FocusRequester,
): Modifier = composed {
    val isTelevision = LocalContext.current.isTelevision()
    this
        .focusRequester(downloadFocusRequester)
        .onPreviewKeyEvent { event ->
            if (shouldFocusItemOnTvLeft(
                    isTelevision = isTelevision,
                    keyCode = event.nativeKeyEvent.keyCode,
                    action = event.nativeKeyEvent.action,
                )
            ) {
                itemFocusRequester.requestFocus()
                true
            } else {
                false
            }
        }
}

internal val IndicatorSize = 26.dp
internal val IndicatorPadding = 2.dp

// To match composable parameter name when used later
internal val IndicatorStrokeWidth = IndicatorPadding

internal val IndicatorModifier = Modifier
    .size(IndicatorSize)
    .padding(IndicatorPadding)
internal val ArrowModifier = Modifier
    .size(IndicatorSize - 7.dp)

internal val preferences: DownloadPreferences by injectLazy()
