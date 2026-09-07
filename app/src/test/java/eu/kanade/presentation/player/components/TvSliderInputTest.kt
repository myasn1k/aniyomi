package eu.kanade.presentation.player.components

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TvSliderInputTest {

    @Test
    fun `left and right map to slider changes`() {
        assertEquals(-1, tvSliderDirection(KeyEvent.KEYCODE_DPAD_LEFT))
        assertEquals(1, tvSliderDirection(KeyEvent.KEYCODE_DPAD_RIGHT))
    }

    @Test
    fun `vertical navigation remains available to leave a slider`() {
        assertNull(tvSliderDirection(KeyEvent.KEYCODE_DPAD_UP))
        assertNull(tvSliderDirection(KeyEvent.KEYCODE_DPAD_DOWN))
        assertNull(tvSliderDirection(KeyEvent.KEYCODE_DPAD_CENTER))
    }

    @Test
    fun `discrete slider step includes both endpoints`() {
        assertEquals(0.1f, tvSliderStep(0f, 1f, 9))
        assertEquals(2f, tvSliderStep(-10f, 10f, 9))
    }

    @Test
    fun `continuous sliders use one percent increments`() {
        assertEquals(1f, tvSliderStep(0f, 100f, 0))
    }
}
