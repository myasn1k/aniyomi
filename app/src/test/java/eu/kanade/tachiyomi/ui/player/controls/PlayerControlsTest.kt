package eu.kanade.tachiyomi.ui.player.controls

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
}
