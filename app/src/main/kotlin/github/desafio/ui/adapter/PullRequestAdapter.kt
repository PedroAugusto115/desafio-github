package github.desafio.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import github.desafio.R
import github.desafio.extension.loadFromUrl
import github.desafio.extension.toSimpleDate
import github.desafio.model.PullRequest

class PullRequestAdapter(private val listener: (url: String) -> Unit) :
        RecyclerView.Adapter<PullRequestAdapter.PullRequestViewHolder>()
{

    private var items: ArrayList<PullRequest> = arrayListOf()

    fun addItems(newItems: List<PullRequest>) {
        items.addAll(newItems)
    }

    override fun onBindViewHolder(holder: PullRequestViewHolder, position: Int) {
        val pullRequestItem = items[position]

        val description: String = if (!pullRequestItem.description.isEmpty())
            pullRequestItem.description
        else
            holder.view.context.resources.getString(R.string.NoDescription)

        holder.itemDate.text = pullRequestItem.dateCreated.toSimpleDate()
        holder.itemDescription.text = description
        holder.itemTitle.text = pullRequestItem.title
        holder.itemUserName.text = pullRequestItem.user.name
        holder.itemUserImage.loadFromUrl(pullRequestItem.user.photoPath)

        holder.itemRoot.setOnClickListener { listener(pullRequestItem.pullRequestUrl) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PullRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val repositoryView = inflater.inflate(R.layout.list_item_repository_pull, parent, false)
        return PullRequestViewHolder(repositoryView)
    }

    override fun getItemCount(): Int = items.size

    class PullRequestViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var itemTitle: TextView = view.findViewById(R.id.pull_title)
        var itemDescription: TextView = view.findViewById(R.id.pull_description)
        var itemUserName: TextView = view.findViewById(R.id.pull_username)
        var itemDate: TextView = view.findViewById(R.id.pull_open_date)
        var itemUserImage: ImageView = view.findViewById(R.id.pull_user_photo)
        var itemRoot: View = view.findViewById(R.id.layout_root)
    }
}