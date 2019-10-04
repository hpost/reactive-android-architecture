package cc.femto.architecture.reactive.components.repositories

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cc.femto.architecture.reactive.App
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.components.repositories.adapter.RepositoryItem
import cc.femto.kommon.extensions.invisible
import cc.femto.kommon.extensions.string
import cc.femto.kommon.extensions.visible
import cc.femto.mvi.BaseView
import cc.femto.rx.extensions.mapDistinct
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.include_error_view.view.*
import kotlinx.android.synthetic.main.include_loading_view.view.*
import kotlinx.android.synthetic.main.repositories_layout.view.*
import javax.inject.Inject

class RepositoriesLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    BaseView<RepositoriesAction, RepositoriesState> {

    @Inject
    lateinit var picasso: Picasso

    override val disposables = CompositeDisposable()
    override val actions = PublishSubject.create<RepositoriesAction>()

    private val repositoriesAdapter = GroupAdapter<GroupieViewHolder>()
    private val repositoriesSection = Section()

    init {
        if (!isInEditMode) {
            App.component.inject(this)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isInEditMode) {
            return
        }
        setupLayout()
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

    override fun render(state: Observable<RepositoriesState>): CompositeDisposable {
        val loading = state.mapDistinct { isLoading }
            .subscribe { isLoading ->
                when (isLoading) {
                    true -> loading_view.visible()
                    else -> loading_view.invisible()
                }
            }

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
                    actions.onNext(RepositoriesAction.TapOnRepository(repository))
                })
            }

        return CompositeDisposable(
            loading,
            error,
            repositories
        )
    }
}
