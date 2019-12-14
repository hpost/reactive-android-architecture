package cc.femto.architecture.reactive.components.home

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesLayout
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesModel
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesState
import cc.femto.architecture.reactive.databinding.HomeLayoutBinding
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
import java.util.concurrent.TimeUnit

class HomeLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    BaseView<HomeAction, HomeState> {

    private val repositoriesModel by lazy {
        (context as HomeActivity).homeComponent
            .repositoriesModel()
    }
    private val binding by lazy { HomeLayoutBinding.bind(this) }
    override val disposables = CompositeDisposable()
    override val actions = PublishSubject.create<HomeAction>()

    override fun attach(state: Observable<HomeState>) {
        super.attach(state)

        attachComponent(
            binding.repositories.root as RepositoriesLayout,
            repositoriesModel
        ) {
            disposables += renderRepositoriesState(
                repositoriesModel.state().observeOn(AndroidSchedulers.mainThread())
            )
            disposables += state.mapDistinct { query }
                .subscribe { repositoriesModel.dispatchEvent(RepositoriesModel.InitEvent(it)) }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        makeActions()
    }

    override fun onDetachedFromWindow() {
        detach()
        super.onDetachedFromWindow()
    }

    private fun makeActions() {
        val queryInput = binding.queryEditText.textChanges()
            .skipInitialValue()
            .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .map { HomeAction.QueryInput(it.toString()) }

        val clearQueryClicks = binding.clearQueryButton.clicks()
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
            .filter { it != binding.queryEditText.text.toString() }
            .subscribe { binding.queryEditText.setText(it) }

        return CompositeDisposable(
            query
        )
    }

    private fun renderRepositoriesState(state: Observable<RepositoriesState>): CompositeDisposable {
        val loading = state.mapDistinct { isLoading }
            .subscribe { isLoading ->
                when (isLoading) {
                    true -> {
                        binding.progressBar.visible()
                        binding.clearQueryButton.invisible()
                    }
                    else -> {
                        binding.progressBar.invisible()
                        binding.clearQueryButton.visible()
                    }
                }
            }

        return CompositeDisposable(
            loading
        )
    }
}
