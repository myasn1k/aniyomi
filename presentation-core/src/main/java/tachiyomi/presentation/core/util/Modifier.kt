package tachiyomi.presentation.core.util

import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import tachiyomi.presentation.core.components.material.SECONDARY_ALPHA

/** Draws a persistent high-contrast highlight for keyboard and TV remote focus. */
fun Modifier.focusHighlight(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme.primary
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val isTelevision = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK ==
        Configuration.UI_MODE_TYPE_TELEVISION ||
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    Modifier
        .onFocusChanged { isFocused = it.isFocused }
        .drawWithContent {
            if (isFocused) {
                drawRoundRect(
                    color.copy(alpha = if (isTelevision) 0.34f else 0.18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                )
            }
            drawContent()
            if (isFocused) {
                drawRoundRect(
                    color = if (isTelevision) Color.White else color,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = (if (isTelevision) 6.dp else 3.dp).toPx(),
                    ),
                )
            }
        }
}

fun Modifier.selectedBackground(isSelected: Boolean): Modifier = if (isSelected) {
    composed {
        val alpha = if (isSystemInDarkTheme()) 0.16f else 0.22f
        val color = MaterialTheme.colorScheme.secondary.copy(alpha = alpha)
        Modifier.drawBehind {
            drawRect(color)
        }
    }
} else {
    this
}

fun Modifier.secondaryItemAlpha(): Modifier = this.alpha(SECONDARY_ALPHA)

fun Modifier.clickableNoIndication(
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
) = this
    .focusHighlight()
    .combinedClickable(
        interactionSource = null,
        indication = null,
        onLongClick = onLongClick,
        onClick = onClick,
    )

/**
 * For TextField, the provided [action] will be invoked when
 * physical enter key is pressed.
 *
 * Naturally, the TextField should be set to single line only.
 */
fun Modifier.runOnEnterKeyPressed(action: () -> Unit): Modifier = this.onPreviewKeyEvent {
    when (it.key) {
        Key.Enter, Key.NumPadEnter -> {
            action()
            true
        }
        else -> false
    }
}

/**
 * For TextField on AppBar, this modifier will request focus
 * to the element the first time it's composed.
 */
fun Modifier.showSoftKeyboard(show: Boolean): Modifier = if (show) {
    composed {
        val focusRequester = remember { FocusRequester() }
        var openKeyboard by rememberSaveable { mutableStateOf(show) }
        LaunchedEffect(focusRequester) {
            if (openKeyboard) {
                focusRequester.requestFocus()
                openKeyboard = false
            }
        }

        Modifier.focusRequester(focusRequester)
    }
} else {
    this
}

/**
 * For TextField, this modifier will clear focus when soft
 * keyboard is hidden.
 */
fun Modifier.clearFocusOnSoftKeyboardHide(
    onFocusCleared: (() -> Unit)? = null,
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardShowedSinceFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeVisible = WindowInsets.isImeVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeVisible) {
            if (imeVisible) {
                keyboardShowedSinceFocused = true
            } else if (keyboardShowedSinceFocused) {
                focusManager.clearFocus()
                onFocusCleared?.invoke()
            }
        }
    }

    Modifier.onFocusChanged {
        if (isFocused != it.isFocused) {
            if (isFocused) {
                keyboardShowedSinceFocused = false
            }
            isFocused = it.isFocused
        }
    }
}
