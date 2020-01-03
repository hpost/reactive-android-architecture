package cc.femto.architecture.reactive.api

import cc.femto.architecture.reactive.api.model.Repository
import cc.femto.architecture.reactive.api.model.SearchOrder
import cc.femto.architecture.reactive.api.model.SearchRepositoriesResponse
import cc.femto.architecture.reactive.api.model.SearchSort
import cc.femto.kommon.extensions.pow
import cc.femto.spek.extras.rxGroup
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SearchApiSpec : Spek({

    val testScheduler = TestScheduler()
    lateinit var service: SearchService
    lateinit var observer: TestObserver<SearchRepositoriesEvent>
    lateinit var searchApi: SearchApi

    val query = "rx-mvi"
    val sort = SearchSort.STARS
    val order = SearchOrder.DESC

    val repositories = listOf(
        Repository(name = "repository-1", htmlUrl = "url/1", stargazersCount = 42),
        Repository(name = "repository-2", htmlUrl = "url/2", stargazersCount = 1)
    )
    val successResponse = Response.success(
        SearchRepositoriesResponse(repositories.size, repositories)
    )
    val successResult = Result.response(successResponse)
    val errorResult = Result.error<SearchRepositoriesResponse>(TimeoutException("took too long"))

    val progressEvent = SearchRepositoriesEvent.InProgress
    val successEvent = SearchRepositoriesEvent.Success(
        successResult.response()?.body()?.items.orEmpty()
    )
    val errorEvent = SearchRepositoriesEvent.Error(errorResult.error())

    beforeEachTest {
        service = mock {
            on { searchRepositories(query, sort, order) } doReturn Observable.just(successResult)
        }
        searchApi = SearchApi(service)
    }

    rxGroup("SearchApi", scheduler = testScheduler) {
        describe("search repositories") {
            context("success") {
                beforeEachTest {
                    observer = searchApi.searchRepositories(query, sort, order).test()
                }
                it("calls service") {
                    verify(service).searchRepositories(query, sort, order)
                }
                it("emits correct events") {
                    observer.assertValues(
                        progressEvent,
                        successEvent
                    )
                }
            }
            context("error") {
                beforeEachTest {
                    whenever(service.searchRepositories(any(), any(), any()))
                        .doReturn(Observable.just(errorResult))
                    observer = searchApi.searchRepositories(query, sort, order).test()
                    // account for exponential backoff retry behavior
                    testScheduler.advanceTimeBy(2.pow(3).toLong(), TimeUnit.SECONDS)
                }
                it("emits correct events") {
                    observer.assertValueAt(0, progressEvent)
                    observer.assertValueAt(1) {
                        val message = (it as SearchRepositoriesEvent.Error).error?.message
                        message == errorEvent.error?.message
                    }
                }
            }
        }
    }
})
