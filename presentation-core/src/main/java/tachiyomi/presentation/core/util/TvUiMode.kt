package tachiyomi.presentation.core.util

import androidx.compose.runtime.compositionLocalOf

/** Resolved by the app layer so shared components obey the same TV-mode preference. */
val LocalTvUiEnabled = compositionLocalOf { false }
