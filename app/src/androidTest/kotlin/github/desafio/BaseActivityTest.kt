package github.desafio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.rule.ActivityTestRule
import br.com.concretesolutions.requestmatcher.InstrumentedTestRequestMatcherRule
import br.com.concretesolutions.requestmatcher.RequestMatcherRule
import github.desafio.api.RemoteData
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy
import org.hamcrest.core.StringContains
import org.junit.Before
import org.junit.Rule

open class BaseActivityTest<T : AppCompatActivity>(activityClass: Class<T>, autoLaunch: Boolean = true)  {

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<T> = if (autoLaunch) IntentsTestRule(activityClass, true, false) else
        ActivityTestRule(activityClass, true, false)

    @JvmField
    @Rule
    val serverRule: RequestMatcherRule = InstrumentedTestRequestMatcherRule()

    @Before
    fun beforeBaseRequest() {
        val url = serverRule.url("/").toString()
        RemoteData.getInstance(url)
    }

    fun launchActivity(intent: Intent = Intent()){
        activityRule.launchActivity(intent)
        doWait()
    }

    private fun doWait(millis: Long = 500){
        Thread.sleep(millis)
    }

    protected fun enqueueError(errorCode: Int) {
        serverRule.addFixture(errorCode, "repository_first_page_success.json")
    }

    protected fun enqueueConnectionError() {
        serverRule.addResponse(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_END))
    }

    protected fun enqueueSuccess(file: String, matcher: String) {
        serverRule.withDefaultHeader("Link", "<https://api.github.com/search/repositories?q=language%3AJava&sort=stars&page=2> rel=\"next\"").addFixture(file)
                .ifRequestMatches()
                .pathMatches(StringContains(matcher))
    }
}