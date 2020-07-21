package concrete.desafio.api

import com.google.gson.GsonBuilder
import concrete.desafio.model.Page
import concrete.desafio.model.PullRequest
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitService {

    @GET("/search/repositories")
    fun getJavaRepositories(@Query("q") language: String = "language:Java",
                            @Query("sort") sort: String = "stars",
                            @Query("page") page: Int?): Call<Page>

    @GET("/repos/{creator}/{repository}/pulls")
    fun getRepositoryPullRequests(@Path("creator") creator: String,
                                  @Path("repository") repository: String): Call<List<PullRequest>>

    companion object {
        internal fun build(url: String, okHttpClient: OkHttpClient) : GitService {
            return Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create()))
                    .client(okHttpClient)
                    .build()
                    .create(GitService::class.java)
        }
    }
}