package tachiyomi.presentation.core.components.material

import android.content.res.Configuration
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvPullRefreshTest {

    @Test
    fun `TV configuration shows a physical refresh button`() {
        assertTrue(
            shouldShowTvRefreshButton(
                uiModeType = Configuration.UI_MODE_TYPE_TELEVISION,
                hasLeanbackFeature = false,
            ),
        )
    }

    @Test
    fun `Leanback devices show refresh even when UI mode is misreported`() {
        assertTrue(
            shouldShowTvRefreshButton(
                uiModeType = Configuration.UI_MODE_TYPE_NORMAL,
                hasLeanbackFeature = true,
            ),
        )
    }

    @Test
    fun `handheld keeps pull to refresh without an extra button`() {
        assertFalse(
            shouldShowTvRefreshButton(
                uiModeType = Configuration.UI_MODE_TYPE_NORMAL,
                hasLeanbackFeature = false,
            ),
        )
    }

    @Test
    fun `TV refresh button replaces the pull gesture`() {
        assertFalse(shouldEnablePullRefreshGesture(enabled = true, isTelevision = true))
        assertTrue(shouldEnablePullRefreshGesture(enabled = true, isTelevision = false))
        assertFalse(shouldEnablePullRefreshGesture(enabled = false, isTelevision = false))
    }
}
