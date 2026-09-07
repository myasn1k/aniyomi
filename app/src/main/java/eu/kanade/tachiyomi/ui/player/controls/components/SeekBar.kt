/*
 * Copyright 2024 Abdallah Mehiz
 * https://github.com/abdallahmehiz/mpvKt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.kanade.tachiyomi.ui.player.controls.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import dev.vivvvek.seeker.Segment
import eu.kanade.tachiyomi.animesource.model.ChapterType
import eu.kanade.tachiyomi.ui.player.controls.LocalPlayerButtonsClickEvent
import eu.kanade.tachiyomi.util.system.isTvUiEnabled
import `is`.xyz.mpv.Utils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.util.focusHighlight

@Immutable
data class IndexedSegment(
    val name: String,
    val start: Float,
    val color: Color = Color.Unspecified,
    val index: Int = 0,
    val chapterType: ChapterType = ChapterType.Other,
) {
    companion object {
        val Unspecified = IndexedSegment(name = "", start = 0f)
    }

    fun toSegment(): Segment = Segment(name, start, color)
}

internal fun seekBarDisplayPosition(
    playerPosition: Float,
    gestureSeekPosition: Float,
    internalSeekPosition: Float,
    isGestureSeeking: Boolean,
    isSeeking: Boolean,
): Float = when {
    isGestureSeeking -> gestureSeekPosition
    isSeeking -> internalSeekPosition
    else -> playerPosition
}

@Composable
fun SeekbarWithTimers(
    playerPosition: Float,
    seekPosition: Float,
    isGestureSeeking: Boolean,
    isSeeking: Boolean,
    duration: Float,
    readAheadValue: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit,
    timersInverted: Pair<Boolean, Boolean>,
    positionTimerOnClick: () -> Unit,
    durationTimerOnCLick: () -> Unit,
    chapters: ImmutableList<Segment>,
    modifier: Modifier = Modifier,
) {
    // This timer is mounted for the entire lifetime of the timeline. The central
    // play button is not: loading and seek feedback can replace it at any time.
    val escapeFocusRequester = remember { FocusRequester() }
    val isTelevision = LocalContext.current.isTvUiEnabled()
    var internalSeekPosition by remember { mutableFloatStateOf(playerPosition) }
    val position = seekBarDisplayPosition(
        playerPosition = playerPosition,
        gestureSeekPosition = seekPosition,
        internalSeekPosition = internalSeekPosition,
        isGestureSeeking = isGestureSeeking,
        isSeeking = isSeeking,
    )
    var remoteSeekInProgress by remember { mutableStateOf(false) }
    LaunchedEffect(position, isSeeking) {
        if (!isSeeking) internalSeekPosition = position
    }
    val clickEvent = LocalPlayerButtonsClickEvent.current
    Row(
        modifier = modifier.height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
    ) {
        VideoTimer(
            value = position,
            timersInverted.first,
            onClick = {
                clickEvent()
                positionTimerOnClick()
            },
            modifier = Modifier.width(92.dp).focusRequester(escapeFocusRequester),
        )
        Seeker(
            value = position.coerceIn(0f, duration),
            range = 0f..duration,
            onValueChange = {
                internalSeekPosition = it
                onValueChange(it)
            },
            onValueChangeFinished = {
                onValueChangeFinished(internalSeekPosition)
            },
            readAheadValue = readAheadValue,
            segments = chapters
                .filter { it.start in 0f..duration }
                .let {
                    // add an extra segment at 0 if it doesn't exist.
                    if (it.isNotEmpty() && it[0].start != 0f) {
                        persistentListOf(Segment("", 0f)) + it
                    } else {
                        it
                    } +
                        it
                },
            modifier = Modifier
                .weight(1f)
                .focusHighlight()
                .focusProperties {
                    if (isTelevision) {
                        up = escapeFocusRequester
                        down = escapeFocusRequester
                    }
                }
                .onFocusChanged {
                    if (!it.isFocused) remoteSeekInProgress = false
                }
                .onPreviewKeyEvent { event ->
                    if (!isTelevision) return@onPreviewKeyEvent false
                    val nativeEvent = event.nativeKeyEvent
                    if (isTvSeekActivationKey(nativeEvent.keyCode)) {
                        // Seeker treats D-pad select as a click. On TV, focus alone enables seeking.
                        return@onPreviewKeyEvent true
                    }
                    if (isTvSeekEscapeKey(nativeEvent.keyCode)) {
                        if (nativeEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                            clickEvent()
                            if (remoteSeekInProgress) {
                                onValueChangeFinished(internalSeekPosition)
                                remoteSeekInProgress = false
                            }
                            escapeFocusRequester.requestFocus()
                        }
                        return@onPreviewKeyEvent true
                    }
                    val step = tvSeekStepSeconds(nativeEvent.keyCode, nativeEvent.repeatCount)
                        ?: return@onPreviewKeyEvent false
                    when (nativeEvent.action) {
                        android.view.KeyEvent.ACTION_DOWN -> {
                            remoteSeekInProgress = true
                            internalSeekPosition = (internalSeekPosition + step).coerceIn(0f, duration)
                            onValueChange(internalSeekPosition)
                            true
                        }
                        android.view.KeyEvent.ACTION_UP -> {
                            if (shouldFinishTvSeek(nativeEvent.keyCode, remoteSeekInProgress)) {
                                onValueChangeFinished(internalSeekPosition)
                            }
                            remoteSeekInProgress = false
                            true
                        }
                        else -> false
                    }
                }
                .focusable(enabled = isTelevision),
            colors = SeekerDefaults.seekerColors(
                progressColor = MaterialTheme.colorScheme.primary,
                thumbColor = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.background,
                readAheadColor = MaterialTheme.colorScheme.inversePrimary,
            ),
        )
        VideoTimer(
            value = if (timersInverted.second) position - duration else duration,
            isInverted = timersInverted.second,
            onClick = {
                clickEvent()
                durationTimerOnCLick()
            },
            modifier = Modifier.width(92.dp),
        )
    }
}

internal fun tvSeekStepSeconds(keyCode: Int, repeatCount: Int): Float? {
    val distance = if (repeatCount == 0) 30f else 5f
    return when (keyCode) {
        android.view.KeyEvent.KEYCODE_DPAD_LEFT -> -distance
        android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> distance
        else -> null
    }
}

internal fun isTvSeekEscapeKey(keyCode: Int): Boolean {
    return keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP ||
        keyCode == android.view.KeyEvent.KEYCODE_DPAD_DOWN
}

internal fun isTvSeekActivationKey(keyCode: Int): Boolean {
    return keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == android.view.KeyEvent.KEYCODE_ENTER ||
        keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER
}

internal fun shouldFinishTvSeek(keyCode: Int, seekInProgress: Boolean): Boolean {
    return seekInProgress && tvSeekStepSeconds(keyCode, repeatCount = 0) != null
}

@Composable
fun VideoTimer(
    value: Float,
    isInverted: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    Text(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
            )
            .wrapContentHeight(Alignment.CenterVertically),
        text = Utils.prettyTime(value.toInt(), isInverted),
        color = Color.White,
        textAlign = TextAlign.Center,
    )
}

@Preview
@Composable
private fun PreviewSeekBar() {
    SeekbarWithTimers(
        5f,
        5f,
        false,
        false,
        20f,
        4f,
        {},
        {},
        Pair(false, true),
        {},
        {},
        persistentListOf(),
    )
}
