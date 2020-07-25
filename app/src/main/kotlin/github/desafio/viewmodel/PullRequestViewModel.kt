package github.desafio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.desafio.SingleLiveEvent
import github.desafio.api.ApiResponse
import github.desafio.model.PullRequest
import github.desafio.model.Repository
import github.desafio.repository.PullRequestRepository
import github.desafio.ui.state.ViewState

class PullRequestViewModel(val api: PullRequestRepository) : ViewModel() {

    var pulls = mutableListOf<PullRequest>()

    private var pullRequestResponse: MutableLiveData<ApiResponse<List<PullRequest>>> = MutableLiveData()

    private val state: SingleLiveEvent<ViewState> = SingleLiveEvent()

    fun getPullRequests(repository: Repository): MutableLiveData<ApiResponse<List<PullRequest>>> {
        if( pullRequestResponse.value == null ) {
            loadPullRequests(repository.owner.name, repository.name)
        }
        return pullRequestResponse
    }

    fun getViewState(): SingleLiveEvent<ViewState> {
        if( state.value == null ) {
            state.value = ViewState.EMPTY
        }
        return state
    }

    private fun loadPullRequests(creator: String, repository: String) {
        if(state.value == ViewState.FIRST_LOADING || state.value == ViewState.LOADING) return

        if (pulls.isEmpty()) state.value = ViewState.FIRST_LOADING
        else state.value = ViewState.LOADING

        pullRequestResponse = api.getPullRequestByRepository(creator, repository)
    }
}