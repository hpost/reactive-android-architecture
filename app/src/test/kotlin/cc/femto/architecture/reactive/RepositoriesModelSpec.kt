package cc.femto.architecture.reactive

import cc.femto.architecture.reactive.api.SearchApi
import cc.femto.architecture.reactive.api.SearchRepositoriesEvent
import cc.femto.architecture.reactive.api.model.Repository
import cc.femto.architecture.reactive.api.model.SearchOrder
import cc.femto.architecture.reactive.api.model.SearchSort
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesAction
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesModel
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesState
import cc.femto.architecture.reactive.navigation.Navigator
import cc.femto.spek.extras.rxGroup
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.concurrent.TimeoutException

class RepositoriesModelSpec : Spek({

    lateinit var searchApi: SearchApi
    lateinit var navigator: Navigator
    lateinit var actions: Subject<RepositoriesAction>
    lateinit var model: RepositoriesModel
    lateinit var observer: TestObserver<RepositoriesState>

    val repositories = listOf(
        Repository(name = "repository-1", html_url = "url/1", stargazers_count = 42),
        Repository(name = "repository-2", html_url = "url/2", stargazers_count = 1)
    )
    val searchSuccess = Observable.just(
        SearchRepositoriesEvent.InProgress,
        SearchRepositoriesEvent.Success(repositories)
    )
    val error = TimeoutException("took too long")
    val searchError = Observable.just(
        SearchRepositoriesEvent.InProgress,
        SearchRepositoriesEvent.Error(error)
    )

    val initEvent = RepositoriesModel.InitEvent(query = "rx-mvi")
    val sort = SearchSort.STARS
    val order = SearchOrder.DESC

    val initialState = RepositoriesState()
    val queryState = RepositoriesState(query = initEvent.query)
    val loadingState = queryState.copy(isLoading = true)
    val successState = queryState.copy(repositories = repositories)
    val errorState = queryState.copy(isError = true, error = error)

    beforeEachTest {
        searchApi = mock {
            on { searchRepositories(initEvent.query, sort, order) } doReturn searchSuccess
        }
        navigator = mock()
        actions = PublishSubject.create()
        model = RepositoriesModel(searchApi, navigator)
    }

    rxGroup("RepositoriesModel") {
        beforeEachTest {
            model.attach(actions)
        }

        describe("search") {
            context("success") {
                beforeEachTest {
                    observer = model.state().test()
                    model.dispatchEvent(initEvent)
                }
                it("emits correct state") {
                    observer.assertValues(
                        initialState,
                        queryState,
                        loadingState,
                        successState
                    )
                }
            }
            context("error") {
                beforeEachTest {
                    whenever(searchApi.searchRepositories(any(), any(), any()))
                        .thenReturn(searchError)
                    observer = model.state().test()
                    model.dispatchEvent(initEvent)
                }
                it("emits correct state") {
                    observer.assertValues(
                        initialState,
                        queryState,
                        loadingState,
                        errorState
                    )
                }
            }
        }

        describe("retry") {
            beforeEachTest {
                whenever(searchApi.searchRepositories(any(), any(), any()))
                    .thenReturn(searchError)
                model.dispatchEvent(initEvent)
                observer = model.state().test()
                whenever(searchApi.searchRepositories(any(), any(), any()))
                    .thenReturn(searchSuccess)
                actions.onNext(RepositoriesAction.RetrySearch)
            }
            it("emits correct state") {
                observer.assertValues(
                    errorState,
                    loadingState,
                    successState
                )
            }
        }

        describe("repository clicks") {
            val repository = repositories.first()
            beforeEachTest {
                actions.onNext(RepositoriesAction.TapOnRepository(repository))
            }
            it("navigates to URL") {
                verify(navigator).navigateToUrl(repository.html_url)
            }
        }
    }
})
