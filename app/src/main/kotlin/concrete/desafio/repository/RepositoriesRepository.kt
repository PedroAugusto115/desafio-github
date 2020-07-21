package concrete.desafio.repository

import androidx.lifecycle.MutableLiveData
import concrete.desafio.api.*
import concrete.desafio.extension.getNextPage
import concrete.desafio.model.Page
import retrofit2.Response

object RepositoriesRepository {

    fun getRepositoriesByPage(liveData: MutableLiveData<ApiResponse<Page>>, page: Int?){
        RemoteData.getInstance().getJavaRepositories(page = page).enqueue(object :
                CallbackRequest<Page>() {
            override fun success(response: Response<Page>) {
                liveData.postValue(success(data = Page(items = response.body()!!.items,
                        nextPage = response.getNextPage())))
            }

            override fun failureHttp(response: Response<Page>?) {
                liveData.postValue(error(msg = response?.message(), data = null))
            }

            override fun failure(throwable: Throwable) {
                liveData.postValue(error(msg = throwable.message, data = null))
            }
        })
    }
}
