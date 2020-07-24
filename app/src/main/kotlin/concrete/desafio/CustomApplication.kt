package concrete.desafio

import android.app.Application
import concrete.desafio.repository.PullRequestRepository
import concrete.desafio.repository.RepositoriesRepository
import concrete.desafio.viewmodel.PullRequestViewModel
import concrete.desafio.viewmodel.RepositoryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CustomApplication)
            modules(myModule)
        }
    }
}

val myModule = module {
    single { RepositoriesRepository() }
    single { PullRequestRepository() }
    single { RepositoryViewModel(get()) }
    single { PullRequestViewModel(get()) }
}