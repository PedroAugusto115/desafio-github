package concrete.desafio

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import concrete.desafio.ui.activity.PullRequestActivity
import concrete.desafio.ui.activity.RepositoryActivity
import concrete.desafio.ui.activity.RepositoryActivity.Companion.REPOSITORY_TAG
import concrete.desafio.ui.adapter.RepositoryAdapter
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryActivityTest : BaseActivityTest<RepositoryActivity>(RepositoryActivity::class.java, false) {

    @Test
    fun badRequestOnFirstReqShowsErrorEmptyState() {
        enqueueError(400)
        launchActivity()
        onView(withId(R.id.errorViewText)).check(matches(isDisplayed()))
    }

    @Test
    fun connectionFailureOnFirstReqShowsErrorEmptyState() {
        enqueueConnectionError()
        launchActivity()
        onView(withId(R.id.errorViewText)).check(matches(isDisplayed()))
    }

    @Test
    fun successOnFirstRequestShowsList() {
        enqueueSuccess("repository_first_page_success.json", "search/repositories")
        launchActivity()
        onView(withId(R.id.repositoryRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickOnAFirstListItemCheckIntent() {
        enqueueSuccess("repository_first_page_success.json", "search/repositories")
        launchActivity()

        Intents.init()

        val result = ActivityResult(Activity.RESULT_OK, null)

        intending(hasComponent(PullRequestActivity::class.java.name)).respondWith(result)

        onView(withId(R.id.repositoryRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RepositoryAdapter.RepositoryItemViewHolder>(0, click()))

        intended(hasExtraWithKey(REPOSITORY_TAG))

        Intents.release()
    }

    @Test
    fun secondRequestReturnsBadRequestDoesNotIncrementList() {
        enqueueSuccess("repository_first_page_success.json", "search/repositories")
        launchActivity()

        enqueueError(400)
        onView(withId(R.id.repositoryRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RepositoryAdapter.RepositoryItemViewHolder>(14))

        assert(activityRule.activity.adapter.itemCount == 30)
    }

    @Test
    fun secondRequestReturnsConnectionErrorShowsSnackBar() {
        enqueueSuccess("repository_first_page_success.json", "search/repositories")
        launchActivity()

        enqueueConnectionError()
        onView(withId(R.id.repositoryRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RepositoryAdapter.RepositoryItemViewHolder>(14))

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.repository_fetch_failed)))
                .check(matches(isDisplayed()))
    }

    @Test
    fun secondRequestReturnsSuccessLoadMoreItems() {
        enqueueSuccess("repository_first_page_success.json", "search/repositories")
        launchActivity()

        enqueueSuccess("repository_second_page_success.json", "search/repositories")
        onView(withId(R.id.repositoryRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RepositoryAdapter.RepositoryItemViewHolder>(14))

        onView(withId(R.id.repositoryRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RepositoryAdapter.RepositoryItemViewHolder>(17))

        onView(withText("tinker")).check(matches(isDisplayed()))
    }
}