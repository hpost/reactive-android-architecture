package cc.femto.architecture.reactive.api

import cc.femto.architecture.reactive.api.model.Repository
import cc.femto.architecture.reactive.api.model.SearchOrder
import cc.femto.architecture.reactive.api.model.SearchRepositoriesResponse
import cc.femto.architecture.reactive.api.model.SearchSort
import cc.femto.kommon.extensions.isSuccess
import cc.femto.kommon.extensions.retryOnNetworkError
import cc.femto.mvi.Event
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface SearchService {
    @GET("/search/repositories")
    fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: SearchSort?,
        @Query("order") order: SearchOrder
    ): Observable<Result<SearchRepositoriesResponse>>
}

@Singleton
class SearchApi @Inject constructor(
    private val service: SearchService
) {

    fun searchRepositories(
        query: String,
        sort: SearchSort? = null,
        order: SearchOrder = SearchOrder.DESC
    ): Observable<SearchRepositoriesEvent> =
        service.searchRepositories(query, sort, order)
            .retryOnNetworkError()
            .map {
                when {
                    it.isSuccess -> SearchRepositoriesEvent.Success(
                        it.response()?.body()?.items.orEmpty()
                    )
                    else -> SearchRepositoriesEvent.Error(it.error())
                }
            }
            .startWith(SearchRepositoriesEvent.InProgress)
}

sealed class SearchRepositoriesEvent : Event {
    object InProgress : SearchRepositoriesEvent()
    data class Success(val repositories: List<Repository>) : SearchRepositoriesEvent()
    data class Error(val error: Throwable? = null) : SearchRepositoriesEvent()
}
