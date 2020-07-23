package concrete.desafio.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import concrete.desafio.R
import concrete.desafio.extension.loadFromUrl
import concrete.desafio.model.Repository

class RepositoryAdapter(private val listener: (Repository) -> Unit) :
        RecyclerView.Adapter<RepositoryAdapter.RepositoryItemViewHolder>()
{

    private var items: ArrayList<Repository> = arrayListOf()

    fun addItems(newItems: List<Repository>) {
        items = ArrayList(newItems)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RepositoryItemViewHolder, position: Int) {
        val repository = items[position]

        val description: String = if (repository.description.isNullOrEmpty())
            holder.view.context.resources.getString(R.string.NoDescription)
        else
            repository.description

        holder.itemName.text = repository.name
        holder.itemDescription.text = description
        holder.itemFork.text = repository.numberOfForks.toString()
        holder.itemStars.text = repository.numberOfWatchers.toString()
        holder.itemUserLogin.text = repository.owner.name
        holder.itemUserImage.loadFromUrl(repository.owner.photoPath)
        holder.itemRoot.setOnClickListener { listener(repository) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val repositoryView = inflater.inflate(R.layout.list_item_repository, parent, false)

        return RepositoryItemViewHolder(repositoryView)
    }

    class RepositoryItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var itemName: TextView = view.findViewById(R.id.repository_name)
        var itemDescription: TextView = view.findViewById(R.id.repository_description)
        var itemFork: TextView = view.findViewById(R.id.repository_fork_number)
        var itemStars: TextView = view.findViewById(R.id.repository_star_number)
        var itemUserLogin: TextView = view.findViewById(R.id.user_login)
        var itemUserImage: ImageView = view.findViewById(R.id.user_photo)
        var itemRoot: View = view.findViewById(R.id.root_layout)
    }
}

