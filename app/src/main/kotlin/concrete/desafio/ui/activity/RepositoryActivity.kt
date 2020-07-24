package concrete.desafio.ui.activity

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import concrete.desafio.R
import concrete.desafio.api.ApiResponse
import concrete.desafio.model.Page
import concrete.desafio.model.Repository
import concrete.desafio.ui.adapter.RepositoryAdapter
import concrete.desafio.ui.state.ViewState
import concrete.desafio.viewmodel.RepositoryViewModel
import kotlinx.android.synthetic.main.activity_repository.*
import kotlinx.android.synthetic.main.view_empty_layout.*
import kotlinx.android.synthetic.main.view_error_layout.*
import kotlinx.android.synthetic.main.view_loading_layout.*
import org.koin.android.ext.android.inject

const val REPOSITORY_TAG = "Repository"
private const val RECYCLER_VIEW_LAST_VISIBLE_INDEX = "RECYCLER_VIEW_LAST_VISIBLE_INDEX"

class RepositoryActivity : AppCompatActivity() {

    private val viewModel: RepositoryViewModel by inject()

    val adapter = RepositoryAdapter { repo -> listViewItemClicked(repo)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)
        repositories_toolbar.setTitle(R.string.RepoListTitle)

        setupRecyclerView()

        viewModel.getState().observe(
                this, Observer { state -> updateView(state) })
        viewModel.getPage().observe(
                this, Observer { repositories -> updateList(repositories) })

        savedInstanceState?.let{
            restoreInstanceState(it)
        }
    }

    private fun setupRecyclerView() {
        repository_recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if(lastPosition == adapter.itemCount - 1)
                    viewModel.loadRepositories()
            }
        })

        repository_recycler_view.adapter = adapter
        repository_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun updateView(state: ViewState?) {
        when(state) {
            ViewState.EMPTY -> updateViewsVisibility(emptyViewVisibility = View.VISIBLE)
            ViewState.FIRST_LOADING -> updateViewsVisibility(loadingViewVisibility = View.VISIBLE)
            ViewState.LOADING -> updateViewsVisibility(loadingBarVisibility = View.VISIBLE,
                    itemListVisibility = View.VISIBLE)
            ViewState.EMPTY_ERROR -> updateViewsVisibility(errorViewVisibility = View.VISIBLE)
            ViewState.ERROR -> showSnackError()
            ViewState.LIST_ITEM -> updateViewsVisibility(itemListVisibility = View.VISIBLE)
        }
    }

    private fun updateList(page: ApiResponse<Page>?) {
        page?.let{
            if(it.errorMessage != null) {
                viewModel.getState().value = if (viewModel.repositories.isEmpty())
                    ViewState.EMPTY_ERROR else ViewState.ERROR
                return
            }

            viewModel.repositories.addAll(it.data?.items as List<Repository>)
            viewModel.nextPage = it.data.nextPage
            viewModel.getState().value = ViewState.LIST_ITEM

            adapter.addItems(viewModel.repositories)
            adapter.notifyDataSetChanged()
        }
    }

    private fun listViewItemClicked(selectedRepository: Repository) {
        val sendToRepositoryPulls = Intent(this, PullRequestActivity::class.java)
        sendToRepositoryPulls.putExtra(REPOSITORY_TAG, selectedRepository)
        startActivity(sendToRepositoryPulls)
    }

    private fun updateViewsVisibility(itemListVisibility: Int = View.GONE,
                                      emptyViewVisibility: Int = View.GONE,
                                      loadingViewVisibility: Int = View.GONE,
                                      errorViewVisibility: Int = View.GONE,
                                      loadingBarVisibility: Int = View.GONE) {
        repository_recycler_view.visibility = itemListVisibility
        empty_view_text.visibility = emptyViewVisibility
        loading_progress.visibility = loadingViewVisibility
        error_view_text.visibility = errorViewVisibility
        load_more_bar.visibility = loadingBarVisibility
    }

    private fun showSnackError() {
        load_more_bar.visibility = View.GONE
        repository_recycler_view.visibility = View.VISIBLE
        Snackbar.make(repository_layout_root, getString(R.string.repository_fetch_failed),
                Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.try_again_snack)) { viewModel.loadRepositories() }
                .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(viewModel.repositories.isNotEmpty()) {
            val layoutManager = repository_recycler_view.layoutManager as LinearLayoutManager
            val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
            outState.putInt(RECYCLER_VIEW_LAST_VISIBLE_INDEX, lastPosition)
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle) {
        if(savedInstanceState.containsKey(RECYCLER_VIEW_LAST_VISIBLE_INDEX)){
            viewModel.getState().value = ViewState.LIST_ITEM
            adapter.addItems(viewModel.repositories)
            adapter.notifyDataSetChanged()

            val lastPosition = savedInstanceState.getInt(RECYCLER_VIEW_LAST_VISIBLE_INDEX)
            val layoutManager = repository_recycler_view.layoutManager as LinearLayoutManager
            layoutManager.scrollToPosition(lastPosition)
            return
        }
        viewModel.getState().value = viewModel.getState().value
    }
}