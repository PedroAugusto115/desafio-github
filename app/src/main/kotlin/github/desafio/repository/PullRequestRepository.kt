package github.desafio.repository

import androidx.lifecycle.MutableLiveData
import github.desafio.api.*
import github.desafio.model.PullRequest
import retrofit2.Response

class PullRequestRepository {

    fun getPullRequestByRepository(
            creator: String,
            repository: String
    ) : MutableLiveData<ApiResponse<List<PullRequest>>> {
        val liveData = MutableLiveData<ApiResponse<List<PullRequest>>>()
        RemoteData.getInstance().getRepositoryPullRequests(creator = creator,
                repository = repository).enqueue(object : CallbackRequest<List<PullRequest>>() {
            override fun success(response: Response<List<PullRequest>>) {
                liveData.postValue(success(data = response.body()!!))
            }

            override fun failureHttp(response: Response<List<PullRequest>>?) {
                liveData.postValue(error(msg = response?.message(), data = null))
            }

            override fun failure(throwable: Throwable) {
                liveData.postValue(error(msg = throwable.message, data = null))
            }
        })
        return liveData
    }
}