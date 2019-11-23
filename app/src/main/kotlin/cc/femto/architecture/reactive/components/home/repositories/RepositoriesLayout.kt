package cc.femto.architecture.reactive.components.home.repositories

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.appComponent
import cc.femto.architecture.reactive.components.home.repositories.adapter.RepositoryItem
import cc.femto.kommon.extensions.string
import cc.femto.mvi.BaseView
import cc.femto.rx.extensions.mapDistinct
import com.jakewharton.rxbinding3.view.clicks
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.include_error_view.view.*
import kotlinx.android.synthetic.main.repositories_layout.view.*
import java.util.concurrent.TimeUnit

class RepositoriesLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    BaseView<RepositoriesAction, RepositoriesState> {

    private val picasso by lazy { appComponent().picasso() }

    override val disposables = CompositeDisposable()
    override val actions = PublishSubject.create<RepositoriesAction>()

    private val repositoriesAdapter = GroupAdapter<GroupieViewHolder>()
    private val repositoriesSection = Section()

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupLayout()
        makeActions()
    }

    override fun onDetachedFromWindow() {
        detach()
        super.onDetachedFromWindow()
    }

    private fun setupLayout() {
        repositoriesAdapter.add(repositoriesSection)
        content_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = repositoriesAdapter
        }
    }

    private fun makeActions() {
        val retry = retry_button.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .map { RepositoriesAction.RetrySearch }

        disposables += Observable.mergeArray(
            retry
        ).subscribe(actions::onNext)
    }

    override fun render(state: Observable<RepositoriesState>): CompositeDisposable {
        val error = state.mapDistinct { isError to error }
            .subscribe { (isError, error) ->
                view_animator.displayedChildId = when {
                    isError -> R.id.error_view
                    else -> R.id.content_view
                }
                if (isError) {
                    error_message.text = error?.message ?: string(R.string.default_error_message)
                }
            }

        val repositories = state.mapDistinct { repositories }
            .subscribe {
                repositoriesSection.update(RepositoryItem.from(it) { repository ->
                    actions.onNext(
                        RepositoriesAction.TapOnRepository(
                            repository
                        )
                    )
                })
            }

        return CompositeDisposable(
            error,
            repositories
        )
    }
}
