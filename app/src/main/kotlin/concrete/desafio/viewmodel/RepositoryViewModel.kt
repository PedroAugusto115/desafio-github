package concrete.desafio.viewmodel

import androidx.lifecycle.ViewModel
import concrete.desafio.SingleLiveEvent
import concrete.desafio.api.ApiResponse
import concrete.desafio.model.Page
import concrete.desafio.model.Repository
import concrete.desafio.repository.RepositoriesRepository
import concrete.desafio.ui.state.ViewState

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