package tachiyomi.presentation.core.components.material

import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.focusHighlight

/**
 * @param refreshing Whether the layout is currently refreshing
 * @param onRefresh Lambda which is invoked when a swipe to refresh gesture is completed.
 * @param enabled Whether the the layout should react to swipe gestures or not.
 * @param indicatorPadding Content padding for the indicator, to inset the indicator in if required.
 * @param content The content containing a vertically scrollable composable.
 */
@Composable
fun PullRefresh(
    refreshing: Boolean,
    enabled: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val showRefreshButton = shouldShowTvRefreshButton(
        uiModeType = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK,
        hasLeanbackFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK),
    )
    Box(
        modifier = modifier
            .pullToRefresh(
                isRefreshing = refreshing,
                state = state,
                enabled = shouldEnablePullRefreshGesture(enabled, showRefreshButton),
                onRefresh = onRefresh,
            ),
    ) {
        content()

        PullToRefreshDefaults.Indicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(indicatorPadding),
            isRefreshing = refreshing,
            state = state,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (showRefreshButton) {
            FilledTonalIconButton(
                onClick = onRefresh,
                enabled = enabled && !refreshing,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(indicatorPadding)
                    .padding(16.dp)
                    .focusHighlight(),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(MR.strings.action_webview_refresh),
                )
            }
        }
    }
}

internal fun shouldShowTvRefreshButton(uiModeType: Int, hasLeanbackFeature: Boolean): Boolean {
    return uiModeType == Configuration.UI_MODE_TYPE_TELEVISION || hasLeanbackFeature
}

internal fun shouldEnablePullRefreshGesture(enabled: Boolean, isTelevision: Boolean): Boolean {
    return enabled && !isTelevision
}
