package eu.kanade.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.StringResource
import eu.kanade.tachiyomi.util.system.isTvUiEnabled
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.TabText
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun TabbedScreen(
    titleRes: StringResource?,
    tabs: ImmutableList<TabContent>,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState { tabs.size },
    tvPage: MutableState<Int> = rememberSaveable { mutableStateOf(0) },
    mangaSearchQuery: String? = null,
    onChangeMangaSearchQuery: (String?) -> Unit = {},
    scrollable: Boolean = false,
    animeSearchQuery: String? = null,
    onChangeAnimeSearchQuery: (String?) -> Unit = {},

) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isTelevision = LocalContext.current.isTvUiEnabled()
    val selectedPage = if (isTelevision) tvPage.value else state.currentPage
    val tvPageStateHolder = rememberSaveableStateHolder()
    val tabFocusRequesters = remember(tabs.size) {
        List(tabs.size) { FocusRequester() }
    }

    LaunchedEffect(isTelevision, selectedPage) {
        if (isTelevision) {
            tabFocusRequesters[selectedPage].requestFocus()
        }
    }

    Scaffold(
        topBar = {
            if (titleRes != null) {
                val tab = tabs[selectedPage]
                val searchEnabled = tab.searchEnabled

                val actualQuery = when (selectedPage % 2) {
                    1 -> mangaSearchQuery // History and Browse
                    else -> animeSearchQuery
                }

                val actualOnChange = when (selectedPage % 2) {
                    1 -> onChangeMangaSearchQuery // History and Browse
                    else -> onChangeAnimeSearchQuery
                }

                SearchToolbar(
                    titleContent = {
                        AppBarTitle(
                            stringResource(titleRes),
                            modifier = modifier,
                            null,
                            tab.numberTitle,
                        )
                    },
                    searchEnabled = searchEnabled,
                    searchQuery = if (searchEnabled) actualQuery else null,
                    onChangeSearchQuery = actualOnChange,
                    actions = { AppBarActions(tab.actions) },
                    navigateUp = tab.navigateUp,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(
                top = contentPadding.calculateTopPadding(),
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
            ),
        ) {
            FlexibleTabRow(
                scrollable = scrollable,
                selectedTabIndex = selectedPage,
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        modifier = Modifier.focusRequester(tabFocusRequesters[index]),
                        selected = selectedPage == index,
                        onClick = {
                            if (isTelevision) {
                                tabFocusRequesters[index].requestFocus()
                                tvPage.value = index
                            } else {
                                scope.launch {
                                    state.animateScrollToPage(index)
                                }
                            }
                        },
                        text = {
                            TabText(
                                text = stringResource(tab.titleRes),
                                badgeCount = tab.badgeNumber,
                            )
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            if (isTelevision) {
                // A pager can bring neighbouring pages into view during D-pad focus search,
                // even with swipe input disabled. Only the active TV page may own focus.
                Box(Modifier.fillMaxSize()) {
                    tvPageStateHolder.SaveableStateProvider(selectedPage) {
                        tabs[selectedPage].content(
                            PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                            snackbarHostState,
                        )
                    }
                }
            } else {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    verticalAlignment = Alignment.Top,
                ) { page ->
                    tabs[page].content(
                        PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                        snackbarHostState,
                    )
                }
            }
        }
    }
}

data class TabContent(
    val titleRes: StringResource,
    val badgeNumber: Int? = null,
    val searchEnabled: Boolean = false,
    val actions: ImmutableList<AppBar.AppBarAction> = persistentListOf(),
    val content: @Composable (contentPadding: PaddingValues, snackbarHostState: SnackbarHostState) -> Unit,
    val numberTitle: Int = 0,
    val cancelAction: () -> Unit = {},
    val navigateUp: (() -> Unit)? = null,
)

@Composable
private fun FlexibleTabRow(
    scrollable: Boolean,
    selectedTabIndex: Int,
    block: @Composable () -> Unit,
) {
    return if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 13.dp,
            modifier = Modifier.zIndex(1f),
        ) {
            block()
        }
    } else {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.zIndex(1f),
        ) {
            block()
        }
    }
}
