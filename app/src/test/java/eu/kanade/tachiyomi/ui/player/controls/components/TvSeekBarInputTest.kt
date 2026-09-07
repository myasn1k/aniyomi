package eu.kanade.tachiyomi.ui.player.controls.components

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TvSeekBarInputTest {

    @Test
    fun `single horizontal press seeks thirty seconds`() {
        assertEquals(30f, tvSeekStepSeconds(KeyEvent.KEYCODE_DPAD_RIGHT, repeatCount = 0))
        assertEquals(-30f, tvSeekStepSeconds(KeyEvent.KEYCODE_DPAD_LEFT, repeatCount = 0))
    }

    @Test
    fun `held horizontal press advances smoothly in five second steps`() {
        assertEquals(5f, tvSeekStepSeconds(KeyEvent.KEYCODE_DPAD_RIGHT, repeatCount = 1))
        assertEquals(-5f, tvSeekStepSeconds(KeyEvent.KEYCODE_DPAD_LEFT, repeatCount = 8))
    }

    @Test
    fun `non horizontal remote keys remain available to focus navigation`() {
        assertNull(tvSeekStepSeconds(KeyEvent.KEYCODE_DPAD_UP, repeatCount = 0))
        assertNull(tvSeekStepSeconds(KeyEvent.KEYCODE_DPAD_CENTER, repeatCount = 0))
    }

    @Test
    fun `vertical keys escape the timeline`() {
        assertEquals(true, isTvSeekEscapeKey(KeyEvent.KEYCODE_DPAD_UP))
        assertEquals(true, isTvSeekEscapeKey(KeyEvent.KEYCODE_DPAD_DOWN))
        assertEquals(false, isTvSeekEscapeKey(KeyEvent.KEYCODE_DPAD_RIGHT))
    }

    @Test
    fun `select keys do not activate the timeline`() {
        assertEquals(true, isTvSeekActivationKey(KeyEvent.KEYCODE_DPAD_CENTER))
        assertEquals(true, isTvSeekActivationKey(KeyEvent.KEYCODE_ENTER))
        assertEquals(false, isTvSeekActivationKey(KeyEvent.KEYCODE_DPAD_RIGHT))
    }

    @Test
    fun `horizontal key release commits only when seeking began on timeline`() {
        assertEquals(true, shouldFinishTvSeek(KeyEvent.KEYCODE_DPAD_RIGHT, seekInProgress = true))
        assertEquals(false, shouldFinishTvSeek(KeyEvent.KEYCODE_DPAD_RIGHT, seekInProgress = false))
        assertEquals(false, shouldFinishTvSeek(KeyEvent.KEYCODE_DPAD_CENTER, seekInProgress = true))
    }
}
