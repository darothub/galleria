package com.darothub.galleria.data

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.darothub.galleria.model.ImageDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.Reader
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val repository: ImageDetailsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

//    val dataSource = repository.getImageStream()
    /**
     * Stream of immutable states representative of the UI.
     */
    lateinit var state: StateFlow<UiState>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    init {
        val (lastQueryScrolled: String, actionStateFlow, searches) = getLastScrolledQuery()

        val queriesScrolled = queriesScrolled(actionStateFlow, lastQueryScrolled)
        getState(searches, queriesScrolled)
        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun getState(
        searches: Flow<UiAction.Search>,
        queriesScrolled: Flow<UiAction.Scroll>
    ) {
        state = searches
            .flatMapLatest { search ->
                combine(
                    queriesScrolled,
                    searchImage(search.query),
                    ::Pair
                )
                    // Each unique PagingData should be submitted once, take the latest from
                    // queriesScrolled
                    .distinctUntilChangedBy { it.second }
                    .map { (scroll, pagingData) ->
                        UiState(
                            query = search.query,
                            pagingData = pagingData,
                            lastQueryScrolled = scroll.currentQuery,
                            // If the search query matches the scroll query, the user has scrolled
                            hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )
    }

    private fun queriesScrolled(
        actionStateFlow: MutableSharedFlow<UiAction>,
        lastQueryScrolled: String
    ): Flow<UiAction.Scroll> {
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }
        return queriesScrolled
    }

    private fun getLastScrolledQuery(): Triple<String, MutableSharedFlow<UiAction>, Flow<UiAction.Search>> {
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
        return Triple(lastQueryScrolled, actionStateFlow, searches)
    }


    fun searchImage(query: String): Flow<PagingData<UiModel.ImageItem>> =
        repository.getImageStream(query).map { pagingData ->
            pagingData.map {
                UiModel.ImageItem(it)
            }
        }.cachedIn(viewModelScope)

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }


}

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

sealed class UiModel {
    data class ImageItem(val image: ImageDetails) : UiModel()
}


data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val pagingData: PagingData<UiModel.ImageItem> = PagingData.empty()
)


private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = ""
private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
