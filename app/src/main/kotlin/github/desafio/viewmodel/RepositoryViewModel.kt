package github.desafio.viewmodel

import androidx.lifecycle.ViewModel
import github.desafio.SingleLiveEvent
import github.desafio.api.ApiResponse
import github.desafio.model.Page
import github.desafio.model.Repository
import github.desafio.repository.RepositoriesRepository
import github.desafio.ui.state.ViewState

class RepositoryViewModel(val api: RepositoriesRepository) : ViewModel() {

    var nextPage: Int? = 1
    var repositories = mutableListOf<Repository>()

    private val page: SingleLiveEvent<ApiResponse<Page>> = SingleLiveEvent()
    private val state: SingleLiveEvent<ViewState> = SingleLiveEvent()

    fun getPage(): SingleLiveEvent<ApiResponse<Page>> {
        if(page.value == null){
            loadRepositories()
        }
        return page
    }

    fun getState(): SingleLiveEvent<ViewState> {
        if( state.value == null ) {
            state.value = ViewState.EMPTY
        }
        return state
    }

    fun loadRepositories() {
        nextPage?.let {
            if (state.value == ViewState.FIRST_LOADING || state.value == ViewState.LOADING) return

            if (repositories.isEmpty())
                state.value = ViewState.FIRST_LOADING
            else
                state.value = ViewState.LOADING

            api.getRepositoriesByPage(page, it)
        }
    }
}