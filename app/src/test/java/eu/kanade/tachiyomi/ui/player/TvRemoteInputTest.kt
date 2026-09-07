package eu.kanade.tachiyomi.ui.player

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvRemoteInputTest {

    @Test
    fun `handled TV key consumes its matching key up`() {
        assertTrue(shouldConsumeTvKeyUp(KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BACK))
    }

    @Test
    fun `unhandled or different key up remains available`() {
        assertFalse(shouldConsumeTvKeyUp(null, KeyEvent.KEYCODE_BACK))
        assertFalse(shouldConsumeTvKeyUp(KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_BACK))
    }

    @Test
    fun `center reveals hidden controls`() {
        assertEquals(
            TvRemoteAction.ShowControls,
            tvRemoteAction(KeyEvent.KEYCODE_DPAD_CENTER, controlsShown = false),
        )
    }

    @Test
    fun `center is left to focused compose control when controls are visible`() {
        assertNull(tvRemoteAction(KeyEvent.KEYCODE_DPAD_CENTER, controlsShown = true))
    }

    @Test
    fun `modal player overlays own all navigation keys`() {
        val navigationKeys = listOf(
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_BACK,
        )

        navigationKeys.forEach { keyCode ->
            assertNull(
                tvRemoteAction(
                    keyCode = keyCode,
                    controlsShown = false,
                    modalOverlayShown = true,
                ),
            )
        }
    }

    @Test
    fun `media keys still work while a modal player overlay is visible`() {
        assertEquals(
            TvRemoteAction.TogglePlayback,
            tvRemoteAction(
                keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                controlsShown = false,
                modalOverlayShown = true,
            ),
        )
    }

    @Test
    fun `directional keys seek only while controls are hidden`() {
        assertEquals(
            TvRemoteAction.SeekBackward,
            tvRemoteAction(KeyEvent.KEYCODE_DPAD_LEFT, controlsShown = false),
        )
        assertEquals(
            TvRemoteAction.SeekForward,
            tvRemoteAction(KeyEvent.KEYCODE_DPAD_RIGHT, controlsShown = false),
        )
        assertNull(tvRemoteAction(KeyEvent.KEYCODE_DPAD_LEFT, controlsShown = true))
        assertNull(tvRemoteAction(KeyEvent.KEYCODE_DPAD_RIGHT, controlsShown = true))
    }

    @Test
    fun `hardware media buttons work independently of overlay visibility`() {
        assertEquals(
            TvRemoteAction.TogglePlayback,
            tvRemoteAction(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, controlsShown = false),
        )
        assertEquals(
            TvRemoteAction.SeekBackward,
            tvRemoteAction(KeyEvent.KEYCODE_MEDIA_REWIND, controlsShown = true),
        )
        assertEquals(
            TvRemoteAction.SeekForward,
            tvRemoteAction(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD, controlsShown = true),
        )
        assertEquals(TvRemoteAction.Play, tvRemoteAction(KeyEvent.KEYCODE_MEDIA_PLAY, false))
        assertEquals(TvRemoteAction.Pause, tvRemoteAction(KeyEvent.KEYCODE_MEDIA_PAUSE, false))
        assertEquals(TvRemoteAction.PreviousEpisode, tvRemoteAction(KeyEvent.KEYCODE_MEDIA_PREVIOUS, false))
        assertEquals(TvRemoteAction.NextEpisode, tvRemoteAction(KeyEvent.KEYCODE_MEDIA_NEXT, false))
    }

    @Test
    fun `back hides visible controls before leaving player`() {
        assertEquals(TvRemoteAction.HideControls, tvRemoteAction(KeyEvent.KEYCODE_BACK, true))
        assertNull(tvRemoteAction(KeyEvent.KEYCODE_BACK, false))
    }

    @Test
    fun `menu and channel keys have TV behavior`() {
        assertEquals(TvRemoteAction.ShowControls, tvRemoteAction(KeyEvent.KEYCODE_MENU, false))
        assertNull(tvRemoteAction(KeyEvent.KEYCODE_MENU, true))
        assertEquals(TvRemoteAction.PreviousEpisode, tvRemoteAction(KeyEvent.KEYCODE_CHANNEL_UP, false))
        assertEquals(TvRemoteAction.NextEpisode, tvRemoteAction(KeyEvent.KEYCODE_CHANNEL_DOWN, false))
    }

    @Test
    fun `TV navigation keys never fall through to MPV`() {
        assertTrue(isTvNavigationKey(KeyEvent.KEYCODE_DPAD_LEFT))
        assertTrue(isTvNavigationKey(KeyEvent.KEYCODE_DPAD_CENTER))
        assertTrue(isTvNavigationKey(KeyEvent.KEYCODE_BACK))
        assertFalse(isTvNavigationKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
        assertFalse(isTvNavigationKey(KeyEvent.KEYCODE_VOLUME_UP))
    }
}
