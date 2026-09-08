package eu.kanade.tachiyomi.ui.reader

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReaderNavigationOverlayTest {

    @Test
    fun `TV never shows touch regions on startup or after navigation changes`() {
        for (firstLaunch in listOf(true, false)) {
            for (showOnStart in listOf(true, false)) {
                for (navigationEnabled in listOf(true, false)) {
                    assertFalse(
                        shouldShowReaderNavigationOverlay(true, firstLaunch, showOnStart, navigationEnabled),
                    )
                }
            }
        }
    }

    @Test
    fun `handheld startup respects the existing guide preference`() {
        assertTrue(shouldShowReaderNavigationOverlay(false, true, true, true))
        assertFalse(shouldShowReaderNavigationOverlay(false, true, false, true))
    }

    @Test
    fun `handheld still previews changes to touch navigation`() {
        assertTrue(shouldShowReaderNavigationOverlay(false, false, false, true))
        assertTrue(shouldShowReaderNavigationOverlay(false, false, true, true))
    }

    @Test
    fun `disabled touch navigation does not show a guide`() {
        assertFalse(shouldShowReaderNavigationOverlay(false, true, true, false))
        assertFalse(shouldShowReaderNavigationOverlay(false, false, true, false))
    }
}
