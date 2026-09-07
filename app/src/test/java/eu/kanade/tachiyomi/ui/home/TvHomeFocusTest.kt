package eu.kanade.tachiyomi.ui.home

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class TvHomeFocusTest {

    @Test
    fun `disabled rail does not route focus to an unattached requester`() {
        assertSame(
            FocusRequester.Default,
            tvContentExitDestination(FocusDirection.Left, FocusRequester(), navigationRailAvailable = false),
        )
    }

    @Test
    fun `left exit returns content focus to the navigation rail`() {
        val navigationFocusRequester = FocusRequester()

        assertSame(
            navigationFocusRequester,
            tvContentExitDestination(FocusDirection.Left, navigationFocusRequester),
        )
    }

    @Test
    fun `other exits retain normal spatial focus behavior`() {
        val navigationFocusRequester = FocusRequester()

        listOf(
            FocusDirection.Right,
            FocusDirection.Up,
            FocusDirection.Down,
            FocusDirection.Next,
            FocusDirection.Previous,
        ).forEach {
            assertSame(FocusRequester.Default, tvContentExitDestination(it, navigationFocusRequester))
        }
    }
}
