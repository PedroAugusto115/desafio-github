package concrete.desafio.repository

import androidx.lifecycle.MutableLiveData
import concrete.desafio.api.*
import concrete.desafio.model.PullRequest
import retrofit2.Response

class PullRequestRepository {

    fun getPullRequestByRepository(items: MutableLiveData<ApiResponse<List<PullRequest>>>,
                                   creator: String, repository: String){
        RemoteData.getInstance().getRepositoryPullRequests(creator = creator,
                repository = repository).enqueue(object : CallbackRequest<List<PullRequest>>() {
            override fun success(response: Response<List<PullRequest>>) {
                items.postValue(success(data = response.body()!!))
            }

            override fun failureHttp(response: Response<List<PullRequest>>?) {
                items.postValue(error(msg = response?.message(), data = null))
            }

            override fun failure(throwable: Throwable) {
                items.postValue(error(msg = throwable.message, data = null))
            }
        })
    }

}