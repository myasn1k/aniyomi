package eu.kanade.tachiyomi.util.system

import android.content.res.Configuration
import eu.kanade.domain.ui.model.TvUiMode
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvUiModeTest {

    @Test
    fun `automatic mode enables remote UI on television hardware`() {
        assertTrue(resolveTvUiMode(TvUiMode.AUTOMATIC, television = true))
        assertFalse(resolveTvUiMode(TvUiMode.AUTOMATIC, television = false))
    }

    @Test
    fun `automatic mode includes misreported Leanback devices`() {
        val television = isTelevision(Configuration.UI_MODE_TYPE_NORMAL, hasLeanbackFeature = true)
        assertTrue(resolveTvUiMode(TvUiMode.AUTOMATIC, television))
    }

    @Test
    fun `forced TV mode also enables remote UI on a phone or generic box`() {
        assertTrue(resolveTvUiMode(TvUiMode.ALWAYS, television = false))
        assertTrue(resolveTvUiMode(TvUiMode.ALWAYS, television = true))
    }

    @Test
    fun `disabled TV mode overrides hardware detection for all UI consumers`() {
        assertFalse(resolveTvUiMode(TvUiMode.NEVER, television = true))
        assertFalse(resolveTvUiMode(TvUiMode.NEVER, television = false))
    }
}
