package eu.kanade.presentation.more.onboarding

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvStorageStepTest {

    @Test
    fun `TV setup cannot complete with an inaccessible folder`() {
        assertFalse(
            isTvStorageReady(
                isTvBox = true,
                storagePreferenceSet = true,
                hasExternalStorageManager = false,
            ),
        )
    }

    @Test
    fun `TV setup completes after folder and storage access are available`() {
        assertTrue(
            isTvStorageReady(
                isTvBox = true,
                storagePreferenceSet = true,
                hasExternalStorageManager = true,
            ),
        )
    }

    @Test
    fun `SAF based handheld storage does not require all files access`() {
        assertTrue(
            isTvStorageReady(
                isTvBox = false,
                storagePreferenceSet = true,
                hasExternalStorageManager = false,
            ),
        )
    }
}
