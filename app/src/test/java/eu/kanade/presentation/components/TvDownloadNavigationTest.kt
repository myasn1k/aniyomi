package eu.kanade.presentation.components

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvDownloadNavigationTest {

    @Test
    fun `right moves from a focused TV item to its download action`() {
        assertTrue(
            shouldFocusDownloadOnTvRight(
                isTelevision = true,
                itemFocused = true,
                keyCode = KeyEvent.KEYCODE_DPAD_RIGHT,
                action = KeyEvent.ACTION_DOWN,
            ),
        )
    }

    @Test
    fun `download shortcut does not replace ordinary navigation`() {
        assertFalse(shouldFocusDownloadOnTvRight(false, true, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.ACTION_DOWN))
        assertFalse(shouldFocusDownloadOnTvRight(true, false, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.ACTION_DOWN))
        assertFalse(shouldFocusDownloadOnTvRight(true, true, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.ACTION_DOWN))
        assertFalse(shouldFocusDownloadOnTvRight(true, true, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.ACTION_UP))
    }

    @Test
    fun `left returns from a download action to its item`() {
        assertTrue(shouldFocusItemOnTvLeft(true, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.ACTION_DOWN))
        assertFalse(shouldFocusItemOnTvLeft(false, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.ACTION_DOWN))
        assertFalse(shouldFocusItemOnTvLeft(true, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.ACTION_DOWN))
        assertFalse(shouldFocusItemOnTvLeft(true, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.ACTION_UP))
    }
}
