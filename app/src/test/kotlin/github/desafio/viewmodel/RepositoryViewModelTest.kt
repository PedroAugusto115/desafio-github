package github.desafio.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import github.desafio.api.ApiResponse
import github.desafio.api.error
import github.desafio.api.success
import github.desafio.model.Person
import github.desafio.model.PullRequest
import github.desafio.model.Repository
import github.desafio.repository.PullRequestRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class RepositoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: PullRequestRepository
    private lateinit var viewModel: PullRequestViewModel

    @Before
    fun setup() {
        repository = mockk()
        viewModel = PullRequestViewModel(repository)
    }

    @Test
    fun whenRepositoryReturnsSuccessButEmpty_liveDataShouldHaveValueButEmptyData() {
        val successLiveData = MutableLiveData<ApiResponse<List<PullRequest>>>()
        successLiveData.postValue(success(mockEmptyApiResponse()))
        every { repository.getPullRequestByRepository(any(), any()) } returns successLiveData

        viewModel.getPullRequests(mockRepository())

        assert(viewModel.getPullRequests(mockRepository()).value != null)
        assert(viewModel.getPullRequests(mockRepository()).value!!.data!!.isEmpty())
    }

    @Test
    fun whenRepositoryReturnsSuccessWithData_liveDataShouldHaveValue() {
        val successLiveData = MutableLiveData<ApiResponse<List<PullRequest>>>()
        successLiveData.postValue(success(mockApiResponse()))
        every { repository.getPullRequestByRepository(any(), any()) } returns successLiveData

        viewModel.getPullRequests(mockRepository())

        assert(viewModel.getPullRequests(mockRepository()).value != null)
        assert(viewModel.getPullRequests(mockRepository()).value!!.data!!.size == 1)
    }

    @Test
    fun whenRepositoryReturnsError_liveDataShouldHaveNullValue() {
        val errorLiveData = MutableLiveData<ApiResponse<List<PullRequest>>>()
        errorLiveData.postValue(error(msg = "Error message", data = null))
        every { repository.getPullRequestByRepository(any(), any()) } returns errorLiveData

        viewModel.getPullRequests(mockRepository())

        assert(viewModel.getPullRequests(mockRepository()).value != null)
        assert(viewModel.getPullRequests(mockRepository()).value!!.data == null)
        assert(viewModel.getPullRequests(mockRepository()).value!!.errorMessage == "Error message")
    }
}

fun mockRepository() = Repository(
        id = 1,
        name = "repo",
        description = "repo",
        numberOfForks = 1,
        numberOfOpenIssues = 0,
        numberOfWatchers = 1,
        owner = Person("", "")
)

fun mockEmptyApiResponse() = listOf<PullRequest>()

fun mockApiResponse() = listOf(
        PullRequest(
                id = 1,
                title = "",
                description = "",
                dateCreated = Date(),
                pullRequestUrl = "",
                user = Person("", "")
        )
)