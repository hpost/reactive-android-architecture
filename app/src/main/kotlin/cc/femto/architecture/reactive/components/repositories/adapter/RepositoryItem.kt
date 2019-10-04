package cc.femto.architecture.reactive.components.repositories.adapter

import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.data.model.Repository
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.repository_row.*

class RepositoryItem(
    private val repository: Repository,
    private val onClickListener: (Repository) -> Unit
) : Item() {

    override fun getLayout() = R.layout.repository_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        with(repository) {
            viewHolder.title.text = name
            viewHolder.owner.text = owner.login
            viewHolder.stargazers.text = stargazers_count.toString()
        }
        viewHolder.root.setOnClickListener { onClickListener(repository) }
    }

    override fun getId() = repository.id.toLong()

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?) =
        this.repository.id == (other as? RepositoryItem)?.repository?.id

    companion object {
        fun from(
            repositories: List<Repository>,
            onClickListener: (Repository) -> Unit
        ) = repositories.map {
            RepositoryItem(it, onClickListener)
        }
    }
}
