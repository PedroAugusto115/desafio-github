package github.desafio.ui.activity

import androidx.lifecycle.Observer
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import github.desafio.R
import github.desafio.api.ApiResponse
import github.desafio.model.PullRequest
import github.desafio.model.Repository
import github.desafio.ui.adapter.PullRequestAdapter
import github.desafio.ui.state.ViewState
import github.desafio.viewmodel.PullRequestViewModel
import kotlinx.android.synthetic.main.activity_repository_pulls.*
import kotlinx.android.synthetic.main.view_empty_layout.*
import kotlinx.android.synthetic.main.view_error_layout.*
import kotlinx.android.synthetic.main.view_loading_layout.*
import org.koin.android.ext.android.inject

private const val PULL_REQUEST_LAST_VISIBLE_INDEX = "RECYCLER_VIEW_LAST_VISIBLE_INDEX"

class PullRequestActivity : AppCompatActivity() {

    private val viewModel: PullRequestViewModel by inject()

    private val adapter = PullRequestAdapter { url -> listViewItemClicked(url)}

    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_pulls)

        val bundle: Bundle = this.intent.extras!!
        repository = bundle.getParcelable(REPOSITORY_TAG)!!

        setupToolbar(repository)
        setupRecyclerView()

        val opened = getString(R.string.pullRequestOpenedText,
                repository.numberOfOpenIssues,
                repository.numberOfForks)
        repository_open_issues.text = opened

        viewModel.getViewState().observe(
                this, Observer { state -> updateView(state) })
        viewModel.getPullRequests(repository).observe(
                this, Observer { repositories -> updateList(repositories) })

        savedInstanceState?.let{
            restoreInstanceState(it)
        }
    }

    private fun setupToolbar(repository: Repository) {
        setSupportActionBar(pull_request_toolbar)

        supportActionBar?.let{
            it.title = repository.name
            it.setDisplayHomeAsUpEnabled(true)
            pull_request_toolbar.navigationIcon?.
                    setColorFilter(ContextCompat.getColor(this, R.color.colorWhite),
                            PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                val intent = NavUtils.getParentActivityIntent(this)
                intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                NavUtils.navigateUpTo(this, intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateView(state: ViewState?) {
        when(state) {
            ViewState.EMPTY -> updateViewsVisibility(emptyViewVisibility = View.VISIBLE)
            ViewState.FIRST_LOADING -> updateViewsVisibility(loadingViewVisibility = View.VISIBLE)
            ViewState.LOADING -> updateViewsVisibility(loadingBarVisibility = View.VISIBLE,
                    itemListVisibility = View.VISIBLE)
            ViewState.EMPTY_ERROR -> updateViewsVisibility(errorViewVisibility = View.VISIBLE)
            ViewState.ERROR -> updateViewsVisibility(errorViewVisibility = View.VISIBLE)
            ViewState.LIST_ITEM -> updateViewsVisibility(itemListVisibility = View.VISIBLE)
        }
    }

    private fun updateList(pullRequests: ApiResponse<List<PullRequest>>?) {
        pullRequests?.let{
            if(it.errorMessage != null) {
                viewModel.getViewState().value = if (viewModel.pulls.isEmpty())
                    ViewState.EMPTY_ERROR
                else
                    ViewState.ERROR
                return
            }

            val newData = it.data!!.toMutableList()

            if (newData.isEmpty()) {
                viewModel.getViewState().value = ViewState.EMPTY
                return
            }

            viewModel.pulls = newData
            viewModel.getViewState().value = ViewState.LIST_ITEM

            adapter.addItems(viewModel.pulls)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupRecyclerView() {
        pull_request_list.adapter = adapter
        pull_request_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun listViewItemClicked(url: String) {
        val openPullRequestOnBrowser = Intent(Intent.ACTION_VIEW)
        openPullRequestOnBrowser.data = Uri.parse(url)
        startActivity(openPullRequestOnBrowser)
    }

    private fun updateViewsVisibility(itemListVisibility: Int = View.GONE,
                                      emptyViewVisibility: Int = View.GONE,
                                      loadingViewVisibility: Int = View.GONE,
                                      errorViewVisibility: Int = View.GONE,
                                      loadingBarVisibility: Int = View.GONE) {
        pull_request_list.visibility = itemListVisibility
        empty_view_text.visibility = emptyViewVisibility
        loading_progress.visibility = loadingViewVisibility
        error_view_text.visibility = errorViewVisibility
        load_more_bar.visibility = loadingBarVisibility
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(viewModel.pulls.isNotEmpty()){
            val layoutManager = pull_request_list.layoutManager as LinearLayoutManager
            val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
            outState.putInt(PULL_REQUEST_LAST_VISIBLE_INDEX, lastPosition)
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle){
        if(savedInstanceState.containsKey(PULL_REQUEST_LAST_VISIBLE_INDEX)){
            viewModel.getViewState().value = ViewState.LIST_ITEM
            adapter.addItems(viewModel.pulls)
            adapter.notifyDataSetChanged()

            val lastPosition = savedInstanceState.getInt(PULL_REQUEST_LAST_VISIBLE_INDEX)
            val layoutManager = pull_request_list.layoutManager as LinearLayoutManager
            layoutManager.scrollToPosition(lastPosition)
            return
        }
        viewModel.getViewState().value = viewModel.getViewState().value
    }
}