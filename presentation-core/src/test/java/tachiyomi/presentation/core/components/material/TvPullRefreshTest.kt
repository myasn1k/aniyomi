package tachiyomi.presentation.core.components.material

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TvPullRefreshTest {

    @Test
    fun `enabled TV mode replaces pull gesture with the remote button`() {
        assertFalse(shouldEnablePullRefreshGesture(enabled = true, isTelevision = true))
    }

    @Test
    fun `disabled TV mode preserves the mobile pull gesture`() {
        assertTrue(shouldEnablePullRefreshGesture(enabled = true, isTelevision = false))
    }

    @Test
    fun `disabled refresh remains disabled in both modes`() {
        assertFalse(shouldEnablePullRefreshGesture(enabled = false, isTelevision = false))
        assertFalse(shouldEnablePullRefreshGesture(enabled = false, isTelevision = true))
    }
}
