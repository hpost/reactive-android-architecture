package cc.femto.architecture.reactive.components.home

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import cc.femto.architecture.reactive.appComponent
import cc.femto.architecture.reactive.components.repositories.RepositoriesLayout
import cc.femto.architecture.reactive.components.repositories.RepositoriesModel
import cc.femto.architecture.reactive.components.repositories.RepositoriesState
import cc.femto.kommon.extensions.invisible
import cc.femto.kommon.extensions.visible
import cc.femto.mvi.BaseView
import cc.femto.mvi.attachComponent
import cc.femto.rx.extensions.mapDistinct
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.home_layout.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    BaseView<HomeAction, HomeState> {

    @Inject
    lateinit var repositoriesModel: RepositoriesModel

    override val disposables = CompositeDisposable()
    override val actions = PublishSubject.create<HomeAction>()

    override fun attach(state: Observable<HomeState>) {
        super.attach(state)

        attachComponent(
            repositories as RepositoriesLayout,
            repositoriesModel
        ) {
            disposables += renderRepositoriesState(
                repositoriesModel.state().observeOn(AndroidSchedulers.mainThread())
            )
            disposables += state.mapDistinct { query }
                .subscribe { repositoriesModel.dispatchEvent(RepositoriesModel.InitEvent(it)) }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isInEditMode) return
        appComponent().inject(this) // TODO use HomeComponent
        makeActions()
    }

    override fun onDetachedFromWindow() {
        detach()
        super.onDetachedFromWindow()
    }

    private fun makeActions() {
        val queryInput = query_edit_text.textChanges()
            .skipInitialValue()
            .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .map { HomeAction.QueryInput(it.toString()) }

        val clearQueryClicks = clear_query_button.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .map { HomeAction.ClearQuery }

        disposables += Observable.mergeArray(
            queryInput,
            clearQueryClicks
        ).subscribe(actions::onNext)
    }

    override fun render(state: Observable<HomeState>): CompositeDisposable {
        val query = state.map { it.query }
            .distinctUntilChanged()
            .filter { it != query_edit_text.text.toString() }
            .subscribe { query_edit_text.setText(it) }

        return CompositeDisposable(
            query
        )
    }

    private fun renderRepositoriesState(state: Observable<RepositoriesState>): CompositeDisposable {
        val loading = state.mapDistinct { isLoading }
            .subscribe { isLoading ->
                when (isLoading) {
                    true -> {
                        progress_bar.visible()
                        clear_query_button.invisible()
                    }
                    else -> {
                        progress_bar.invisible()
                        clear_query_button.visible()
                    }
                }
            }

        return CompositeDisposable(
            loading
        )
    }
}
