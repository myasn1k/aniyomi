package eu.kanade.tachiyomi.ui.player.controls.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SeekBarPositionTest {

    @Test
    fun `playback position is used when not seeking`() {
        assertEquals(10f, position(isGestureSeeking = false, isSeeking = false))
    }

    @Test
    fun `touch gesture position takes priority over slider position`() {
        assertEquals(20f, position(isGestureSeeking = true, isSeeking = true))
        assertEquals(20f, position(isGestureSeeking = true, isSeeking = false))
    }

    @Test
    fun `slider and remote seeking use their latest local position`() {
        assertEquals(30f, position(isGestureSeeking = false, isSeeking = true))
    }

    private fun position(isGestureSeeking: Boolean, isSeeking: Boolean): Float = seekBarDisplayPosition(
        playerPosition = 10f,
        gestureSeekPosition = 20f,
        internalSeekPosition = 30f,
        isGestureSeeking = isGestureSeeking,
        isSeeking = isSeeking,
    )
}
