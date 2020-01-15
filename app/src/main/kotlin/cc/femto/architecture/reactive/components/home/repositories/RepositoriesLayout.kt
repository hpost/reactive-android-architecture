package cc.femto.architecture.reactive.components.home.repositories

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.appComponent
import cc.femto.architecture.reactive.databinding.RepositoriesLayoutBinding
import cc.femto.kommon.extensions.string
import cc.femto.mvi.BaseView
import cc.femto.rx.extensions.mapDistinct
import com.jakewharton.rxbinding3.view.clicks
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class RepositoriesLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    BaseView<RepositoriesAction, RepositoriesState> {

    private val picasso by lazy { appComponent().picasso() }
    private val binding by lazy { RepositoriesLayoutBinding.bind(this) }

    override val disposables = CompositeDisposable()
    override val actions = PublishSubject.create<RepositoriesAction>()

    private val repositoriesAdapter = GroupAdapter<GroupieViewHolder>()
    private val repositoriesSection = Section()

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupLayout()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        makeActions()
    }

    override fun onDetachedFromWindow() {
        detach()
        super.onDetachedFromWindow()
    }

    private fun setupLayout() {
        repositoriesAdapter.add(repositoriesSection)
        binding.contentView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = repositoriesAdapter
        }
    }

    private fun makeActions() {
        val retry = binding.errorView.retryButton.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .map { RepositoriesAction.RetrySearch }

        disposables += Observable.mergeArray(
            retry
        ).subscribe(actions::onNext)
    }

    override fun render(state: Observable<RepositoriesState>): CompositeDisposable {
        val error = state.mapDistinct { isError to error }.subscribe { (isError, error) ->
            binding.viewAnimator.displayedChildId = when {
                isError -> R.id.error_view
                else -> R.id.content_view
            }
            if (isError) {
                binding.errorView.errorMessage.text = error?.message ?: string(R.string.default_error_message)
            }
        }

        val repositories = state.mapDistinct { repositories }.subscribe {
            repositoriesSection.update(RepositoryItem.from(picasso, it) { repository ->
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
