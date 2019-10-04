package cc.femto.architecture.reactive.data.api

import cc.femto.architecture.reactive.data.model.Repository
import cc.femto.architecture.reactive.data.model.SearchRepositoriesResponse
import cc.femto.kommon.extensions.isSuccess
import cc.femto.mvi.Event
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface SearchService {
    @GET("/search/repositories")
    fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("order") order: String = "desc"
    ): Observable<Result<SearchRepositoriesResponse>>
}

@Singleton
class SearchApi @Inject constructor(
    private val service: SearchService
) {

    fun searchRepositories(
        query: String,
        sort: String? = null,
        order: String = "desc"
    ): Observable<SearchRepositoriesEvent> =
        service.searchRepositories(query, sort, order)
            .map {
                when {
                    it.isSuccess -> SearchRepositoriesEvent.Success(
                        it.response()?.body()?.items.orEmpty()
                    )
                    else -> SearchRepositoriesEvent.Error(it.error())
                }
            }
            .startWith(SearchRepositoriesEvent.InProgress)
            .subscribeOn(Schedulers.io())
}

sealed class SearchRepositoriesEvent : Event {
    object InProgress : SearchRepositoriesEvent()
    data class Success(val repositories: List<Repository>) : SearchRepositoriesEvent()
    data class Error(val error: Throwable? = null) : SearchRepositoriesEvent()
}
