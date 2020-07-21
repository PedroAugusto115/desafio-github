package concrete.desafio.ui.activity

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import concrete.desafio.R
import concrete.desafio.api.ApiResponse
import concrete.desafio.model.PullRequest
import concrete.desafio.model.Repository
import concrete.desafio.ui.activity.RepositoryActivity.Companion.REPOSITORY_TAG
import concrete.desafio.ui.adapter.PullRequestAdapter
import concrete.desafio.ui.state.ViewState
import concrete.desafio.viewmodel.PullRequestViewModel
import kotlinx.android.synthetic.main.activity_repository_pulls.*
import kotlinx.android.synthetic.main.view_empty_layout.*
import kotlinx.android.synthetic.main.view_error_layout.*
import kotlinx.android.synthetic.main.view_loading_layout.*

class PullRequestActivity : AppCompatActivity() {

    companion object {
        const val RECYCLER_VIEW_LAST_VISIBLE_INDEX = "RECYCLER_VIEW_LAST_VISIBLE_INDEX"
    }

    private lateinit var viewModel: PullRequestViewModel

    private val adapter = PullRequestAdapter { url -> listViewItemClicked(url)}

    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_pulls)

        val bundle: Bundle = this.intent.extras
        repository = bundle.getParcelable(REPOSITORY_TAG)

        setupToolbar(repository)
        setupRecyclerView()

        val opened = getString(R.string.pullRequestOpenedText,
                repository.numberOfOpenIssues,
                repository.numberOfForks)
        repository_open_issues.text = opened

        viewModel = ViewModelProviders.of(this)
                .get(PullRequestViewModel::class.java)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(viewModel.pulls.isNotEmpty()){
            val layoutManager = pull_request_list.layoutManager as LinearLayoutManager
            val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
            outState!!.putInt(RECYCLER_VIEW_LAST_VISIBLE_INDEX, lastPosition)
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle){
        if(savedInstanceState.containsKey(RECYCLER_VIEW_LAST_VISIBLE_INDEX)){
            viewModel.getViewState().value = ViewState.LIST_ITEM
            adapter.addItems(viewModel.pulls)
            adapter.notifyDataSetChanged()

            val lastPosition = savedInstanceState.getInt(RECYCLER_VIEW_LAST_VISIBLE_INDEX)
            val layoutManager = pull_request_list.layoutManager as LinearLayoutManager
            layoutManager.scrollToPosition(lastPosition)
            return
        }
        viewModel.getViewState().value = viewModel.getViewState().value
    }
}