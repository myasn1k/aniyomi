package eu.kanade.tachiyomi.util.system

import android.content.res.Configuration
import eu.kanade.domain.ui.model.TabletUiMode
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DisplayExtensionsTest {

    @Test
    fun `automatic mode uses navigation rail on a TV regardless of reported width`() {
        assertTrue(
            shouldUseTabletUi(
                mode = TabletUiMode.AUTOMATIC,
                smallestScreenWidthDp = 540,
                orientation = Configuration.ORIENTATION_LANDSCAPE,
                isTelevision = true,
            ),
        )
    }

    @Test
    fun `automatic mode retains phone behavior`() {
        assertFalse(
            shouldUseTabletUi(
                mode = TabletUiMode.AUTOMATIC,
                smallestScreenWidthDp = 540,
                orientation = Configuration.ORIENTATION_LANDSCAPE,
                isTelevision = false,
            ),
        )
    }

    @Test
    fun `explicit never mode remains an escape hatch on TVs`() {
        assertFalse(
            shouldUseTabletUi(
                mode = TabletUiMode.NEVER,
                smallestScreenWidthDp = 960,
                orientation = Configuration.ORIENTATION_LANDSCAPE,
                isTelevision = true,
            ),
        )
    }
}
