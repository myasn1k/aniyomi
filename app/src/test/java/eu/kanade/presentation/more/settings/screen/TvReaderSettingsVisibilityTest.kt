package eu.kanade.presentation.more.settings.screen

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvReaderSettingsVisibilityTest {

    @Test
    fun `TV hides hardware specific reader settings`() {
        assertFalse(shouldShowEInkSettings(isTelevision = true))
        assertFalse(shouldShowVolumeKeySettings(isTelevision = true))
    }

    @Test
    fun `handheld keeps existing reader settings`() {
        assertTrue(shouldShowEInkSettings(isTelevision = false))
        assertTrue(shouldShowVolumeKeySettings(isTelevision = false))
    }
}
