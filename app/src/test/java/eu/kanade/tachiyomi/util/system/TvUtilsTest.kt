package eu.kanade.tachiyomi.util.system

import android.content.res.Configuration
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvUtilsTest {

    @Test
    fun `television ui mode is a TV`() {
        assertTrue(isTelevision(Configuration.UI_MODE_TYPE_TELEVISION, hasLeanbackFeature = false))
    }

    @Test
    fun `leanback device is a TV even when ui mode is misreported`() {
        assertTrue(isTelevision(Configuration.UI_MODE_TYPE_NORMAL, hasLeanbackFeature = true))
    }

    @Test
    fun `ordinary touch device is not a TV`() {
        assertFalse(isTelevision(Configuration.UI_MODE_TYPE_NORMAL, hasLeanbackFeature = false))
    }
}
