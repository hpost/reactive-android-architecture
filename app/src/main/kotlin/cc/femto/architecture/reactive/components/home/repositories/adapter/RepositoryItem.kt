package cc.femto.architecture.reactive.components.home.repositories.adapter

import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.api.model.Repository
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.repository_row.*

class RepositoryItem(
    private val picasso: Picasso,
    private val repository: Repository,
    private val onClickListener: (Repository) -> Unit
) : Item() {

    override fun getLayout() = R.layout.repository_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        with(repository) {
            picasso.load(repository.owner.avatar_url)
                .fit().centerCrop()
                .into(viewHolder.avatar)
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
            picasso: Picasso,
            repositories: List<Repository>,
            onClickListener: (Repository) -> Unit
        ) = repositories.map {
            RepositoryItem(picasso, it, onClickListener)
        }
    }
}
