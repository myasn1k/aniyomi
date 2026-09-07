package eu.kanade.tachiyomi.ui.player.controls

import android.view.KeyEvent
import eu.kanade.tachiyomi.ui.player.Sheets
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerControlsTest {

    @Test
    fun `playing controls auto hide after the configured timeout`() {
        assertTrue(
            shouldAutoHidePlayerControls(
                controlsShown = true,
                paused = false,
                isSeeking = false,
            ),
        )
    }

    @Test
    fun `paused controls remain available for remote navigation`() {
        assertFalse(
            shouldAutoHidePlayerControls(
                controlsShown = true,
                paused = true,
                isSeeking = false,
            ),
        )
    }

    @Test
    fun `controls remain visible during seeking`() {
        assertFalse(
            shouldAutoHidePlayerControls(
                controlsShown = true,
                paused = false,
                isSeeking = true,
            ),
        )
    }

    @Test
    fun `controls remain visible while a submenu is open`() {
        assertFalse(
            shouldAutoHidePlayerControls(
                controlsShown = true,
                paused = false,
                isSeeking = false,
                sheetShown = Sheets.More,
            ),
        )
    }

    @Test
    fun `TV remote key down restarts the auto hide countdown`() {
        assertTrue(shouldResetTvControlsAutoHide(isTelevision = true, keyAction = KeyEvent.ACTION_DOWN))
        assertFalse(shouldResetTvControlsAutoHide(isTelevision = true, keyAction = KeyEvent.ACTION_UP))
        assertFalse(shouldResetTvControlsAutoHide(isTelevision = false, keyAction = KeyEvent.ACTION_DOWN))
    }

    @Test
    fun `aspect control is kept on phones and hidden on TVs`() {
        assertFalse(shouldShowAspectControl(isTelevision = true))
        assertTrue(shouldShowAspectControl(isTelevision = false))
    }
}
