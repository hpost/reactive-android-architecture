package cc.femto.architecture.reactive.components.home.repositories.adapter

import android.view.View
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.api.model.Repository
import cc.femto.architecture.reactive.databinding.RepositoryRowBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class RepositoryItem(
    private val picasso: Picasso,
    private val repository: Repository,
    private val onClickListener: (Repository) -> Unit
) : Item<RepositoryItem.RepositoryViewHolder>() {

    override fun getLayout() = R.layout.repository_row

    override fun createViewHolder(itemView: View): RepositoryViewHolder {
        return RepositoryViewHolder(RepositoryRowBinding.bind(itemView))
    }

    override fun bind(viewHolder: RepositoryViewHolder, position: Int) {
        with(repository) {
            picasso.load(owner.avatar_url)
                .fit().centerCrop()
                .into(viewHolder.binding.avatar)
            viewHolder.binding.title.text = name
            viewHolder.binding.owner.text = owner.login
            viewHolder.binding.stargazers.text = stargazers_count.toString()
        }
        viewHolder.root.setOnClickListener { onClickListener(repository) }
    }

    override fun getId() = repository.id.toLong()

    override fun hasSameContentAs(other: Item<*>?) =
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

    class RepositoryViewHolder(val binding: RepositoryRowBinding) : GroupieViewHolder(binding.root)
}
