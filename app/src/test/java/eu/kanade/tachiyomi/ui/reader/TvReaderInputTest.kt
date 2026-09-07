package eu.kanade.tachiyomi.ui.reader

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvReaderInputTest {

    @Test
    fun `center and enter reveal hidden reader controls`() {
        assertTrue(isTvReaderMenuKey(KeyEvent.KEYCODE_DPAD_CENTER))
        assertTrue(isTvReaderMenuKey(KeyEvent.KEYCODE_ENTER))
        assertTrue(isTvReaderMenuKey(KeyEvent.KEYCODE_NUMPAD_ENTER))
        assertTrue(isTvReaderMenuKey(KeyEvent.KEYCODE_BUTTON_A))
        assertTrue(isTvReaderMenuKey(KeyEvent.KEYCODE_SPACE))
    }

    @Test
    fun `directional reader navigation remains available to the viewer`() {
        assertFalse(isTvReaderMenuKey(KeyEvent.KEYCODE_DPAD_LEFT))
        assertFalse(isTvReaderMenuKey(KeyEvent.KEYCODE_DPAD_RIGHT))
        assertFalse(isTvReaderMenuKey(KeyEvent.KEYCODE_DPAD_UP))
        assertFalse(isTvReaderMenuKey(KeyEvent.KEYCODE_DPAD_DOWN))
    }

    @Test
    fun `visible reader UI owns directional and select keys`() {
        assertTrue(isTvReaderUiNavigationKey(KeyEvent.KEYCODE_DPAD_LEFT))
        assertTrue(isTvReaderUiNavigationKey(KeyEvent.KEYCODE_DPAD_RIGHT))
        assertTrue(isTvReaderUiNavigationKey(KeyEvent.KEYCODE_DPAD_UP))
        assertTrue(isTvReaderUiNavigationKey(KeyEvent.KEYCODE_DPAD_DOWN))
        assertTrue(isTvReaderUiNavigationKey(KeyEvent.KEYCODE_DPAD_CENTER))
        assertTrue(isTvReaderUiNavigationKey(KeyEvent.KEYCODE_ENTER))
        assertFalse(isTvReaderUiNavigationKey(KeyEvent.KEYCODE_VOLUME_UP))
    }
}
