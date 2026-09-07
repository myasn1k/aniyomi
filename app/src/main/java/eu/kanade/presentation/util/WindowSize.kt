package eu.kanade.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.tachiyomi.util.system.isTabletUi
import eu.kanade.tachiyomi.util.system.isTelevision
import eu.kanade.tachiyomi.util.system.resolveTvUiMode
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
@ReadOnlyComposable
fun isTabletUi(): Boolean {
    return LocalConfiguration.current.isTabletUi()
}

@Composable
fun isTvUi(): Boolean {
    val mode by Injekt.get<UiPreferences>().tvUiMode().collectAsState()
    return resolveTvUiMode(mode, LocalContext.current.isTelevision())
}
