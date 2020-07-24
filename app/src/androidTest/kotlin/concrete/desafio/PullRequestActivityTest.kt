package concrete.desafio

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import concrete.desafio.model.Person
import concrete.desafio.model.Repository
import concrete.desafio.ui.activity.PullRequestActivity
import concrete.desafio.ui.activity.RepositoryActivity
import concrete.desafio.ui.adapter.RepositoryAdapter
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PullRequestActivityTest : BaseActivityTest<PullRequestActivity>(PullRequestActivity::class.java, false) {

    @Test
    fun badRequestOnFirstReqShowsErrorEmptyState() {
        enqueueError(400)
        launchActivity(createIntent())
        Espresso.onView(ViewMatchers.withId(R.id.error_view_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun connectionFailureOnFirstReqShowsErrorEmptyState() {
        enqueueConnectionError()
        launchActivity(createIntent())
        Espresso.onView(ViewMatchers.withId(R.id.error_view_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun successOnFirstRequestShowsList() {
        enqueueSuccess("pull_request_success.json", "pulls")
        launchActivity(createIntent())
        Espresso.onView(ViewMatchers.withId(R.id.pull_request_list)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testClickOnAFirstListItemCheckIntent() {
        enqueueSuccess("pull_request_success.json", "pulls")
        launchActivity(createIntent())

        Intents.init()

        Espresso.onView(ViewMatchers.withId(R.id.pull_request_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RepositoryAdapter.RepositoryItemViewHolder>(0, ViewActions.click()))

        Intents.intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("https://github.com/ReactiveX/RxJava/pull/5748")))

        Intents.release()
    }

    private fun createIntent() : Intent {
        val intent = Intent()
        intent.putExtra(RepositoryActivity.REPOSITORY_TAG, createRepositoryModel())
        return intent
    }

    private fun createRepositoryModel() : Repository =
            Repository(0, "RxJava", "null", 0, 0, 0, Person("RxJava", "www.test.com"))
}