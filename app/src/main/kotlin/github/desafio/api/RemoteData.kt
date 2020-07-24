package github.desafio.api

import github.desafio.BuildConfig

internal object RemoteData {

    private var instance: GitService? = null

    fun getInstance(url: String = BuildConfig.BASE_URL): GitService {
        val okHttpClient = OkHttpProvider.getInstance()
        return instance ?: synchronized(this) {
                    instance
                            ?: GitService
                                    .build(url, okHttpClient)
                                    .also { instance = it }
                }
    }
}